package com.teamlava.fyberwrapper;

import com.fyber.ads.AdFormat;
import com.fyber.ads.videos.RewardedVideoActivity;
import com.fyber.annotations.FyberSDK;
import com.fyber.Fyber;
import com.fyber.user.User;
import com.fyber.currency.VirtualCurrencyErrorResponse;
import com.fyber.currency.VirtualCurrencyResponse;
import com.fyber.requesters.RequestCallback;
import com.fyber.requesters.RequestError;
import com.fyber.requesters.RewardedVideoRequester;
import com.fyber.requesters.VirtualCurrencyCallback;
import com.fyber.requesters.VirtualCurrencyRequester;
import com.fyber.utils.FyberLogger;

import com.jirbo.adcolony.AdColony;
import com.ideaworks3d.marmalade.LoaderAPI;

import android.content.Intent;
import android.util.Log;
import android.os.Bundle;

@FyberSDK
public class FyberActivity implements RequestCallback {
	protected static final int INTERSTITIAL_REQUEST_CODE = 8792;
	protected static final int OFFERWALL_REQUEST_CODE = 8795;
	protected static final int REWARDED_VIDEO_REQUEST_CODE = 8796;

	private static final int EXT_FYBER_STARTED = 0;
	private static final int EXT_FYBER_CLOSE_FINISHED = 1;
	private static final int EXT_FYBER_CLOSE_ABORTED = 2;
	private static final int EXT_FYBER_ERROR = 3;

	private static final int EXT_FYBER_AD_AVAILABLE = 101;
	private static final int EXT_FYBER_AD_NOT_AVAILABLE = 102;
	private static final int EXT_FYBER_AD_REQUEST_ERROR = 103;

	private static final String TAG = "marmalade";
	private static final String DEBUG_CONFIG_KEY = "ExtFyber_debug";
  
	private static boolean DEBUG_ENABLED;

	private boolean isRequestingState;
	protected Intent intent;

	public static FyberActivity singleton = new FyberActivity();

	protected int getRequestCode() {
		return REWARDED_VIDEO_REQUEST_CODE;
	}

	private String getLogTag() {
		return TAG;
	}


	void debugLog(String message) {
        Log.d(TAG, "FyberActivity:" + message);
    }

	public void setup(String appId, String securityToken, String userId, String bucketId, String conditionGroupId) {
		FyberLogger.enableLogging(true);
		try {
			debugLog("setup appId = " + appId + "; securityToken = " + securityToken + "; userId = " + userId + "; bucketId = " + bucketId + "; conditionGroupId = " + conditionGroupId);

// 			Fyber.Settings fyberSettings = Fyber
// 					.with(APP_ID, LoaderAPI.getActivity())
// 					.withSecurityToken(SECURITY_TOKEN)
// // by default Fyber SDK will start precaching. If you wish to only start precaching at a later time you can uncomment this line and use 'CacheManager' to start, pause or resume on demand.
// //					.withManualPrecaching()
// // if you do not provide an user id Fyber SDK will generate one for you
// //					.withUserId(USER_ID)
// 					.start();
			// ** SDK INITIALIZATION **

			//when you start Fyber SDK you get a Settings object that you can use to customise the SDK behaviour.
			//Have a look at the method 'customiseFyberSettings' to learn more about possible customisation.
			Fyber.Settings fyberSettings = Fyber
					.with(appId, LoaderAPI.getActivity())
					.withSecurityToken(securityToken)
// by default Fyber SDK will start precaching. If you wish to only start precaching at a later time you can uncomment this line and use 'CacheManager' to start, pause or resume on demand.
//					.withManualPrecaching()
// if you do not provide an user id Fyber SDK will generate one for you
					.withUserId(userId)
					.start();
// uncomment to customise Fyber SDK
//			customiseFyberSettings(fyberSettings);

			User.addCustomValue("bucketId", bucketId);
			User.addCustomValue("conditionGroupId", conditionGroupId);

			fyberSettings.addParameter("pub0", bucketId).notifyUserOnReward(false).notifyUserOnCompletion(false);

		} catch (IllegalArgumentException e) {
			debugLog(e.getLocalizedMessage());
		}
	}

	private void customiseFyberSettings(Fyber.Settings fyberSettings) {
		fyberSettings.notifyUserOnReward(false)
				.closeOfferWallOnRedirect(true)
				.notifyUserOnCompletion(true)
				.addParameter("myCustomParamKey", "myCustomParamValue")
				.setCustomUIString(Fyber.Settings.UIStringIdentifier.GENERIC_ERROR, "my custom generic error msg");
	}

    public void onDestroy() {
    	singleton = null;
    }

    public void finish() {
        onDestroy();
    }

    // when a button is clicked, request or show the ad according to Intent availability
	public boolean showAd() {
		//avoid requesting an ad when already requesting
		if (!isRequestingState()) {
			//if we already have an Intent, we start the ad Activity
			if (isIntentAvailable()) {
				//start the ad format specific Activity
				LoaderAPI.getActivity().startActivityForResult(intent, getRequestCode());
				return true;
			} else {
				isRequestingState = true;
				//perform the ad request. Each Fragment has its own implementation.
				performRequest();
			}
		}

		return false;
	}

	public void performRequest() {
		if (isIntentAvailable()) {
			return;
		}
		//Requesting a rewarded video ad
		RewardedVideoRequester
				.create(this)
						// you can add a virtual Currency Requester by chaining this extra method
				// .withVirtualCurrencyRequester(getVirtualCurrencyRequester())
				.request(LoaderAPI.getActivity());
	}

		@Override
	public void onAdAvailable(Intent intent) {
		resetRequestingState();
		this.intent = intent;
		ExtFyber.safe_notifyStatusChange(EXT_FYBER_AD_AVAILABLE);
	}

	@Override
	public void onAdNotAvailable(AdFormat adFormat) {
		debugLog("No ad available");
		resetRequestingState();
		resetIntent();
		ExtFyber.safe_notifyStatusChange(EXT_FYBER_AD_NOT_AVAILABLE);
	}

	@Override
	public void onRequestError(RequestError requestError) {
		debugLog("Semething went wrong with the request: " + requestError.getDescription() + ";" + requestError);
		resetRequestingState();
		resetIntent();
		ExtFyber.safe_notifyStatusChange(EXT_FYBER_AD_REQUEST_ERROR);
	}

	private void resetRequestingState() {
		isRequestingState = false;
	}

	private void resetIntent() {
		intent = null;
	}

	protected boolean isIntentAvailable() {
		return intent != null;
	}

	protected boolean isRequestingState() {
		return isRequestingState;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		debugLog("requestCode = " + requestCode + "; resultCode = " + resultCode + "; data = " + data);
		resetIntent();

		if (data != null) {
			debugLog("ENGAGEMENT_STATUS = " + data.getStringExtra("ENGAGEMENT_STATUS"));

			String status = data.getStringExtra("ENGAGEMENT_STATUS");
			if ("CLOSE_ABORTED".equals(status)) {
				ExtFyber.safe_notifyStatusChange(EXT_FYBER_CLOSE_ABORTED);
			} else if ("CLOSE_FINISHED".equals(status)) {
				ExtFyber.safe_notifyStatusChange(EXT_FYBER_CLOSE_FINISHED);
			} else if ("ERROR".equals(status)) {
				ExtFyber.safe_notifyStatusChange(EXT_FYBER_ERROR);
			} else {
				debugLog("unknown status: " + status);
			}
		}	
	}

    public static FyberActivity getInstance() {
        return singleton;
    }
}
