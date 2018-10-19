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

package com.ae.apps.libs.billingclient;

import android.os.Bundle;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Base class that implements Google Play Billing Client, InApp Products
 * https://developer.android.com/google/play/billing/billing_library_overview#java
 *
 * @since 4.0
 */
public abstract class BaseBillingClientFragment extends Fragment implements PurchasesUpdatedListener {

    private BillingClient mBillingClient;

    /**
     * Invoked when the BillingClient has setup correctly
     */
    protected abstract void onBillingClientSetup();

    /**
     * List of SKUs
     *
     * @return list of SKUs
     */
    protected abstract List<String> getSkus();

    /**
     * Callback when there is a purchase error
     *
     * @param purchases    list of purchases
     * @param responseCode responseCode
     */
    protected abstract void handlePurchaseError(List<Purchase> purchases, int responseCode);

    /**
     * Callback when user has cancelled the purchase
     *
     * @param purchases list of purchases
     */
    protected abstract void handleUserCancelled(List<Purchase> purchases);

    /**
     * Compare with "skuDetails.getSku()" to sku name and fetch the corresponding
     * price associated with it as "skuDetails.getPrice()"
     *
     * @param skuDetails list of SKUs
     */
    protected abstract void skuDetailsResponse(List<SkuDetails> skuDetails);

    /**
     * Method invoked on a successful purchase
     *
     * @param purchase purchase details
     */
    protected abstract void handlePurchase(Purchase purchase);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBillingClient = BillingClient
                .newBuilder(requireActivity())
                .setListener(this)
                .build();

        startServiceConnection();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mBillingClient) {
            mBillingClient.endConnection();
        }
    }

    /**
     * Launch the billing flow
     *
     * @param skuId id of the sku
     */
    protected void launchBillingFlow(final String skuId) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(skuId)
                .setType(BillingClient.SkuType.INAPP)
                .build();
        mBillingClient.launchBillingFlow(requireActivity(), flowParams);
    }

    private void startServiceConnection() {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int responseCode) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    // Billing client is ready
                    onBillingClientSetup();

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
                .setSkusList(getSkus())
                .setType(BillingClient.SkuType.INAPP)
                .build();

        mBillingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    skuDetailsResponse(skuDetailsList);
                }
            }
        });

    }

    /**
     * Consume the Purchase so that it could be bought again
     *
     * @param purchaseToken the purchase token
     * @param listener      a callback when the purchase is consumed
     */
    protected void consumeAsync(String purchaseToken, ConsumeResponseListener listener) {
        mBillingClient.consumeAsync(purchaseToken, listener);
    }

    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK && null != purchases) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle user cancelled case
            handleUserCancelled(purchases);
        } else {
            // Handle an error flow
            handlePurchaseError(purchases, responseCode);
        }
    }

}
