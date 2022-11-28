package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserPlanScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.bookings.payment_screen.PaymentScreen
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.UserMyAccountScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.Data
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar

class UserPlanScreen : AppCompatActivity(), UserPlanListener
//    , PaymentResultListener
{

    private lateinit var walletBalance: String
    private var paymentData: Data? = null
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityUserPlanScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPlanScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        viewModel.getUserPlans(this).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.recyclerView.adapter = UserPlanAdapter(
                        it.data!!,
                        it.data.data,
                        it.data.activated_plan.toInt(),
                        this
                    )
                    walletBalance = it.data.wallet_balance
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        }

    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.choose_your_plan)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE
    }

    override fun loadPayment(data: Data) {
        paymentData = data
        if (data.premium_tag == "Yes") {
//            Checkout.preload(applicationContext)
            UserUtils.isFromUserPlans(this, true)
            UserUtils.isFromProviderPlans(this, false)
            UserUtils.isFromUserSetGoals(this, false)
            UserUtils.isFromCompleteBooking(this, false)
            UserUtils.isFromUserBookingAddress(this, false)
            UserUtils.isFromProviderBookingResponse(this, false)
//            PaymentScreen.FROM_USER_PLANS = true
//            PaymentScreen.FROM_PROVIDER_PLANS = false
//            PaymentScreen.FROM_PROVIDER_BOOKING_RESPONSE = false
//            PaymentScreen.FROM_USER_BOOKING_ADDRESS = false
//            PaymentScreen.FROM_USER_SET_GOALS = false
            UserUtils.setPayableAmount(this, data.amount.toInt())
//            PaymentScreen.amount = data.amount.toInt()
            PaymentScreen.period = data.period.toInt()
            PaymentScreen.id = data.id.toInt()
            UserUtils.setFinalWalletBalance(this, walletBalance.toDouble().toInt().toString())
//            PaymentScreen.finalWalletBalance = walletBalance.toDouble().toInt().toString()
            startActivity(Intent(this, PaymentScreen::class.java))
//            makePayment()
        } else {
            showSuccessDialog()
        }
    }

//    private fun makePayment() {
//        val checkout = Checkout()
//        checkout.setKeyID(getString(R.string.razorpay_api_key))
//        try {
//            val orderRequest = JSONObject()
//            orderRequest.put("currency", "INR")
//            orderRequest.put("amount", paymentData!!.amount.toDouble() * 100) // 500rs * 100 = 50000 paisa passed
//            orderRequest.put("receipt", "order_rcptid_${System.currentTimeMillis()}")
//            orderRequest.put("image", "https://dev.satrango.com/public/assets/img/logo-black.png")
//            orderRequest.put("theme.color", R.color.blue)
//            checkout.open(this, orderRequest)
//        } catch (e: Exception) {
//            toast(this, e.message!!)
//        }
//    }

//    @SuppressLint("SimpleDateFormat")
//    override fun onPaymentSuccess(paymentId: String?) {
//        val factory = ViewModelFactory(PostJobRepository())
//        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
//        val requestBody = UserPlanPaymentReqModel(
//            paymentData!!.amount.toInt(),
//            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
//            RetrofitBuilder.USER_KEY,
//            paymentData!!.period.toInt(),
//            paymentData!!.id.toInt(),
//            UserUtils.getUserId(this).toInt(),
//
//        )
//
//        viewModel.saveUserPlanPayment(this, requestBody).observe(this) {
//            when (it) {
//                is NetworkResponse.Loading -> {
//                    progressDialog.show()
//                }
//                is NetworkResponse.Success -> {
//                    progressDialog.dismiss()
//                    showSuccessDialog()
//                }
//                is NetworkResponse.Failure -> {
//                    progressDialog.dismiss()
//                    snackBar(binding.recyclerView, it.message!!)
//                }
//            }
//        }
//    }

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

//    override fun onPaymentError(p0: Int, p1: String?) {
//        snackBar(binding.recyclerView, "Payment Failed")
//    }

    override fun onBackPressed() {
        if (UserMyAccountScreen.FROM_MY_ACCOUNT) {
            super.onBackPressed()
        } else {
            startActivity(Intent(this, UserDashboardScreen::class.java))
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

}