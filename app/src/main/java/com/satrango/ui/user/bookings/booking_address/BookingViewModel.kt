package com.satrango.ui.user.bookings.booking_address

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ChangeExtraDemandStatusReqModel
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
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SlotsData
import com.satrango.utils.UserUtils
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class BookingViewModel(val repository: BookingRepository): ViewModel() {

    val singleMoveBooking = MutableLiveData<NetworkResponse<String>>()
    val blueCollarBooking = MutableLiveData<NetworkResponse<String>>()
    val multiMoveBooking = MutableLiveData<NetworkResponse<String>>()
    val addBookingAddress = MutableLiveData<NetworkResponse<String>>()
    val confirmBooking = MutableLiveData<NetworkResponse<String>>()
    val viewBookingDetails = MutableLiveData<NetworkResponse<BookingDetailsResModel>>()
    val providerResponse = MutableLiveData<NetworkResponse<String>>()
    val rescheduleBooking = MutableLiveData<NetworkResponse<RescheduleBookingResModel>>()
    val spSlots = MutableLiveData<NetworkResponse<SlotsData>>()
    val cancelBooking = MutableLiveData<NetworkResponse<String>>()
    val changeExtraDemandStatus = MutableLiveData<NetworkResponse<String>>()
    val getInstallmentsList = MutableLiveData<NetworkResponse<GoalsInstallmentsResModel>>()
    val postApproveReject = MutableLiveData<NetworkResponse<PostApproveRejectResModel>>()

    fun singleMoveBooking(context: Context, requestBody: SingleMoveBookingReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                singleMoveBooking.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.bookSingleMoveServiceProvider(requestBody) }
                    val jsonResponse = JSONObject(response.await().string())
                    Log.e("BOOKING RESPONSE:", jsonResponse.toString())
                    if (jsonResponse.getInt("status") == 200) {
                        UserUtils.saveBookingId(context, jsonResponse.getInt("booking_id").toString())
                        UserUtils.saveBookingRefId(context, jsonResponse.getString("booking_ref_id"))
                        singleMoveBooking.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        singleMoveBooking.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    singleMoveBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            singleMoveBooking.value = NetworkResponse.Failure("No Internet Connection")
        }

        return singleMoveBooking
    }

    fun blueCollarBooking(context: Context, requestBody: BlueCollarBookingReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                blueCollarBooking.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.bookBlueCollarServiceProvider(requestBody) }
                    val jsonResponse = JSONObject(response.await().string())
                    if (jsonResponse.getInt("status") == 200) {
                        UserUtils.saveBookingId(context, jsonResponse.getInt("booking_id").toString())
                        UserUtils.saveBookingRefId(context, jsonResponse.getString("booking_ref_id"))
                        blueCollarBooking.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        blueCollarBooking.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    blueCollarBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            blueCollarBooking.value = NetworkResponse.Failure("No Internet Connection")
        }
        return blueCollarBooking
    }

    fun multiMoveBooking(context: Context, requestBody: MultiMoveReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                multiMoveBooking.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.bookMultiMoveServiceProvider(requestBody) }
                    val jsonResponse = JSONObject(response.await().string())
                    if (jsonResponse.getInt("status") == 200) {
                        UserUtils.saveBookingId(context, jsonResponse.getInt("booking_id").toString())
                        UserUtils.saveBookingRefId(context, jsonResponse.getString("booking_ref_id"))
                        multiMoveBooking.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        multiMoveBooking.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    multiMoveBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            multiMoveBooking.value = NetworkResponse.Failure("No Internet Connection")
        }
        return multiMoveBooking
    }

    fun addBookingAddress(context: Context, requestBody: AddBookingAddressReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                addBookingAddress.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.addBookingAddress(requestBody) }
                    addBookingAddress.value = NetworkResponse.Success(response.await().string())
                } catch (e: Exception) {
                    addBookingAddress.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            addBookingAddress.value = NetworkResponse.Failure("No Internet Connection")
        }
        return addBookingAddress
    }

    fun confirmPayment(context: Context, requestBody: PaymentConfirmReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                confirmBooking.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.confirmBooking(requestBody) }
                    confirmBooking.value = NetworkResponse.Success(response.await().string())
                } catch (e: Exception) {
                    confirmBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            confirmBooking.value = NetworkResponse.Failure("No Internet Connection")
        }
        return confirmBooking
    }

    fun viewBookingDetails(context: Context, requestBody: BookingDetailsReqModel): MutableLiveData<NetworkResponse<BookingDetailsResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                viewBookingDetails.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.viewBookingDetails(requestBody) }
                    viewBookingDetails.value = NetworkResponse.Success(response.await())
                } catch (e: Exception) {
                    viewBookingDetails.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            viewBookingDetails.value = NetworkResponse.Failure("No Internet Connection")
        }
        return viewBookingDetails
    }

    fun setProviderResponse(context: Context, requestBody: ProviderResponseReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                providerResponse.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.setProviderResponse(requestBody) }
                    providerResponse.value = NetworkResponse.Success(response.await().string())
                } catch (e: Exception) {
                    providerResponse.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            providerResponse.value = NetworkResponse.Failure("No Internet Connection")
        }
        return providerResponse
    }

    fun rescheduleBooking(context: Context, requestBody: RescheduleBookingReqModel): MutableLiveData<NetworkResponse<RescheduleBookingResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                rescheduleBooking.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.reschedule(requestBody) }
                    rescheduleBooking.value = NetworkResponse.Success(response.await())
                } catch (e: Exception) {
                    rescheduleBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            rescheduleBooking.value = NetworkResponse.Failure("No Internet Connection")
        }
        return rescheduleBooking
    }

    fun spSlots(context: Context, spId: Int): MutableLiveData<NetworkResponse<SlotsData>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                spSlots.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.getSpSlots(spId) }
                    val jsonObject = JSONObject(JSONObject(response.await().string()).getJSONObject("slots_data").toString())
                    jsonObject.put("user_id", UserUtils.getUserId(context))
                    spSlots.value = NetworkResponse.Success(Gson().fromJson(jsonObject.toString(), SlotsData::class.java))
                } catch (e: Exception) {
                    spSlots.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            spSlots.value = NetworkResponse.Failure("No Internet Connection")
        }
        return spSlots
    }

    fun cancelBooking(context: Context, requestBody: UserBookingCancelReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                cancelBooking.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.cancelBooking(requestBody) }
                    val jsonObject = JSONObject(response.await().string())
                    if (jsonObject.getInt("status") == 200) {
                        cancelBooking.value = NetworkResponse.Success(jsonObject.getString("message"))
                    } else {
                        cancelBooking.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                } catch (e: Exception) {
                    cancelBooking.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            cancelBooking.value = NetworkResponse.Failure("No Internet Connection")
        }
        return cancelBooking
    }

    fun changeExtraDemandStatus(context: Context, requestBody: ChangeExtraDemandStatusReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                changeExtraDemandStatus.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.changeExtraDemandStatus(requestBody) }
                    val jsonObject = JSONObject(response.await().string())
                    if (jsonObject.getInt("status") == 200) {
                        changeExtraDemandStatus.value = NetworkResponse.Success(jsonObject.getString("message"))
                    } else {
                        changeExtraDemandStatus.value = NetworkResponse.Failure(jsonObject.getString("message"))
                    }
                } catch (e: Exception) {
                    changeExtraDemandStatus.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            changeExtraDemandStatus.value = NetworkResponse.Failure("No Internet Connection")
        }
        return changeExtraDemandStatus
    }

    fun getInstallmentsList(context: Context, postJobId: Int): MutableLiveData<NetworkResponse<GoalsInstallmentsResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                getInstallmentsList.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.getInstallments(postJobId) }
                    if (response.await().status == 200) {
                        getInstallmentsList.value = NetworkResponse.Success(response.await())
                    } else {
                        getInstallmentsList.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    getInstallmentsList.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            getInstallmentsList.value = NetworkResponse.Failure("No Internet Connection")
        }
        return getInstallmentsList
    }

    fun postApproveReject(context: Context, requestBody: PostApproveRejectReqModel): MutableLiveData<NetworkResponse<PostApproveRejectResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                postApproveReject.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.postInstallmentApproveReject(requestBody) }
                    if (response.await().status == 200) {
                        postApproveReject.value = NetworkResponse.Success(response.await())
                    } else {
                        postApproveReject.value = NetworkResponse.Failure(response.await().message)
                    }
                } catch (e: Exception) {
                    postApproveReject.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            postApproveReject.value = NetworkResponse.Failure("No Internet Connection")
        }
        return postApproveReject
    }

}