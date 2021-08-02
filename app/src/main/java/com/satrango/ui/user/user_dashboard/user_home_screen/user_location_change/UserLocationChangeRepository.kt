package com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import okhttp3.ResponseBody

class UserLocationChangeRepository: BaseRepository() {

    suspend fun changeUserLocation(requestBody: UserLocationChangeReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().changeUserLocation(requestBody)
    }

}