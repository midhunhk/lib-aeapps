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

package com.ae.apps.lib.billingclient;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.List;

/**
 * An implementation for Google Play Billing Client to enable purchase of InApp Products
 * <p>
 * <a href="https://developer.android.com/google/play/billing/">...</a>
 * <a href="https://developer.android.com/google/play/billing/integrate">...</a>
 *
 * @since 4.1
 */
public class AeBillingClient implements PurchasesUpdatedListener, LifecycleObserver {

    private BillingClient billingClient;
    private BillingClientHandler handler;

    /**
     *
     * @param context use requireActivity() to pass in the base
     * @param handler a handler to handle the result
     */
    public void initialize(final AppCompatActivity context, final BillingClientHandler handler) {
        if (null == handler) {
            throw new IllegalArgumentException("BillingClientHandler cannot be null");
        }
        this.handler = handler;
        context.getLifecycle().addObserver(this);

        billingClient = BillingClient
                .newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        startServiceConnection();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (null != billingClient) {
            billingClient.endConnection();
        }
    }

    /**
     * Launch the billing flow
     *
     * @param skuDetails the skuDetails
     * @return launchBillingFlow responseCode
     */
    protected int launchBillingFlow(final Activity activity, final SkuDetails skuDetails) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        return billingClient.launchBillingFlow(activity, flowParams).getResponseCode();
    }

    /**
     * Start the service connection
     */
    private void startServiceConnection() {
        billingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Billing client is ready
                    handler.onBillingClientSetup();

                    querySkuDetails();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    private void querySkuDetails() {
        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(handler.getSkus())
                .setType(BillingClient.SkuType.INAPP)
                .build();

        billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                handler.skuDetailsResponse(list);
            }
        });
    }

    /**
     * For Consumables, Consume the Purchase so that it could be bought again
     *
     * @param purchase the purchase
     * @param listener a callback when the purchase is consumed
     */
    protected void consumeAsync(Purchase purchase, ConsumeResponseListener listener) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        billingClient.consumeAsync(consumeParams, listener);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && null != purchases) {
            for (Purchase purchase : purchases) {
                handler.handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle user cancelled case
            handler.handleUserCancelled(purchases);
        } else {
            // Handle an error flow
            handler.handlePurchaseError(purchases, billingResult.getResponseCode());
        }
    }

    public interface BillingClientHandler {
        /**
         * Invoked when the BillingClient has setup correctly
         */
        void onBillingClientSetup();

        /**
         * List of SKUs
         *
         * @return list of SKUs
         */
        List<String> getSkus();

        /**
         * Callback when there is a purchase error
         *
         * @param purchases    list of purchases
         * @param responseCode responseCode
         */
        void handlePurchaseError(List<Purchase> purchases, int responseCode);

        /**
         * Callback when user has cancelled the purchase
         *
         * @param purchases list of purchases
         */
        void handleUserCancelled(List<Purchase> purchases);

        /**
         * Compare with "skuDetails.getSku()" to sku name and fetch the corresponding
         * price associated with it as "skuDetails.getPrice()"
         *
         * @param skuDetails list of SKUs
         */
        void skuDetailsResponse(List<SkuDetails> skuDetails);

        /**
         * Method invoked on a successful purchase
         *
         * @param purchase purchase details
         */
        void handlePurchase(Purchase purchase);
    }
}
