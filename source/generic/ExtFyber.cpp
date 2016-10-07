/*
Generic implementation of the ExtFyber extension.
This file should perform any platform-indepedentent functionality
(e.g. error checking) before calling platform-dependent implementations.
*/

/*
 * NOTE: This file was originally written by the extension builder, but will not
 * be overwritten (unless --force is specified) and is intended to be modified.
 */


#include "ExtFyber_internal.h"
s3eResult ExtFyberInit()
{
    //Add any generic initialisation code here
    return ExtFyberInit_platform();
}

void ExtFyberTerminate()
{
    //Add any generic termination code here
    ExtFyberTerminate_platform();
}

void fyber_marmalade_setup(const char* appId, const char* securityToken, const char* userId, const char* bucketId, const char* conditionGroupId, ExtFyberStatusCallbackFn fn)
{
	fyber_marmalade_setup_platform(appId, securityToken, userId, bucketId, conditionGroupId, fn);
}

void requestOffers()
{
	requestOffers_platform();
}

bool showAd()
{
	return showAd_platform();
}

void fyber_cache_pause_download()
{
	fyber_cache_pause_download_platform();
}

void fyber_cache_resume_download()
{
	fyber_cache_resume_download_platform();
}
