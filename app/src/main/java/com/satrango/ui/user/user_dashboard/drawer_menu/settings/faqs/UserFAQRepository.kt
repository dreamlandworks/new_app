package com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.UserSettingsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.models.UserFAQResModel

class UserFAQRepository: BaseRepository() {

    suspend fun getFAQs(): UserFAQResModel {
        return if (UserSettingsScreen.FROM_PROVIDER) {
            RetrofitBuilder.getServiceProviderRetrofitInstance().providerFAQs(RetrofitBuilder.PROVIDER_KEY)
        } else {
            RetrofitBuilder.getUserRetrofitInstance().userFAQs(RetrofitBuilder.USER_KEY)
        }
    }

}