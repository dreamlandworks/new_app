package com.satrango.ui.service_provider.provider_dashboard.alerts

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.alerts.models.ProviderAlertsReqModel
import com.satrango.ui.service_provider.provider_dashboard.alerts.models.UpdateAlertsToReadReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModelX
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

class ProviderAlertRepository: BaseRepository() {

    suspend fun getProviderAlerts(context: Context) : UserAlertsResModelX {
        val requestBody = ProviderAlertsReqModel(RetrofitBuilder.PROVIDER_KEY,UserUtils.getUserId(context))
        return RetrofitBuilder.getServiceProviderRetrofitInstance().getProviderAlerts(requestBody)
    }

    suspend fun updateAlertToRead(context: Context, alertType: String) : ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance()
            .updateAlertsToRead(UpdateAlertsToReadReqModel(UserUtils.getUserId(context), RetrofitBuilder.PROVIDER_KEY, "2", alertType))
    }

}