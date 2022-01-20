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
import android.util.Log
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderInVoiceScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMService
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review.ProviderRatingReviewScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsViewModel
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

class ProviderInVoiceScreen : AppCompatActivity() {

    private lateinit var toolBar: View
    private lateinit var binding: ActivityProviderInVoiceScreenBinding
    private lateinit var response: BookingDetailsResModel
    private lateinit var progressDialog: BeautifulProgressDialog
    private var categoryId = ""
    private var bookingId = ""
    private var userId = ""

    companion object {
        var FROM_PROVIDER = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderInVoiceScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
        categoryId = intent.getStringExtra(getString(R.string.category_id))!!
        userId = intent.getStringExtra(getString(R.string.user_id))!!

        registerReceiver(myReceiver, IntentFilter(FCMService.OTP_INTENT_FILTER));

        initializeProgressDialog()

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.toInt()
        )
        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
        viewModel.viewBookingDetails(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    response = it.data!!
                    updateUI(response)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.amount, it.message!!)
                }
            }
        })

        binding.backBtn.setOnClickListener { onBackPressed() }

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.userName.text =
            response.booking_details.fname + " " + response.booking_details.lname
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        binding.bookingIdText.text = bookingId

        if (FROM_PROVIDER) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = resources.getColor(R.color.purple_700)
            }
        } else {
            toolBar.setBackgroundColor(resources.getColor(R.color.blue))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = resources.getColor(R.color.blue)
            }
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
        }
        updateInvoice()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun updateInvoice() {

        val factory = ViewModelFactory(ProviderBookingRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]

        val requestBody = ProviderInvoiceReqModel(bookingId.toInt(), RetrofitBuilder.PROVIDER_KEY)
        viewModel.getInvoice(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val response = it.data!!
                    binding.apply {

                        workStartedAt.text = response.booking_details.started_at
                        workCompletedAt.text = response.booking_details.completed_at

                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-hh hh:mm:ss")

                        val date1 = simpleDateFormat.parse(response.booking_details.completed_at)
                        val date2 = simpleDateFormat.parse(response.booking_details.started_at)

                        val difference: Long = date2.time - date1.time
                        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
                        var hours =
                            ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
                        val min =
                            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
                        hours = if (hours < 0) -hours else hours
                        timeLapsedMins.text = "${min + (hours * 60)} Minutes"

                        technicianCharges.text = response.booking_details.technician_charges
                        materialCharges.text = response.booking_details.material_advance
                        totalDues.text = response.booking_details.extra_demand_total_amount
                        netAmount.text = response.booking_details.extra_demand_total_amount
                        totalTimeLapsed.text = "$hours:${min}:00 Hrs"
                        paidList.adapter = InvoiceListAdapter(response.booking_paid_transactions)
                        var lessAmountCount = 0.0
                        for (paid in response.booking_paid_transactions) {
                            lessAmountCount += paid.amount.toDouble()
                        }
                        lessAmount.text = lessAmountCount.toString()
                        nextBtn.setOnClickListener {
                            requestOTP("User")
//                            if (FROM_PROVIDER) {
//                                requestOTP("SP")
//                            } else {
//
//                            }

//                            if (!FROM_PROVIDER) {
//                                requestOTP("User")
//                            } else {
//                                ProviderRatingReviewScreen.FROM_PROVIDER = true
//                                val intent = Intent(this@ProviderInVoiceScreen, ProviderRatingReviewScreen::class.java)
//                                intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
//                                intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
//                                intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
//                                startActivity(intent)
//                            }
                        }
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.amount, it.message!!)
                }
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun requestOTP(userType: String) {
//        val factory = ViewModelFactory(MyBookingsRepository())
//        val viewModel = ViewModelProvider(this, factory)[MyBookingsViewModel::class.java]
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitBuilder.getUserRetrofitInstance().getBookingStatusOTP(RetrofitBuilder.USER_KEY, bookingId.toInt(), userType)
                val jsonResponse = JSONObject(response.string())
                if (jsonResponse.getInt("status") == 200) {
                    val requestedOTP = jsonResponse.getInt("otp")
                    toast(this@ProviderInVoiceScreen, requestedOTP.toString())
                    UserUtils.sendOTPFCM(this@ProviderInVoiceScreen, ViewUserBookingDetailsScreen.FCM_TOKEN, bookingId, requestedOTP.toString())
                    if (!FROM_PROVIDER) {
                        otpDialog(requestedOTP, bookingId)
                    } else {
                        showotpInDialog(requestedOTP.toString())
                    }
                } else {
                    snackBar(binding.amount, jsonResponse.getString("message"))
                }
            } catch (e: Exception) {
                snackBar(binding.amount, e.message!!)
            }
        }
//        viewModel.otpRequest(this, bookingId.toInt(), userType)
//            .observe(this, {
//                when (it) {
//                    is NetworkResponse.Loading -> {
//                        progressDialog.show()
//                    }
//                    is NetworkResponse.Success -> {
//                        progressDialog.dismiss()
//                        val requestedOTP = it.data!!
//                        toast(this, requestedOTP.toString())
//                        UserUtils.sendOTPFCM(this, ViewUserBookingDetailsScreen.FCM_TOKEN, bookingId, requestedOTP.toString())
//                        if (!FROM_PROVIDER) {
//                            otpDialog(requestedOTP, bookingId)
//                        } else {
//                            showotpInDialog(requestedOTP.toString())
//                        }
//                    }
//                    is NetworkResponse.Failure -> {
//                        progressDialog.dismiss()
//                        snackBar(binding.amount, it.message!!)
//                    }
//                }
//            })
    }

    @SuppressLint("SetTextI18n")
    fun otpDialog(requestedOTP: Int, bookingId: String) {

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

        if (ViewUserBookingDetailsScreen.FROM_PROVIDER) {
            title.text = "OTP to Start Job"
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
                snackBar(binding.amount, "Invalid OTP")
            } else if (secondNo.text.toString().trim().isEmpty()) {
                snackBar(binding.amount, "Invalid OTP")
            } else if (thirdNo.text.toString().trim().isEmpty()) {
                snackBar(binding.amount, "Invalid OTP")
            } else if (fourthNo.text.toString().trim().isEmpty()) {
                snackBar(binding.amount, "Invalid OTP")
            } else {
                val otp = firstNo.text.toString().trim() + secondNo.text.toString()
                    .trim() + thirdNo.text.toString().trim() + fourthNo.text.toString().trim()
                if (requestedOTP == otp.toInt()) {
                    UserUtils.spid = "0"
                    val factory = ViewModelFactory(MyBookingsRepository())
                    val viewModel =
                        ViewModelProvider(this, factory)[MyBookingsViewModel::class.java]
                    viewModel.validateOTP(this, bookingId.toInt(), UserUtils.spid.toInt())
                        .observe(this, {
                            when (it) {
                                is NetworkResponse.Loading -> {
                                    progressDialog.show()
                                }
                                is NetworkResponse.Success -> {
                                    progressDialog.dismiss()
                                    dialog.dismiss()
                                    FROM_PROVIDER = false
                                    dialog.dismiss()
                                    showBookingCompletedSuccessDialog()
//                                    val intent = Intent(this, ProviderInVoiceScreen::class.java)
//                                    intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
//                                    intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
//                                    intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
//                                    startActivity(intent)
                                }
                                is NetworkResponse.Failure -> {
                                    progressDialog.dismiss()
                                    snackBar(binding.amount, it.message!!)
                                }
                            }
                        })
                } else {
                    snackBar(binding.amount, "Invalid OTP")
                }
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showBookingCompletedSuccessDialog() {
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
            ProviderRatingReviewScreen.FROM_PROVIDER = false
            val intent = Intent(this, ProviderRatingReviewScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
            startActivity(intent)
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
            val otp = intent.getStringExtra(getString(R.string.category_id))!!
            val userId = intent.getStringExtra(getString(R.string.user_id))!!
            toast(context, "$bookingId|$otp|$userId")
            if (!FROM_PROVIDER) {
                otpDialog(otp.toInt(), bookingId)
            } else {
                showotpInDialog(otp)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showotpInDialog(otp: String) {
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
        submitBtn.text = "Close"
        closeBtn.setOnClickListener {
            ProviderRatingReviewScreen.FROM_PROVIDER = true
            val intent = Intent(this@ProviderInVoiceScreen, ProviderRatingReviewScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
            startActivity(intent)
        }
        submitBtn.setOnClickListener {
            ProviderRatingReviewScreen.FROM_PROVIDER = true
            val intent = Intent(this@ProviderInVoiceScreen, ProviderRatingReviewScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
            startActivity(intent)
        }
        bottomSheetDialog.setContentView(bottomSheet)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
    }

}