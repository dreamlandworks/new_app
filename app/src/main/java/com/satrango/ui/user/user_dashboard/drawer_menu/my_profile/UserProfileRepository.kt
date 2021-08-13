package com.satrango.ui.user.user_dashboard.drawer_menu.my_profile

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileUpdateReqModel
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

open class UserProfileRepository : BaseRepository() {

    suspend fun userProfileInfo(userId: String): UserProfileResModel {
        val requestBody = BrowseCategoryReqModel(userId, RetrofitBuilder.USER_KEY)
        return RetrofitBuilder.getUserRetrofitInstance().getUserProfile(requestBody)
    }

    suspend fun updateProfileInfo(requestBody: UserProfileUpdateReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().updateUserProfile(requestBody)
    }

    suspend fun deleteUserAddress(requestBody: BrowseCategoryReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().deleteUserAddress(requestBody)
    }

}