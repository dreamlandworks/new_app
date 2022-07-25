package com.satrango.ui.user.bookings.provider_response

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.razorpay.Checkout
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderBookingResponseScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.models.Notification
import com.satrango.remote.fcm.models.SendFcmToAllReqModel
import com.satrango.remote.fcm.models.To
import com.satrango.ui.user.bookings.payment_screen.PaymentScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProviderBookingResponseScreen : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var amount: String
    private lateinit var binding: ActivityProviderBookingResponseScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderBookingResponseScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val response = intent.getStringExtra("response")!!
        initializeProgressDialog()

        userId = response.split("|")[2]

        if (response.split("|")[0] == "accept") {
            UserUtils.saveBookingType(this, response.split("|")[6])
            binding.accept.visibility = View.VISIBLE
            binding.acceptCloseBtn.setOnClickListener {
                startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
            }
            UserUtils.sendFCMtoAllServiceProviders(this, "accepted", "accepted", "accepted|${UserUtils.getBookingType(this)}")
            Handler().postDelayed({
                PaymentScreen.FROM_PROVIDER_BOOKING_RESPONSE = true
                PaymentScreen.FROM_USER_PLANS = false
                PaymentScreen.FROM_PROVIDER_PLANS = false
                PaymentScreen.FROM_USER_BOOKING_ADDRESS = false
                PaymentScreen.FROM_USER_SET_GOALS = false
                val spDetails = Gson().fromJson(UserUtils.getSelectedAllSPDetails(this), SearchServiceProviderResModel::class.java)
                if (spDetails != null) {
                    for (sp in spDetails.data) {
                        if (userId == sp.users_id) {
                            PaymentScreen.amount = sp.final_amount
                            PaymentScreen.userId = sp.users_id.toInt()
                            startActivity(Intent(this, PaymentScreen::class.java))
                            finish()
                        }
                    }
                }
            }, 3000)
        } else {
            UserUtils.saveBookingType(this, response.split("|")[4])
            showProviderRejectedDialog(response.split("|")[5])
            UserUtils.sendFCMtoAllServiceProviders(this, "accepted", "accepted", "accepted|${UserUtils.getBookingType(this)}")
        }

        binding.successCloseBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }

        binding.acceptCloseBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }

        binding.rejectCloseBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }

        binding.noBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }

        binding.yesBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }
    }

    private fun showProviderRejectedDialog(messageText: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.rejected_by_service_provider_dialog, null)
        val closeBtn = bottomSheet.findViewById<MaterialCardView>(R.id.closeBtn)
        val message = bottomSheet.findViewById<TextView>(R.id.message)
        val noBtn = bottomSheet.findViewById<TextView>(R.id.noBtn)
        val yesBtn = bottomSheet.findViewById<TextView>(R.id.yesBtn)
        message.text = Html.fromHtml("Looks like Service Provider rejected due to<b>'$messageText'</b>. Shall we choose the best for your need.")
        closeBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }
        noBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }
        yesBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        }
        bottomSheetDialog.setContentView(bottomSheet)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    override fun onBackPressed() {
        finish()
        startActivity(Intent(this, UserDashboardScreen::class.java))
    }
}