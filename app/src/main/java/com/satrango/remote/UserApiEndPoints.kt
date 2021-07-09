package com.satrango.remote

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
    final const val USER_SEARCH_KEYWORDS = "user/keywords"
    final const val VERIFY_USER = "user/verify"

}