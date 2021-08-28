package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models.TransactionHistoryResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MyAccountViewModel(private val repository: MyAccountRepository) : ViewModel() {

    val transactionHistory = MutableLiveData<NetworkResponse<TransactionHistoryResModel>>()

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

}