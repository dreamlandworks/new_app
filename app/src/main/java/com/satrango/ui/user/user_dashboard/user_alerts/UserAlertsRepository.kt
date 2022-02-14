package com.satrango.ui.user.user_dashboard.user_alerts

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModelX
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserUpdateAlertsToReadReqModel
import com.satrango.ui.user.user_dashboard.user_offers.models.OffersListReqModel
import com.satrango.ui.user.user_dashboard.user_offers.models.OffersListResModel
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

open class UserAlertsRepository: BaseRepository() {

    suspend fun getUserAlerts(context: Context) : UserAlertsResModelX {
        return RetrofitBuilder.getUserRetrofitInstance().getUserAlerts(UserAlertsReqModel(UserUtils.getUserId(context), RetrofitBuilder.USER_KEY))
    }

    suspend fun updateAlertsToRead(context: Context, type: String) : ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance()
            .updateAlertToRead(UserUpdateAlertsToReadReqModel(UserUtils.getUserId(context), RetrofitBuilder.USER_KEY, type))
    }

    suspend fun getUserOffers(requestBody: OffersListReqModel) : OffersListResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getOffersList(requestBody)
    }

}