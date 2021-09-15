package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileResModel
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

class ProviderDashboardRepository: BaseRepository() {

    suspend fun userProfile(context: Context): UserProfileResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getUserProfile(BrowseCategoryReqModel(UserUtils.getUserId(context), RetrofitBuilder.USER_KEY))
    }

    suspend fun uploadUserLocation(requestBody: ProviderLocationReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().saveProviderLocation(requestBody)
    }

}