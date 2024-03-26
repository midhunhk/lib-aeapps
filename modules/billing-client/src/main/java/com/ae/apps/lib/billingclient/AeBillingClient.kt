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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.google.common.collect.ImmutableList

/**
 * An implementation for Google Play Billing Client to enable purchase of InApp Products
 *
 *
 * [...](https://developer.android.com/google/play/billing/)
 * [...](https://developer.android.com/google/play/billing/integrate)
 *
 * @since 4.1
 */
class AeBillingClient : PurchasesUpdatedListener, DefaultLifecycleObserver {

    private lateinit var billingClient: BillingClient
    private lateinit var handler: BillingClientHandler
    private var connected = false

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

        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()


        startServiceConnection()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        billingClient.endConnection()
    }

    /**
     * Launch the billing flow
     * @see https://developer.android.com/reference/com/android/billingclient/api/BillingClient.BillingResponseCode
     *
     * @param productDetails the productDetails
     * @return BillingClient.BillingResponseCode
     */
    protected fun launchBillingFlow(activity: Activity, productDetails: ProductDetails): Int {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(productDetails)
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
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    connected = true
                    handler.onBillingClientSetup()
                    queryProductDetailsForInApp()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                connected = false
            }
        })
    }

    private val inAppProducts = ImmutableList.of(
        Product.newBuilder()
            .setProductType(ProductType.INAPP)
            .build()
    )

    private fun queryProductDetailsForInApp() {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(inAppProducts)
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                            productDetailsList ->
            // check billingResult
            // process returned productDetailsList
            if (billingResult.responseCode == BillingResponseCode.OK) {
                handler.productDetailsResponse(productDetailsList)
            }
        }
    }

    /**
     * For Consumables, Consume the Purchase so that it could be bought again
     *
     * @param purchase the purchase
     * @param listener a callback when the purchase is consumed
     */
    protected fun consumeAsync(purchase: Purchase, listener: ConsumeResponseListener?) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.consumeAsync(consumeParams, listener!!)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingResponseCode.OK && null != purchases) {
            for (purchase in purchases) {
                handler.handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
            // Handle user cancelled case
            handler.handleUserCancelled(purchases)
        } else {
            // Handle an error flow
            handler.handlePurchaseError(purchases, billingResult.responseCode)
        }
    }

    interface BillingClientHandler {
        /**
         * Invoked when the BillingClient has setup correctly
         */
        fun onBillingClientSetup()

        /**
         * List of SKUs
         *
         * @return list of SKUs
         */
        val skus: List<String?>?

        /**
         * Callback when there is a purchase error
         *
         * @param purchases    list of purchases
         * @param responseCode responseCode
         */
        fun handlePurchaseError(purchases: List<Purchase>?, responseCode: Int)

        /**
         * Callback when user has cancelled the purchase
         *
         * @param purchases list of purchases
         */
        fun handleUserCancelled(purchases: List<Purchase>?)

        /**
         * Compare with "skuDetails.getSku()" to sku name and fetch the corresponding
         * price associated with it as "skuDetails.getPrice()"
         *
         * @param productDetails list of SKUs
         */
        fun productDetailsResponse(productDetails: List<ProductDetails>?)

        /**
         * Method invoked on a successful purchase
         *
         * @param purchase purchase details
         */
        fun handlePurchase(purchase: Purchase?)
    }
}
