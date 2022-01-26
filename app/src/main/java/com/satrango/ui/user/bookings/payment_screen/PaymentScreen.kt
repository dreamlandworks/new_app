package com.satrango.ui.user.bookings.payment_screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
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
import kotlin.math.round

class PaymentScreen : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityPaymentScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        var id = 0
        var period = 0
        var amount = 0.0
        var userId = 0
        var FROM_USER_PLANS = false
        var FROM_PROVIDER_PLANS = false
        var FROM_USER_SET_GOALS = false
        var FROM_USER_BOOKING_ADDRESS = false
        var FROM_COMPLETE_BOOKING = false
        var FROM_PROVIDER_BOOKING_RESPONSE = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgressDialog()
        initializeToolbar()

        binding.apply {

            googlePayBtn.setOnClickListener {
                when {
                    FROM_PROVIDER_PLANS -> {
//                        startActivity(Intent(this@PaymentScreen, ProviderDashboard::class.java))
                        saveProviderPlan("paymentId")
                    }
                    FROM_USER_PLANS -> {
//                        startActivity(Intent(this@PaymentScreen, UserDashboardScreen::class.java))
                        saveUserPlan("paymentId")
                    }
                    FROM_USER_BOOKING_ADDRESS -> {
//                        startActivity(Intent(this@PaymentScreen, UserDashboardScreen::class.java))
                        updateStatusInServer("paymentId", "Success")
                    }
                    FROM_PROVIDER_BOOKING_RESPONSE -> {
//                        startActivity(Intent(this@PaymentScreen, UserDashboardScreen::class.java))
                        updateStatusInServer("paymentId", "Success")
                    }
                    FROM_USER_SET_GOALS -> {
//                        startActivity(Intent(this@PaymentScreen, UserDashboardScreen::class.java))
                        updateInstallmentPaymentStatus("Success", "paymentId")
                    }
                    FROM_COMPLETE_BOOKING -> {
//                        startActivity(Intent(this@PaymentScreen, UserDashboardScreen::class.java))
                        completeBooking("Success", "paymentId")
                    }
                }
            }

            otherPaymentBtn.setOnClickListener {
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
//                        startActivity(Intent(this@PaymentScreen, UserDashboardScreen::class.java))
                        completeBooking("Success", "paymentId")
                    }
                }
//                makePayment()
            }

        }
    }

    private fun completeBooking(status: String, referenceId: String) {

        val inVoiceDetails = Gson().fromJson(UserUtils.getInvoiceDetails(this), ProviderInvoiceResModel::class.java)
        Log.e("INVOICE:", Gson().toJson(inVoiceDetails))
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = CompleteBookingReqModel(
                    inVoiceDetails.booking_details.extra_demand_total_amount,
                    inVoiceDetails.booking_details.booking_id.toString(),
                    inVoiceDetails.booking_details.cgst_tax,
                    inVoiceDetails.booking_details.completed_at,
                    RetrofitBuilder.USER_KEY,
                    "1",
                    status,
                    referenceId,
                    inVoiceDetails.booking_details.sgst_tax,
                    inVoiceDetails.booking_details.sp_id,
                    inVoiceDetails.booking_details.extra_demand_total_amount,
                    UserUtils.getUserId(this@PaymentScreen)
                )
//                Log.e("COMPLETE BOOKING:", Gson().toJson(requestBody))
                toast(this@PaymentScreen, Gson().toJson(requestBody))
                val response = RetrofitBuilder.getUserRetrofitInstance().completeBooking(requestBody)
                if (JSONObject(response.string()).getInt("status") == 200) {
                    showBookingCompletedSuccessDialog(inVoiceDetails.booking_details.booking_id)
                } else {
                    toast(this@PaymentScreen,"Error:" + response.string())
                }
//                toast(this@PaymentScreen, response.string())
            } catch (e: Exception) {
                toast(this@PaymentScreen, "Error1:" + e.message!!)
            }
        }
    }

    private fun initializeToolbar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val window: Window = window
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.statusBarColor = resources.getColor(R.color.purple_700)
//        }

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn)
            .setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
            .setOnClickListener { onBackPressed() }
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
            round(Gson().fromJson(UserUtils.getSelectedSPDetails(this), com.satrango.ui.user.user_dashboard.search_service_providers.models.Data::class.java).final_amount.toDouble()).toInt()
        }else {
            amount.toInt()
        }
        val requestBody = UserPlanPaymentReqModel(
            finalAmount,
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            RetrofitBuilder.USER_KEY,
            "Success",
            period,
            id,
            paymentId!!,
            UserUtils.data!!.users_id.toInt()
        )
        Log.d("USER PLAN:", Gson().toJson(requestBody))
        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        viewModel.saveUserPlanPayment(this, requestBody).observe(this, {
            when(it) {
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
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveProviderPlan(paymentId: String?) {
        val factory = ViewModelFactory(ProviderPlansRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderPlansViewModel::class.java]
        val finalAmount = if (UserUtils.data != null) {
            round(UserUtils.data!!.final_amount.toDouble()).toInt()
        }else {
            amount.toInt()
        }
        val requestBody = ProviderMemberShipPlanPaymentReqModel(
            finalAmount,
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            RetrofitBuilder.USER_KEY,
            "Success",
            period,
            id,
            paymentId!!,
            UserUtils.data!!.users_id.toInt()
        )
        Log.d("SP PLAN:", Gson().toJson(requestBody))
        viewModel.saveMemberShip(this, requestBody).observe(this, {
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
        })
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
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
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
        val finalAmount = if (UserUtils.data != null) {
            round(UserUtils.data!!.final_amount.toDouble()).toInt()
        } else {
            amount.toInt()
        }
        val finalUserId = if (UserUtils.data != null) {
            UserUtils.data!!.users_id.toInt()
        } else {
            userId
        }
        val requestBody = PaymentConfirmReqModel(
            finalAmount.toString(),
            UserUtils.getBookingId(this),
            UserUtils.scheduled_date,
            RetrofitBuilder.USER_KEY,
            status,
            paymentResponse!!,
            finalUserId,
            UserUtils.time_slot_from,
            UserUtils.getUserId(this).toInt(),
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).CGST_amount,
            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).SGST_amount,
            Gson().fromJson(UserUtils.getSelectedAllSPDetails(this), SearchServiceProviderResModel::class.java).wallet_balance
        )
        Log.d("PAYMENT STATUS:", Gson().toJson(requestBody))
//        toast(this, Gson().toJson(requestBody))
        val bookingFactory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, bookingFactory)[BookingViewModel::class.java]
        viewModel.confirmPayment(this, requestBody).observe(this, {
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
                    snackBar(binding.googlePayBtn, "Error03:" +it.message!!)
                }
            }
        })
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
        viewModel.installmentPayments(this, requestBody).observe(this, {
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
        })
    }

    private fun paymentSuccessDialog(context: Context) {
        val dialog = BottomSheetDialog(context)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.payment_success_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showBookingCompletedSuccessDialog(bookingId: Int) {
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
            ProviderRatingReviewScreen.FROM_PROVIDER =  false
            ProviderRatingReviewScreen.bookingId = bookingId.toString()
            ProviderRatingReviewScreen.userId = userId.toString()
            ProviderRatingReviewScreen.categoryId =  "0"
            startActivity(Intent(this, ProviderRatingReviewScreen::class.java))
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }
}