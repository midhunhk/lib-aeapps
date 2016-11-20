/*
 * Copyright 2015 Midhun Harikumar
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

package com.ae.apps.common.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ae.apps.common.utils.inapp.IabHelper;
import com.ae.apps.common.utils.inapp.IabResult;
import com.ae.apps.common.utils.inapp.Purchase;

/**
 * Base activity that implements google play in-app billing v3 methods.
 * 
 * Activities that implement this class can supply body for abstract methods
 * that plug into the in-app billing flow implementation
 * 
 * @author Midhun
 *
 */
public abstract class DonationsBaseActivity extends ToolBarBaseActivity{

	private static final String	TAG			= "DonationsBase";
	private boolean mDebugLog 				= false;
	private static final int	RC_REQUEST	= 2001;
	private IabHelper			mHelper		= null;
	private Activity mActivity 				= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = this;
		
		// Create the IABHelper and start the IAB setup
		mHelper = new IabHelper(this, getBase64PublicKey());

		// This should be false in release build
		mHelper.enableDebugLogging(false);

		// Setup IAB
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					logDebug("problem in setup inapp " + result);
				}
			}
		});

	}
	
	/**
	 * Get the public key for in-app billing processes
	 * 
	 */
	protected abstract String getBase64PublicKey();
	
	/**
	 * Launches the purchase flow using IAB
	 * 
	 * @param productCode 
	 * @param extraData
	 */
	protected void launchPurchaseFlow(String productCode, String extraData){
		if(null != productCode && !TextUtils.isEmpty(productCode)){
			// on button click after selecting a purchase item
			mHelper.launchPurchaseFlow(mActivity, productCode, RC_REQUEST, mPurchaseFinishedlistener, extraData);
		}
	}
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedlistener = new IabHelper.OnIabPurchaseFinishedListener() {
		
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase info) {
			logDebug("onpurchasefinished " + result);
			
			if(null == mHelper){
				return;
			}
			
			// Process the purchase of the item
			processPurchase(result, info);
		}
	};
	
	private void processPurchase(IabResult result, Purchase info) {
		if(result.isFailure()){
			// Toast.makeText(getApplicationContext(), result.getResponse(), Toast.LENGTH_SHORT).show();
			return;
		}
		
		// A manage product in in-app-billing v3 cannot be purchased again unless it is consumed
		// We want to let users donate more than once
		if(checkPurchaseResponse(info)){
			mHelper.consumeAsync(info, mConsumeFinishedListener);
		}
	}

	/**
	 * Check the purchase response to see if we are good
	 * 
	 * @param info
	 * @return
	 */
	protected abstract boolean checkPurchaseResponse(Purchase info);

	/**
	 * Invoked when purchase is consumed successfully
	 * 
	 * @param purchase
	 * @param result
	 */
	protected abstract void onPurchaseConsumeFinished(Purchase purchase, IabResult result);
	
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			logDebug("onconsumefinished " + result);
			
			if(null == mHelper){
				return;
			}
			
			onPurchaseConsumeFinished(purchase, result);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		logDebug("onactivityresult ");

		if (null == mHelper) {
			return;
		}

		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled by IABUtil
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			logDebug("onactivityresult handled by IABUtil");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mHelper) {
			mHelper.dispose();
		}
		mHelper = null;
	}

	private void logDebug(String msg){
		if(mDebugLog) Log.d(TAG, msg);
	}

}
