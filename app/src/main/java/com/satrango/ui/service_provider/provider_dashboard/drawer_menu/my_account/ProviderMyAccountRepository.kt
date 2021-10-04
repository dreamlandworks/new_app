package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMyAccountResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews.models.ProviderReviewResModel
import com.satrango.utils.UserUtils

class ProviderMyAccountRepository: BaseRepository() {

    suspend fun myAccount(context: Context): ProviderMyAccountResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().myAccount(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(context).toInt())
    }

    suspend fun reviews(context: Context): ProviderReviewResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().reviews(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(context).toInt())
    }

}