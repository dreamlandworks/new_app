package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderInVoiceScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review.ProviderRatingReviewScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.payment_screen.PaymentScreen
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.CompleteBookingReqModel
import com.satrango.utils.Constants.close
import com.satrango.utils.Constants.completed
import com.satrango.utils.Constants.invalid_otp
import com.satrango.utils.Constants.otp_to_start_job
import com.satrango.utils.Constants.status
import com.satrango.utils.Constants.success_camil
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isFromCompleteBooking
import com.satrango.utils.UserUtils.isFromProviderBookingResponse
import com.satrango.utils.UserUtils.isFromProviderPlans
import com.satrango.utils.UserUtils.isFromUserBookingAddress
import com.satrango.utils.UserUtils.isFromUserPlans
import com.satrango.utils.UserUtils.isFromUserSetGoals
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.UserUtils.setFinalWalletBalance
import com.satrango.utils.UserUtils.setPayableAmount
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class ProviderInVoiceScreen : AppCompatActivity() {

    private var spFcmToken = ""
    private lateinit var toolBar: View
    private lateinit var binding: ActivityProviderInVoiceScreenBinding
    private lateinit var response: BookingDetailsResModel
    private lateinit var progressDialog: BeautifulProgressDialog
    private var categoryId = ""
    private var bookingId = ""
    private var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderInVoiceScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
        categoryId = intent.getStringExtra(getString(R.string.category_id))!!
        userId = intent.getStringExtra(getString(R.string.user_id))!!

        registerReceiver(myReceiver, IntentFilter(getString(R.string.OTP_INTENT_FILTER)));
        registerReceiver(myDoneReceiver, IntentFilter(getString(R.string.OTP_RESPONSE_INTENT_FILTER_DONE)));

        initializeProgressDialog()

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.toInt()
        )
        viewModel.viewBookingDetails(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.shimmerLayout.visibility = View.VISIBLE
                    binding.shimmerLayout.startShimmerAnimation()
                }
                is NetworkResponse.Success -> {
                    response = it.data!!
                    updateUI(response)

                }
                is NetworkResponse.Failure -> {
                    binding.shimmerLayout.visibility = View.GONE
                    binding.shimmerLayout.stopShimmerAnimation()
                    snackBar(binding.amount, it.message!!)
                }
            }
        }

        binding.backBtn.setOnClickListener { onBackPressed() }

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.userName.text = response.booking_details.fname + " " + response.booking_details.lname
        if (isProvider(this)) {
            Glide.with(this).load(response.booking_details.user_profile_pic).error(R.drawable.images).into(binding.profilePic)
        } else {
            Glide.with(this).load(response.booking_details.sp_profile_pic).error(R.drawable.images).into(binding.profilePic)
        }
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        binding.bookingIdText.text = bookingId
        spFcmToken = response.booking_details.sp_fcm_token

        val window: Window = window
        if (isProvider(this)) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        } else {
            toolBar.setBackgroundColor(resources.getColor(R.color.blue))
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.blue)
            binding.card.setCardBackgroundColor(resources.getColor(R.color.blue))
            binding.priceLayout.setBackgroundResource(R.drawable.blue_out_line)
            binding.bookingIdLayout.setBackgroundResource(R.drawable.blue_out_line)
            binding.dateTimeLayout.setBackgroundResource(R.drawable.blue_out_line)
            binding.timeLapsedLayout.setBackgroundResource(R.drawable.blue_out_line)
            binding.totalLayout.setBackgroundResource(R.drawable.blue_out_line)
            binding.lessAmountLayout.setBackgroundResource(R.drawable.blue_out_line)
            binding.backBtn.setBackgroundResource(R.drawable.blue_out_line)
            binding.backBtn.setTextColor(resources.getColor(R.color.blue))
            binding.totalDueLayout.setBackgroundResource(R.drawable.category_bg)
            binding.nextBtn.setBackgroundResource(R.drawable.category_bg)
            binding.occupation.text = response.booking_details.sp_profession
        }
        updateInvoice()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun updateInvoice() {

        val factory = ViewModelFactory(ProviderBookingRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]

        val requestBody = ProviderInvoiceReqModel(bookingId.toInt(), RetrofitBuilder.PROVIDER_KEY, UserUtils.isExtraDemandRaised(this))
        viewModel.getInvoice(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.shimmerLayout.visibility = View.VISIBLE
                    binding.shimmerLayout.startShimmerAnimation()
                }
                is NetworkResponse.Success -> {
                    val response = it.data!!
                    UserUtils.saveInVoiceDetails(this, Gson().toJson(response))
                    binding.apply {
                        workStartedAt.text = response.booking_details.started_at
                        workCompletedAt.text = response.booking_details.completed_at
                        if (isProvider(this@ProviderInVoiceScreen)) {
                            timeLapsedText.text = resources.getString(R.string.otp)
                            timeLapsedMins.text = response.booking_details.finish_OTP
                        } else {
                            timeLapsedMins.text = response.booking_details.time_lapsed
                        }
                        cgst.text = response.booking_details.cgst_tax
                        sgst.text = response.booking_details.sgst_tax
                        technicianCharges.text = response.booking_details.technician_charges
                        materialCharges.text = response.booking_details.expenditure_incurred
                        totalDues.text = response.booking_details.dues
                        netAmount.text = "${response.booking_details.final_dues}.00"
                        totalTimeLapsed.text = response.booking_details.time_lapsed
                        lessAmount.text = response.booking_details.paid
                        paidList.adapter = InvoiceListAdapter(response.booking_paid_transactions)
                        nextBtn.setOnClickListener {
                            if (!isProvider(this@ProviderInVoiceScreen)) {
                                otpDialog(response.booking_details.finish_OTP.toInt(), response.booking_details.booking_id.toString(), response.booking_details.final_dues, response)
                            } else {
                                showotpInDialog(response.booking_details.finish_OTP)
                            }
                        }
                    }
                    binding.shimmerLayout.visibility = View.GONE
                    binding.shimmerLayout.stopShimmerAnimation()
                }
                is NetworkResponse.Failure -> {
                    binding.shimmerLayout.visibility = View.GONE
                    binding.shimmerLayout.stopShimmerAnimation()
                    toast(this, "Extra Demand Response is Pending from User:${it.message}")
                    onBackPressed()
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    @SuppressLint("SetTextI18n")
    fun otpDialog(
        requestedOTP: Int,
        bookingId: String,
        finalDues: String,
        response: ProviderInvoiceResModel
    ) {

        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.booking_status_change_otp_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val firstNo = dialogView.findViewById<EditText>(R.id.firstNo)
        val secondNo = dialogView.findViewById<EditText>(R.id.secondNo)
        val thirdNo = dialogView.findViewById<EditText>(R.id.thirdNo)
        val fourthNo = dialogView.findViewById<EditText>(R.id.fourthNo)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)

        closeBtn.setOnClickListener { dialog.dismiss() }

        if (isProvider(this)) {
            title.text = otp_to_start_job
            title.setTextColor(resources.getColor(R.color.purple_500))
            firstNo.setBackgroundResource(R.drawable.purpleborderbutton)
            secondNo.setBackgroundResource(R.drawable.purpleborderbutton)
            thirdNo.setBackgroundResource(R.drawable.purpleborderbutton)
            fourthNo.setBackgroundResource(R.drawable.purpleborderbutton)
            submitBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        }

        firstNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    firstNo.clearFocus()
                    secondNo.requestFocus()
                }
            }

        })
        secondNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    secondNo.clearFocus()
                    thirdNo.requestFocus()
                }
            }

        })
        thirdNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    thirdNo.clearFocus()
                    fourthNo.requestFocus()
                }
            }

        })
        fourthNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    fourthNo.clearFocus()
                }
            }

        })

        submitBtn.setOnClickListener {

            if (firstNo.text.toString().trim().isEmpty()) {
                toast(this, invalid_otp)
            } else if (secondNo.text.toString().trim().isEmpty()) {
                toast(this, invalid_otp)
            } else if (thirdNo.text.toString().trim().isEmpty()) {
                toast(this, invalid_otp)
            } else if (fourthNo.text.toString().trim().isEmpty()) {
                toast(this, invalid_otp)
            } else {
                val otp = firstNo.text.toString().trim() + secondNo.text.toString()
                    .trim() + thirdNo.text.toString().trim() + fourthNo.text.toString().trim()
                if (requestedOTP == otp.toInt()) {
                    if (finalDues.toInt() > 0) {
                        UserUtils.saveSpId(binding.root.context, "0")
                        progressDialog.dismiss()
                        dialog.dismiss()
                        setPayableAmount(this, finalDues.toInt())
                        setFinalWalletBalance(this, response.booking_details.wallet_balance)
                        isFromUserPlans(this, false)
                        isFromProviderPlans(this, false)
                        isFromUserSetGoals(this, false)
                        isFromCompleteBooking(this, true)
                        isFromUserBookingAddress(this, false)
                        isFromProviderBookingResponse(this, false)
                        startActivity(Intent(this, PaymentScreen::class.java))
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                val requestBody = CompleteBookingReqModel(
                                    "0",
                                    response.booking_details.wallet_balance,
                                    bookingId,
                                    "0",
                                    response.booking_details.completed_at,
                                    RetrofitBuilder.USER_KEY,
                                    success_camil,
                                    "0",
                                    response.booking_details.sp_id,
                                    "0",
                                    UserUtils.getUserId(this@ProviderInVoiceScreen),
                                    "0",
                                    response.booking_details.expenditure_incurred
                                )
                                val response = RetrofitBuilder.getUserRetrofitInstance().completeBooking(requestBody)
                                if (JSONObject(response.string()).getInt(status) == 200) {
                                    UserUtils.sendOTPResponseFCM(this@ProviderInVoiceScreen, spFcmToken, "$bookingId|$categoryId|$userId|sp")
                                    divertToProviderRatingScreen()
                                } else {
                                    toast(this@ProviderInVoiceScreen,"Error:" + response.string())
                                }
                            } catch (e: Exception) {
                                toast(this@ProviderInVoiceScreen, "Error:" + e.message!!)
                            }
                        }
                    }
                } else {
                    toast(this, "Incorrect OTP")
                }
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
//            val bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
//            val otp = intent.getStringExtra(getString(R.string.category_id))!!
//            val userId = intent.getStringExtra(getString(R.string.user_id))!!
//            toast(context, "$bookingId|$otp|$userId")
//            if (!isProvider(this@ProviderInVoiceScreen)) {
//                if (!(context as Activity).isFinishing) {
//                    otpDialog(otp.toInt(), bookingId, response.booking_details.final_dues)
//                }
//            } else {
//                showotpInDialog(otp)
//            }
        }
    }

    private val myDoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
            val otp = intent.getStringExtra(getString(R.string.category_id))!!
            val userId = intent.getStringExtra(getString(R.string.user_id))!!
            ProviderRatingReviewScreen.bookingId = bookingId
            ProviderRatingReviewScreen.userId = userId
            ProviderRatingReviewScreen.categoryId =  "0"
            startActivity(Intent(this@ProviderInVoiceScreen, ProviderRatingReviewScreen::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showotpInDialog(otp: String) {
        binding.timeLapsedMins.text = otp
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.booking_closing_dialog, null)
        val title = bottomSheet.findViewById<TextView>(R.id.title)
        val firstNo = bottomSheet.findViewById<TextView>(R.id.firstNo)
        val secondNo = bottomSheet.findViewById<TextView>(R.id.secondNo)
        val thirdNo = bottomSheet.findViewById<TextView>(R.id.thirdNo)
        val fourthNo = bottomSheet.findViewById<TextView>(R.id.fourthNo)
        val submitBtn = bottomSheet.findViewById<TextView>(R.id.submitBtn)
        val closeBtn = bottomSheet.findViewById<MaterialCardView>(R.id.closeBtn)
        title.setTextColor(resources.getColor(R.color.purple_500))
        firstNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
        secondNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
        thirdNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
        fourthNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
        submitBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        firstNo.text = otp[0].toString()
        secondNo.text = otp[1].toString()
        thirdNo.text = otp[2].toString()
        fourthNo.text = otp[3].toString()
        submitBtn.text = close
        closeBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(
                        BookingDetailsReqModel(bookingId.toInt(), categoryId.toInt(), RetrofitBuilder.USER_KEY, userId.toInt())
                    )
                    if (response.status == 200) {
                        if (response.booking_details.booking_status == completed) {
                            divertToProviderRatingScreen()
                        } else {
                            toast(this@ProviderInVoiceScreen, "Please wait User have to pay the dues to close the booking")
                        }
                    }
                } catch (e: Exception) {
                    toast(this@ProviderInVoiceScreen, "BookingDetailsError01:${e.message}")
                }
            }
        }
        submitBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(
                        BookingDetailsReqModel(bookingId.toInt(), categoryId.toInt(), RetrofitBuilder.USER_KEY, userId.toInt())
                    )
                    if (response.status == 200) {
                        if (response.booking_details.booking_status == completed) {
                            divertToProviderRatingScreen()
                        } else {
                            toast(this@ProviderInVoiceScreen, "Please wait User have to pay the dues to close the booking")
                        }
                    }
                } catch (e: Exception) {
                    toast(this@ProviderInVoiceScreen, "BookingDetailsError02:${e.message}")
                }
            }
        }
        bottomSheetDialog.setContentView(bottomSheet)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
    }

    private fun divertToProviderRatingScreen() {
        ProviderRatingReviewScreen.bookingId = bookingId
        ProviderRatingReviewScreen.categoryId = categoryId
        ProviderRatingReviewScreen.userId = userId
        startActivity(Intent(this@ProviderInVoiceScreen, ProviderRatingReviewScreen::class.java))
    }

}