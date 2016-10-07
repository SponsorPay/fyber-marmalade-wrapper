#import "ExtFyber.h"

@interface SPBrandEngageClient : NSObject

@end

/** These constants are used to refer to the different states an engagement can be in. */
typedef NS_ENUM(NSInteger, SPBrandEngageClientStatus) {
    /// The BrandEngage player's underlying content has been loaded and the engagement has started.
    STARTED,
    
    /// The engagement has finished after completing. User will be rewarded.
    CLOSE_FINISHED,
    
    /// The engagement has finished before completing.
    /// The user might have aborted it, either explicitly (by tapping the close button) or
    /// implicitly (by switching to another app) or it was interrupted by an asynchronous event
    /// like an incoming phone call.
    CLOSE_ABORTED,
    
    /// The engagement was interrupted by an error.
    ERROR
};


@protocol SPBrandEngageClientDelegate<NSObject>

/** @name Requesting offers */

/** Sent when BrandEngage receives an answer about offers availability.

@param brandEngageClient The instance of SPBrandEngageClient that sent this message.
@param areOffersAvailable A boolean value indicating whether offers are available. If this value is YES, you can start the engagement.
*/
- (void)brandEngageClient:(SPBrandEngageClient *)brandEngageClient didReceiveOffers:(BOOL)areOffersAvailable;

/** @name Showing offers */

/** Sent when a running engagement changes state.

@param brandEngageClient The instance of SPBrandEngageClient that sent this message.
@param newStatus A constant value of the SPBrandEngageClientStatus type indicating the new status of the engagement.
*/

- (void)brandEngageClient:(SPBrandEngageClient *)brandEngageClient didChangeStatus:(SPBrandEngageClientStatus)newStatus;


@end
