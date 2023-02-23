package com.satrango.ui.auth.login_screen

import android.util.Log
import com.google.gson.Gson
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.models.UserLoginModel
import okhttp3.ResponseBody

open class LoginRepository : BaseRepository() {

    suspend fun login(requestBody: UserLoginModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().login(requestBody)
    }

    suspend fun logout(requestBody: LogoutReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().logout(requestBody)
    }

}