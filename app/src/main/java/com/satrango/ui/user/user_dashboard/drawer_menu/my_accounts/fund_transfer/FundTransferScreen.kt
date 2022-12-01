package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityFundTransferScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.ProviderMyAccountScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.AddFundsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.MyAccountRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.MyAccountViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.UserMyAccountScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models.AllBankDetailsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models.FundTransferReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models.WithdrawFundsReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FundTransferScreen : AppCompatActivity(),
//    PaymentResultListener,
    AllBankDetailsInterface {

    private lateinit var allBankDetails: List<UserBankAccount>
    private var withdrawAmountInDouble = 0.0
    private lateinit var viewModel: MyAccountViewModel
    private val activityRequestCode: Int = 1
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityFundTransferScreenBinding

    companion object {
        var ubdId = ""
        var availableBalance = 0.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFundTransferScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolbar()
        initializeProgressDialog()

        val factory = ViewModelFactory(MyAccountRepository())
        viewModel =
            ViewModelProvider(this@FundTransferScreen, factory)[MyAccountViewModel::class.java]

        loadExistingBankAccounts()

        binding.apply {
            accountRv.layoutManager =
                LinearLayoutManager(this@FundTransferScreen, LinearLayoutManager.HORIZONTAL, false)

            hundredBtn.setOnClickListener {
                if (isProvider(this@FundTransferScreen)) {
                    hundredBtn.setBackgroundResource(R.drawable.provider_btn_bg_sm)
                    hundredBtn.setTextColor(resources.getColor(R.color.white))
                    fiveHundredBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    fiveHundredBtn.setTextColor(resources.getColor(R.color.purple_500))
                    oneThousandBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    oneThousandBtn.setTextColor(resources.getColor(R.color.purple_500))
                    twoThousandBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    twoThousandBtn.setTextColor(resources.getColor(R.color.purple_500))
                } else {
                    hundredBtn.setBackgroundResource(R.drawable.blue_bg_sm)
                    hundredBtn.setTextColor(resources.getColor(R.color.white))
                    fiveHundredBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    fiveHundredBtn.setTextColor(resources.getColor(R.color.blue))
                    oneThousandBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    oneThousandBtn.setTextColor(resources.getColor(R.color.blue))
                    twoThousandBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    twoThousandBtn.setTextColor(resources.getColor(R.color.blue))
                }
                depositAmount.setText(hundredBtn.text.toString().trim())
            }

            fiveHundredBtn.setOnClickListener {
                if (isProvider(this@FundTransferScreen)) {
                    fiveHundredBtn.setBackgroundResource(R.drawable.provider_btn_bg_sm)
                    fiveHundredBtn.setTextColor(resources.getColor(R.color.white))
                    hundredBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    hundredBtn.setTextColor(resources.getColor(R.color.purple_500))
                    oneThousandBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    oneThousandBtn.setTextColor(resources.getColor(R.color.purple_500))
                    twoThousandBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    twoThousandBtn.setTextColor(resources.getColor(R.color.purple_500))
                } else {
                    fiveHundredBtn.setBackgroundResource(R.drawable.blue_bg_sm)
                    fiveHundredBtn.setTextColor(resources.getColor(R.color.white))
                    hundredBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    hundredBtn.setTextColor(resources.getColor(R.color.blue))
                    oneThousandBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    oneThousandBtn.setTextColor(resources.getColor(R.color.blue))
                    twoThousandBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    twoThousandBtn.setTextColor(resources.getColor(R.color.blue))
                }
                depositAmount.setText(fiveHundredBtn.text.toString())
            }

            oneThousandBtn.setOnClickListener {
                if (isProvider(this@FundTransferScreen)) {
                    oneThousandBtn.setBackgroundResource(R.drawable.provider_btn_bg_sm)
                    oneThousandBtn.setTextColor(resources.getColor(R.color.white))
                    hundredBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    hundredBtn.setTextColor(resources.getColor(R.color.purple_500))
                    fiveHundredBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    fiveHundredBtn.setTextColor(resources.getColor(R.color.purple_500))
                    twoThousandBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    twoThousandBtn.setTextColor(resources.getColor(R.color.purple_500))
                } else {
                    oneThousandBtn.setBackgroundResource(R.drawable.blue_bg_sm)
                    oneThousandBtn.setTextColor(resources.getColor(R.color.white))
                    hundredBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    hundredBtn.setTextColor(resources.getColor(R.color.blue))
                    fiveHundredBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    fiveHundredBtn.setTextColor(resources.getColor(R.color.blue))
                    twoThousandBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    twoThousandBtn.setTextColor(resources.getColor(R.color.blue))
                }
                depositAmount.setText(oneThousandBtn.text.toString())
            }

            twoThousandBtn.setOnClickListener {
                if (isProvider(this@FundTransferScreen)) {
                    twoThousandBtn.setBackgroundResource(R.drawable.provider_btn_bg_sm)
                    twoThousandBtn.setTextColor(resources.getColor(R.color.white))
                    hundredBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    hundredBtn.setTextColor(resources.getColor(R.color.purple_500))
                    fiveHundredBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    fiveHundredBtn.setTextColor(resources.getColor(R.color.purple_500))
                    oneThousandBtn.setBackgroundResource(R.drawable.purple_out_line_sm)
                    oneThousandBtn.setTextColor(resources.getColor(R.color.purple_500))
                } else {
                    twoThousandBtn.setBackgroundResource(R.drawable.blue_bg_sm)
                    twoThousandBtn.setTextColor(resources.getColor(R.color.white))
                    hundredBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    hundredBtn.setTextColor(resources.getColor(R.color.blue))
                    fiveHundredBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    fiveHundredBtn.setTextColor(resources.getColor(R.color.blue))
                    oneThousandBtn.setBackgroundResource(R.drawable.blue_out_line_sm)
                    oneThousandBtn.setTextColor(resources.getColor(R.color.blue))
                }
                depositAmount.setText(twoThousandBtn.text.toString())
            }

            addFundsBtn.setOnClickListener {
                val depositAmountStr = depositAmount.text.toString().trim()
                when {
                    depositAmountStr.isNotEmpty() -> {
//                        depositAmountInDouble = depositAmountStr.toDouble()
                        updateToServerToAddFund(depositAmountStr)
//                        makePayment(depositAmountInDouble)
                        return@setOnClickListener
                    }
                    else -> {
                        snackBar(depositAmount, "Select Fund or Enter Fund")
                    }
                }
            }

            withDrawBtn.setOnClickListener {
                val withdrawAmountStr = withdrawAmount.text.toString().trim()
                when {
                    withdrawAmountStr.isEmpty() -> {
                        snackBar(withDrawBtn, "Please Enter Withdraw amount")
                    }
                    ubdId.isEmpty() -> {
                        snackBar(withDrawBtn, "Please select Bank Account")
                    }
                    else -> {
                        withdrawAmountInDouble = withdrawAmountStr.toDouble()
                        withdrawAmountToServer()
                    }
                }
            }

            addAnotherAmountBtn.setOnClickListener {
                startActivity(Intent(this@FundTransferScreen, AddBankAccountScreen::class.java))
            }

        }

    }

    private fun loadExistingBankAccounts() {
        val requestBody =
            AllBankDetailsReqModel(RetrofitBuilder.USER_KEY, UserUtils.getUserId(this).toInt())
        viewModel.allBankDetails(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.availableBalance.text = availableBalance.toString()
                    allBankDetails = it.data!!
//                    Log.e("BANK DETAILS:", allBankDetails.toString())
                    binding.accountRv.adapter = AllBankDetailsAdapter(allBankDetails, this)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.withDrawBtn, it.message!!)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun withdrawAmountToServer() {
        val requestBody = WithdrawFundsReqModel(
            withdrawAmountInDouble.toString(),
            SimpleDateFormat("yyyy-MM-dd").format(Date()),
            RetrofitBuilder.USER_KEY,
            ubdId.toInt(),
            UserUtils.getUserId(this).toInt()
        )
//        Log.e("WITHDRAW:", Gson().toJson(requestBody))
        viewModel.withDrawFunds(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    if (isProvider(this@FundTransferScreen)) {
                        startActivity(Intent(this, ProviderMyAccountScreen::class.java))
                    } else {
                        startActivity(Intent(this, UserMyAccountScreen::class.java))
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.withDrawBtn, it.message!!)
                }
            }
        }
    }

//    private fun makePayment(amount: Double) {
//        Checkout.preload(this)
//        val checkout = Checkout()
//        checkout.setKeyID(getString(R.string.razorpay_api_key))
//        try {
//            val orderRequest = JSONObject()
//            orderRequest.put("currency", "INR")
//            orderRequest.put("amount", amount * 100) // 500rs * 100 = 50000 paisa passed
//            orderRequest.put("receipt", "order_rcptid_${System.currentTimeMillis()}")
//            orderRequest.put("image", "https://dev.satrango.com/public/assets/img/logo-black.png")
//            orderRequest.put("theme.color", R.color.blue)
//            checkout.open(this, orderRequest)
//        } catch (e: Exception) {
//            toast(this, e.message!!)
//        }
//    }

//    override fun onPaymentSuccess(statusCode: String?) {
//        updateToServerToAddFund("Success", statusCode!!)
//    }

    @SuppressLint("SimpleDateFormat")
    private fun updateToServerToAddFund(amount: String) {

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitBuilder.getUserRetrofitInstance().addFunds(
                    AddFundsReqModel(
                        amount,
                        RetrofitBuilder.USER_KEY,
                        UserUtils.getUserId(this@FundTransferScreen).toInt()
                    )
                )
//                toast(this@FundTransferScreen, Gson().toJson(amount))
                if (response.status == 200) {
                    UserUtils.saveOrderId(this@FundTransferScreen, response.order_id)
                    UserUtils.saveTxnToken(this@FundTransferScreen, response.txn_id)
                    addFundsByPaytmGateWay(amount)
                } else {
                    snackBar(binding.withDrawBtn, response.message)
                }
            } catch (e: Exception) {
                toast(this@FundTransferScreen, e.message!!)
            }
        }

    }

    private fun addFundsByPaytmGateWay(amount: String) {
        val callbackUrl =
            "http://dev.satrango.com/user/verify_txn?order_id=${UserUtils.getOrderId(this)}"
        val paytmOrder = PaytmOrder(
            UserUtils.getOrderId(this),
            resources.getString(R.string.paytm_mid),
            UserUtils.getTxnToken(this),
            amount,
            callbackUrl
        )
        val transactionManager =
            TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback {
                @SuppressLint("ObsoleteSdkInt", "SimpleDateFormat")
                override fun onTransactionResponse(inResponse: Bundle?) {
                    if (inResponse!!.getString(resources.getString(R.string.status_caps)) == resources.getString(
                            R.string.txn_success
                        )
                    ) {
                        val requestBody = FundTransferReqModel(
                            amount,
                            RetrofitBuilder.USER_KEY,
                            UserUtils.getUserId(this@FundTransferScreen).toInt(),
                            UserUtils.getOrderId(this@FundTransferScreen)
                        )
                        viewModel.fundTransfer(this@FundTransferScreen, requestBody)
                            .observe(this@FundTransferScreen) {
                                when (it) {
                                    is NetworkResponse.Loading -> {
                                        progressDialog.show()
                                    }
                                    is NetworkResponse.Success -> {
                                        progressDialog.dismiss()
                                        if (isProvider(this@FundTransferScreen)) {
                                            startActivity(
                                                Intent(
                                                    this@FundTransferScreen,
                                                    ProviderMyAccountScreen::class.java
                                                )
                                            )
                                        } else {
                                            startActivity(
                                                Intent(
                                                    this@FundTransferScreen,
                                                    UserMyAccountScreen::class.java
                                                )
                                            )
                                        }
                                    }
                                    is NetworkResponse.Failure -> {
                                        progressDialog.dismiss()
                                        snackBar(binding.depositAmount, it.message!!)
                                    }
                                }
                            }
                    }
                }

                override fun networkNotAvailable() {
                    snackBar(binding.addFundsBtn, "Network not available")
                }

                override fun onErrorProceed(p0: String?) {
                    Toast.makeText(this@FundTransferScreen, p0.toString(), Toast.LENGTH_LONG).show()
                }

                override fun clientAuthenticationFailed(p0: String?) {
                    Toast.makeText(this@FundTransferScreen, p0.toString(), Toast.LENGTH_LONG).show()
                }

                override fun someUIErrorOccurred(p0: String?) {
                    Toast.makeText(this@FundTransferScreen, p0.toString(), Toast.LENGTH_LONG).show()
                }

                override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
                    Toast.makeText(this@FundTransferScreen, p0.toString(), Toast.LENGTH_LONG).show()
                }

                override fun onBackPressedCancelTransaction() {
                    snackBar(binding.addFundsBtn, "Transaction Cancelled")
                }

                override fun onTransactionCancel(p0: String?, p1: Bundle?) {
                    Toast.makeText(this@FundTransferScreen, p0.toString(), Toast.LENGTH_LONG).show()
                }

            })
        transactionManager.setAppInvokeEnabled(false)
        transactionManager.setShowPaymentUrl("https://securegw.paytm.in/theia/api/v1/showPaymentPage")
        transactionManager.setEmiSubventionEnabled(true)
        transactionManager.startTransaction(this, activityRequestCode)
        transactionManager.startTransactionAfterCheckingLoginStatus(
            this,
            resources.getString(R.string.paytm_client_id),
            activityRequestCode
        )
    }

//    override fun onPaymentError(p0: Int, statusCode: String?) {
//        updateToServerToAddFund("Failure", statusCode!!)
//    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            this,
            BeautifulProgressDialog.withGIF,
            resources.getString(R.string.loading)
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun initializeToolbar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.fund_transfer)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
        if (isProvider(this@FundTransferScreen)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = resources.getColor(R.color.purple_700)
            }
            binding.hundredBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.hundredBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.fiveHundredBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.fiveHundredBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.oneThousandBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.oneThousandBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.twoThousandBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.twoThousandBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.addFundsBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.addFundsBtn.setTextColor(resources.getColor(R.color.white))

            binding.resetBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.resetBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.withDrawBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.withDrawBtn.setTextColor(resources.getColor(R.color.white))
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            binding.availableBalanceText.setTextColor(resources.getColor(R.color.purple_500))
        }
    }

    override fun selectedAccount(bankAccount: UserBankAccount) {
        val tempList = ArrayList<UserBankAccount>()
        allBankDetails.forEachIndexed { index, userBankAccount ->
            if (allBankDetails.indexOf(bankAccount) == index) {
                tempList.add(
                    UserBankAccount(
                        userBankAccount.account_name,
                        userBankAccount.account_no,
                        userBankAccount.bank_details_id,
                        userBankAccount.ifsc_code,
                        userBankAccount.ubd_id,
                        userBankAccount.users_id,
                        !userBankAccount.isSelected
                    )
                )
                ubdId = userBankAccount.ubd_id
            } else {
                tempList.add(userBankAccount)
            }
        }
        allBankDetails = tempList
        binding.accountRv.adapter = AllBankDetailsAdapter(allBankDetails, this)
    }

    override fun onBackPressed() {
        if (isProvider(this@FundTransferScreen)) {
            startActivity(Intent(this, ProviderMyAccountScreen::class.java))
        } else {
            startActivity(Intent(this, UserMyAccountScreen::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == activityRequestCode && data != null) {
//            Toast.makeText(
//                this,
//                data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"),
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

}