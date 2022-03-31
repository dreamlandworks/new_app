package com.satrango.ui.user.bookings.payment_screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
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
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.payment_screen.models.GetUserUpiReqModel
import com.satrango.ui.user.bookings.payment_screen.models.SaveUserUpiReqModel
import com.satrango.ui.user.bookings.provider_response.PaymentConfirmReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.CompleteBookingReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.installment_payments.InstallmentPaymentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.UserPlanPaymentReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

class PaymentScreen : AppCompatActivity(), PaymentResultListener, UpiInterface {

    private lateinit var upiList: List<com.satrango.ui.user.bookings.payment_screen.models.Data>
    private lateinit var binding: ActivityPaymentScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var transactionManager: TransactionManager
    private val activityRequestCode: Int = 1
    private val REQUEST_CODE = 123

    val BHIM_UPI = "in.org.npci.upiapp"
    val GOOGLE_PAY = "com.google.android.apps.nbu.paisa.user"
    val PHONE_PE = "com.phonepe.app"
    val PAYTM = "net.one97.paytm"

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

            if (!FROM_COMPLETE_BOOKING) {
                val tempFinalAmount = Gson().fromJson(
                    UserUtils.getSelectedAllSPDetails(this@PaymentScreen),
                    SearchServiceProviderResModel::class.java
                ).wallet_balance
                if (tempFinalAmount > finalWalletBalance.toInt()) {
                    finalWalletBalance = tempFinalAmount.toString()
                }
            }

            finalAmount = if (FROM_USER_BOOKING_ADDRESS) {
//                toast(this@PaymentScreen, finalWalletBalance)
                Gson().fromJson(
                    UserUtils.getSelectedSPDetails(this@PaymentScreen),
                    Data::class.java
                ).final_amount
            } else {
                amount
            }
            payableAmount.text = "Rs. $finalAmount"
            payableBalance.text =
                resources.getString(R.string.total_amount_payable) + " Rs. " + finalAmount.toString()
            walletBalance.text = "Wallet Balance - Rs. $finalWalletBalance"
            walletBalanceCheck.isClickable = finalWalletBalance.toInt() != 0
            walletBalanceCheck.setOnCheckedChangeListener { compoundButton, checked ->
                walletBalanceChecked = checked
                if (checked) {
                    if (finalWalletBalance.toInt() >= finalAmount) {
                        phonePeBtn.isEnabled = false
                        googlePayBtn.isEnabled = false
                        paytmBtn.isEnabled = false
                        amazonBtn.isEnabled = false
                        wtsappPayBtn.isEnabled = false
                        upisRV.isEnabled = false
                        payableAmount.text = "Rs. 0.00"
                        currentBalance.text = "Deducted from Wallet - Rs. $finalAmount"
                    } else {
                        phonePeBtn.isEnabled = true
                        googlePayBtn.isEnabled = true
                        paytmBtn.isEnabled = true
                        amazonBtn.isClickable = true
                        wtsappPayBtn.isEnabled = true
                        upisRV.isEnabled = true
                        payableAmount.text = "Rs. ${finalAmount - finalWalletBalance.toInt()}"
                        currentBalance.text = "Deducted from Wallet - Rs. $finalWalletBalance"
                    }
                } else {
                    currentBalance.text = "Deducted from Wallet - Rs. 0"
                    payableAmount.text = "Rs. $finalAmount"
                }
                upisRV.adapter = UpiAdapter(upiList, this@PaymentScreen)
//                toast(this@PaymentScreen, walletBalanceChecked.toString())
            }

            proceedWithUPIBtn.setOnClickListener {
                val uri = "upi://pay?pa=paytmqr2810050501011ooqggb29a01@paytm&pn=Paytm%20Merchant&mc=5499&mode=02&orgid=0&paytmqr=2810050501011OOQGGB29A01&am=$amount&sign=MEYCIQDq96qhUnqvyLsdgxtfdZ11SQP//6F7f7VGJ0qr//lF/gIhAPgTMsopbn4Y9DiE7AwkQEPPnb2Obx5Fcr0HJghd4gzo"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.data = Uri.parse(uri)
                val chooser = Intent.createChooser(intent, "Pay with...")
                startActivityForResult(chooser, REQUEST_CODE)
            }

            proceedToPayBtn.setOnClickListener {
                when {
                    FROM_PROVIDER_PLANS -> {
                        saveProviderPlan("paymentId")
                    }
                    FROM_USER_PLANS -> {
                        saveUserPlan("paymentId")
                    }
                    FROM_USER_BOOKING_ADDRESS -> {
                        updateStatusInServer("paymentId", "Success")
                    }
                    FROM_PROVIDER_BOOKING_RESPONSE -> {
                        updateStatusInServer("paymentId", "Success")
                    }
                    FROM_USER_SET_GOALS -> {
                        updateInstallmentPaymentStatus("Success", "paymentId")
                    }
                    FROM_COMPLETE_BOOKING -> {
                        completeBooking("Success", "paymentId")
                    }
                }
            }


            googlePayBtn.setOnClickListener {

            }

            phonePeBtn.setOnClickListener {

            }

            paytmBtn.setOnClickListener {
                try {
                    processPaytm()
                } catch (e: java.lang.Exception) {
                    toast(this@PaymentScreen, e.message!!)
                }
            }

//            for(i in upiApps.indices){
//                val b = upiAppButtons[i]
//                val p = upiApps[i]
////                Log.d("UpiAppVisibility", p + " | " + isAppInstalled(p).toString() + " | " + isAppUpiReady(p))
//                if(isAppInstalled(p)&&isAppUpiReady(p)) {
//                    b.visibility = View.VISIBLE
//                    b.setOnClickListener{
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//                        intent.data = Uri.parse(uri)
//                        intent.setPackage(p)
//                        startActivityForResult(intent, REQUEST_CODE)
//                    }
//                }
//                else{
//                    b.visibility = View.INVISIBLE
//                }
//            }

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

            loadSavedUpiList()
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }
    /*
        This function checks if the app with this package name is responding to UPI intent
        - i.e. the app has a ready UPI user (as per the NPCI recommended implementation)
        - Circular: https://www.npci.org.in/sites/default/files/circular/Circular-73-Payer_App_behaviour_for_Intent_based_transaction_on_UPI.pdf
    */
    private fun isAppUpiReady(packageName: String): Boolean {
        var appUpiReady = false
        val upiIntent = Intent(Intent.ACTION_VIEW, Uri.parse("upi://pay"))
        val pm = packageManager
        val upiActivities: List<ResolveInfo> = pm.queryIntentActivities(upiIntent, 0)
        for (a in upiActivities){
            if (a.activityInfo.packageName == packageName) appUpiReady = true
        }
        return appUpiReady
    }

    private fun processPaytm() {
        val callbackUrl = "http://dev.satrango.com/user/verify_txn?order_id=${UserUtils.getOrderId(this)}"
        toast(this, callbackUrl)
        val paytmOrder = PaytmOrder(UserUtils.getOrderId(this), resources.getString(R.string.paytm_mid), UserUtils.getTxnToken(this), amount.toString(), callbackUrl)
        transactionManager = TransactionManager(paytmOrder,object: PaytmPaymentTransactionCallback {
            override fun onTransactionResponse(p0: Bundle?) {
                Log.e("PAYTM: ", Gson().toJson(p0.toString()))
                if (resources.getString(R.string.txn_success) == p0!!.getString(resources.getString(R.string.status_caps))!!) {
                    showPaymentSuccessDialog()
                } else {
                    showPaymentFailureDialog()
                }
            }

            override fun networkNotAvailable() {
                Toast.makeText(this@PaymentScreen, "Network Not available", Toast.LENGTH_LONG).show()

            }

            override fun onErrorProceed(p0: String?) {
                Toast.makeText(this@PaymentScreen, p0.toString(), Toast.LENGTH_LONG).show()
            }

            override fun clientAuthenticationFailed(p0: String?) {
                Toast.makeText(this@PaymentScreen, p0.toString(), Toast.LENGTH_LONG).show()
            }

            override fun someUIErrorOccurred(p0: String?) {
                Toast.makeText(this@PaymentScreen, p0.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
                Toast.makeText(this@PaymentScreen, p0.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onBackPressedCancelTransaction() {
                Toast.makeText(this@PaymentScreen, "Transaction Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onTransactionCancel(p0: String?, p1: Bundle?) {
                Toast.makeText(this@PaymentScreen, p0.toString(), Toast.LENGTH_LONG).show()
            }

        })
        transactionManager.setAppInvokeEnabled(false)
        transactionManager.setShowPaymentUrl("https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage")
        transactionManager.setEmiSubventionEnabled(true)
        transactionManager.startTransaction(this, activityRequestCode)
        transactionManager.startTransactionAfterCheckingLoginStatus(this, resources.getString(R.string.paytm_client_id), activityRequestCode)
    }

    private fun showPaymentFailureDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.paytm_payment_failure_dialog, null)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun showPaymentSuccessDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.paytm_payment_success_dialog, null)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
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
                    toast(this@PaymentScreen, response.message)
                }
            } catch (e: java.lang.Exception) {
                toast(this@PaymentScreen, e.message!!)
            }
        }
    }

    private fun completeBooking(status: String, referenceId: String) {

        val inVoiceDetails =
            Gson().fromJson(UserUtils.getInvoiceDetails(this), ProviderInvoiceResModel::class.java)
        Log.e("INVOICE:", Gson().toJson(inVoiceDetails))
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = CompleteBookingReqModel(
                    binding.payableAmount.text.toString().split(" ")[1],
                    binding.currentBalance.text.toString().split(" ")[5],
                    inVoiceDetails.booking_details.booking_id.toString(),
                    inVoiceDetails.booking_details.cgst_tax,
                    inVoiceDetails.booking_details.completed_at,
                    RetrofitBuilder.USER_KEY,
                    "1",
                    status,
                    referenceId,
                    inVoiceDetails.booking_details.sgst_tax,
                    inVoiceDetails.booking_details.sp_id,
                    inVoiceDetails.booking_details.dues,
                    UserUtils.getUserId(this@PaymentScreen)
                )
                Log.e("COMPLETE BOOKING:", Gson().toJson(requestBody))
//                toast(this@PaymentScreen,"COMPLETED BOOKING" +  Gson().toJson(requestBody))
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
        if (requestCode == activityRequestCode && data != null) {
            Toast.makeText(
                this,
                data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"),
                Toast.LENGTH_SHORT
            ).show()
        }
        if (requestCode == REQUEST_CODE) {
            // Process based on the data in response.
            Log.d("result", data.toString())
            data?.getStringExtra("Status")?.let { Log.d("result", it) }
            data?.getStringExtra("Status")?.let { Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show() }
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

    private fun makePayment() {
        Checkout.preload(this)
        val checkout = Checkout()
        checkout.setKeyID(getString(R.string.razorpay_api_key))
        try {
            val orderRequest = JSONObject()
            orderRequest.put("currency", "INR")
            orderRequest.put(
                "amount",
                amount * 100
            ) // 500rs * 100 = 50000 paisa passed
            orderRequest.put("receipt", "order_rcptid_${System.currentTimeMillis()}")
            orderRequest.put("image", "https://dev.satrango.com/public/assets/img/logo-black.png")
            orderRequest.put("theme.color", R.color.blue)
            checkout.open(this, orderRequest)
        } catch (e: Exception) {
            toast(this, e.message!!)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onPaymentSuccess(paymentId: String?) {
        when {
            FROM_PROVIDER_PLANS -> {
                saveProviderPlan(paymentId)
            }
            FROM_USER_PLANS -> {
                saveUserPlan(paymentId)
            }
            FROM_USER_BOOKING_ADDRESS -> {
                updateStatusInServer(paymentId, "Success")
            }
            FROM_PROVIDER_BOOKING_RESPONSE -> {
                updateStatusInServer(paymentId, "Success")
            }
            FROM_USER_SET_GOALS -> {
                updateInstallmentPaymentStatus("Success", paymentId!!)
            }
            FROM_COMPLETE_BOOKING -> {
                completeBooking("Success", paymentId!!)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveUserPlan(paymentId: String?) {
        val finalAmount = if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            round(
                Gson().fromJson(
                    UserUtils.getSelectedSPDetails(this),
                    Data::class.java
                ).final_amount.toDouble()
            ).toInt()
        } else {
            amount
        }
        val requestBody = UserPlanPaymentReqModel(
            finalAmount,
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            RetrofitBuilder.USER_KEY,
            "Success",
            period,
            id,
            paymentId!!,
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).users_id.toInt()
        )
        Log.d("USER PLAN:", Gson().toJson(requestBody))
        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        viewModel.saveUserPlanPayment(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    showSuccessDialog()
                    Handler().postDelayed({
                        startActivity(Intent(this, UserDashboardScreen::class.java))
                    }, 3000)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.googlePayBtn, "Error01:" + it.message!!)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveProviderPlan(paymentId: String?) {
        val factory = ViewModelFactory(ProviderPlansRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderPlansViewModel::class.java]
        val finalAmount =
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).final_amount
        val requestBody = ProviderMemberShipPlanPaymentReqModel(
            finalAmount,
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            RetrofitBuilder.USER_KEY,
            "Success",
            period,
            id,
            paymentId!!,
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).users_id.toInt()
        )
        Log.d("SP PLAN:", Gson().toJson(requestBody))
        viewModel.saveMemberShip(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    showSuccessDialog()
                    Handler().postDelayed({
                        startActivity(Intent(this, ProviderDashboard::class.java))
                    }, 3000)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.googlePayBtn, "Error02:" + it.message!!)
                }
            }
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        when {
            FROM_PROVIDER_PLANS -> {
                snackBar(binding.googlePayBtn, "Payment Failed")
            }
            FROM_USER_PLANS -> {
                snackBar(binding.googlePayBtn, "Payment Failed")
            }
            FROM_USER_BOOKING_ADDRESS -> {
                updateStatusInServer("", "Failure")
            }
            FROM_PROVIDER_BOOKING_RESPONSE -> {
                updateStatusInServer("", "Failure")
            }
            FROM_USER_SET_GOALS -> {
                updateInstallmentPaymentStatus("Failure", "")
            }
            FROM_COMPLETE_BOOKING -> {
                completeBooking("Failure", "")
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

    private fun updateStatusInServer(paymentResponse: String?, status: String) {
        var finalAmount =
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).final_amount
        val finalUserId =
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).users_id.toInt()
        var finalWalletBalance = Gson().fromJson(
            UserUtils.getSelectedAllSPDetails(this),
            SearchServiceProviderResModel::class.java
        ).wallet_balance
        if (finalWalletBalance.toInt() <= 0) {
            finalWalletBalance = 0
        } else {
            if (binding.walletBalanceCheck.isChecked) {
                finalWalletBalance = if (finalWalletBalance >= finalAmount) {
                    finalWalletBalance = finalAmount
                    finalAmount = 0
                    finalWalletBalance
                } else {
                    finalWalletBalance -= finalAmount
                    finalAmount -= finalWalletBalance
                    finalWalletBalance
                }
            }
        }
//        if (finalAmount <= finalWalletBalance) {
//            if (binding.walletBalanceCheck.isChecked) {
//            updateBookingStatusInServer(finalAmount.toString(), status, paymentResponse!!, finalUserId, finalWalletBalance.toString())
//            } else {
//                toast(this, "Please select wallet balance")
//            }
//        } else {
        updateBookingStatusInServer(
            finalAmount.toString(),
            status,
            paymentResponse!!,
            finalUserId,
            finalWalletBalance.toString()
        )
//        }
    }

    private fun updateBookingStatusInServer(
        finalAmount: String,
        status: String,
        paymentResponse: String,
        finalUserId: Int,
        finalWalletBalance: String
    ) {
        val requestBody = PaymentConfirmReqModel(
            finalAmount,
            UserUtils.getBookingId(this),
            UserUtils.scheduled_date,
            RetrofitBuilder.USER_KEY,
            status,
            paymentResponse,
            finalUserId,
            UserUtils.time_slot_from,
            UserUtils.getUserId(this).toInt(),
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).CGST_amount,
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).SGST_amount,
            finalWalletBalance
        )
        Log.d("PAYMENT STATUS:", Gson().toJson(requestBody))
//        toast(this, "PAYMENT STATUS: " + Gson().toJson(requestBody))
        val bookingFactory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, bookingFactory)[BookingViewModel::class.java]
        viewModel.confirmPayment(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    showSuccessDialog()
                    Handler().postDelayed({
                        startActivity(Intent(this, UserDashboardScreen::class.java))
                    }, 3000)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.googlePayBtn, "Error03:" + it.message!!)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateInstallmentPaymentStatus(status: String, referenceId: String) {
        val requestBody = InstallmentPaymentReqModel(
            amount.toString(),
            ViewBidsScreen.bookingId,
            SimpleDateFormat("yyyy-MM-dd").format(Date()),
            UserUtils.getInstallmentDetId(this)!!.toInt(),
            RetrofitBuilder.USER_KEY,
            status,
            referenceId,
            UserUtils.getUserId(this).toInt(),
            ViewBidsScreen.bidId,
            ViewBidsScreen.spId
        )
        Log.d("INSTALLMENT PLAN:", Gson().toJson(requestBody))
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
                        snackBar(binding.googlePayBtn, "Payment Failed")
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
                    com.satrango.ui.user.bookings.payment_screen.models.Data(
                        upiList[index].upi,
                        upiList[index].upiAdded,
                        upiList[index].userId,
                        true
                    )
                )
            } else {
                tempList.add(
                    com.satrango.ui.user.bookings.payment_screen.models.Data(
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