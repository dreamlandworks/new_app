package com.satrango.remote.end_points

object UserApiEndPoints {

    final const val LOGIN = "user/login"
    final const val NEW_USER = "user/newuser"
    final const val OTP_REQUEST = "user/regsms"
    final const val OTP_REQUEST_FORGOT_PWD = "user/forgot"
    final const val USER_RESET_PASSWORD = "user/changepwd"
    final const val USER_BROWSE_CATEGORIES = "user/cat"
    final const val USER_BROWSE_SUB_CATEGORIES = "user/subcat/id"
    final const val SHOW_USER_PROFILE = "user/show"
    final const val USER_PROFILE_UPDATE = "user/update"
    final const val DELETE_USER_ADDRESS = "user/delete/address"
    final const val USER_GET_ALERTS = "user/alerts/get"
    final const val USER_SEARCH_KEYWORDS = "user/autocomplete"
    final const val VERIFY_USER = "user/verify"
    final const val USER_KEYWORDS = "user/keywords"
    final const val USER_SEARCH_RESULTS = "user/search_result"
    final const val USER_FAQS = "user/user_faq"
    final const val USER_SINGLE_MOVE_BOOKING = "user/single_move_booking"
    final const val USER_BLUE_COLLAR_BOOKING = "user/blue_collar_booking"
    final const val USER_MULTI_MOVE_BOOKING = "user/multi_move_booking"
    final const val USER_ADD_BOOKING_ADDRESS = "user/add_address"
    final const val UPDATE_FCM_TOKEN = "user/update_token"
    final const val FCM_SEND = "fcm/send"
    final const val CONFIRM_PAYMENT = "fcm/booking_payments"
    final const val PROVIDER_RESPONSE = "user/sp_booking_response"
    final const val USER_DETAILS_ACCEPT = "user/booking_details"

}