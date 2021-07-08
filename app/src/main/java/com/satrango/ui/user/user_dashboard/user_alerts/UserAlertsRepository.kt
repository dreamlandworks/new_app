package com.satrango.ui.user.user_dashboard.user_alerts

import com.bumptech.glide.load.HttpException
import com.google.gson.JsonSyntaxException
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.user_alerts.models.Data
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModel
import okhttp3.ResponseBody
import java.net.SocketTimeoutException

open class UserAlertsRepository: BaseRepository() {

    suspend fun getUserAlerts(alertType: String) : UserAlertsResModel {
        return RetrofitBuilder.getRetrofitInstance().getUserAlerts(UserAlertsReqModel("5", alertType, RetrofitBuilder.KEY, "0"))
    }

}