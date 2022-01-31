package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models.*
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models.MyAccountDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.models.TransactionHistoryResModel
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

class MyAccountRepository: BaseRepository() {

    suspend fun getTransactionHistory(context: Context): TransactionHistoryResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getTransactionHistory(RetrofitBuilder.USER_KEY, UserUtils.getUserId(context))
    }

    suspend fun getAccountDetails(context: Context): MyAccountDetailsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getMyAccountDetails(RetrofitBuilder.USER_KEY, UserUtils.getUserId(context).toInt())
    }

    suspend fun fundTransfer(requestBody: FundTransferReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().fundTransfer(requestBody)
    }

    suspend fun withdrawFunds(requestBody: WithdrawFundsReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().withdrawFunds(requestBody)
    }

    suspend fun addBankAccount(requestBody: AddBankAccountReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().addBankAccount(requestBody)
    }

    suspend fun allBankDetails(requestBody: AllBankDetailsReqModel): AllBankDetailsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().allBankDetails(requestBody)
    }

}