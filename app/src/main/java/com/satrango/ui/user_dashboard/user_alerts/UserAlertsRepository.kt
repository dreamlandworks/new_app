package com.satrango.ui.user_dashboard.user_alerts

import com.bumptech.glide.load.HttpException
import com.google.gson.JsonSyntaxException
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user_dashboard.user_alerts.models.Data
import com.satrango.ui.user_dashboard.user_alerts.models.UserAlertsReqModel
import java.net.SocketTimeoutException

open class UserAlertsRepository: BaseRepository() {

    suspend fun getUserAlerts(alertType: String) : List<Data> {
        val response = RetrofitBuilder.getRetrofitInstance().getUserAlerts(UserAlertsReqModel("5", alertType))
        try {
            if (response.status == 200) {
                return response.data
            }
        } catch (e: HttpException) {

        } catch (e: JsonSyntaxException) {

        } catch (e: SocketTimeoutException) {

        }
        return emptyList<Data>()
    }

}