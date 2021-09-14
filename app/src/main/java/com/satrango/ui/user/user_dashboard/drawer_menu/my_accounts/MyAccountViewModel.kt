package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models.MyAccountDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.models.TransactionHistoryResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MyAccountViewModel(private val repository: MyAccountRepository) : ViewModel() {

    val transactionHistory = MutableLiveData<NetworkResponse<TransactionHistoryResModel>>()
    val myAccountDetails = MutableLiveData<NetworkResponse<MyAccountDetailsResModel>>()

    fun transactionHistory(context: Context): MutableLiveData<NetworkResponse<TransactionHistoryResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                transactionHistory.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.getTransactionHistory(context) }
                    transactionHistory.value = NetworkResponse.Success(response.await())
                } catch (e: Exception) {
                    transactionHistory.value = NetworkResponse.Failure(e.message!!)
                }
            }
        }
        return transactionHistory
    }

    fun myAccountDetails(context: Context): MutableLiveData<NetworkResponse<MyAccountDetailsResModel>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                myAccountDetails.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.getAccountDetails(context) }
                    val jsonResponse = response.await()
                    if (jsonResponse.status == 200) {
                        myAccountDetails.value = NetworkResponse.Success(jsonResponse)
                    } else {
                        myAccountDetails.value = NetworkResponse.Failure(jsonResponse.message)
                    }
                } catch (e: Exception) {
                    myAccountDetails.value = NetworkResponse.Failure(e.message!!)
                }
            }
        }
        return myAccountDetails
    }

}