package com.satrango.ui.auth.user_signup.set_password

import android.util.Log
import com.google.gson.Gson
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.models.UserResetPwdModel
import com.satrango.ui.auth.user_signup.models.UserSignUpModel
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

open class SetPasswordRepository : BaseRepository() {

    suspend fun resetPasswordInServer(): ResponseBody {
        val requestBody = UserResetPwdModel(UserUtils.USER_ID, UserUtils.password, RetrofitBuilder.USER_KEY)
        Log.e("PASSWORD", Gson().toJson(requestBody))
        return RetrofitBuilder.getUserRetrofitInstance().userResetPassword(requestBody)
    }

    suspend fun createNewUser(requestBody: UserSignUpModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().userSignUp(requestBody)
    }

}