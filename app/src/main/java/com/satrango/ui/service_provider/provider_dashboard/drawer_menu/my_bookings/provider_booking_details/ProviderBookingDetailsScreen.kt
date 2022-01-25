package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
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
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderBookingDetailsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMService
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.ProviderInVoiceScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.ProviderReleaseGoalsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review.ProviderRatingReviewScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.NumberFormatException
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

//        registerReceiver(myReceiver, IntentFilter(FCMService.OTP_INTENT_FILTER));

        val factory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        }

//        bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
//        categoryId = intent.getStringExtra(getString(R.string.category_id))!!
//        userId = intent.getStringExtra(getString(R.string.user_id))!!

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

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.userName.text = response.booking_details.fname + " " + response.booking_details.lname
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        if (response.booking_details.otp_raised_by != response.booking_details.sp_id && response.booking_details.otp_raised_by != "0") {
            binding.otp.text = response.booking_details.otp
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

        binding.raiseExtraDemandBtn.setOnClickListener {
            showExtraDemandDialog()
        }

        binding.markCompleteBtn.setOnClickListener {
            if (response.booking_details.extra_demand_total_amount != "0") {
                ProviderInVoiceScreen.isExtraDemandRaised = "1"
                finalExpenditureDialog()
            } else {
                AlertDialog.Builder(this)
                    .setMessage("Extra Demand Not Raised, Do you want to Continue?")
                    .setPositiveButton("YES") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        ProviderInVoiceScreen.isExtraDemandRaised = "0"
                        finalExpenditureDialog()
                    }.setNegativeButton("NO") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }.show()
            }
        }

        binding.callBtn.setOnClickListener {
            UserUtils.makePhoneCall(this, response.booking_details.mobile.replace(" ", "").takeLast(10))
        }

        binding.messageBtn.setOnClickListener {
            UserUtils.makeMessage(this, response.booking_details.mobile.replace(" ", "").takeLast(10))
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

        if (categoryId == "2") {
            binding.viewFilesBtn.visibility = View.VISIBLE
        } else {
            binding.viewFilesBtn.visibility = View.GONE
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
                                UserUtils.sendExtraDemandFCM(this, response.booking_details.fcm_token, bookingId, categoryId, userId)
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

    private fun finalExpenditureDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_final_extra_expenditure_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val raisedExtraDemand = dialogView.findViewById<TextView>(R.id.raiseExtraDemand)
        val finalExpenditure = dialogView.findViewById<EditText>(R.id.finalExpenditure)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)
        raisedExtraDemand.text = response.booking_details.material_advance

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
                            ProviderInVoiceScreen.FROM_PROVIDER = true
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
                })
            }
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

//    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        @RequiresApi(Build.VERSION_CODES.O)
//        override fun onReceive(context: Context, intent: Intent) {
//            val bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
//            val otp = intent.getStringExtra(getString(R.string.category_id))!!
//            val userId = intent.getStringExtra(getString(R.string.user_id))!!
////            toast(context, "$bookingId|$otp|$userId")
//            showotpInDialog(otp)
//        }
//    }

    @SuppressLint("SetTextI18n")
    private fun showotpInDialog(otp: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.booking_closing_dialog, null)
        val firstNo = bottomSheet.findViewById<TextView>(R.id.firstNo)
        val secondNo = bottomSheet.findViewById<TextView>(R.id.secondNo)
        val thirdNo = bottomSheet.findViewById<TextView>(R.id.thirdNo)
        val fourthNo = bottomSheet.findViewById<TextView>(R.id.fourthNo)
        val submitBtn = bottomSheet.findViewById<TextView>(R.id.submitBtn)
        val closeBtn = bottomSheet.findViewById<MaterialCardView>(R.id.closeBtn)
        firstNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
        secondNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
        thirdNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
        fourthNo.setBackgroundResource(R.drawable.otp_digit_purple_bg)
        firstNo.text = otp[0].toString()
        secondNo.text = otp[1].toString()
        thirdNo.text = otp[2].toString()
        fourthNo.text = otp[3].toString()
        submitBtn.text = "Close"
        closeBtn.setOnClickListener {
            ProviderRatingReviewScreen.FROM_PROVIDER = true
            val intent = Intent(this@ProviderBookingDetailsScreen, ProviderRatingReviewScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
            startActivity(intent)
        }
        submitBtn.setOnClickListener {
            ProviderRatingReviewScreen.FROM_PROVIDER = true
            val intent = Intent(this@ProviderBookingDetailsScreen, ProviderRatingReviewScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
            startActivity(intent)
        }
        bottomSheetDialog.setContentView(bottomSheet)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
    }

    override fun onResume() {
        super.onResume()
        updateStatusList()
    }

    private fun updateStatusList() {
        viewModel.getBookingStatusList(this, bookingId.toInt()).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    Log.e("STATUS:", Gson().toJson(it.data!!.booking_status_details))
//                    toast(this, Gson().toJson(it.data.booking_status_details))
                    binding.recyclerView.adapter = GetBookingStatusListAdapter(it.data.booking_status_details)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }
}