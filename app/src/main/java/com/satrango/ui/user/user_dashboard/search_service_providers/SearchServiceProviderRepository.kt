package com.satrango.ui.user.user_dashboard.search_service_providers

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.UserKeywordsResModel

class SearchServiceProviderRepository: BaseRepository() {

    suspend fun getKeyWords(): UserKeywordsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getUserSearchKeywords(RetrofitBuilder.USER_KEY)
    }

    suspend fun getSearchResults(requestBody: SearchServiceProviderReqModel): SearchServiceProviderResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getUserSearchResults(requestBody)
    }

}