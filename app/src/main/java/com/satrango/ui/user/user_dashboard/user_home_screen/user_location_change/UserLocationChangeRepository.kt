package com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models.AllLocationsResModel

class UserLocationChangeRepository: BaseRepository() {

    suspend fun getAllLocation(): AllLocationsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getAllLocations(RetrofitBuilder.USER_KEY)
    }

}