package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.*
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models.AddBankAccountReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models.AllBankDetailsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models.FundTransferReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models.WithdrawFundsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models.MyAccountDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.models.TransactionHistoryResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class MyAccountViewModel(private val repository: MyAccountRepository) : ViewModel() {

    val transactionHistory = MutableLiveData<NetworkResponse<TransactionHistoryResModel>>()
    val myAccountDetails = MutableLiveData<NetworkResponse<MyAccountDetailsResModel>>()
    val fundTransfer = MutableLiveData<NetworkResponse<String>>()
    val withdrawfunds = MutableLiveData<NetworkResponse<String>>()
    val addBankAccount = MutableLiveData<NetworkResponse<String>>()
    val allBankAccounts = MutableLiveData<NetworkResponse<List<UserBankAccount>>>()

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
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    myAccountDetails.value = NetworkResponse.Loading()
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
//        }
        return myAccountDetails
    }

    fun fundTransfer(context: Context, requestBody: FundTransferReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                fundTransfer.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.fundTransfer(requestBody) }
                    val jsonResponse = response.await()
                    val json = JSONObject(jsonResponse.string())
                    if (json.getInt("status") == 200) {
                        fundTransfer.value = NetworkResponse.Success(json.getString("message"))
                    } else {
                        fundTransfer.value = NetworkResponse.Failure(json.getString("message"))
                    }
                } catch (e: Exception) {
                    fundTransfer.value = NetworkResponse.Failure(e.message!!)
                }
            }
        }
        return fundTransfer
    }

    fun withDrawFunds(context: Context, requestBody: WithdrawFundsReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                withdrawfunds.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.withdrawFunds(requestBody) }
                    val jsonResponse = response.await()
                    val json = JSONObject(jsonResponse.string())
                    if (json.getInt("status") == 200) {
                        withdrawfunds.value = NetworkResponse.Success(json.getString("message"))
                    } else {
                        withdrawfunds.value = NetworkResponse.Failure(json.getString("message"))
                    }
                } catch (e: Exception) {
                    withdrawfunds.value = NetworkResponse.Failure(e.message!!)
                }
            }
        }
        return withdrawfunds
    }

    fun addBankAccount(context: Context, requestBody: AddBankAccountReqModel): MutableLiveData<NetworkResponse<String>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                addBankAccount.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.addBankAccount(requestBody) }
                    val jsonResponse = response.await()
                    val json = JSONObject(jsonResponse.string())
                    if (json.getInt("status") == 200) {
                        addBankAccount.value = NetworkResponse.Success(json.getString("message"))
                    } else {
                        addBankAccount.value = NetworkResponse.Failure(json.getString("message"))
                    }
                } catch (e: Exception) {
                    addBankAccount.value = NetworkResponse.Failure(e.message!!)
                }
            }
        }
        return addBankAccount
    }

    fun allBankDetails(context: Context, requestBody: AllBankDetailsReqModel): MutableLiveData<NetworkResponse<List<UserBankAccount>>> {
        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                allBankAccounts.value = NetworkResponse.Loading()
                try {
                    val response = async { repository.allBankDetails(requestBody) }
                    val jsonResponse = response.await()
                    if (jsonResponse.status == 200) {
                        allBankAccounts.value = NetworkResponse.Success(jsonResponse.user_bank_accounts)
                    } else {
                        allBankAccounts.value = NetworkResponse.Failure(jsonResponse.message)
                    }
                } catch (e: Exception) {
                    allBankAccounts.value = NetworkResponse.Failure(e.message!!)
                }
            }
        }
        return allBankAccounts
    }

}