package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.api_services.ProviderApiService
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.ProviderMyTrainingResModel
import com.satrango.utils.UserUtils

class ProviderMyTrainingRepository : BaseRepository() {

    suspend fun getTrainingVideos(context: Context, subCategoryId: String): ProviderMyTrainingResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().providerTrainingList(RetrofitBuilder.PROVIDER_KEY, subCategoryId, UserUtils.getUserId(context))
    }

}