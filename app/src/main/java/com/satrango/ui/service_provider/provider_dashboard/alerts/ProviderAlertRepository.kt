package com.satrango.ui.service_provider.provider_dashboard.alerts

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.alerts.models.ProviderAlertsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModel

class ProviderAlertRepository: BaseRepository() {

    suspend fun getProviderAlerts(alertType: String) : UserAlertsResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance()
            .getProviderAlerts(ProviderAlertsReqModel(RetrofitBuilder.PROVIDER_KEY,"12",  "0", alertType))
    }

}