package com.satrango.ui.auth.provider_signup.provider_sign_up_four

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProviderIdProofReqModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProviderSignUpFourReqModel
import okhttp3.ResponseBody

class ProviderSignUpFourRepository : BaseRepository() {

    suspend fun serviceProviderActivation(requestBody: ProviderSignUpFourReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance()
            .providerActivationConfirmation(requestBody)
    }

    suspend fun uploadIdProof(requestBody: ProviderIdProofReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance()
            .uploadIdProof(requestBody)
    }

}