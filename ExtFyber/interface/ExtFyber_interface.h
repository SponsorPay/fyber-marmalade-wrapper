/*
 * WARNING: this is an autogenerated file and will be overwritten by
 * the extension interface script.
 */
/**
 * Definitions for functions types passed to/from s3eExt interface
 */
typedef  s3eResult(*ExtFyberRegister_t)(ExtFyberCallback cbid, s3eCallback fn, void* userData);
typedef  s3eResult(*ExtFyberUnRegister_t)(ExtFyberCallback cbid, s3eCallback fn);
typedef       void(*fyber_marmalade_setup_t)(const char* appId, const char* securityToken, const char* userId, const char* bucketId, const char* conditionGroupId, ExtFyberStatusCallbackFn fn);
typedef       void(*requestOffers_t)();
typedef        int(*showAd_t)();
typedef       void(*fyber_cache_pause_download_t)();
typedef       void(*fyber_cache_resume_download_t)();

/**
 * struct that gets filled in by ExtFyberRegister
 */
typedef struct ExtFyberFuncs
{
    ExtFyberRegister_t m_ExtFyberRegister;
    ExtFyberUnRegister_t m_ExtFyberUnRegister;
    fyber_marmalade_setup_t m_fyber_marmalade_setup;
    requestOffers_t m_requestOffers;
    showAd_t m_showAd;
    fyber_cache_pause_download_t m_fyber_cache_pause_download;
    fyber_cache_resume_download_t m_fyber_cache_resume_download;
} ExtFyberFuncs;
