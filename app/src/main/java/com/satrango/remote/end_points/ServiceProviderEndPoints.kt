package com.satrango.remote.end_points

object ServiceProviderEndPoints {

    final const val PROFESSIONS_LIST = "provider/get_initialization_list"
    final const val ACTIVATION_CONFIRMATION = "provider/confirm_activation"
    final const val VIDEO_VERIFICATION = "provider/video_verification"
    final const val PROVIDER_LOCATION = "provider/update_location"
    final const val BOOKING_LIST_WITH_DETAILS = "provider/get_sp_booking_details"
    final const val EXTRA_DEMAND = "provider/post_sp_extra_demand"
    final const val EXPENDITURE_INCURRED = "provider/update_final_expenditure"
    final const val USER_REVIEW = "provider/post_user_review"
    final const val INVOICE = "provider/get_booking_work_summary"
    final const val GOALS_INSTALLMENTS_LIST = "provider/get_goals_installments_list"
    final const val POST_REQUEST_INSTALLMENT = "provider/job_post_request_installment"
    final const val RESUME_BOOKING = "provider/resume_booking"
    final const val PAUSE_BOOKING = "provider/pause_booking"
    final const val PROVIDER_JOBS_LIST = "provider/sp_job_post_bids_list"
    final const val PROVIDER_JOB_POSTS_LIST = "provider/sp_job_post_list"
    final const val POST_BID = "provider/sp_post_bid"
    final const val DELETE_BID_ATTACHMENT = "provider/delete_bid_attachment"
    final const val EDIT_BID = "provider/sp_edit_bid"
    final const val SP_ALERTS = "provider/get_sp_alerts"
    final const val PROFESSIONAL_DETAILS = "provider/get_sp_professional_details"
    final const val UPDATE_SKILLS = "provider/update_sp_prof_details"
    final const val UPDATE_TARIFF = "provider/update_sp_tariff_time_slot"

}