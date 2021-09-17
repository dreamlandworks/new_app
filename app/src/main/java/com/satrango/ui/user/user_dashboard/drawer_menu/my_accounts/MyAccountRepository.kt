package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models.MyAccountDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.models.TransactionHistoryResModel
import com.satrango.utils.UserUtils

class MyAccountRepository: BaseRepository() {

    suspend fun getTransactionHistory(context: Context): TransactionHistoryResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getTransactionHistory(RetrofitBuilder.USER_KEY, UserUtils.getUserId(context))
    }

    suspend fun getAccountDetails(context: Context): MyAccountDetailsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getMyAccountDetails(RetrofitBuilder.USER_KEY, UserUtils.getUserId(context).toInt())
    }

}