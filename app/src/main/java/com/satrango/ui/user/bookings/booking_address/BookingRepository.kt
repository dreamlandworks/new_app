package com.satrango.ui.user.bookings.booking_address

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ChangeExtraDemandStatusReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.GetBookingStatusListResModel
import com.satrango.ui.user.bookings.provider_response.PaymentConfirmReqModel
import com.satrango.ui.user.bookings.booking_address.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.booking_address.models.SingleMoveBookingReqModel
import com.satrango.ui.user.bookings.booking_attachments.models.MultiMoveReqModel
import com.satrango.ui.user.bookings.cancel_booking.models.UserBookingCancelReqModel
import com.satrango.ui.user.bookings.change_address.AddBookingAddressReqModel
import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.GoalsInstallmentsResModel
import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.PostApproveRejectReqModel
import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.PostApproveRejectResModel
import com.satrango.ui.user.bookings.view_booking_details.models.*
import okhttp3.ResponseBody

class BookingRepository: BaseRepository() {

    suspend fun bookSingleMoveServiceProvider(requestBody: SingleMoveBookingReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().bookSingleMoveProvider(requestBody)
    }

    suspend fun bookBlueCollarServiceProvider(requestBody: BlueCollarBookingReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().bookBlueCollarProvider(requestBody)
    }

    suspend fun bookMultiMoveServiceProvider(requestBody: MultiMoveReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().bookMultiMoveProvider(requestBody)
    }

    suspend fun addBookingAddress(requestBody: AddBookingAddressReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().addBookingAddress(requestBody)
    }

    suspend fun confirmBooking(requestBody: PaymentConfirmReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().confirmPayment(requestBody)
    }

    suspend fun viewBookingDetails(requestBody: BookingDetailsReqModel): BookingDetailsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(requestBody)
    }

    suspend fun setProviderResponse(requestBody: ProviderResponseReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().setProviderResponse(requestBody)
    }

    suspend fun reschedule(requestBody: RescheduleBookingReqModel): RescheduleBookingResModel {
        return RetrofitBuilder.getUserRetrofitInstance().rescheduleBooking(requestBody)
    }

    suspend fun getSpSlots(spId: Int): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().getSpSlots(RetrofitBuilder.USER_KEY, spId)
    }

    suspend fun cancelBooking(requestBody: UserBookingCancelReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().cancelBooking(requestBody)
    }

    suspend fun changeExtraDemandStatus(requestBody: ChangeExtraDemandStatusReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().changeExtraDemandStatus(requestBody)
    }

    suspend fun getInstallments(postJobId: Int): GoalsInstallmentsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getInstallmentsRequestList(RetrofitBuilder.USER_KEY, postJobId)
    }

    suspend fun postInstallmentApproveReject(requestBody: PostApproveRejectReqModel): PostApproveRejectResModel {
        return RetrofitBuilder.getUserRetrofitInstance().postInstallmentApproveReject(requestBody)
    }

    suspend fun getBookingStatusList(bookingId: Int): GetBookingStatusListResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getBookingStatusList(RetrofitBuilder.USER_KEY, bookingId)
    }

}