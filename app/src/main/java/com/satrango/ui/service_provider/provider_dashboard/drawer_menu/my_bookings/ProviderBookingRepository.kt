package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderGoalsInstallmentsListResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderPostRequestInstallmentReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderPostRequestInstallmentResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review.ProviderRatingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review.UserRatingReqModel
import okhttp3.ResponseBody

class ProviderBookingRepository: BaseRepository() {

    suspend fun getBookingListWithDetails(requestBody: ProviderBookingReqModel): ProviderBookingResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().bookingListWithDetails(requestBody)
    }

    suspend fun postExtraDemand(requestBody: ExtraDemandReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().postExtraDemand(requestBody)
    }

    suspend fun expenditureIncurred(requestBody: ExpenditureIncurredReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().expenditureIncurred(requestBody)
    }

    suspend fun userReview(requestBody: UserRatingReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().userReview(requestBody)
    }

    suspend fun providerReview(requestBody: ProviderRatingReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().providerReview(requestBody)
    }

    suspend fun getInvoice(requestBody: ProviderInvoiceReqModel): ProviderInvoiceResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().getInvoice(requestBody)
    }

    suspend fun getGoalsInstallmentsList(postJobId: Int): ProviderGoalsInstallmentsListResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().getGoalsInstallmentsList(RetrofitBuilder.PROVIDER_KEY, postJobId)
    }

    suspend fun postRequestInstallment(requestBody: ProviderPostRequestInstallmentReqModel): ProviderPostRequestInstallmentResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().postRequestInstallment(requestBody)
    }

}