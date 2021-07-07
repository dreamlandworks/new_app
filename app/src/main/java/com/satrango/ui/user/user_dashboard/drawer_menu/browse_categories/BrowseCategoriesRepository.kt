package com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import okhttp3.ResponseBody

class BrowseCategoriesRepository : BaseRepository() {

    suspend fun getBrowseCategories(): ResponseBody {
        return RetrofitBuilder.getRetrofitInstance().userBrowseCategories(RetrofitBuilder.KEY)
    }

    suspend fun getBrowseSubCategories(categoryId: String): ResponseBody {
        return RetrofitBuilder.getRetrofitInstance().userBrowseSubCategories(BrowseCategoryReqModel(categoryId, RetrofitBuilder.KEY))
    }

}