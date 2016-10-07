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

import com.ideaworks3d.marmalade.LoaderAPI;
import com.ideaworks3d.marmalade.LoaderActivity;

import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.app.Activity;

@FyberSDK
public class FyberActivity extends LoaderActivity implements RequestCallback {
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

	private static final String TAG = "FyberActivity";
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
        Log.d(TAG, message);
    }

	public void setup(String appId, String securityToken, String userId, String bucketId, String conditionGroupId) {
		FyberLogger.enableLogging(true);
		try {
			debugLog("setup appId = " + appId + "; securityToken = " + securityToken + "; userId = " + userId + "; bucketId = " + bucketId + "; conditionGroupId = " + conditionGroupId);

			Fyber.Settings fyberSettings = Fyber
					.with(appId, LoaderAPI.getActivity())
					.withSecurityToken(securityToken)
					.start();

			User.addCustomValue("bucketId", bucketId);
			User.addCustomValue("conditionGroupId", conditionGroupId);

			fyberSettings.addParameter("pub0", bucketId).notifyUserOnReward(false).notifyUserOnCompletion(false);

		} catch (IllegalArgumentException e) {
			debugLog(e.getLocalizedMessage());
		}
		ExtFyber.safe_notifyStatusChange(EXT_FYBER_STARTED);
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
			}
		}

		return false;
	}

	public void performRequest() {
		RewardedVideoRequester
            .create(this)
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
		resetRequestingState();
		resetIntent();
		ExtFyber.safe_notifyStatusChange(EXT_FYBER_AD_NOT_AVAILABLE);
	}

	@Override
	public void onRequestError(RequestError requestError) {
		debugLog(requestError.getDescription());
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

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		resetIntent();

		if (resultCode == Activity.RESULT_OK && requestCode == REWARDED_VIDEO_REQUEST_CODE) {

			String status = data.getStringExtra(RewardedVideoActivity.ENGAGEMENT_STATUS);
			debugLog("ENGAGEMENT_STATUS = " + status);

            switch(status) {
                case RewardedVideoActivity.REQUEST_STATUS_PARAMETER_ABORTED_VALUE:
                    ExtFyber.safe_notifyStatusChange(EXT_FYBER_CLOSE_ABORTED);
                    break;
                case RewardedVideoActivity.REQUEST_STATUS_PARAMETER_FINISHED_VALUE:
                    ExtFyber.safe_notifyStatusChange(EXT_FYBER_CLOSE_FINISHED);
                    break;
                case RewardedVideoActivity.REQUEST_STATUS_PARAMETER_ERROR:
                    ExtFyber.safe_notifyStatusChange(EXT_FYBER_ERROR);
                    break;
                default:
                    debugLog("Unknown Engagement Status: " + status);
            }
		}	
	}

    public static FyberActivity getInstance() {
        return singleton;
    }
}
