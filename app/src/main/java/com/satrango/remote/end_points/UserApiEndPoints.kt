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
    final const val USER_SEARCH_KEYWORDS = "user/phrase"
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
    final const val CONFIRM_PAYMENT = "user/booking_payments"
    final const val PROVIDER_RESPONSE = "user/sp_booking_response"
    final const val USER_DETAILS_ACCEPT = "user/booking_details"
    final const val USER_BOOKING_DETAILS = "user/user_booking_details"
    final const val AUTO_COMPLETE_ADDRESS = "user/autocomplete_address"
    final const val TRANSACTiON_HISTORY = "user/get_transaction_history"
    final const val PLANS = "user/user_plans"
    final const val SKILLS = "user/autocomplete"
    final const val BID_RANGE = "user/bid_range"
    final const val USER_PLAN_PAYMENT = "user/membership_payments"
    final const val POST_JOB_SINGLE_MOVE = "user/single_move_job_post"
    final const val POST_JOB_BLUE_COLLAR = "user/blue_collar_job_post"
    final const val POST_JOB_MULTI_MOVE = "user/multi_move_job_post"
    final const val USER_JOB_POST_DETAILS = "user/user_job_post_details"
    final const val USER_JOB_POST_VIEW_DETAILS = "user/job_post_details"
    final const val VIEW_BIDS = "user/job_post_bids_list"
    final const val VIEW_PROPOSAL = "user/job_post_bids_details"
    final const val POST_DISCUSSION = "user/post_discussion"
    final const val DISCUSSION_LIST = "user/job_post_discussion_list"
    final const val SET_GOALS = "user/goals_list"
    final const val SAVE_INSTALLMENTS = "user/job_post_installments"
    final const val INSTALLMENT_PAYMENT = "user/job_post_installments_payments"
    final const val COMPLAINT_MODULES = "user/complaint_modules_list"
    final const val POST_FEEDBACK = "user/post_feedback"
    final const val POST_COMPLAINT = "user/post_complaints"
    final const val DELETE_ATTACHMENTS = "user/delete_attachment"
    final const val LIKE_POST_DISCUSSION = "user/like_post_discussion"
    final const val OFFERS_LIST = "user/offers_list"
    final const val UPDATE_SINGLE_MOVE_MY_POST_JOB = "user/update_single_move_job_post"
    final const val UPDATE_BLUE_COLLAR_MY_POST_JOB = "user/update_blue_collar_job_post"
    final const val UPDATE_MULTI_MOVE_MY_POST_JOB = "user/update_multi_move_job_post"
    final const val BOOKING_STATUS_OTP = "user/generate_otp"
    final const val VALIDATE_OTP = "user/validate_otp"
    final const val POST_JOB_STATUS = "user/update_post_job_status"
    final const val MY_ACCOUNT_DETAILS = "user/get_account_details"
    final const val RESCHEDULE_BOOKING = "user/reschedule_booking"
    final const val SP_SLOTS = "user/get_sp_slots"
    final const val CANCEL_BOOKING = "user/cancel_booking"
    final const val UPDATE_EXTRA_DEMAND_STATUS = "user/update_extra_demand_status"
    final const val GOALS_INSTALLMENTS_REQUESTS_LIST = "user/get_goals_installments_requested_list"
    final const val POST_APPROVE_REJECT_INSTALLMENTS = "user/job_post_approve_reject_installment"
    final const val GET_BOOKING_STATUS_LIST = "user/get_booking_status_list"
    final const val COMPLAINT_REQUESTS = "user/complaints_requests_list"
    final const val UPDATE_ALERTS_TO_READ = "user/alerts/update"
    final const val FUND_TRANSFER = "user/transfer_funds"
    final const val WITHDRAW_FUNDS = "user/withdraw_funds"
    final const val ADD_BANK_ACCOUNT = "user/add_bank_account"
    final const val ALL_BANK_ACCOUNTS = "user/user_bank_account_details"
    final const val UPDATE_RESCHEDULE_REQUEST_STATUS = "user/update_reschedule_status_by_sp"
    final const val LOGOUT = "user/logout"
    final const val CITIES = "user/get_cities_list"
    final const val AUTOCOMPLETE_BY_CATEGORY = "user/autocomplete_by_category"
    final const val COMPLETE_BOOKING = "user/complete_booking"
    final const val SAVE_USER_UPI = "user/save_user_upi"
    final const val GET_USER_UPI = "user/get_user_upi"
    final const val PAYTM_PROCESS_TXN = "user/process_txn"
    final const val MEMBERSHIP_PAYMENT_TXN = "user/membership_payments_txn_user"
    final const val SEND_FCM = "user/send_fcm"
    final const val ADD_FUNDS = "user/add_funds"

}