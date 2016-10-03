/*
 * android-specific implementation of the ExtFyber extension.
 * Add any platform-specific functionality here.
 */
/*
 * NOTE: This file was originally written by the extension builder, but will not
 * be overwritten (unless --force is specified) and is intended to be modified.
 */
#include "ExtFyber_internal.h"

#include "s3eEdk.h"
#include "s3eEdk_android.h"
#include <jni.h>
#include "IwDebug.h"

#include <android/log.h>
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "marmalade", __VA_ARGS__)

static jobject g_Obj;
static jmethodID g_fyber_marmalade_setup;
static jmethodID g_requestOffers;
static jmethodID g_showAd;
static ExtFyberStatusCallbackFn g_statusCallbackFn;
static jmethodID g_fyber_cache_pause_download;
static jmethodID g_fyber_cache_resume_download;

char* getCStringInNewMemory(jstring str)
{
    JNIEnv* env = s3eEdkJNIGetEnv();
    if (!str)
        return NULL;

    jboolean free;
    const char* res = env->GetStringUTFChars(str, &free);
    int length = strlen(res) + 1;

    char * retStr = (char *)s3eEdkReallocOS(NULL, length);
    strcpy(retStr, res);
    env->ReleaseStringUTFChars(str, res); 
    env->DeleteLocalRef(str);
    return retStr;
}

static int32 onNotifyStatusChange(void* systemData, void* userData) {
    int* status = (int*)systemData;
    if (g_statusCallbackFn) {
        g_statusCallbackFn((ExtFyberStatus)*status);
    } else {
        LOGD("onNotifyStatusChange: g_statusCallbackFn not set");
    }

    return 0;
}

static void ExtFyber_notifyStatusChange(JNIEnv *env, jclass _this, jint status) {
    s3eEdkCallbacksEnqueue(S3E_EXT_EXTFYBER_HASH,
        EXTFYBER_NOTIFY_STATUS_CHANGE,
        &status,
        sizeof(status),
        NULL,
        S3E_FALSE,
        NULL
    );
}

s3eResult ExtFyberInit_platform()
{
    // Get the environment from the pointer
    JNIEnv* env = s3eEdkJNIGetEnv();
    jobject obj = NULL;
    jmethodID cons = NULL;

    const JNINativeMethod nativeMethodDefs[] =
    {   {"notifyStatusChange", "(I)V", (void *)&ExtFyber_notifyStatusChange}
    };

    // Get the extension class
    jclass cls = s3eEdkAndroidFindClass("com/teamlava/fyberwrapper/ExtFyber");
    if (!cls)
        goto fail;

    // Get its constructor
    cons = env->GetMethodID(cls, "<init>", "()V");
    if (!cons)
        goto fail;

    env->RegisterNatives(cls, nativeMethodDefs, sizeof(nativeMethodDefs)/sizeof(nativeMethodDefs[0]));

    // Construct the java class
    obj = env->NewObject(cls, cons);
    if (!obj)
        goto fail;

    // Get all the extension methods
    g_fyber_marmalade_setup = env->GetMethodID(cls, "fyber_marmalade_setup", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    if (!g_fyber_marmalade_setup)
        goto fail;

    g_requestOffers = env->GetMethodID(cls, "requestOffers", "()V");
    if (!g_requestOffers)
        goto fail;

    g_showAd = env->GetMethodID(cls, "showAd", "()Z");
    if (!g_showAd)
        goto fail;

    g_fyber_cache_pause_download = env->GetMethodID(cls, "fyber_cache_pause_download", "()V");
    if (!g_fyber_cache_pause_download)
        goto fail;

    g_fyber_cache_resume_download = env->GetMethodID(cls, "fyber_cache_resume_download", "()V");
    if (!g_fyber_cache_resume_download)
        goto fail;

    s3eEdkCallbacksRegister(
    S3E_EXT_EXTFYBER_HASH,
    EXTFYBER_CALLBACK_MAX,
    EXTFYBER_NOTIFY_STATUS_CHANGE,
    &onNotifyStatusChange,
    NULL,
    S3E_TRUE);

    IwTrace(EXTFYBER, ("EXTFYBER init success"));
    g_Obj = env->NewGlobalRef(obj);
    env->DeleteLocalRef(obj);
    env->DeleteGlobalRef(cls);

    // Add any platform-specific initialisation code here
    return S3E_RESULT_SUCCESS;

fail:
    jthrowable exc = env->ExceptionOccurred();
    if (exc)
    {
        env->ExceptionDescribe();
        env->ExceptionClear();
        IwTrace(ExtFyber, ("One or more java methods could not be found"));
    }

    env->DeleteLocalRef(obj);
    env->DeleteGlobalRef(cls);
    return S3E_RESULT_ERROR;

}

void ExtFyberTerminate_platform()
{ 
    // Add any platform-specific termination code here
    JNIEnv* env = s3eEdkJNIGetEnv();
    env->DeleteGlobalRef(g_Obj);
    g_Obj = NULL;
}

void fyber_marmalade_setup_platform(const char* appId, const char* securityToken, const char* userId, const char* bucketId, const char* conditionGroupId, ExtFyberStatusCallbackFn fn)
{
    g_statusCallbackFn = fn;
    JNIEnv* env = s3eEdkJNIGetEnv();
    jstring appId_jstr = env->NewStringUTF(appId);
    jstring securityToken_jstr = env->NewStringUTF(securityToken);
    jstring userId_jstr = env->NewStringUTF(userId);
    jstring bucketId_jstr = env->NewStringUTF(bucketId);
    jstring conditionGroupId_jstr = env->NewStringUTF(conditionGroupId);
    env->CallVoidMethod(g_Obj, g_fyber_marmalade_setup, appId_jstr, securityToken_jstr, userId_jstr, bucketId_jstr, conditionGroupId_jstr);
}

void requestOffers_platform()
{
    JNIEnv* env = s3eEdkJNIGetEnv();
    env->CallVoidMethod(g_Obj, g_requestOffers);
}

int showAd_platform()
{
    JNIEnv* env = s3eEdkJNIGetEnv();
    return (int)env->CallIntMethod(g_Obj, g_showAd);
}

void fyber_cache_pause_download_platform()
{
    JNIEnv* env = s3eEdkJNIGetEnv();
    env->CallVoidMethod(g_Obj, g_fyber_cache_pause_download);
}

void fyber_cache_resume_download_platform()
{
    JNIEnv* env = s3eEdkJNIGetEnv();
    env->CallVoidMethod(g_Obj, g_fyber_cache_resume_download);
}
