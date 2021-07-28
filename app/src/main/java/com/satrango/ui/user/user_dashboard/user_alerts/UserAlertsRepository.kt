package com.satrango.ui.user.user_dashboard.user_alerts

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModel

open class UserAlertsRepository: BaseRepository() {

    suspend fun getUserAlerts(alertType: String) : UserAlertsResModel {
        return RetrofitBuilder.getUserRetrofitInstance()
            .getUserAlerts(UserAlertsReqModel("5", alertType, RetrofitBuilder.USER_KEY, "0"))
    }

}