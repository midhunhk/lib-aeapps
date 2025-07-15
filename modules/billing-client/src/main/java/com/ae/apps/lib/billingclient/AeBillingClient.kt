/*
 * Copyright 2018 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ae.apps.lib.billingclient

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams

/**
 * An implementation for Google Play Billing Client to enable purchase of InApp Products
 *
 *
 * [billing](https://developer.android.com/google/play/billing/)
 * [integrate](https://developer.android.com/google/play/billing/integrate)
 * [migrate-gplv8](https://developer.android.com/google/play/billing/migrate-gpblv8)
 *
 * @since 4.1 (First Implementation)
 * @since 5.0 (PBLv8 Migration)
 */
class AeBillingClient : PurchasesUpdatedListener, DefaultLifecycleObserver {

    private lateinit var billingClient: BillingClient
    private lateinit var handler: BillingClientHandler
    private var connected = false

    // To store retrieved ProductDetails for easy access when launching the flow
    private var productDetailsMap = mutableMapOf<String, ProductDetails>()

    /**
     * Initialize the billing client
     *
     * @param context use requireActivity() to pass in the base
     * @param handler a handler to handle the result
     */
    fun initialize(context: AppCompatActivity, handler: BillingClientHandler?) {
        requireNotNull(handler) { "BillingClientHandler cannot be null" }
        this.handler = handler
        context.lifecycle.addObserver(this)

        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()

        startServiceConnection()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (::billingClient.isInitialized && billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    /**
     * Launch the billing flow for a given product ID.
     * Retrieves the ProductDetails internally.
     *
     * @param activity The Activity to launch the billing flow from.
     * @param productId The product ID (SKU) to purchase.
     * @return BillingClient.BillingResponseCode from launchBillingFlow.
     *         Returns BillingResponseCode.ITEM_UNAVAILABLE if product details are not found.
     */
    fun launchBillingFlowForProductId(activity: Activity, productId: String): Int {
        val productDetails = productDetailsMap[productId]
        if (productDetails == null) {
            handler.handlePurchaseError(null, BillingResponseCode.ITEM_UNAVAILABLE, "Product ID $productId details not found. Ensure queryProductDetailsAsync was successful.")
            return BillingResponseCode.ITEM_UNAVAILABLE
        }
        return launchBillingFlow(activity, productDetails)
    }

    /**
     * Launch the billing flow
     * @see [BillingClient.BillingResponseCode](https://developer.android.com/reference/com/android/billingclient/api/BillingClient.BillingResponseCode)
     *
     * @param productDetails the productDetails
     * @return BillingClient.BillingResponseCode
     */
    protected fun launchBillingFlow(activity: Activity, productDetails: ProductDetails): Int {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                // If this is a subscription with multiple offers, you might need to set an offer token here.
                // For one-time products, this is usually not needed.
                // .setOfferToken(selectedOfferToken) // Example for subscriptions
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        // Launch the billing flow and return the response
        return billingClient.launchBillingFlow(activity, billingFlowParams).responseCode
    }

    /**
     * Start the service connection
     */
    private fun startServiceConnection() {
        if (!::billingClient.isInitialized) {
            // Or log an error, handle appropriately
            return
        }
        if (billingClient.isReady) {
            // Already connected, potentially query purchases or products if needed
            // This might happen if initialize is called multiple times
            if (connected) { // only if our connected flag is also true
                handler.onBillingClientSetup() // Notify handler it's ready
                queryProductDetails() // Query products if already connected
            }
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    connected = true
                    handler.onBillingClientSetup()
                    queryProductDetails() // PBL v5+ Change: Query for ProductDetails
                } else {
                    connected = false
                    // Optionally, notify handler of setup failure
                    // handler.onBillingSetupFailed(billingResult.responseCode)
                }
            }

            override fun onBillingServiceDisconnected() {
                connected = false
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method or notify user.
            }
        })
    }

    /**
     * Queries product details for the SKUs provided by the handler.
     * This replaces querySkuDetailsAsync from older PBL versions.
     */
    private fun queryProductDetails() {
        val productIds = handler.getProductIds() // Assuming handler provides a list of product IDs (SKUs)
        if (productIds.isEmpty()) {
            // No SKUs to query, perhaps notify handler or log
            handler.productDetailsResponse(emptyList()) // Notify with empty list
            return
        }

        val productList = productIds.map { productId ->
            Product.newBuilder()
                .setProductId(productId)
                .setProductType(ProductType.INAPP) // Or ProductType.SUBS for subscriptions
                .build()
        }

        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, queryProductDetailsResult ->
            val productDetailsList = queryProductDetailsResult.productDetailsList

            if (billingResult.responseCode == BillingResponseCode.OK) {
                // If BillingResult is OK, productDetailsList from a non-null queryProductDetailsResult should be non-null (possibly empty).
                productDetailsList.forEach { productDetail ->
                    productDetailsMap[productDetail.productId] = productDetail
                }
                handler.productDetailsResponse(productDetailsList)
            } else {
                // Handle error in fetching product details
                Log.e("AeBillingClient", "queryProductDetailsAsync error: ${billingResult.responseCode}, ${billingResult.debugMessage}")
                handler.productDetailsResponse(emptyList())
            }
        }
    }

    /**
     * For Consumables, Consume the Purchase so that it could be bought again.
     * For non-consumables/subscriptions, you should Acknowledge purchases instead.
     *
     * @param purchase the purchase
     */
    fun consumePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            // Don't consume if not purchased (e.g., pending)
            return
        }
        // Verify purchase signature if not done already (server-side recommended)
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                handler.onPurchaseConsumed(purchaseToken)
            } else {
                handler.onConsumePurchaseError(purchaseToken, billingResult.responseCode)
            }
        }
    }

    /**
     * Acknowledge a purchase. Required for all non-consumable one-time products and all subscriptions.
     * Purchases that are not acknowledged within 3 days will be automatically refunded.
     * This client currently focuses on INAPP (consumable by default), so this is an example.
     *
     * @param purchase The purchase to acknowledge.
     */
    fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            // Don't acknowledge if not purchased (e.g., pending)
            return
        }
        if (purchase.isAcknowledged) {
            // Already acknowledged
            return
        }

        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                handler.onPurchaseAcknowledged(purchase.purchaseToken)
            } else {
                handler.onAcknowledgePurchaseError(purchase.purchaseToken, billingResult.responseCode)
            }
        }
    }


    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                if (purchases != null) {
                    for (purchase in purchases) {
                        // Process each purchase.
                        // For consumables, you might consume them.
                        // For non-consumables/subscriptions, you must acknowledge them.
                        // The handler should decide this, or AeBillingClient can have more logic.
                        handler.handlePurchase(purchase)
                    }
                } else {
                    // Purchases list is null, though OK response. Should not happen often.
                    handler.handlePurchaseError(null, billingResult.responseCode, "OK response with null purchases.")
                }
            }
            BillingResponseCode.USER_CANCELED -> {
                handler.handleUserCancelled(purchases)
            }
            BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // This is important. Query existing purchases to ensure user has access.
                // Potentially tell the handler about this state.
                handler.handleItemAlreadyOwned(purchases)
                // You might want to query purchases again here if you don't do it on every onResume
                queryPurchasesAsync()
            }
            BillingResponseCode.SERVICE_DISCONNECTED,
            BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingResponseCode.BILLING_UNAVAILABLE,
            BillingResponseCode.ERROR,
            BillingResponseCode.DEVELOPER_ERROR,
            BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingResponseCode.ITEM_UNAVAILABLE,
            BillingResponseCode.NETWORK_ERROR -> { // Added specific error codes
                handler.handlePurchaseError(purchases, billingResult.responseCode, billingResult.debugMessage)
            }
            else -> { // Other codes not explicitly handled
                handler.handlePurchaseError(purchases, billingResult.responseCode, billingResult.debugMessage)
            }
        }
    }

    /**
     * Queries current user's active INAPP and SUBS purchases.
     * Call this on onResume() of your Activity/Fragment to ensure purchases are synced.
     */
    fun queryPurchasesAsync() {
        if (!::billingClient.isInitialized || !billingClient.isReady) {
            return
        }

        // Query for INAPP products
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(ProductType.INAPP).build()
        ) { billingResultInApp, activeInAppPurchases ->
            if (billingResultInApp.responseCode == BillingResponseCode.OK) {
                handler.onQueryPurchasesResponse(ProductType.INAPP, activeInAppPurchases)
            } else {
                handler.onQueryPurchasesError(ProductType.INAPP, billingResultInApp.responseCode)
            }
        }

        // Query for SUBS products (if your app supports subscriptions)

        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()
        ) { billingResultSubs, activeSubs ->
            if (billingResultSubs.responseCode == BillingResponseCode.OK) {
                handler.onQueryPurchasesResponse(ProductType.SUBS, activeSubs)
            } else {
                handler.onQueryPurchasesError(ProductType.SUBS, billingResultSubs.responseCode)
            }
        }
    }

    interface BillingClientHandler {
        /**
         * Invoked when the BillingClient has setup correctly.
         */
        fun onBillingClientSetup()

        /**
         * Provide a list of product IDs (formerly SKUs) that your app offers.
         * These will be used to query for ProductDetails.
         *
         * @return list of product IDs (e.g., "my_consumable_1", "my_premium_upgrade")
         */
        fun getProductIds(): List<String> // Changed from getSkus

        /**
         * Callback when ProductDetails query is complete.
         *
         * @param productDetailsList list of ProductDetails (new, replaces SkuDetails)
         */
        fun productDetailsResponse(productDetailsList: List<ProductDetails>)

        /**
         * Callback when there is a purchase error during the flow or in onPurchasesUpdated.
         *
         * @param purchases list of purchases (can be null)
         * @param responseCode responseCode from BillingResponseCode
         * @param debugMessage Optional debug message from BillingResult
         */
        fun handlePurchaseError(purchases: List<Purchase>?, responseCode: Int, debugMessage: String? = null)

        /**
         * Callback when user has cancelled the purchase flow.
         *
         * @param purchases list of purchases (can be null)
         */
        fun handleUserCancelled(purchases: List<Purchase>?)

        /**
         * Method invoked on a successful purchase or when a purchase needs processing.
         * The implementation should check `purchase.purchaseState`.
         * If `PurchaseState.PURCHASED`:
         *   - Verify the purchase (server-side recommended).
         *   - For consumables: call `aeBillingClient.consumePurchase(purchase)`.
         *   - For non-consumables/subscriptions: call `aeBillingClient.acknowledgePurchase(purchase)`.
         * If `PurchaseState.PENDING`:
         *   - Update UI to inform user purchase is pending. Do not grant entitlement yet.
         *
         * @param purchase purchase details
         */
        fun handlePurchase(purchase: Purchase)

        /**
         * Callback when a purchase is successfully consumed.
         * @param purchaseToken The token of the consumed purchase.
         */
        fun onPurchaseConsumed(purchaseToken: String)

        /**
         * Callback when consuming a purchase fails.
         * @param purchaseToken The token of the purchase that failed to consume.
         * @param responseCode The error code.
         */
        fun onConsumePurchaseError(purchaseToken: String?, responseCode: Int)

        /**
         * Callback when a purchase is successfully acknowledged.
         * @param purchaseToken The token of the acknowledged purchase.
         */
        fun onPurchaseAcknowledged(purchaseToken: String)

        /**
         * Callback when acknowledging a purchase fails.
         * @param purchaseToken The token of the purchase that failed to acknowledge.
         * @param responseCode The error code.
         */
        fun onAcknowledgePurchaseError(purchaseToken: String?, responseCode: Int)

        /**
         * Callback when the user already owns the item they are trying to buy.
         * @param purchases List of relevant purchases, may be null.
         */
        fun handleItemAlreadyOwned(purchases: List<Purchase>?)

        /**
         * Callback with the results of a queryPurchasesAsync call.
         * @param productType The type of product queried (INAPP or SUBS).
         * @param purchasesList The list of active purchases for that type.
         */
        fun onQueryPurchasesResponse(productType: String, purchasesList: List<Purchase>)

        /**
         * Callback if queryPurchasesAsync fails for a product type.
         * @param productType The type of product queried.
         * @param responseCode The error code.
         */
        fun onQueryPurchasesError(productType: String, responseCode: Int)
        }
}
