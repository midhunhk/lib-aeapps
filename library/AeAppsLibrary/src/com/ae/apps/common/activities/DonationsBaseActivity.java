package com.ae.apps.common.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

	private boolean mDebugLog 				= true;
	private static final int	RC_REQUEST	= 2001;
	private static final String	TAG			= "DonationsBase";
	private IabHelper			mHelper		= null;
	private Activity mActivity 				= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = this;
		
		// Create the IABHelper and start the IAB setup
		mHelper = new IabHelper(this, getBase64PublicKey());

		// This should be false in release build
		mHelper.enableDebugLogging(true);

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
		if(null != productCode && !productCode.isEmpty()){
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
			
			processPurchase(result, info);
		}
	};
	
	private void processPurchase(IabResult result, Purchase info) {
		if(result.isFailure()){
			// Toast.makeText(getApplicationContext(), result.getResponse(), Toast.LENGTH_SHORT).show();
			return;
		}
		
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
