package com.satrango.ui.user_dashboard.drawer_menu.browse_categories

import com.google.gson.JsonSyntaxException
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException

class BrowseCategoriesRepository : BaseRepository() {

    suspend fun getBrowseCategories(): ResponseBody {
        return RetrofitBuilder.getRetrofitInstance().userBrowseCategories(RetrofitBuilder.KEY)
    }

    suspend fun getBrowseSubCategories(categoryId: String): ResponseBody {
        return RetrofitBuilder.getRetrofitInstance().userBrowseSubCategories(BrowseCategoryReqModel(categoryId, RetrofitBuilder.KEY))
    }

}