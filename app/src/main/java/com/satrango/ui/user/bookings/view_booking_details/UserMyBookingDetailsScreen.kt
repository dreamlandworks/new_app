package com.satrango.ui.user.bookings.view_booking_details

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
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
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserMyBookingDetailsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.GetBookingStatusListAdapter
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.ProviderInVoiceScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ChangeExtraDemandStatusReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.GoalsInstallmentsDetail
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_attachments.ViewFilesScreen
import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.PostApproveRejectReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.UserMyBookingsScreen
import com.satrango.utils.Constants.extra_demand_accepted
import com.satrango.utils.Constants.extra_demand_rejected
import com.satrango.utils.Constants.mark_complete_alert
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isCompleted
import com.satrango.utils.UserUtils.isPending
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserMyBookingDetailsScreen : AppCompatActivity() {

    private lateinit var extraDemandAcceptRejectDialog: BottomSheetDialog
    private lateinit var viewModel: BookingViewModel
    private lateinit var binding: ActivityUserMyBookingDetailsScreenBinding
    private lateinit var response: BookingDetailsResModel

    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        var userId = "0"
        var categoryId = "0"
        var bookingId = "0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMyBookingDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        registerReceiver(myReceiver, IntentFilter(getString(R.string.EXTRA_DEMAND_ACCEPT_REJECT)))

        val factory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        loadScreen()
    }

    private fun loadScreen() {
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
                    updateUI(response)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.viewDetailsBtn,"Error 01:" + it.message!!)
                }
            }
        }
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
        binding.userName.text = response.booking_details.fname + " " + response.booking_details.lname
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        binding.occupation.text = response.booking_details.sp_profession
        Glide.with(this).load(response.booking_details.sp_profile_pic).error(R.drawable.images).into(binding.profilePic)
        binding.otpText.text = resources.getString(R.string.time_lapsed)
        binding.otp.text = response.booking_details.time_lapsed
        binding.bookingIdText.text = bookingId

        binding.viewDetailsBtn.setOnClickListener {
            ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
            val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), binding.bookingIdText.text.toString().trim())
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
            isProvider(this, true)
            binding.root.context.startActivity(intent)
        }

        binding.viewDetailsBtn.setOnClickListener {
            ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
            isProvider(this, false)
            isPending(this, false)
            val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), UserUtils.getUserId(binding.root.context))
            binding.root.context.startActivity(intent)
        }

        if (response.booking_details.extra_demand_total_amount.isEmpty()) {
            binding.markCompleteBtn.isEnabled = false
            binding.markCompleteBtn.setBackgroundResource(R.drawable.gray_corner)
            binding.markCompleteBtn.setTextColor(resources.getColor(R.color.gray))
        }

        binding.markCompleteBtn.setOnClickListener {
            val requestBody = BookingDetailsReqModel(
                bookingId.toInt(),
                categoryId.toInt(),
                RetrofitBuilder.USER_KEY,
                userId.toInt()
            )
//            Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
            CoroutineScope(Dispatchers.Main).launch {
                progressDialog.show()
                val bookingDetails = RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(requestBody)
                if (response.status == 200) {
                    progressDialog.dismiss()
                    if (bookingDetails.booking_details.extra_demand_status == "0" && bookingDetails.booking_details.expenditure_incurred.isEmpty()) {
                        UserUtils.isExtraDemandRaised(this@UserMyBookingDetailsScreen, "0")
                        divertToInvoiceScreen()
                    } else if (bookingDetails.booking_details.extra_demand_status == "1" && bookingDetails.booking_details.expenditure_incurred.isNotEmpty()) {
                        UserUtils.isExtraDemandRaised(this@UserMyBookingDetailsScreen, "1")
                        divertToInvoiceScreen()
                    } else if (bookingDetails.booking_details.extra_demand_status == "2" && bookingDetails.booking_details.expenditure_incurred.isEmpty()) {
                        UserUtils.isExtraDemandRaised(this@UserMyBookingDetailsScreen, "0")
                        divertToInvoiceScreen()
                    } else if (bookingDetails.booking_details.extra_demand_status.isEmpty() && bookingDetails.booking_details.expenditure_incurred.isEmpty()) {
                        UserUtils.isExtraDemandRaised(this@UserMyBookingDetailsScreen, "0")
                        divertToInvoiceScreen()
                    } else {
                        toast(
                            this@UserMyBookingDetailsScreen,
                            mark_complete_alert
                        )
                    }
                } else {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, "Error 02:" +  response.message)
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
        if (!response.booking_details.extra_demand_status.isNullOrBlank()) {
            if (response.booking_details.extra_demand_total_amount != "0") {
                if (response.booking_details.extra_demand_status == "0") {
                    showExtraDemandAcceptDialog(
                        bookingId.toInt(),
                        response.booking_details.material_advance,
                        response.booking_details.technician_charges,
                        response.booking_details.extra_demand_total_amount,
                        progressDialog
                    )
                }
            }
        }

        val postJobId = response.booking_details.post_job_id.toInt()
        if (postJobId != 0) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val postJobIdResponse = RetrofitBuilder.getServiceProviderRetrofitInstance().getGoalsInstallmentsList(RetrofitBuilder.PROVIDER_KEY, postJobId)
                    if (postJobIdResponse.status == 200) {
                        for (installment in postJobIdResponse.goals_installments_details) {
                            if (installment.status_id.toInt() == 33) { // 32 - installment added, 33 - installment requested, 34 - installment approved, 35 - installment rejected
                                viewInstallmentRequest(installment)
                            }
                        }
                    }
                } catch (e: Exception) {
                    toast(this@UserMyBookingDetailsScreen, "Error:$postJobId:" + e.message!!)
                }
            }
        }

        if (isCompleted(this)) {
            binding.markCompleteBtn.visibility = View.GONE
            binding.viewDetailsBtn.setOnClickListener { onBackPressed() }
            binding.callBtn.setCardBackgroundColor(resources.getColor(R.color.gray))
            binding.messageBtn.setCardBackgroundColor(resources.getColor(R.color.gray))
            binding.callBtn.isClickable = false
            binding.messageBtn.isClickable = false
        }
    }

    private fun dismissDialogs() {
        supportFragmentManager.fragments.takeIf { it.isNotEmpty() }
            ?.map { (it as? DialogFragment)?.dismiss() }
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun viewInstallmentRequest(installment: GoalsInstallmentsDetail) {
        val dialog = Dialog(this)
        val dialogView = layoutInflater.inflate(R.layout.installment_raised_dialog, null)
        val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val installmentText = dialogView.findViewById<TextView>(R.id.installmentText)
        val installmentAmount = dialogView.findViewById<TextView>(R.id.installmentAmount)

        installmentText.text = installment.description
        installmentAmount.text = "Rs ${installment.amount}/-"

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        acceptBtn.setOnClickListener {
            updateInstallmentStatus(installment, 34)
            dialog.dismiss()
        }
        rejectBtn.setOnClickListener {
            updateInstallmentStatus(installment, 35)
            dialog.dismiss()
        }
         
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
    }

    private fun updateInstallmentStatus(installment: GoalsInstallmentsDetail, statusCode: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = PostApproveRejectReqModel(
                    installment.booking_id.toInt(),
                    installment.inst_no.toInt(),
                    RetrofitBuilder.PROVIDER_KEY,
                    UserUtils.getSpId(this@UserMyBookingDetailsScreen).toInt(),
                    statusCode,
                    UserUtils.getUserId(this@UserMyBookingDetailsScreen).toInt()
                )
                val response = RetrofitBuilder.getUserRetrofitInstance().postInstallmentApproveReject(requestBody)
                toast(this@UserMyBookingDetailsScreen, response.message)
            } catch (e: Exception) {
                toast(this@UserMyBookingDetailsScreen, e.message!!)
            }
        }
    }

    private fun divertToInvoiceScreen() {
        val intent = Intent(this@UserMyBookingDetailsScreen, ProviderInVoiceScreen::class.java)
        intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
        intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
        intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun showExtraDemandAcceptDialog(
        bookingId: Int,
        materialAdvance: String,
        technicalCharges: String,
        extraDemandTotalAmount: String,
        progressDialog: BeautifulProgressDialog
    ) {
        extraDemandAcceptRejectDialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_extra_demand_accept_dialog, null)
        val materialCharges = dialogView.findViewById<TextView>(R.id.materialCharges)
        val technicianCharges = dialogView.findViewById<TextView>(R.id.technicianCharges)
        val totalCost = dialogView.findViewById<TextView>(R.id.totalCost)
        val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)

        materialCharges.text = "Rs $materialAdvance.00"
        technicianCharges.text = "Rs $technicalCharges.00"
        totalCost.text = "Rs $extraDemandTotalAmount.00"

        closeBtn.setOnClickListener {
            extraDemandAcceptRejectDialog.dismiss()
            startActivity(intent)
        }

        acceptBtn.setOnClickListener {
            changeExtraDemandStatus(bookingId, 1, extraDemandAcceptRejectDialog, progressDialog, materialAdvance, technicalCharges, extraDemandTotalAmount)
        }

        rejectBtn.setOnClickListener {
            changeExtraDemandStatus(
                bookingId,
                2,
                extraDemandAcceptRejectDialog,
                progressDialog,
                materialAdvance,
                technicalCharges,
                extraDemandTotalAmount
            )
        }

        extraDemandAcceptRejectDialog.setCancelable(true)
        extraDemandAcceptRejectDialog.setCanceledOnTouchOutside(true)
        extraDemandAcceptRejectDialog.setContentView(dialogView)
        extraDemandAcceptRejectDialog.show()
    }

    private fun changeExtraDemandStatus(
        bookingId: Int,
        status: Int,
        dialog: BottomSheetDialog,
        progressDialog: BeautifulProgressDialog,
        materialAdvance: String,
        technicalCharges: String,
        extraDemandTotalAmount: String
    ) {

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        val requestBody = ChangeExtraDemandStatusReqModel(bookingId, RetrofitBuilder.USER_KEY, status)
        if (status == 1) {
            UserUtils.sendExtraDemandFCM(this, response.booking_details.sp_fcm_token, bookingId.toString(), categoryId, "$userId|1|$materialAdvance|$technicalCharges|$extraDemandTotalAmount")
            toast(this, extra_demand_accepted)
        } else {
            UserUtils.sendExtraDemandFCM(this, response.booking_details.sp_fcm_token, bookingId.toString(), categoryId, "$userId|2|$materialAdvance|$technicalCharges|$extraDemandTotalAmount")
            toast(this, extra_demand_rejected)
        }
        viewModel.changeExtraDemandStatus(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    dialog.dismiss()
//                    loadScreen()
//                    updateStatusList()
                    startActivity(intent)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(this, it.message!!)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatusList()
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
                    binding.recyclerView.adapter = GetBookingStatusListAdapter(it.data!!.booking_status_details)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, "Error 03:" +  it.message!!)
                }
            }
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

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
            val categoryId = intent.getStringExtra(getString(R.string.category_id))!!
            val userId = intent.getStringExtra(getString(R.string.user_id))!!
            dismissDialogs()
            if (userId.contains("|")) {
                binding.markCompleteBtn.isEnabled = true
                binding.markCompleteBtn.setBackgroundResource(R.drawable.user_btn_bg)
                binding.markCompleteBtn.setTextColor(resources.getColor(R.color.white))
            } else {
                openBookingDetails(bookingId, categoryId, userId)
            }
        }
    }

    fun openBookingDetails(bookingId: String, categoryId: String, userId: String) {
        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.toInt()
        )
//        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
        CoroutineScope(Dispatchers.Main).launch {
            progressDialog.show()
            val response =
                RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(requestBody)
            if (response.status == 200) {
                progressDialog.dismiss()
                updateUI(response)
            } else {
                progressDialog.dismiss()
                snackBar(binding.recyclerView, "Error 04:" +  response.message)
            }
        }
    }

    override fun onBackPressed() {
        if (isCompleted(this)) {
            super.onBackPressed()
        } else {
            startActivity(Intent(this, UserMyBookingsScreen::class.java))
        }
    }

}