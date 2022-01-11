package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderGoalsInstallmentsListResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderPostRequestInstallmentReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderPostRequestInstallmentResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review.ProviderRatingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review.UserRatingReqModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class ProviderBookingViewModel(private val repository: ProviderBookingRepository): ViewModel() {

    val bookingListWithDetails = MutableLiveData<NetworkResponse<List<BookingDetail>>>()
    val extraDemand = MutableLiveData<NetworkResponse<String>>()
    val expenditureIncurred = MutableLiveData<NetworkResponse<String>>()
    val userRating = MutableLiveData<NetworkResponse<String>>()
    val providerRating = MutableLiveData<NetworkResponse<String>>()
    val invoice = MutableLiveData<NetworkResponse<ProviderInvoiceResModel>>()
    val installmentsList = MutableLiveData<NetworkResponse<ProviderGoalsInstallmentsListResModel>>()
    val postRequestInstallment = MutableLiveData<NetworkResponse<ProviderPostRequestInstallmentResModel>>()

    fun bookingListWithDetails(context: Context, requestBody: ProviderBookingReqModel): MutableLiveData<NetworkResponse<List<BookingDetail>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    bookingListWithDetails.value = NetworkResponse.Loading()
                    val request = async { repository.getBookingListWithDetails(requestBody) }
                    val response = request.await()
                    if (response.status == 200) {
                        bookingListWithDetails.value = NetworkResponse.Success(response.booking_details)
                    } else {
                        bookingListWithDetails.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    bookingListWithDetails.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            bookingListWithDetails.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return bookingListWithDetails
    }

    fun extraDemand(context: Context, requestBody: ExtraDemandReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    extraDemand.value = NetworkResponse.Loading()
                    val request = async { repository.postExtraDemand(requestBody) }
                    val response = request.await()
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") == 200) {
                        extraDemand.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        extraDemand.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    extraDemand.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            extraDemand.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return extraDemand
    }

    fun expenditureIncurred(context: Context, requestBody: ExpenditureIncurredReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    expenditureIncurred.value = NetworkResponse.Loading()
                    val request = async { repository.expenditureIncurred(requestBody) }
                    val response = request.await()
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") == 200) {
                        expenditureIncurred.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        expenditureIncurred.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    expenditureIncurred.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            expenditureIncurred.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return expenditureIncurred
    }

    fun userRating(context: Context, requestBody: UserRatingReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    userRating.value = NetworkResponse.Loading()
                    val request = async { repository.userReview(requestBody) }
                    val response = request.await()
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") == 200) {
                        userRating.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        userRating.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    userRating.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            userRating.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return userRating
    }

    fun providerRating(context: Context, requestBody: ProviderRatingReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    userRating.value = NetworkResponse.Loading()
                    val request = async { repository.providerReview(requestBody) }
                    val response = request.await()
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") == 200) {
                        userRating.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        userRating.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    userRating.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            userRating.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return userRating
    }

    fun getInvoice(context: Context, requestBody: ProviderInvoiceReqModel): MutableLiveData<NetworkResponse<ProviderInvoiceResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    invoice.value = NetworkResponse.Loading()
                    val request = async { repository.getInvoice(requestBody) }
                    val response = request.await()
                    if (response.status == 200) {
                        invoice.value = NetworkResponse.Success(response)
                    } else {
                        invoice.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    invoice.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            invoice.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return invoice
    }

    fun getInstallmentsList(context: Context, postJobId: Int): MutableLiveData<NetworkResponse<ProviderGoalsInstallmentsListResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    installmentsList.value = NetworkResponse.Loading()
                    val request = async { repository.getGoalsInstallmentsList(postJobId) }
                    val response = request.await()
                    if (response.status == 200) {
                        installmentsList.value = NetworkResponse.Success(response)
                    } else {
                        installmentsList.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    installmentsList.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            installmentsList.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return installmentsList
    }

    fun postRequestInstallment(context: Context, requestBody: ProviderPostRequestInstallmentReqModel): MutableLiveData<NetworkResponse<ProviderPostRequestInstallmentResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    postRequestInstallment.value = NetworkResponse.Loading()
                    val request = async { repository.postRequestInstallment(requestBody) }
                    val response = request.await()
                    if (response.status == 200) {
                        postRequestInstallment.value = NetworkResponse.Success(response)
                    } else {
                        postRequestInstallment.value = NetworkResponse.Failure(response.message)
                    }
                } catch (e: Exception) {
                    postRequestInstallment.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            postRequestInstallment.value = NetworkResponse.Failure("No Internet Connection")
//        }
        return postRequestInstallment
    }

}