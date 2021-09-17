package com.satrango.ui.user.bookings.view_booking_details

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityViewUserBookingDetailsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.cancel_booking.UserBookingCancelScreen
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.ProviderResponseReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class ViewUserBookingDetailsScreen : AppCompatActivity() {

    private var requestedOTP = 0
    private var userId = ""
    private var categoryId = ""
    private var bookingId = ""
    private lateinit var response: BookingDetailsResModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var binding: ActivityViewUserBookingDetailsScreenBinding

    companion object {
        var RESCHEDULE = false
        var FROM_MY_BOOKINGS_SCREEN = false
        var FROM_PROVIDER = false
        var FROM_PENDING = false
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewUserBookingDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        if (FROM_MY_BOOKINGS_SCREEN) {
            toolBar.setBackgroundColor(Color.parseColor("#0A84FF"))
            binding.providerBtnsLayout.visibility = View.GONE
            binding.userBtnsLayout.visibility = View.VISIBLE
            if (FROM_PENDING) {
                binding.pendingLayout.visibility = View.VISIBLE
                binding.inProgressLayout.visibility = View.GONE
            } else {
                binding.inProgressLayout.visibility = View.VISIBLE
                binding.pendingLayout.visibility = View.GONE
            }
        } else {
            binding.providerBtnsLayout.visibility = View.VISIBLE
        }
        if (FROM_PROVIDER) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            binding.card.setCardBackgroundColor(resources.getColor(R.color.purple_500))
            binding.reScheduleBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.reScheduleBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.startBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.cancelBookingBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.cancelBookingBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.layoutOne.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutTwo.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutThree.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutFour.setBackgroundResource(R.drawable.purple_out_line)
            binding.startBtn.text = "Start"
            binding.startBtn.setOnClickListener {
                requestOTP()
            }
        }

        bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
        categoryId = intent.getStringExtra(getString(R.string.category_id))!!
        userId = intent.getStringExtra(getString(R.string.user_id))!!

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
                    snackBar(binding.acceptBtn, it.message!!)
                }
            }
        })
        binding.apply {

            acceptBtn.setOnClickListener {
                val requestBody = ProviderResponseReqModel(
                    response.booking_details.amount,
                    bookingId.toInt(),
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                    "",
                    RetrofitBuilder.USER_KEY,
                    response.booking_details.sp_id.toInt(),
                    5
                )
                viewModel.setProviderResponse(this@ViewUserBookingDetailsScreen, requestBody)
                    .observe(this@ViewUserBookingDetailsScreen, {
                        when (it) {
                            is NetworkResponse.Loading -> {
                                progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                progressDialog.dismiss()
                                Log.e("AMOUNT", response.booking_details.amount)
                                UserUtils.sendFCM(
                                    this@ViewUserBookingDetailsScreen,
                                    response.booking_details.fcm_token,
                                    "accept",
                                    "accept|" + response.booking_details.amount + "|${response.booking_details.sp_id}|provider"
                                )
                            }
                            is NetworkResponse.Failure -> {
                                progressDialog.dismiss()
                                snackBar(binding.acceptBtn, it.message!!)
                            }
                        }
                    })
            }

            rejectBtn.setOnClickListener {
                val requestBody = ProviderResponseReqModel(
                    response.booking_details.amount,
                    bookingId.toInt(),
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                    "",
                    RetrofitBuilder.USER_KEY,
                    response.booking_details.sp_id.toInt(),
                    5
                )
                viewModel.setProviderResponse(this@ViewUserBookingDetailsScreen, requestBody)
                    .observe(this@ViewUserBookingDetailsScreen, {
                        when (it) {
                            is NetworkResponse.Loading -> {
                                progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                progressDialog.dismiss()
                                UserUtils.sendFCM(
                                    this@ViewUserBookingDetailsScreen,
                                    response.booking_details.fcm_token,
                                    "reject",
                                    "reject|" + response.booking_details.amount + "|${response.booking_details.sp_id} + |provider"
                                )
                            }
                            is NetworkResponse.Failure -> {
                                progressDialog.dismiss()
                                snackBar(binding.acceptBtn, it.message!!)
                            }
                        }
                    })
            }

            completedBtn.setOnClickListener {
                finalExpenditureDialog()
            }

        }

    }

    private fun finalExpenditureDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_final_extra_expenditure_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val raisedExtraDemand = dialogView.findViewById<TextView>(R.id.raiseExtraDemand)
        val finalExpenditure = dialogView.findViewById<EditText>(R.id.finalExpenditure)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)
        raisedExtraDemand.text = response.booking_details.extra_demand_total_amount.toString()

        closeBtn.setOnClickListener { dialog.dismiss() }
        submitBtn.setOnClickListener {
            if (finalExpenditure.text.toString().isEmpty()) {
                toast(this, "Enter Expenditure Incurred")
            } else {
                val factory = ViewModelFactory(ProviderBookingRepository())
                val viewModel = ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
                val requestBody = ExpenditureIncurredReqModel(bookingId.toInt(), finalExpenditure.text.toString().toInt(), RetrofitBuilder.PROVIDER_KEY)
                viewModel.expenditureIncurred(this, requestBody).observe(this, {
                    when(it) {
                        is NetworkResponse.Loading -> {
                            progressDialog.show()
                        }
                        is NetworkResponse.Success -> {
                            progressDialog.dismiss()
                            dialog.dismiss()
                        }
                        is NetworkResponse.Failure -> {
                            progressDialog.dismiss()
                            toast(this, it.message!!)
                        }
                    }
                })
            }
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun requestOTP() {
        val factory = ViewModelFactory(MyBookingsRepository())
        val viewModel = ViewModelProvider(this, factory)[MyBookingsViewModel::class.java]
        viewModel.otpRequest(this, bookingId.toInt(), UserUtils.spid.toInt())
            .observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        requestedOTP = it.data!!
                        toast(this, requestedOTP.toString())
                        otpDialog()
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.acceptBtn, it.message!!)
                    }
                }
            })
    }

    @SuppressLint("SetTextI18n")
    fun otpDialog() {

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

        if (FROM_PROVIDER) {
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
                snackBar(binding.acceptBtn, "Invalid OTP")
            } else if (secondNo.text.toString().trim().isEmpty()) {
                snackBar(binding.acceptBtn, "Invalid OTP")
            } else if (thirdNo.text.toString().trim().isEmpty()) {
                snackBar(binding.acceptBtn, "Invalid OTP")
            } else if (fourthNo.text.toString().trim().isEmpty()) {
                snackBar(binding.acceptBtn, "Invalid OTP")
            } else {
                val otp = firstNo.text.toString().trim() + secondNo.text.toString()
                    .trim() + thirdNo.text.toString().trim() + fourthNo.text.toString().trim()
                if (requestedOTP == otp.toInt()) {
                    if (FROM_PROVIDER) {
                        snackBar(binding.acceptBtn, "OTP Verification Success")
                        binding.startBtn.visibility = View.GONE
                        onBackPressed()
                    } else {
                        successDialog()
                    }
                    dialog.dismiss()
                } else {
                    snackBar(binding.acceptBtn, "Invalid OTP")
                }
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun successDialog() {

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.userName.text =
            response.booking_details.fname + " " + response.booking_details.lname
        binding.mobileNo.text = response.booking_details.mobile
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        binding.bookingIdText.text = bookingId

        binding.jobDetailsRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.attachmentsRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        if (response.job_details.isEmpty()) {
            binding.jobDetailsText.visibility = View.GONE
        }
        if (response.attachments.isEmpty()) {
            binding.attachmentsText.visibility = View.GONE
        }
        binding.jobDetailsRV.adapter = JobDetailsAdapter(response.job_details)
        binding.attachmentsRV.adapter = JobDetailsAttachmentsAdapter(response.attachments)

        binding.cancelBookingBtn.setOnClickListener {
            if (FROM_PROVIDER) {
                UserBookingCancelScreen.FROM_PROVIDER = true
            }
            val intent = Intent(binding.root.context, UserBookingCancelScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
            binding.root.context.startActivity(intent)
        }
        binding.reScheduleBtn.setOnClickListener {
            RESCHEDULE = true
            ViewBidsScreen.bookingId = bookingId.toInt()
            BookingDateAndTimeScreen.FROM_PROVIDER = FROM_PROVIDER
            UserUtils.spid = response.booking_details.sp_id
            UserUtils.re_scheduled_date = response.booking_details.scheduled_date
            UserUtils.re_scheduled_time_slot_from = response.booking_details.time_slot_id
            startActivity(Intent(this, BookingDateAndTimeScreen::class.java))
        }

        binding.raiseExtraDemandBtn.setOnClickListener {
            showExtraDemandDialog()
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun showExtraDemandDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_extra_demand_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val materialCharges = dialogView.findViewById<TextInputEditText>(R.id.materialCharges)
        val technicianCharges = dialogView.findViewById<TextInputEditText>(R.id.technicianCharges)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)
        val totalCost = dialogView.findViewById<TextView>(R.id.totalCost)

        materialCharges.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (materialCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString().isNotEmpty() && materialCharges.text.toString().isNotEmpty()) {
                    totalCost.text = (materialCharges.text.toString().toInt() + technicianCharges.text.toString().toInt()).toString()
                }
            }

        })
        technicianCharges.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (materialCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString().isNotEmpty() && materialCharges.text.toString().isNotEmpty()) {
                    totalCost.text = (materialCharges.text.toString().toInt() + technicianCharges.text.toString().toInt()).toString()
                }
            }

        })

        submitBtn.setOnClickListener {

            when {
                materialCharges.text.toString().isEmpty() && technicianCharges.text.toString().isEmpty() -> {
                    toast(this, "Enter Material Charges or Technician Charges")
                }
                else -> {

                    val mCharges = materialCharges.text.toString()
                    val tCharges = technicianCharges.text.toString()

                    val factory = ViewModelFactory(ProviderBookingRepository())
                    val viewModel = ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
                    val requestBody = ExtraDemandReqModel(bookingId, SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format((Date())), totalCost.text.toString().trim(), mCharges, tCharges, RetrofitBuilder.PROVIDER_KEY)
                    viewModel.extraDemand(this, requestBody).observe(this, {
                        when(it) {
                            is NetworkResponse.Loading -> {
                                progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                progressDialog.dismiss()
                                dialog.dismiss()
                                toast(this, "Extra Demand Raised")
                            }
                            is NetworkResponse.Failure -> {
                                progressDialog.dismiss()
                                toast(this, it.message!!)
                            }
                        }
                    })

                }
            }

        }
        closeBtn.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(dialogView)
        dialog.show()
    }

}