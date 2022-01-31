package com.satrango.ui.auth.provider_signup.provider_sign_up_one

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder

class ProviderSignUpOneRepository: BaseRepository() {

    suspend fun providerData(): com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().providerList(RetrofitBuilder.PROVIDER_KEY)
    }

}