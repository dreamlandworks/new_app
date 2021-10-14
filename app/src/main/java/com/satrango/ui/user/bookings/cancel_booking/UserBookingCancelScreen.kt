package com.satrango.ui.user.bookings.cancel_booking

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserBookingCancelScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.ProviderDashboard
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.cancel_booking.models.UserBookingCancelReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class UserBookingCancelScreen : AppCompatActivity() {

    private var cancel_id = 0
    private var reason = ""
    private var userId = ""
    private var categoryId = ""
    private var bookingId = ""
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityUserBookingCancelScreenBinding

    companion object {
        var FROM_PROVIDER = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBookingCancelScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()

        bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
        categoryId = intent.getStringExtra(getString(R.string.category_id))!!
        userId = intent.getStringExtra(getString(R.string.user_id))!!

        cancel_id = 24

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        initializeProgressDialog()

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
                    val response = it.data!!
                    updateUI(response)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.amount, it.message!!)
                }
            }
        })

    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.offers)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        if (FROM_PROVIDER) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            binding.apply {
                card.setCardBackgroundColor(resources.getColor(R.color.purple_500))
                message.text = resources.getString(R.string.sp_cancel_message)
                radioBtn1.text = resources.getString(R.string.sp_cancel_message_radio1)
                radioBtn2.text = resources.getString(R.string.sp_cancel_message_radio2)
                radioBtn3.text = resources.getString(R.string.sp_cancel_message_radio3)
                radioBtn4.text = resources.getString(R.string.sp_cancel_message_radio4)
                cancelBookingBtn.setBackgroundResource(R.drawable.purple_out_line)
                cancelBookingBtn.setTextColor(resources.getColor(R.color.purple_500))
                submitBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                cancel_id = 25
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
    private fun updateUI(response: BookingDetailsResModel) {

        binding.apply {

            userName.text =
                response.booking_details.fname + " " + response.booking_details.lname
            date.text = response.booking_details.scheduled_date
            amount.text = "Rs ${response.booking_details.amount}"
            time.text = response.booking_details.from
            bookingIdText.text = bookingId


            cancelBookingBtn.setOnClickListener {
                onBackPressed()
            }
            submitBtn.setOnClickListener {

                if (radioGroup.checkedRadioButtonId == -1 && otherReasons.text.toString()
                        .isEmpty()
                ) {
                    snackBar(binding.amount, "Please Provide Reason")
                } else {
                    reason = if (radioGroup.checkedRadioButtonId != -1) {
                        val selectedRadioBtn =
                            findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                        selectedRadioBtn.text.toString()
                    } else {
                        otherReasons.text.toString().trim()
                    }
                    weAreSorryDialog()
                }

            }

        }


    }

    private fun submitToServer() {
        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        val requestBody = UserBookingCancelReqModel(
            bookingId.toInt(),
            UserUtils.getUserId(this).toInt(),
            RetrofitBuilder.USER_KEY,
            reason,
            cancel_id // pass 24 if cancelled by user  // pass 25 if cancelled by sp
        )

        viewModel.cancelBooking(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    showSuccessDialog()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.amount, it.message!!)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun weAreSorryDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.no_service_provider_found, null)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val headerMessage = dialogView.findViewById<TextView>(R.id.header_message)
        val message = dialogView.findViewById<TextView>(R.id.message)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        if (FROM_PROVIDER) {
            noBtn.setBackgroundResource(R.drawable.purple_out_line)
            noBtn.setTextColor(resources.getColor(R.color.purple_500))
            yesBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        }
        title.text = "Confirmation"
        headerMessage.text = "Please confirm to cancel"
        message.text =
            "You will be charged with 'Cancellation charges (if any)' as per the policy guidelines"
        yesBtn.text = "confirm"
        noBtn.text = "Don't Cancel the Booking"
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesBtn.setOnClickListener {
            dialog.dismiss()
            submitToServer()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showSuccessDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.reschedule_requested_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        val homeBtn = dialogView.findViewById<TextView>(R.id.homeBtn)
        val searchBtn = dialogView.findViewById<TextView>(R.id.myBookingsBtn)
        message.text = "Your booking is cancelled. Would you like to book again?"
        if (FROM_PROVIDER) {
            homeBtn.setBackgroundResource(R.drawable.purple_out_line)
            homeBtn.setTextColor(resources.getColor(R.color.purple_500))
            searchBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            homeBtn.setOnClickListener {
                startActivity(Intent(this, ProviderDashboard::class.java))
            }
            searchBtn.setOnClickListener {
                startActivity(Intent(this, ProviderDashboard::class.java))
            }
        } else {
            homeBtn.setOnClickListener {
                startActivity(Intent(this, UserDashboardScreen::class.java))
            }
            searchBtn.setOnClickListener {
                startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
            }
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }
}