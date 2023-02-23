package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details

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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderBookingDetailsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderMyBookingsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingResumeReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderPauseBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.ProviderInVoiceScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.ProviderReleaseGoalsScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_attachments.ViewFilesScreen
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.UserMyBookingsScreen
import com.satrango.utils.Constants.enter_expenditure_incurred
import com.satrango.utils.Constants.enter_material_charges_alert
import com.satrango.utils.Constants.extra_demand_accepted
import com.satrango.utils.Constants.extra_demand_raised
import com.satrango.utils.Constants.extra_demand_rejected
import com.satrango.utils.Constants.extra_demand_waiting_alert
import com.satrango.utils.Constants.final_expenditure_alert
import com.satrango.utils.Constants.message
import com.satrango.utils.Constants.pause
import com.satrango.utils.Constants.resume
import com.satrango.utils.Constants.status
import com.satrango.utils.Constants.yes_camil
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.currentDateTime
import com.satrango.utils.UserUtils.isCompleted
import com.satrango.utils.UserUtils.isProgress
import com.satrango.utils.UserUtils.isProvider
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

class ProviderBookingDetailsScreen : AppCompatActivity() {

    private lateinit var viewModel: BookingViewModel
    private lateinit var binding: ActivityProviderBookingDetailsScreenBinding
    private lateinit var response: BookingDetailsResModel
    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        var userId = ""
        var categoryId = ""
        var bookingId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderBookingDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgressDialog()

        registerReceiver(myReceiver, IntentFilter(getString(R.string.EXTRA_DEMAND_ACCEPT_REJECT)));

        val factory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.purple_700)

        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.toInt()
        )
//        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
//        toast(this, Gson().toJson(requestBody))
        viewModel.viewBookingDetails(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    response = it.data!!
//                    toast(this, Gson().toJson(response))
                    updateUI(response)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
//                    toast(this, it.message!!)
                    snackBar(binding.viewDetailsBtn, it.message!!)
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.userName.text = response.booking_details.fname + " " + response.booking_details.lname
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        Glide.with(this).load(response.booking_details.user_profile_pic)
            .error(R.drawable.images).into(binding.profilePic)
        binding.otpText.text = resources.getString(R.string.time_lapsed)
        binding.otp.text = response.booking_details.time_lapsed

        binding.bookingIdText.text = bookingId

        if (isCompleted(this)) {
            binding.markCompleteBtn.visibility = View.GONE
            binding.secondLayout.visibility = View.GONE
            binding.viewDetailsBtn.isClickable = true
            binding.viewDetailsBtn.setOnClickListener {
                super.onBackPressed()
            }
            binding.callBtn.setCardBackgroundColor(resources.getColor(R.color.gray))
            binding.messageBtn.setCardBackgroundColor(resources.getColor(R.color.gray))
            binding.callBtn.isClickable = false
            binding.messageBtn.isClickable = false
        }
        if (isProgress(this)) {
            binding.viewDetailsBtn.setOnClickListener {
                ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
                intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
                intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
                intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
                binding.root.context.startActivity(intent)
            }

            if (UserUtils.getBookingPauseResumeStatus(binding.root.context).equals(yes_camil, true)) {
                binding.pauseResumeBtn.text = resume
                binding.callBtn.isEnabled = false
                binding.messageBtn.isEnabled = false
                binding.markCompleteBtn.isEnabled = false
                binding.raiseExtraDemandBtn.isEnabled = false
                binding.markCompleteBtn.setBackgroundResource(R.drawable.gray_corner)
                binding.markCompleteBtn.setTextColor(resources.getColor(R.color.gray))
                binding.raiseExtraDemandBtn.setTextColor(resources.getColor(R.color.gray))
                binding.raiseExtraDemandBtn.setBackgroundResource(R.drawable.gray_corner)
            } else {
                binding.pauseResumeBtn.text = pause
                binding.raiseExtraDemandBtn.setTextColor(resources.getColor(R.color.purple_500))
                binding.raiseExtraDemandBtn.setBackgroundResource(R.drawable.purple_out_line)
            }
            binding.pauseResumeBtn.visibility = View.VISIBLE

            binding.pauseResumeBtn.setOnClickListener {
                if (UserUtils.getBookingPauseResumeStatus(this).equals(yes_camil, true)) {
                    resumeBooking()
                } else {
                    pauseBooking()
                }
            }

            binding.raiseExtraDemandBtn.setOnClickListener {
                showExtraDemandDialog()
            }

            binding.markCompleteBtn.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val bookingDetailsResponse = RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(
                            BookingDetailsReqModel(bookingId.toInt(), categoryId.toInt(), RetrofitBuilder.USER_KEY, userId.toInt())
                        )
                        if (bookingDetailsResponse.status == 200) {
                            if (bookingDetailsResponse.booking_details.extra_demand_total_amount != "0" &&
                                bookingDetailsResponse.booking_details.extra_demand_total_amount.isNotEmpty() &&
                                bookingDetailsResponse.booking_details.extra_demand_status != "0" &&
                                bookingDetailsResponse.booking_details.extra_demand_status.isNotEmpty()) {

                                UserUtils.isExtraDemandRaised(this@ProviderBookingDetailsScreen, "1")
                                if (bookingDetailsResponse.booking_details.extra_demand_status == "1") {
                                    finalExpenditureDialog(bookingDetailsResponse.booking_details.extra_demand_total_amount)
                                } else {
                                    divertToInvoiceScreen()
                                }
                            } else if(bookingDetailsResponse.booking_details.extra_demand_total_amount != "0" && bookingDetailsResponse.booking_details.extra_demand_status == "0") {
                                toast(this@ProviderBookingDetailsScreen, extra_demand_waiting_alert)
                            } else {
                                UserUtils.sendExtraDemandFCM(this@ProviderBookingDetailsScreen, response.booking_details.fcm_token, bookingId, categoryId, "$userId|0|0|0|0")
                                UserUtils.isExtraDemandRaised(this@ProviderBookingDetailsScreen, "0")
                                divertToInvoiceScreen()
                            }
                        } else {
                            toast(this@ProviderBookingDetailsScreen, "Mark Complete Issue02: ${response.message}")
                        }
                    }  catch (e: Exception) {
                        toast(this@ProviderBookingDetailsScreen, "Mark Complete Issue02: ${e.message!!}")
                    }
                }
            }

            binding.callBtn.setOnClickListener {
                UserUtils.makePhoneCall(
                    this,
                    response.booking_details.mobile.replace(" ", "").takeLast(10)
                )
            }

            binding.messageBtn.setOnClickListener {
                UserUtils.makeMessage(
                    this,
                    response.booking_details.mobile.replace(" ", "").takeLast(10)
                )
            }

            if (response.booking_details.post_job_id != "0") {
                binding.requestInstallmentBtn.visibility = View.VISIBLE
            } else {
                binding.requestInstallmentBtn.visibility = View.GONE
            }

            binding.requestInstallmentBtn.setOnClickListener {
                ProviderReleaseGoalsScreen.userId = userId
                ProviderReleaseGoalsScreen.postJobId = response.booking_details.post_job_id
                startActivity(Intent(this, ProviderReleaseGoalsScreen::class.java))
            }
        } else {
            binding.pauseResumeBtn.visibility = View.GONE
        }

        if (categoryId == "2") {
            binding.viewFilesBtn.visibility = View.VISIBLE
            binding.viewFilesBtn.setOnClickListener {
                UserUtils.saveBookingId(this, bookingId)
                ViewFilesScreen.categoryId = categoryId.toInt()
                ViewFilesScreen.userId = userId.toInt()
                startActivity(Intent(this, ViewFilesScreen::class.java))
            }
        } else {
            binding.viewFilesBtn.visibility = View.GONE
        }

    }

    private fun divertToInvoiceScreen() {
        isProvider(this, true)
        val intent = Intent(this, ProviderInVoiceScreen::class.java)
        intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
        intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
        intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
        startActivity(intent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun showExtraDemandDialog() {

        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.toInt()
        )
//        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
        viewModel.viewBookingDetails(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    response = it.data!!
                    updateExtraDemandDialog(response)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.viewDetailsBtn, it.message!!)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateExtraDemandDialog(response: BookingDetailsResModel) {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_extra_demand_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val materialCharges = dialogView.findViewById<TextInputEditText>(R.id.materialCharges)
        val technicianCharges = dialogView.findViewById<TextInputEditText>(R.id.technicianCharges)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)
        val totalCost = dialogView.findViewById<TextView>(R.id.totalCost)
        materialCharges.setText(response.booking_details.material_advance)
        technicianCharges.setText(response.booking_details.technician_charges)

        materialCharges.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                if (materialCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString()
                        .isNotEmpty() && materialCharges.text.toString().isNotEmpty()
                ) {
                    totalCost.text = "Rs " + (materialCharges.text.toString().toInt() + technicianCharges.text.toString().toInt()).toString()
                }
            }

        })
        technicianCharges.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                if (materialCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString().isNotEmpty()) {
                    totalCost.text = materialCharges.text.toString()
                }
                if (technicianCharges.text.toString()
                        .isNotEmpty() && materialCharges.text.toString().isNotEmpty()
                ) {
                    totalCost.text = "Rs " + (materialCharges.text.toString().toInt() + technicianCharges.text.toString().toInt()).toString()
                }
            }

        })

        submitBtn.setOnClickListener {

            when {
                materialCharges.text.toString().isEmpty() && technicianCharges.text.toString()
                    .isEmpty() -> {
                    toast(this, enter_material_charges_alert)
                }
                else -> {

                    val mCharges = materialCharges.text.toString()
                    val tCharges = technicianCharges.text.toString()

                    val factory = ViewModelFactory(ProviderBookingRepository())
                    val viewModel =
                        ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
                    val requestBody = ExtraDemandReqModel(
                        bookingId,
                        currentDateTime(),
                        (mCharges.toInt() + tCharges.toInt()).toString(),
                        mCharges,
                        tCharges,
                        RetrofitBuilder.PROVIDER_KEY
                    )
                    viewModel.extraDemand(this, requestBody).observe(this) {
                        when (it) {
                            is NetworkResponse.Loading -> {
                                progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                UserUtils.sendExtraDemandFCM(this, this.response.booking_details.fcm_token, bookingId, categoryId, userId)
                                progressDialog.dismiss()
                                dialog.dismiss()
                                toast(this, extra_demand_raised)
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

        }
        closeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(intent)
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun finalExpenditureDialog(extraDemandRaised: String) {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_final_extra_expenditure_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val raisedExtraDemand = dialogView.findViewById<TextView>(R.id.raiseExtraDemand)
        val finalExpenditure = dialogView.findViewById<EditText>(R.id.finalExpenditure)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)
        raisedExtraDemand.text = response.booking_details.material_advance

        closeBtn.setOnClickListener { dialog.dismiss() }
        submitBtn.setOnClickListener {
            val finalDemand = finalExpenditure.text.toString()
            if (finalDemand.isEmpty()) {
                toast(this, enter_expenditure_incurred)
            } else if (finalDemand.toDouble() > extraDemandRaised.toDouble()) {
                toast(this, final_expenditure_alert)
            } else {
                val factory = ViewModelFactory(ProviderBookingRepository())
                val viewModel =
                    ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
                val requestBody = ExpenditureIncurredReqModel(
                    bookingId.toInt(),
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
                            isProvider(this, true)
                            val intent = Intent(this, ProviderInVoiceScreen::class.java)
                            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
                            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
                            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
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

    @SuppressLint("UseCompatpaLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        updateStatusList()
        if (UserUtils.getBookingPauseResumeStatus(binding.root.context).equals(yes_camil, true)) {
            binding.pauseResumeBtn.text = resume
            binding.callBtn.isEnabled = false
            binding.messageBtn.isEnabled = false
            binding.markCompleteBtn.isEnabled = false
            binding.raiseExtraDemandBtn.isEnabled = false
            binding.markCompleteBtn.setBackgroundResource(R.drawable.gray_corner)
            binding.markCompleteBtn.setTextColor(resources.getColor(R.color.gray))
            binding.raiseExtraDemandBtn.setTextColor(resources.getColor(R.color.gray))
            binding.raiseExtraDemandBtn.setBackgroundResource(R.drawable.gray_corner)
        } else {
            binding.pauseResumeBtn.text = pause
            binding.raiseExtraDemandBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.raiseExtraDemandBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.markCompleteBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.markCompleteBtn.setBackgroundResource(R.drawable.purple_out_line)
        }
    }

    private fun updateStatusList() {
        viewModel.getBookingStatusList(this, bookingId.toInt()).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
//                    Log.e("STATUS:", Gson().toJson(it.data!!.booking_status_details))
//                    toast(this, Gson().toJson(it.data.booking_status_details))
                    binding.recyclerView.adapter =
                        GetBookingStatusListAdapter(it.data!!.booking_status_details)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        }
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
            val categoryId = intent.getStringExtra(getString(R.string.category_id))!!
            val userId = intent.getStringExtra(getString(R.string.user_id))!!
            dismissDialogs()
            openBookingDetails(bookingId, categoryId, userId)
        }
    }

    private fun dismissDialogs() {
        supportFragmentManager.fragments.takeIf { it.isNotEmpty() }
            ?.map { (it as? DialogFragment)?.dismiss() }
    }

    fun openBookingDetails(bookingId: String, categoryId: String, userId: String) {
        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.split("|")[0].toInt()
        )
//        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
        CoroutineScope(Dispatchers.Main).launch {
            try {
                progressDialog.show()
                val response =
                    RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(requestBody)
                if (response.status == 200) {
                    progressDialog.dismiss()
                    updateStatusList()
                    showExtraDemandResponseDialog(response, userId)
                } else {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, response.message)
                }
            } catch (e: Exception) {
//                toast(this@ProviderBookingDetailsScreen, "BookingDetailsError:${e.message}")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showExtraDemandResponseDialog(
        data: BookingDetailsResModel,
        status: String
    ) {
        if (!data.booking_details.extra_demand_status.isNullOrBlank()) {
            if (data.booking_details.extra_demand_total_amount != "0") {
                if (data.booking_details.extra_demand_status != "0") {
                    val extraDemandAcceptRejectResponseDialog = BottomSheetDialog(this)
                    val dialogView = layoutInflater.inflate(R.layout.provider_extra_demand_accept_dialog, null)
                    val materialCharges = dialogView.findViewById<TextView>(R.id.materialCharges)
                    val technicianCharges = dialogView.findViewById<TextView>(R.id.technicianCharges)
                    val totalCost = dialogView.findViewById<TextView>(R.id.totalCost)
                    val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
                    val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
                    val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)

                    if (status.split("|")[1] == "1") {
                        materialCharges.text = "Rs ${data.booking_details.material_advance}.00"
                        technicianCharges.text = "Rs ${data.booking_details.technician_charges}.00"
                        totalCost.text = "Rs ${data.booking_details.extra_demand_total_amount}.00"
                        acceptBtn.text = extra_demand_accepted
                    } else {
                        materialCharges.text = "Rs ${status.split("|")[2]}.00"
                        technicianCharges.text = "Rs ${status.split("|")[3]}.00"
                        totalCost.text = "Rs ${status.split("|")[4]}.00"
                        acceptBtn.text = extra_demand_rejected
                    }

                    closeBtn.setOnClickListener {
                        extraDemandAcceptRejectResponseDialog.dismiss()
                        startActivity(intent)
                    }
                    rejectBtn.visibility = View.GONE

                    extraDemandAcceptRejectResponseDialog.setCancelable(true)
                    extraDemandAcceptRejectResponseDialog.setCanceledOnTouchOutside(true)
                    extraDemandAcceptRejectResponseDialog.setContentView(dialogView)
                    extraDemandAcceptRejectResponseDialog.show()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun resumeBooking() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = ProviderBookingResumeReqModel(
                    bookingId.toInt(),
                    RetrofitBuilder.PROVIDER_KEY,
                    currentDateTime(),
                    UserUtils.getUserId(this@ProviderBookingDetailsScreen).toInt()
                )
                val response = RetrofitBuilder.getServiceProviderRetrofitInstance().resumeBooking(requestBody)
                val jsonObject = JSONObject(response.string())
                if (jsonObject.getInt(status) == 200) {
                    UserUtils.saveBookingPauseResumeStatus(this@ProviderBookingDetailsScreen, "")
                    binding.callBtn.isEnabled = true
                    binding.messageBtn.isEnabled = true
                    binding.markCompleteBtn.isEnabled = true
                    binding.raiseExtraDemandBtn.isEnabled = true
                    binding.markCompleteBtn.setBackgroundResource(R.drawable.purple_out_line)
                    binding.markCompleteBtn.setTextColor(resources.getColor(R.color.purple_500))
                    binding.raiseExtraDemandBtn.setTextColor(resources.getColor(R.color.purple_500))
                    binding.raiseExtraDemandBtn.setBackgroundResource(R.drawable.purple_out_line)
                    binding.pauseResumeBtn.text = pause
                } else {
                    snackBar(binding.recyclerView, jsonObject.getString(message))
                }
            } catch (e: Exception) {
                snackBar(binding.recyclerView, e.message!!)
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun pauseBooking() {

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = ProviderPauseBookingReqModel(
                    bookingId.toInt(),
                    RetrofitBuilder.PROVIDER_KEY,
                    currentDateTime(),
                    UserUtils.getUserId(this@ProviderBookingDetailsScreen).toInt()
                )
                val response = RetrofitBuilder.getServiceProviderRetrofitInstance().pauseBooking(requestBody)
                val jsonObject = JSONObject(response.string())
                if (jsonObject.getInt(status) == 200) {
                    binding.pauseResumeBtn.text = resume
                    UserUtils.saveBookingPauseResumeStatus(this@ProviderBookingDetailsScreen, yes_camil)
                    if (UserUtils.getBookingPauseResumeStatus(binding.root.context).equals(yes_camil, true)) {
                        binding.pauseResumeBtn.text = resume
                        binding.callBtn.isEnabled = false
                        binding.messageBtn.isEnabled = false
                        binding.markCompleteBtn.isEnabled = false
                        binding.raiseExtraDemandBtn.isEnabled = false
                        binding.pauseResumeBtn.text = resume
                        binding.markCompleteBtn.setBackgroundResource(R.drawable.gray_corner)
                        binding.markCompleteBtn.setTextColor(resources.getColor(R.color.gray))
                        binding.raiseExtraDemandBtn.setTextColor(resources.getColor(R.color.gray))
                        binding.raiseExtraDemandBtn.setBackgroundResource(R.drawable.gray_corner)
                    } else {
                        binding.pauseResumeBtn.text = pause
                    }
                } else {
                    snackBar(binding.recyclerView, jsonObject.getString(message))
                }
            } catch (e: Exception) {
                snackBar(binding.recyclerView, e.message!!)
            }
        }
    }

    override fun onBackPressed() {
        if (isCompleted(this)) {
            super.onBackPressed()
        } else {
            startActivity(Intent(this, ProviderMyBookingsScreen::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver((myReceiver), IntentFilter(getString(R.string.EXTRA_DEMAND_ACCEPT_REJECT)))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
    }
}