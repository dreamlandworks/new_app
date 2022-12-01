package com.satrango.ui.user.user_dashboard.user_home_screen

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import okhttp3.ResponseBody

class UserHomeRepository : BaseRepository() {

    suspend fun getPopularServices(categoryId: String): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance()
            .userBrowseSubCategories(BrowseCategoryReqModel(categoryId, RetrofitBuilder.USER_KEY))
    }

    suspend fun getBrowseCategories(): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().userBrowseCategories(RetrofitBuilder.USER_KEY)
    }

}