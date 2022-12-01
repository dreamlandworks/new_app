package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.api_services.ProviderApiService
import com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.models.CitiesResModel
import com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.models.LeaderboardResModel
import com.satrango.ui.service_provider.provider_dashboard.dashboard.models.ProviderDashboardResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.ProviderMyTrainingResModel
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

class ProviderMyTrainingRepository : BaseRepository() {

    suspend fun getTrainingVideos(context: Context): ProviderMyTrainingResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().providerTrainingList(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(context))
    }

    suspend fun submitYoutubePoints(context: Context, videoId: String, points: String): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().submitYoutubePoints(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(context), videoId, points)
    }

    suspend fun getLeaderboardList(context: Context, cityId: String): LeaderboardResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().getLeaderBoardList(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(context), cityId)
    }

    suspend fun getCities(userId: String): CitiesResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getCities(RetrofitBuilder.USER_KEY, userId)
    }

    suspend fun getProviderDashboardDetails(context: Context, cityId: String): ProviderDashboardResModel {
//        Log.e("SPDASHBOARD:", UserUtils.getUserId(context) + "|" + cityId)
        return RetrofitBuilder.getServiceProviderRetrofitInstance().getDashboardDetails(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(context).toInt(), cityId.toInt())
    }

}