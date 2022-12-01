package com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.UserSettingsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.models.UserFAQResModel
import com.satrango.utils.UserUtils.isProvider

class UserFAQRepository: BaseRepository() {

    suspend fun getFAQs(context: Context): UserFAQResModel {
        return if (isProvider(context)) {
            RetrofitBuilder.getServiceProviderRetrofitInstance().providerFAQs(RetrofitBuilder.PROVIDER_KEY)
        } else {
            RetrofitBuilder.getUserRetrofitInstance().userFAQs(RetrofitBuilder.USER_KEY)
        }
    }

}