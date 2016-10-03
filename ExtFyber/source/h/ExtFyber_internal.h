/*
 * Internal header for the ExtFyber extension.
 *
 * This file should be used for any common function definitions etc that need to
 * be shared between the platform-dependent and platform-indepdendent parts of
 * this extension.
 */

/*
 * NOTE: This file was originally written by the extension builder, but will not
 * be overwritten (unless --force is specified) and is intended to be modified.
 */


#ifndef EXTFYBER_INTERNAL_H
#define EXTFYBER_INTERNAL_H

#include "s3eTypes.h"
#include "ExtFyber.h"
#include "ExtFyber_autodefs.h"


/**
 * Initialise the extension.  This is called once then the extension is first
 * accessed by s3eregister.  If this function returns S3E_RESULT_ERROR the
 * extension will be reported as not-existing on the device.
 */
s3eResult ExtFyberInit();

/**
 * Platform-specific initialisation, implemented on each platform
 */
s3eResult ExtFyberInit_platform();

/**
 * Terminate the extension.  This is called once on shutdown, but only if the
 * extension was loader and Init() was successful.
 */
void ExtFyberTerminate();

/**
 * Platform-specific termination, implemented on each platform
 */
void ExtFyberTerminate_platform();
void fyber_marmalade_setup_platform(const char* appId, const char* securityToken, const char* userId, const char* bucketId, const char* conditionGroupId, ExtFyberStatusCallbackFn fn);

void requestOffers_platform();

int showAd_platform();

void fyber_cache_pause_download_platform();

void fyber_cache_resume_download_platform();


#endif /* !EXTFYBER_INTERNAL_H */