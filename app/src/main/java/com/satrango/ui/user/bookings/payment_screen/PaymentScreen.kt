package com.satrango.ui.user.bookings.payment_screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityPaymentScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMemberShipPlanPaymentReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review.ProviderRatingReviewScreen
import com.satrango.ui.service_provider.provider_dashboard.plans.ProviderPlansRepository
import com.satrango.ui.service_provider.provider_dashboard.plans.ProviderPlansViewModel
import com.satrango.ui.user.bookings.booking_address.models.GetTxnReqModel
import com.satrango.ui.user.bookings.payment_screen.models.*
import com.satrango.ui.user.bookings.provider_response.PaymentConfirmReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.CompleteBookingReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.installment_payments.InstallmentPaymentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.UserPlanPaymentReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class PaymentScreen : AppCompatActivity(), UpiInterface {
//    PaymentResultListener

    private var callbackUrl = ""
    private lateinit var upiList: List<com.satrango.ui.user.bookings.payment_screen.models.Data>
    private lateinit var binding: ActivityPaymentScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var transactionManager: TransactionManager
    private val activityRequestCode: Int = 1
    private val REQUEST_CODE = 123

    companion object {
        var id = 0
        var period = 0
        var amount = 0
        var userId = 0
        var FROM_USER_PLANS = false
        var FROM_PROVIDER_PLANS = false
        var FROM_USER_SET_GOALS = false
        var FROM_USER_BOOKING_ADDRESS = false
        var FROM_COMPLETE_BOOKING = false
        var FROM_PROVIDER_BOOKING_RESPONSE = false
        var finalAmount: Int = 0
        var finalWalletBalance = "0"
        var walletBalanceChecked = false
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgressDialog()
        initializeToolbar()

        binding.apply {

            if (FROM_USER_PLANS) {

            } else if (FROM_USER_SET_GOALS) {

            } else {
                if (!FROM_COMPLETE_BOOKING) {
                    val tempFinalAmount = Gson().fromJson(UserUtils.getSelectedAllSPDetails(this@PaymentScreen), SearchServiceProviderResModel::class.java).wallet_balance
                    if (tempFinalAmount > finalWalletBalance.toInt()) {
                        finalWalletBalance = tempFinalAmount.toString()
                    }
                }
            }

            finalAmount = if (FROM_USER_BOOKING_ADDRESS) {
                Gson().fromJson(UserUtils.getSelectedSPDetails(this@PaymentScreen), Data::class.java).final_amount
            } else {
                amount
            }

            if (FROM_PROVIDER_BOOKING_RESPONSE) {
                CoroutineScope(Dispatchers.Main).launch {
                     try {
                         val response = RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(
                             BookingDetailsReqModel(
                                 UserUtils.getBookingId(this@PaymentScreen).toInt(),
                                 UserUtils.getSelectedKeywordCategoryId(this@PaymentScreen).toInt(),
                                 RetrofitBuilder.USER_KEY,
                                 UserUtils.getUserId(this@PaymentScreen).toInt()
                             )
                         )
                         if (response.status == 200) {
                             totalAmount.text = "Rs.${response.booking_details.amount.toDouble().toInt()}/-"
                             bookingDateTime.text = "${response.booking_details.scheduled_date}, ${response.booking_details.from}"
                             finalAmount = response.booking_details.amount.toDouble().toInt()
                             payAmount.text = "Rs.$finalAmount/-"

                             if (finalWalletBalance.toInt() == 0) {
                                 currentBalance.visibility = View.GONE
                                 walletBalanceCheck.visibility = View.GONE
                             } else {
                                 if (finalWalletBalance.toInt() > response.booking_details.amount.toDouble().toInt()) {
                                     currentBalance.text = "Deduct from wallet balance: Rs. ${response.booking_details.amount}/-"
                                 } else if (finalWalletBalance.toInt() > 0 && finalWalletBalance.toInt() < response.booking_details.amount.toDouble().toInt()) {
                                     currentBalance.text = "Deduct from wallet balance: Rs. ${finalWalletBalance}/-"
                                 }
                                 walletBalance.text = "Rs.$finalWalletBalance/-"
                             }
                         }
                     } catch (e: Exception) {
                        toast(this@PaymentScreen, "Error" + e.message!!)
                     }
                }
                bookingDetailsLayout.visibility = View.VISIBLE
                amountLayout.visibility = View.VISIBLE
                bookingSummaryLayout.visibility = View.GONE
                locationText.text = UserUtils.getCity(this@PaymentScreen)

            } else if (FROM_COMPLETE_BOOKING) {

                bookingSummaryLayout.visibility = View.VISIBLE
                bookingDetailsLayout.visibility = View.GONE
                amountLayout.visibility = View.VISIBLE
                val data = Gson().fromJson(UserUtils.getInvoiceDetails(this@PaymentScreen), ProviderInvoiceResModel::class.java)
                bookingId.text = data.booking_details.booking_id.toString()
                workStartedAt.text = data.booking_details.started_at
                completedAt.text = data.booking_details.completed_at
                invoiceAmount.text = data.booking_details.final_dues
                paidAmount.text = data.booking_details.paid
                duesAmount.text = data.booking_details.final_dues
                cgstAmount.text = data.booking_details.cgst_tax
                sgstAmount.text = data.booking_details.sgst_tax
                summaryPayableAmount.text = data.booking_details.dues

                totalAmount.text = "Rs.$amount/-"
                walletBalance.text = "Rs.$finalWalletBalance/-"
                currentBalance.text = "Deduct from wallet Balance: Rs. $amount/-"
                payAmount.text = "Rs.$amount/-"

            } else if (FROM_USER_PLANS || FROM_PROVIDER_PLANS) {

                bookingSummaryLayout.visibility = View.GONE
                bookingSummaryCard.visibility = View.VISIBLE
                amountText.visibility = View.GONE
                invoiceAmountCard.visibility = View.GONE
                amountLayout.visibility = View.VISIBLE

                bookingIdText.text = "Plan Id:"
                bookingId.text = "Freedom"
                startedOnText.text = "Activation:"
                workStartedAt.text = "Activation Date and Time"
                completedText.text = "Valid till:"
                completedAt.text = "Completed At Date"

                totalAmount.text = "Rs.$amount/-"
                walletBalance.text = "Rs.$finalWalletBalance/-"
                currentBalance.text = "Deduct from wallet Balance: Rs. $amount/-"
                payAmount.text = "Rs.$amount/-"

            } else if (FROM_USER_SET_GOALS) {

                bookingSummaryLayout.visibility = View.GONE
                bookingSummaryCard.visibility = View.GONE
                amountText.visibility = View.GONE
                invoiceAmountCard.visibility = View.GONE
                amountLayout.visibility = View.VISIBLE

                totalAmount.text = "Rs.$amount/-"
                walletBalance.text = "Rs.$finalWalletBalance/-"
                currentBalance.text = "Deduct from wallet Balance: Rs. $amount/-"
                payAmount.text = "Rs.$amount/-"
            }

            walletBalanceCheck.isClickable = finalWalletBalance.toInt() != 0
            walletBalanceCheck.setOnCheckedChangeListener { _, checked ->
                walletBalanceChecked = checked
                if (checked) {
                    if (finalWalletBalance.toInt() >= finalAmount) {
                        currentBalance.text = "Deducted from Wallet - Rs. $finalAmount/-"
                        val totalAmount = totalAmount.text.toString().trim().split(".")[1].split("/")[0].toDouble()
                        val totalWallet = walletBalance.text.toString().trim().split(".")[1].split("/")[0].toDouble()
                        if (totalAmount > totalWallet) {
                            val result = totalAmount - totalWallet
                            payAmount.text = "Rs. ${result.toInt()}/-"
                        }
                        if (totalWallet > totalAmount) {
                            payAmount.text = "Rs.0/-"
                        }
                    } else {
                        currentBalance.text = "Deducted from Wallet - Rs. $finalWalletBalance/-"
                        val totalAmount = totalAmount.text.toString().trim().split(".")[1].split("/")[0].toDouble()
                        val totalWallet = walletBalance.text.toString().trim().split(".")[1].split("/")[0].toDouble()
                        if (totalAmount > totalWallet) {
                            val result = totalAmount - totalWallet
                            payAmount.text = "Rs. ${result.toInt()}/-"
                        }
                        if (totalWallet > totalAmount) {
                            payAmount.text = "Rs.0/-"
                        }
                    }
                } else {
                    currentBalance.text = "Deducted from Wallet - Rs. 0/-"
                    payAmount.text = totalAmount.text.toString().trim()
                }
            }

            confirmPaymentBtn.setOnClickListener {
                if (FROM_USER_PLANS) {
                    processPaytm("3")
                }
                if (FROM_PROVIDER_PLANS) {
                    processPaytm("4")
                }
                if (FROM_PROVIDER_BOOKING_RESPONSE) {
                    processPaytm("1")
                }
                if (FROM_COMPLETE_BOOKING) {
                    processPaytm("2")
                }
                if (FROM_USER_SET_GOALS) {
                    processPaytm("5")
                }
            }

            addNewUPI.setOnClickListener {
                if (enterUPIid.visibility == View.VISIBLE) {
                    if (enterUPIid.text.toString().trim().isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val response = RetrofitBuilder.getUserRetrofitInstance()
                                .saveUserUpi(
                                    SaveUserUpiReqModel(
                                        RetrofitBuilder.USER_KEY,
                                        enterUPIid.text.toString().trim(),
                                        UserUtils.getUserId(this@PaymentScreen).toInt()
                                    )
                                )
                            if (response.status == 200) {
                                enterUPIid.visibility = View.GONE
                                loadSavedUpiList()
                            } else {
                                toast(this@PaymentScreen, response.message)
                            }
                        }
                    } else {
                        toast(this@PaymentScreen, "Enter UPI ID")
                    }
                } else {
                    enterUPIid.visibility = View.VISIBLE
                    addNewUPI.text = "Add New UPI ID"
                }
            }
//            loadSavedUpiList()
        }
    }

    private fun processPaytm(txnType: String) {
        var walletBalance = 0.0
        var payableAmount = 0.0
        if (binding.amountLayout.visibility == View.VISIBLE) {
            walletBalance = if (binding.walletBalanceCheck.isChecked) {
                 binding.currentBalance.text.toString().trim().split(".")[1].split("/")[0].trim().toDouble()
            } else {
                0.0
            }
            payableAmount = binding.payAmount.text.toString().trim().split(".")[1].split("/")[0].toDouble()
        }

        if (binding.invoiceAmountCard.visibility == View.VISIBLE) {
            payableAmount = binding.summaryPayableAmount.text.toString().trim().split(".")[1].split("/")[0].toDouble()
        }

        if (FROM_COMPLETE_BOOKING || FROM_USER_SET_GOALS) {
            if (payableAmount == 0.0) {
                updateToServer(payableAmount.toString(), walletBalance.toString())
            } else {
                generateTxn(payableAmount, walletBalance, txnType)
            }
        } else {
            val dues = binding.duesAmount.text.toString().trim().split(".")[1].split("/")[0].toDouble()
            val summaryPayAmount = binding.summaryPayableAmount.text.toString().trim().split(".")[1].split("/")[0].toDouble()
            if (dues == 0.0 && summaryPayAmount == 0.0) {
                if (payableAmount > 0) {
                    generateTxn(payableAmount, walletBalance, txnType)
                } else {
                    updateToServer(payableAmount.toString(), walletBalance.toString())
                }
            } else {
                generateTxn(payableAmount, walletBalance, txnType)
            }
        }

    }

    private fun generateTxn(payableAmount: Double, walletBalance: Double,  txnType: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = GetTxnReqModel(payableAmount.toString(), RetrofitBuilder.USER_KEY, UserUtils.getUserId(this@PaymentScreen), txnType)
//                Log.e("REQUEST:", Gson().toJson(requestBody))
                val response = RetrofitBuilder.getUserRetrofitInstance().getTxn(requestBody)
//                Log.e("RESPONSE:", Gson().toJson(response))
                if (response.status == 200) {
                    UserUtils.saveTxnToken(this@PaymentScreen, response.txn_id)
                    UserUtils.saveOrderId(this@PaymentScreen, response.order_id)
                    processPaytmGateWay(payableAmount, walletBalance)
                } else {
                    toast(this@PaymentScreen, response.message)
                }
            } catch (e: Exception) {
                toast(this@PaymentScreen, e.message!!)
            }
        }
    }

    private fun processPaytmGateWay(payableAmount: Double, walletBalance: Double) {
        binding.confirmPaymentBtn.text = getString(R.string.processing_payment)
        callbackUrl = "http://dev.satrango.com/user/verify_txn?order_id=${UserUtils.getOrderId(this@PaymentScreen)}"
        val paytmOrder = PaytmOrder(UserUtils.getOrderId(this), resources.getString(R.string.paytm_mid), UserUtils.getTxnToken(this), payableAmount.toString(), callbackUrl)
        transactionManager = TransactionManager(paytmOrder, object: PaytmPaymentTransactionCallback {
            @SuppressLint("ObsoleteSdkInt")
            override fun onTransactionResponse(inResponse: Bundle?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (inResponse!!.getString(resources.getString(R.string.status_caps)) == resources.getString(R.string.txn_success)) {
                        updateToServer(payableAmount.toString(), walletBalance.toString())
                    } else if (!inResponse.getBoolean(resources.getString(R.string.status_caps))) {
                        toast(this@PaymentScreen, "PAYMENT FAILED")
                        showPaymentFailureDialog()
                    }
                }
                binding.confirmPaymentBtn.text = getString(R.string.confirm_payment)
            }

            override fun networkNotAvailable() {
                binding.confirmPaymentBtn.text = getString(R.string.confirm_payment)
                snackBar(binding.addCreditDebitCard, "Network not available")
            }

            override fun onErrorProceed(p0: String?) {
                binding.confirmPaymentBtn.text = getString(R.string.confirm_payment)
                Toast.makeText(this@PaymentScreen, p0.toString(), Toast.LENGTH_LONG).show()
            }

            override fun clientAuthenticationFailed(p0: String?) {
                binding.confirmPaymentBtn.text = getString(R.string.confirm_payment)
                Toast.makeText(this@PaymentScreen, p0.toString(), Toast.LENGTH_LONG).show()
            }

            override fun someUIErrorOccurred(p0: String?) {
                binding.confirmPaymentBtn.text = getString(R.string.confirm_payment)
                Toast.makeText(this@PaymentScreen, p0.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
                binding.confirmPaymentBtn.text = getString(R.string.confirm_payment)
                Toast.makeText(this@PaymentScreen, p0.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onBackPressedCancelTransaction() {
                binding.confirmPaymentBtn.text = getString(R.string.confirm_payment)
                snackBar(binding.addCreditDebitCard, "Transaction Cancelled")
            }

            override fun onTransactionCancel(p0: String?, p1: Bundle?) {
                binding.confirmPaymentBtn.text = getString(R.string.confirm_payment)
                Toast.makeText(this@PaymentScreen, p0.toString(), Toast.LENGTH_LONG).show()
            }

        })
        transactionManager.setAppInvokeEnabled(false)
        transactionManager.setShowPaymentUrl("https://securegw.paytm.in/theia/api/v1/showPaymentPage")
        transactionManager.setEmiSubventionEnabled(true)
        transactionManager.startTransaction(this@PaymentScreen, activityRequestCode)
        transactionManager.startTransactionAfterCheckingLoginStatus(this@PaymentScreen, resources.getString(R.string.paytm_client_id), activityRequestCode)

    }

    private fun updateToServer(paidAmount: String, walletBalance: String) {
        when {
            FROM_PROVIDER_PLANS -> {
                saveProviderPlan(paidAmount, walletBalance)
            }
            FROM_USER_PLANS -> {
                saveUserPlan(paidAmount, walletBalance)
            }
            FROM_USER_BOOKING_ADDRESS -> {
                updateStatusInServer(paidAmount, walletBalance)
            }
            FROM_PROVIDER_BOOKING_RESPONSE -> {
                updateStatusInServer(paidAmount, walletBalance)
            }
            FROM_USER_SET_GOALS -> {
                updateInstallmentPaymentStatus("Success", "paymentId")
            }
            FROM_COMPLETE_BOOKING -> {
                completeBooking(paidAmount, walletBalance, "Success")
            }
        }
    }

    private fun showPaymentFailureDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.paytm_payment_failure_dialog, null)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun loadSavedUpiList() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitBuilder.getUserRetrofitInstance().getUserUpi(
                    GetUserUpiReqModel(
                        RetrofitBuilder.USER_KEY,
                        UserUtils.getUserId(this@PaymentScreen).toInt()
                    )
                )
                if (response.status == 200) {
                    binding.upisRV.layoutManager = LinearLayoutManager(this@PaymentScreen)
                    upiList = response.data
                    binding.upisRV.adapter = UpiAdapter(upiList, this@PaymentScreen)
                } else {
                    snackBar(binding.addCreditDebitCard, response.message)
                }
            } catch (e: java.lang.Exception) {
                toast(this@PaymentScreen, e.message!!)
            }
        }
    }

    private fun completeBooking(paidAmount: String, walletBalance: String, referenceId: String) {

        val inVoiceDetails = Gson().fromJson(UserUtils.getInvoiceDetails(this), ProviderInvoiceResModel::class.java)
//        Log.e("INVOICE:", Gson().toJson(inVoiceDetails))
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = CompleteBookingReqModel(
                    paidAmount,
                    walletBalance,
                    inVoiceDetails.booking_details.booking_id.toString(),
                    inVoiceDetails.booking_details.cgst_tax,
                    inVoiceDetails.booking_details.completed_at,
                    RetrofitBuilder.USER_KEY,
                    referenceId,
                    inVoiceDetails.booking_details.sgst_tax,
                    inVoiceDetails.booking_details.sp_id,
                    inVoiceDetails.booking_details.dues,
                    UserUtils.getUserId(this@PaymentScreen),
                    UserUtils.getOrderId(this@PaymentScreen)
                )
//                Log.e("COMPLETE BOOKING:", Gson().toJson(requestBody))
                val response =
                    RetrofitBuilder.getUserRetrofitInstance().completeBooking(requestBody)
                if (JSONObject(response.string()).getInt("status") == 200) {
                    showBookingCompletedSuccessDialog(
                        inVoiceDetails.booking_details.booking_id,
                        inVoiceDetails.booking_details.sp_id
                    )
                } else {
                    toast(this@PaymentScreen, "Error:" + response.string())
                }
//                toast(this@PaymentScreen, response.string())
            } catch (e: Exception) {
                toast(this@PaymentScreen, "Error:" + e.message!!)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == activityRequestCode && data != null) { }
        if (requestCode == REQUEST_CODE) {
            data?.getStringExtra("Status")?.let {
//                Log.d("result", it)
            }
            data?.getStringExtra("Status")?.let {
                if (it.lowercase(Locale.getDefault()) == "success") {
                    paymentSuccessDialog(this)
                } else {
                    toast(this@PaymentScreen, "ON ACTIVITY RESULTS")
                    showPaymentFailureDialog()
                }
            }
        }
    }

    private fun initializeToolbar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.my_account)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveUserPlan(paidAmount: String, walletBalance: String) {
        val requestBody = UserPlanPaymentReqModel(
            paidAmount.toInt(),
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            RetrofitBuilder.USER_KEY,
            period,
            id,
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).users_id.toInt(),
            walletBalance,
            UserUtils.getOrderId(this)
        )
//        Log.d("USER PLAN:", Gson().toJson(requestBody))
        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        viewModel.saveUserPlanPayment(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    if (it.data!!.status == 200) {
                        showSuccessDialog()
                        Handler().postDelayed({
                            startActivity(Intent(this, UserDashboardScreen::class.java))
                        }, 3000)
                    } else {
                        toast(this@PaymentScreen, "USER PLAN PAYMENT FAILURE")
                        showPaymentFailureDialog()
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.googlePayBtn, "Error01:" + it.message!!)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveProviderPlan(paidAmount: String, walletBalance: String) {
        val factory = ViewModelFactory(ProviderPlansRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderPlansViewModel::class.java]
        val requestBody = ProviderMemberShipPlanPaymentReqModel(
            paidAmount.toInt(),
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            RetrofitBuilder.USER_KEY,
            period,
            id,
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).users_id.toInt(),
            walletBalance,
            UserUtils.getOrderId(this)
        )
//        Log.d("SP PLAN:", Gson().toJson(requestBody))
        viewModel.saveMemberShip(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    if (it.data!!.status == 200) {
                        showSuccessDialog()
                        Handler().postDelayed({
                            startActivity(Intent(this, ProviderDashboard::class.java))
                        }, 3000)
                    } else {
                        toast(this@PaymentScreen, "MEMBERSHIP FAILURE")
                        showPaymentFailureDialog()
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.googlePayBtn, "Error02:" + it.message!!)
                }
            }
        }
    }

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

    private fun showSuccessDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.payment_success_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val homeBtn = dialogView.findViewById<TextView>(R.id.closBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        homeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun updateStatusInServer(paidAmount: String, walletBalance: String) {
//        var finalAmount = Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).final_amount
        val finalUserId = Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).users_id.toInt()
//        var finalWalletBalance = Gson().fromJson(UserUtils.getSelectedAllSPDetails(this), SearchServiceProviderResModel::class.java).wallet_balance
//        if (finalWalletBalance <= 0) {
//            finalWalletBalance = 0
//        } else {
//            if (binding.walletBalanceCheck.isChecked) {
//                finalWalletBalance = if (finalWalletBalance >= finalAmount) {
//                    finalWalletBalance = finalAmount
//                    finalAmount = 0
//                    finalWalletBalance
//                } else {
//                    finalWalletBalance -= finalAmount
//                    finalAmount -= finalWalletBalance
//                    finalWalletBalance
//                }
//            }
//        }
//        updateBookingStatusInServer(finalAmount.toString(), finalUserId, finalWalletBalance.toString())
        updateBookingStatusInServer(paidAmount, finalUserId, walletBalance)
    }

    private fun updateBookingStatusInServer(
        finalAmount: String,
        finalUserId: Int,
        finalWalletBalance: String
    ) {

        val requestBody = PaymentConfirmReqModel(
            finalAmount,
            UserUtils.getBookingId(this),
            UserUtils.scheduled_date,
            RetrofitBuilder.USER_KEY,
            finalUserId,
            UserUtils.time_slot_from,
            UserUtils.getUserId(this).toInt(),
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).CGST_amount,
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).SGST_amount,
            UserUtils.getOrderId(this),
            finalWalletBalance
        )
//        Log.d("PAYMENT STATUS:", Gson().toJson(requestBody))
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitBuilder.getUserRetrofitInstance().confirmPayment(requestBody)
                if (JSONObject(response.string()).getInt("status") == 200) {
                    progressDialog.dismiss()
                    showSuccessDialog()
                } else {
                    progressDialog.dismiss()
                    toast(this@PaymentScreen, "CONFIRM PAYMENT FAILURE")
                    showPaymentFailureDialog()
                }
            } catch (e: java.lang.Exception) {
                progressDialog.dismiss()
                snackBar(binding.addCreditDebitCard, e.message!!)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateInstallmentPaymentStatus(status: String, referenceId: String) {
        val requestBody = InstallmentPaymentReqModel(
            amount.toString(),
            ViewBidsScreen.bookingId,
            SimpleDateFormat("yyyy-MM-dd").format(Date()),
            RetrofitBuilder.USER_KEY,
            status,
            referenceId,
            UserUtils.getUserId(this).toInt(),
            ViewBidsScreen.bidId,
            ViewBidsScreen.spId
        )
//        Log.d("INSTALLMENT PLAN:", Gson().toJson(requestBody))
        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        viewModel.installmentPayments(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    if (status == "Success") {
                        paymentSuccessDialog(this)
                    } else {
                        toast(this@PaymentScreen, "INSTALLMENT FAILURE")
                        showPaymentFailureDialog()
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.googlePayBtn, "Error04:" + it.message!!)
                }
            }
        }
    }

    private fun paymentSuccessDialog(context: Context) {
        val dialog = BottomSheetDialog(context)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.payment_success_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val closBtn = dialogView.findViewById<TextView>(R.id.closBtn)
        closBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        closeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showBookingCompletedSuccessDialog(bookingId: Int, spId: String) {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.payment_success_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val closBtn = dialogView.findViewById<TextView>(R.id.closBtn)
        closBtn.visibility = View.GONE
        val text = dialogView.findViewById<TextView>(R.id.text)
        text.text =
            "You have successfully completed the booking. You will now be redirected to Rating screen. Please give rating to the service provider."
        closeBtn.setOnClickListener {
            dialog.dismiss()
            ProviderRatingReviewScreen.bookingId = bookingId.toString()
            ProviderRatingReviewScreen.userId = spId
            ProviderRatingReviewScreen.categoryId = "0"
            startActivity(Intent(this, ProviderRatingReviewScreen::class.java))
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    override fun updateList(position: Int) {
        val tempList = ArrayList<com.satrango.ui.user.bookings.payment_screen.models.Data>()
        for (index in upiList.indices) {
            if (index == position) {
                tempList.add(
                    Data(
                        upiList[index].upi,
                        upiList[index].upiAdded,
                        upiList[index].userId,
                        true
                    )
                )
            } else {
                tempList.add(
                    Data(
                        upiList[index].upi,
                        upiList[index].upiAdded,
                        upiList[index].userId,
                        false
                    )
                )
            }
        }
        upiList = tempList
        binding.upisRV.adapter = UpiAdapter(upiList, this)
    }

}