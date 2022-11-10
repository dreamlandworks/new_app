package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.ProviderProfileProfessionResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_skills.UpdateSkillsReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_tariff.UpdateTariffReqModel
import okhttp3.ResponseBody

class ProviderProfileRepository: BaseRepository() {

    suspend fun getProfessionalDetails(requestBody: ProviderBookingReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().getProfessionalDetails(requestBody)
    }

    suspend fun updateTariff(requestBody: UpdateTariffReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().updateTariff(requestBody)
    }

    suspend fun updateSkills(requestBody: UpdateSkillsReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().updateSkills(requestBody)
    }

}