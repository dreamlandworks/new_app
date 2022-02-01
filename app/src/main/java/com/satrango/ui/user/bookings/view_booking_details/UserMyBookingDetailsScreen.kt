package com.satrango.ui.user.bookings.view_booking_details

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserMyBookingDetailsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMService
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.GetBookingStatusListAdapter
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.ProviderInVoiceScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ChangeExtraDemandStatusReqModel
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.UserMyBookingsScreen
import com.satrango.utils.UserUtils
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
        var userId = ""
        var categoryId = ""
        var bookingId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMyBookingDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        registerReceiver(myReceiver, IntentFilter(FCMService.EXTRA_DEMAND_ACCEPT_REJECT))

        val factory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.toInt()
        )
        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
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
                    snackBar(binding.viewDetailsBtn, it.message!!)
                }
            }
        }
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
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

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.userName.text =
            response.booking_details.fname + " " + response.booking_details.lname
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        if (response.booking_details.otp_raised_by == response.booking_details.sp_id) {
            binding.otp.text = response.booking_details.time_lapsed
        }
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
            intent.putExtra(
                binding.root.context.getString(R.string.user_id),
                UserUtils.getUserId(binding.root.context)
            )
            binding.root.context.startActivity(intent)
        }

        binding.markCompleteBtn.setOnClickListener {
//            if (response.booking_details.extra_demand_total_amount != "0") {
                ProviderInVoiceScreen.isExtraDemandRaised = "1"
                val intent = Intent(this, ProviderInVoiceScreen::class.java)
                intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
                intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
                intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
                startActivity(intent)
//            } else {
//                AlertDialog.Builder(this)
//                    .setMessage("Extra Demand Not Raised, Do you want to Continue?")
//                    .setPositiveButton("YES") { dialogInterface, _ ->
//                        dialogInterface.dismiss()
//                        ProviderInVoiceScreen.isExtraDemandRaised = "0"
//                        val intent = Intent(this, ProviderInVoiceScreen::class.java)
//                        intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
//                        intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
//                        intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
//                        startActivity(intent)
//                    }.setNegativeButton("NO") { dialogInterface, _ ->
//                        dialogInterface.dismiss()
//                    }.show()
//            }
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
        } else {
            binding.viewFilesBtn.visibility = View.GONE
        }

        if (response.booking_details.post_job_id != "0") {
            binding.messageBtn.visibility = View.GONE
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

        if (ViewUserBookingDetailsScreen.FROM_COMPLETED) {
            binding.markCompleteBtn.visibility = View.GONE
            binding.viewDetailsBtn.setOnClickListener { onBackPressed() }
            binding.callBtn.setCardBackgroundColor(resources.getColor(R.color.gray))
            binding.messageBtn.setCardBackgroundColor(resources.getColor(R.color.gray))
            binding.callBtn.isClickable = false
            binding.messageBtn.isClickable = false
        }
    }

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

        materialCharges.text = materialAdvance
        technicianCharges.text = technicalCharges
        totalCost.text = extraDemandTotalAmount

        closeBtn.setOnClickListener { extraDemandAcceptRejectDialog.dismiss() }

        acceptBtn.setOnClickListener {
            changeExtraDemandStatus(bookingId, 1, extraDemandAcceptRejectDialog, progressDialog)
        }

        rejectBtn.setOnClickListener {
            changeExtraDemandStatus(bookingId, 2, extraDemandAcceptRejectDialog, progressDialog)
        }

        extraDemandAcceptRejectDialog.setCancelable(false)
        extraDemandAcceptRejectDialog.setContentView(dialogView)
        extraDemandAcceptRejectDialog.show()
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
        viewModel.changeExtraDemandStatus(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    dialog.dismiss()
                    if (status == 1) {
                        UserUtils.sendExtraDemandFCM(
                            this,
                            response.booking_details.sp_fcm_token,
                            bookingId.toString(),
                            categoryId,
                            "$userId|1"
                        )
                        toast(this, "Extra Demand Accepted")
                    } else {
                        UserUtils.sendExtraDemandFCM(
                            this,
                            response.booking_details.sp_fcm_token,
                            bookingId.toString(),
                            categoryId,
                            "$userId|2"
                        )
                        toast(this, "Extra Demand Rejected")
                    }
                    updateStatusList()
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
                    Log.e("STATUS:", Gson().toJson(it.data!!.booking_status_details))
                    binding.recyclerView.adapter =
                        GetBookingStatusListAdapter(it.data.booking_status_details)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver((myReceiver), IntentFilter(FCMService.EXTRA_DEMAND_ACCEPT_REJECT))
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
            openBookingDetails(bookingId, categoryId, userId)
        }
    }

    fun openBookingDetails(bookingId: String, categoryId: String, userId: String) {
        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.toInt()
        )
        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
        CoroutineScope(Dispatchers.Main).launch {
            progressDialog.show()
            val response =
                RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(requestBody)
            if (response.status == 200) {
                progressDialog.dismiss()
                updateUI(response)
            } else {
                progressDialog.dismiss()
                snackBar(binding.recyclerView, response.message)
            }
        }
    }

    override fun onBackPressed() {
        if (ViewUserBookingDetailsScreen.FROM_COMPLETED) {
            super.onBackPressed()
        } else {
            startActivity(Intent(this, UserMyBookingsScreen::class.java))
        }
    }

}