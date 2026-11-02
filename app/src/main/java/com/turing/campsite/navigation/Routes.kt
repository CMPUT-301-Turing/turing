
package com.turing.campsite.navigation

/**
 * Centralized navigation route definitions for the Turing Campsite app.
 *
 * All navigation destinations should reference these constants
 * rather than using hardcoded route strings.
 */
object Routes {

    // Authentication
    const val LOGIN = "Login"
    const val REGISTER = "Register"
    const val LOG_OUT = "LogOut"

    // Administrator
    const val ADMIN_HOME = "AdminHome"
    const val ADMIN_PROFILE = "AdminProfile"
    const val ADMIN_PROFILE_SETTING = "AdminProfileSetting"
    const val ADMIN_CAMPSITE = "AdminCampsite"
    const val ADMIN_MESSAGE_LOG = "AdminMessageLog"
    const val ADMIN_BROWSE_IMAGE = "AdminBrowseImage"
    const val ADMIN_NOTIFICATION_LOG = "AdminNotificationLog"
    const val ADMIN_CONVERSATION = "AdminConversation"

    // Entrant
    const val ENTRANT_HOME = "EntrantHome"
    const val ENTRANT_PROFILE = "EntrantProfile"
    const val ENTRANT_EDIT_PROFILE = "EntrantEditProfile"
    const val ENTRANT_CAMPSITE_LIST = "EntrantCampsiteList"
    const val ENTRANT_SELECT_CAMPSITE = "EntrantSelectCampsite"
    const val ENTRANT_QR_CODE = "EntrantQRCode"
    const val ENTRANT_NOTIFICATION = "EntrantNotification"
    const val ENTRANT_ACCEPT_RESERVATION = "EntrantAcceptReservation"

    // Campsite Owner
    const val OWNER_HOME = "OwnerHome"
    const val OWNER_REGISTER_NEW_CAMPSITE = "OwnerRegisterNewCampsite"
    const val OWNER_CAMPSITE_FULL_DETAIL = "OwnerCampsiteFullDetail"
    const val OWNER_CAMPSITE_HISTORY = "OwnerCampsiteHistory"
    const val OWNER_MESSAGE = "OwnerMessage"
    const val OWNER_LOTTERY_DETAILS = "OwnerLotteryDetails"
    const val OWNER_EDIT_LOTTERY = "OwnerEditLottery"
    const val OWNER_CREATE_LOTTERY = "OwnerCreateLottery"
    const val OWNER_CAMPSITE_QR_CODE = "OwnerCampsiteQRCode"
    const val OWNER_EDIT_CAMPSITE = "OwnerEditCampsiteScreen"
    const val OWNER_NOTIFICATION = "OwnerNotification"
}
