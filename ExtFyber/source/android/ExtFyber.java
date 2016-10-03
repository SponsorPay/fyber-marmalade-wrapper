/*
java implementation of the ExtFyber extension.

Add android-specific functionality here.

These functions are called via JNI from native code.
*/
/*
 * NOTE: This file was originally written by the extension builder, but will not
 * be overwritten (unless --force is specified) and is intended to be modified.
 */
package com.teamlava.fyberwrapper;

import com.android.mainactivity.MainActivity;

import com.ideaworks3d.marmalade.LoaderAPI;
import com.teamlava.fyberwrapper.FyberActivity;
import com.fyber.cache.CacheManager;
// Comment in the following line if you want to use ResourceUtility
// import com.ideaworks3d.marmalade.ResourceUtility;

import android.util.Log;

class ExtFyber
{
    private static boolean isNativeMethodLinked = false;
    private static final String TAG = "marmalade";

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
        if (FyberActivity.singleton == null) {
            Log.d(TAG, "[ExtFyber] fyber_marmalade_setup: FyberActivity.singleton == null");
            return;
        }
        FyberActivity.singleton.setup(appId, securityToken, userId, bucketId, conditionGroupId);
    }

    public void requestOffers()
    {
        if (FyberActivity.singleton == null) {
            Log.d(TAG, "[ExtFyber] requestOffers: FyberActivity.singleton == null");
            return;
        }
        FyberActivity.singleton.performRequest();
    }

    public boolean showAd()
    {
        if (FyberActivity.singleton == null) {
            Log.d(TAG, "[ExtFyber] showAd: FyberActivity.singleton == null");
            return false;
        }
        return FyberActivity.singleton.showAd();
    }
    public void fyber_cache_pause_download()
    {
        CacheManager.pauseDownloads(MainActivity.singleton);
    }
    public void fyber_cache_resume_download()
    {
     	CacheManager.resumeDownloads(MainActivity.singleton);   
    }
}
