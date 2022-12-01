package com.satrango.ui.service_provider.provider_dashboard.plans

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMemberShipPlanPaymentReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMemberShipPlanPaymentResModel
import com.satrango.ui.service_provider.provider_dashboard.plans.models.ProviderPlansResModel
import com.satrango.utils.UserUtils
import okhttp3.RequestBody

class ProviderPlansRepository: BaseRepository() {

    suspend fun getPlans(context: Context): ProviderPlansResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().getPlans(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(context).toInt())
    }

    suspend fun saveMemberShip(requestBody: ProviderMemberShipPlanPaymentReqModel): ProviderMemberShipPlanPaymentResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().saveMemberShipPlan(requestBody)
    }

}