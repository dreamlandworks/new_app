package com.satrango.ui.user.user_dashboard.drawer_menu.settings

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models.ComplaintModuleResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models.ComplaintReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models.ComplaintResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models.FeedbackReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models.FeedbackResModel

class SettingsRepository: BaseRepository() {

    suspend fun getComplaintModules(): ComplaintModuleResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getComplaintModules(RetrofitBuilder.USER_KEY)
    }

    suspend fun postComplaint(requestBody: ComplaintReqModel): ComplaintResModel {
        return RetrofitBuilder.getUserRetrofitInstance().postComplaint(requestBody)
    }

    suspend fun postFeedback(requestBody: FeedbackReqModel): FeedbackResModel {
        return RetrofitBuilder.getUserRetrofitInstance().postFeedBack(requestBody)
    }


}