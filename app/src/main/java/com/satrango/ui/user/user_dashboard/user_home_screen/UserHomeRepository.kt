package com.satrango.ui.user.user_dashboard.user_home_screen

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.UserKeywordsResModel
import okhttp3.ResponseBody

class UserHomeRepository : BaseRepository() {

    suspend fun getPopularServices(): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance()
            .userBrowseSubCategories(BrowseCategoryReqModel("1", RetrofitBuilder.USER_KEY))
    }

    suspend fun getBrowseCategories(): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().userBrowseCategories(RetrofitBuilder.USER_KEY)
    }

    suspend fun getKeyWords(): UserKeywordsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getUserSearchKeywords(RetrofitBuilder.USER_KEY)
    }

}