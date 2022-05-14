package com.satrango.ui.service_provider.provider_dashboard.dashboard

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.ProviderLocationReqModel
import com.satrango.ui.service_provider.provider_dashboard.models.ProviderOnlineReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileResModel
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

class ProviderDashboardRepository: BaseRepository() {

    suspend fun userProfile(context: Context, requestBody: UserProfileReqModel): UserProfileResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getUserProfile(requestBody)
    }

    suspend fun uploadUserLocation(requestBody: ProviderLocationReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().saveProviderLocation(requestBody)
    }

    suspend fun updateProviderOnlineStatus(requestBody: ProviderOnlineReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().updateSpOnlineStatus(requestBody)
    }

}