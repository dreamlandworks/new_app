package com.satrango.ui.auth.provider_signup.provider_sign_up_two

import android.util.Log
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.provider_signup.provider_sign_up_two.models.ProviderSignUpTwoKeywordsResModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.UserKeywordsResModel
import com.satrango.utils.ProviderUtils

class ProviderSignUpTwoRepository: BaseRepository() {

    suspend fun getKeyWords(subCatId: String): ProviderSignUpTwoKeywordsResModel {
        val map = mutableMapOf<String, String>()
        map["key"] = RetrofitBuilder.USER_KEY
        map["subcat_id"] = subCatId
        Log.e("SUBCAT:", subCatId)
        return RetrofitBuilder.getUserRetrofitInstance().userKeywords(map)
    }

}