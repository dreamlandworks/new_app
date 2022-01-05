package com.satrango.ui.user.bookings.view_booking_details

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserMyBookingDetailsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.GetBookingStatusListAdapter
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ChangeExtraDemandStatusReqModel
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.UserMyBookingsScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class UserMyBookingDetailsScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserMyBookingDetailsScreenBinding
    private lateinit var response: BookingDetailsResModel

    private lateinit var progressDialog: BeautifulProgressDialog
    private var userId = ""
    private var categoryId = ""
    private var bookingId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMyBookingDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

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
                    snackBar(binding.viewDetailsBtn, it.message!!)
                }
            }
        })
        viewModel.getBookingStatusList(this, bookingId.toInt()).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.recyclerView.adapter = GetBookingStatusListAdapter(it.data!!.booking_status_details)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.userName.text =
            response.booking_details.fname + " " + response.booking_details.lname
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        binding.otp.text = response.booking_details.otp
        binding.bookingIdText.text = bookingId

        binding.viewDetailsBtn.setOnClickListener {
            ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
            val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
            ViewUserBookingDetailsScreen.FROM_PROVIDER = true
            binding.root.context.startActivity(intent)
        }

        binding.viewDetailsBtn.setOnClickListener {
            ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
            ViewUserBookingDetailsScreen.FROM_PENDING = false
            ViewUserBookingDetailsScreen.FROM_PROVIDER = false
            val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), UserUtils.getUserId(binding.root.context))
            binding.root.context.startActivity(intent)
        }

        binding.markCompleteBtn.setOnClickListener {
            if (ViewUserBookingDetailsScreen.FROM_PROVIDER) {
                requestOTP("SP")
            } else {
                requestOTP("User")
            }
        }

        binding.callBtn.setOnClickListener {
            UserUtils.makePhoneCall(this, response.booking_details.mobile.replace(" ", "").takeLast(10))
        }

        binding.messageBtn.setOnClickListener {
            UserUtils.makeMessage(this, response.booking_details.mobile.replace(" ", "").takeLast(10))
        }

        if (categoryId == "2") {
            binding.viewFilesBtn.visibility = View.VISIBLE
        } else {
            binding.viewFilesBtn.visibility = View.GONE
        }


        if (response.booking_details.post_job_id != "0") {
            binding.messageBtn.visibility = View.GONE
        }
        if (!response.booking_details.extra_demand_status.isNullOrBlank()) {
            if (response.booking_details.extra_demand_total_amount != 0.0) {
                if (response.booking_details.extra_demand_status == "0") {
                    showExtraDemandAcceptDialog(
                        bookingId.toInt(),
                        response.booking_details.material_advance.toString(),
                        response.booking_details.technician_charges.toString(),
                        response.booking_details.extra_demand_total_amount.toString(),
                        progressDialog
                    )
                }
            }
        }
    }

    private fun showExtraDemandAcceptDialog(
        bookingId: Int,
        materialAdvance: String,
        technicalCharges: String,
        extraDemandTotalAmount: String,
        progressDialog: BeautifulProgressDialog
    ) {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_extra_demand_accept_dialog, null)
        val materialCharges = dialogView.findViewById<TextView>(R.id.materialCharges)
        val technicianCharges = dialogView.findViewById<TextView>(R.id.technicianCharges)
        val totalCost = dialogView.findViewById<TextView>(R.id.totalCost)
        val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)

//        materialCharges.text = response.booking_details.material_advance.toString()
//        technicianCharges.text = response.booking_details.technician_charges.toString()
//        totalCost.text = response.booking_details.extra_demand_total_amount.toString()

        materialCharges.text = materialAdvance
        technicianCharges.text = technicalCharges
        totalCost.text = extraDemandTotalAmount

        closeBtn.setOnClickListener { dialog.dismiss() }

        acceptBtn.setOnClickListener {
            changeExtraDemandStatus(bookingId, 1, dialog, progressDialog)
        }

        rejectBtn.setOnClickListener {
            changeExtraDemandStatus(bookingId, 2, dialog, progressDialog)
        }

        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun requestOTP(userType: String) {
        val factory = ViewModelFactory(MyBookingsRepository())
        val viewModel = ViewModelProvider(this, factory)[MyBookingsViewModel::class.java]
        viewModel.otpRequest(this, bookingId.toInt(), userType)
            .observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        val requestedOTP = it.data!!
                        toast(this, requestedOTP.toString())
                        otpDialog(requestedOTP, bookingId, userType)
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.recyclerView, it.message!!)
                    }
                }
            })
    }

    @SuppressLint("SetTextI18n")
    fun otpDialog(requestedOTP: Int, bookingId: String, userType: String) {

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
                snackBar(binding.recyclerView, "Invalid OTP")
            } else if (secondNo.text.toString().trim().isEmpty()) {
                snackBar(binding.recyclerView, "Invalid OTP")
            } else if (thirdNo.text.toString().trim().isEmpty()) {
                snackBar(binding.recyclerView, "Invalid OTP")
            } else if (fourthNo.text.toString().trim().isEmpty()) {
                snackBar(binding.recyclerView, "Invalid OTP")
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
                                    if (userType == "User") {
                                        startActivity(Intent(this, UserMyBookingsScreen::class.java))
                                    } else {
                                        startActivity(intent)
                                    }
                                }
                                is NetworkResponse.Failure -> {
                                    progressDialog.dismiss()
                                    snackBar(binding.recyclerView, it.message!!)
                                }
                            }
                        })
                } else {
                    snackBar(binding.recyclerView, "Invalid OTP")
                }
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun changeExtraDemandStatus(
        bookingId: Int,
        status: Int,
        dialog: BottomSheetDialog,
        progressDialog: BeautifulProgressDialog
    ) {

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        val requestBody =
            ChangeExtraDemandStatusReqModel(bookingId, RetrofitBuilder.USER_KEY, status)
        viewModel.changeExtraDemandStatus(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    dialog.dismiss()
                    if (status == 1) {
                        snackBar(binding.recyclerView, "Extra Demand Accepted")
                    } else {
                        snackBar(binding.recyclerView, "Extra Demand Rejected")
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(this, it.message!!)
                }
            }
        })

    }

}