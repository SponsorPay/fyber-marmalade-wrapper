/*
java implementation of the ExtFyber extension.

Add android-specific functionality here.

These functions are called via JNI from native code.
*/
package com.fyber.marmalade;

import com.ideaworks3d.marmalade.LoaderAPI;
import com.fyber.marmalade.FyberActivity;
import com.fyber.cache.CacheManager;

import android.util.Log;

class ExtFyber
{
    private static boolean isNativeMethodLinked = false;
    private static final String TAG = "marmalade";
    private static final FyberActivity activity = FyberActivity.getInstance();

    public ExtFyber() {
        isNativeMethodLinked = true; // in ExtFyber_platform.cpp, new instance is created before native method is linked.
        Log.d(TAG, "isNativeMethodLinked is set to true in constructor");
    }

	native private static void notifyStatusChange(int status);

    public static void safe_notifyStatusChange(int status) {
        if (isNativeMethodLinked) {
            notifyStatusChange(status);
        }
    }

    public void fyber_marmalade_setup(String appId, String securityToken, String userId, String bucketId, String conditionGroupId)
    {
        if (activity == null) {
            Log.d(TAG, "[ExtFyber] fyber_marmalade_setup: activity == null");
            return;
        }
        activity.setup(appId, securityToken, userId, bucketId, conditionGroupId);
    }

    public void requestOffers()
    {
        if (activity == null) {
            Log.d(TAG, "[ExtFyber] requestOffers: activity == null");
            return;
        }
        activity.performRequest();
    }

    public boolean showAd()
    {
        if (activity == null) {
            Log.d(TAG, "[ExtFyber] showAd: activity == null");
            return false;
        }
        return activity.showAd();
    }
    public void fyber_cache_pause_download()
    {
        CacheManager.pauseDownloads(LoaderAPI.getActivity());
    }
    public void fyber_cache_resume_download()
    {
     	CacheManager.resumeDownloads(LoaderAPI.getActivity());   
    }
}
