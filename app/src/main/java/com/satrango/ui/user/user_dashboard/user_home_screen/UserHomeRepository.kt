package com.satrango.ui.user.user_dashboard.user_home_screen

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.UserKeywordsResModel
import okhttp3.ResponseBody

class UserHomeRepository : BaseRepository() {

    suspend fun getPopularServices(): ResponseBody {
        return RetrofitBuilder.getRetrofitInstance().userBrowseSubCategories(BrowseCategoryReqModel("1", RetrofitBuilder.KEY))
    }

    suspend fun getBrowseCategories(): ResponseBody {
        return RetrofitBuilder.getRetrofitInstance().userBrowseCategories(RetrofitBuilder.KEY)
    }

    suspend fun getKeyWords(): UserKeywordsResModel {
        return RetrofitBuilder.getRetrofitInstance().getUserSearchKeywords(RetrofitBuilder.KEY)
    }

}