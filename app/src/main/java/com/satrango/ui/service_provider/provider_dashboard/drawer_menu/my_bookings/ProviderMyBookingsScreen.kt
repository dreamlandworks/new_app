package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderMyBookingsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingResumeReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderPauseBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.ProviderBookingDetailsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.ProviderInVoiceScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsViewModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProviderMyBookingsScreen : AppCompatActivity(), ProviderMyBookingInterface {

    private lateinit var myBookingViewModel: MyBookingsViewModel
    private lateinit var binding: ActivityProviderMyBookingsScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var viewModel: ProviderBookingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderMyBookingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.purple_700))
        }

        val factory = ViewModelFactory(ProviderBookingRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]

        val myBookingFactory = ViewModelFactory(MyBookingsRepository())
        myBookingViewModel = ViewModelProvider(this, myBookingFactory)[MyBookingsViewModel::class.java]

        initializeProgressDialog()

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.my_booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        binding.inProgressBtn.setOnClickListener {
            binding.inProgressBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.completedBtn.setBackgroundResource(0)
            binding.pendingBtn.setBackgroundResource(0)
            binding.inProgressBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.completedBtn.setTextColor(Color.parseColor("#000000"))
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            updateUI("InProgress")
        }
        binding.pendingBtn.setOnClickListener {
            updatePendingBtnUI()
            updateUI("Pending")
        }
        binding.completedBtn.setOnClickListener {
            binding.completedBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.inProgressBtn.setBackgroundResource(0)
            binding.pendingBtn.setBackgroundResource(0)
            binding.inProgressBtn.setTextColor(Color.parseColor("#000000"))
            binding.completedBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            updateUI("Completed")
        }
    }

    private fun updatePendingBtnUI() {
        binding.pendingBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        binding.completedBtn.setBackgroundResource(0)
        binding.inProgressBtn.setBackgroundResource(0)
        binding.inProgressBtn.setTextColor(Color.parseColor("#000000"))
        binding.completedBtn.setTextColor(Color.parseColor("#000000"))
        binding.pendingBtn.setTextColor(Color.parseColor("#FFFFFF"))
    }

    private fun updateUI(status: String) {
        val requestBody = ProviderBookingReqModel(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(this).toInt())
        viewModel.bookingListWithDetails(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val list = ArrayList<BookingDetail>()
                    for (details in it.data!!) {
                        if (status == "Completed") {
                            if (details.booking_status.equals(
                                    status,
                                    ignoreCase = true
                                ) || details.booking_status.equals(
                                    "Expired",
                                    ignoreCase = true
                                ) || details.booking_status.equals("Cancelled", ignoreCase = true)
                            ) {
                                list.add(details)
                            }
                        } else {
                            if (details.booking_status.equals(status, ignoreCase = true)) {
                                list.add(details)
                            }
                        }
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = ProviderMyBookingAdapter(list, status, this)
                    if (list.isEmpty()) {
                        binding.note.visibility = View.VISIBLE
                    } else {
                        binding.note.visibility = View.GONE
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        }
    }

    private fun finalExpenditureDialog(
        extraDemandAmount: String,
        bookingId: Int,
        categoryId: String,
        userId: String
    ) {
        val dialog = BottomSheetDialog(this)
        val dialogView =
            layoutInflater.inflate(R.layout.provider_final_extra_expenditure_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val raisedExtraDemand = dialogView.findViewById<TextView>(R.id.raiseExtraDemand)
        val finalExpenditure = dialogView.findViewById<EditText>(R.id.finalExpenditure)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)
        raisedExtraDemand.text = extraDemandAmount

        closeBtn.setOnClickListener { dialog.dismiss() }
        submitBtn.setOnClickListener {
            if (finalExpenditure.text.toString().isEmpty()) {
                toast(this, "Enter Expenditure Incurred")
            } else {
                val factory = ViewModelFactory(ProviderBookingRepository())
                val viewModel = ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
                val requestBody = ExpenditureIncurredReqModel(
                    bookingId,
                    finalExpenditure.text.toString().toInt(),
                    RetrofitBuilder.PROVIDER_KEY
                )
                viewModel.expenditureIncurred(this, requestBody).observe(this) {
                    when (it) {
                        is NetworkResponse.Loading -> {
                            progressDialog.show()
                        }
                        is NetworkResponse.Success -> {
                            progressDialog.dismiss()
                            dialog.dismiss()
                            ProviderInVoiceScreen.FROM_PROVIDER = true
                            val intent = Intent(this, ProviderInVoiceScreen::class.java)
                            intent.putExtra(
                                binding.root.context.getString(R.string.booking_id),
                                bookingId.toString()
                            )
                            intent.putExtra(
                                binding.root.context.getString(R.string.category_id),
                                categoryId
                            )
                            intent.putExtra(
                                binding.root.context.getString(R.string.user_id),
                                userId
                            )
                            startActivity(intent)
                        }
                        is NetworkResponse.Failure -> {
                            progressDialog.dismiss()
                            toast(this, it.message!!)
                        }
                    }
                }
            }
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun requestOTP(bookingId: Int, categoryId: String, userId: String, spId: String, userFcmToken: String, spFcmToken: String) {
        myBookingViewModel.otpRequest(this, bookingId, "SP")
            .observe(this) {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        val requestedOTP = it.data!!
                        toast(this, requestedOTP.toString())
                        UserUtils.sendOTPFCM(
                            this,
                            userFcmToken,
                            bookingId.toString(),
                            requestedOTP.toString()
                        )
                        otpDialog(requestedOTP, bookingId, categoryId, userId, spId, userFcmToken)
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.recyclerView, it.message!!)
                    }
                }
            }
    }

    override fun markComplete(extraDemand: String, bookingId: Int, categoryId: String, userId: String) {
        finalExpenditureDialog(extraDemand, bookingId, categoryId, userId)
    }

    @SuppressLint("SimpleDateFormat")
    override fun pauseBooking(bookingId: Int) {
        val requestBody = ProviderPauseBookingReqModel(
            bookingId,
            RetrofitBuilder.PROVIDER_KEY,
            SimpleDateFormat("yyyy-MM-dd hh:mm").format(Date()),
            UserUtils.getUserId(this).toInt()
        )
        myBookingViewModel.pauseBooking(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                    finish()
                    startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun resumeBooking(bookingId: Int) {
        val requestBody = ProviderBookingResumeReqModel(
            bookingId,
            RetrofitBuilder.PROVIDER_KEY,
            SimpleDateFormat("yyyy-MM-dd hh:mm").format(Date()),
            UserUtils.getUserId(this).toInt()
        )
        myBookingViewModel.resumeBooking(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                    finish()
                    startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun otpDialog(requestedOTP: Int, bookingId: Int, categoryId: String, userId: String, spId: String, userFcmToken: String) {

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
                val otp = firstNo.text.toString().trim() + secondNo.text.toString().trim() + thirdNo.text.toString().trim() + fourthNo.text.toString().trim()
                if (requestedOTP == otp.toInt()) {
                    val factory = ViewModelFactory(MyBookingsRepository())
                    val viewModel = ViewModelProvider(this, factory)[MyBookingsViewModel::class.java]
                    viewModel.validateOTP(this, bookingId, UserUtils.getUserId(this).toInt()).observe(this) {
                        when (it) {
                            is NetworkResponse.Loading -> {
                                progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                val intent = Intent(
                                    binding.root.context,
                                    ProviderBookingDetailsScreen::class.java
                                )
                                ProviderBookingDetailsScreen.bookingId = bookingId.toString()
                                ProviderBookingDetailsScreen.categoryId = categoryId
                                ProviderBookingDetailsScreen.userId = userId
                                ViewUserBookingDetailsScreen.FROM_PROVIDER = true
                                ViewUserBookingDetailsScreen.FROM_PENDING = false
                                UserUtils.spid = spId
                                startActivity(intent)
                                progressDialog.dismiss()
                                dialog.dismiss()
                                UserUtils.sendOTPResponseFCM(
                                    this,
                                    userFcmToken,
                                    "$bookingId|$categoryId|$userId|sp"
                                )
                                snackBar(binding.recyclerView, "Booking Started!")
                            }
                            is NetworkResponse.Failure -> {
                                progressDialog.dismiss()
                                toast(this, "Error:${it.message!!}")
                            }
                        }
                    }
                } else {
                    toast(this, "Invalid OTP")
                }
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        updatePendingBtnUI()
        updateUI("Pending")
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }
}