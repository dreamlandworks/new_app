package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderBookingDetailsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.invoice.ProviderInVoiceScreen
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.release_goals.ProviderReleaseGoalsScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class ProviderBookingDetailsScreen : AppCompatActivity() {

    private lateinit var binding: ActivityProviderBookingDetailsScreenBinding
    private lateinit var response: BookingDetailsResModel
    private lateinit var progressDialog: ProgressDialog
    private var userId = ""
    private var categoryId = ""
    private var bookingId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderBookingDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

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
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.userName.text =
            response.booking_details.fname + " " + response.booking_details.lname
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        binding.bookingIdText.text = bookingId

        binding.viewDetailsBtn.setOnClickListener {
            ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
            val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
            intent.putExtra(binding.root.context.getString(R.string.booking_id), bookingId)
            intent.putExtra(binding.root.context.getString(R.string.category_id), categoryId)
            intent.putExtra(binding.root.context.getString(R.string.user_id), userId)
            ViewUserBookingDetailsScreen.FROM_PROVIDER = true
            ViewUserBookingDetailsScreen.FROM_PENDING = false
            binding.root.context.startActivity(intent)
        }

        binding.raiseExtraDemandBtn.setOnClickListener {
            showExtraDemandDialog()
        }

        binding.markCompleteBtn.setOnClickListener {
            finalExpenditureDialog()
        }

        binding.requestInstallmentBtn.setOnClickListener {
            ProviderReleaseGoalsScreen.postJobId = response.booking_details.post_job_id
            ProviderReleaseGoalsScreen.userId = userId
            startActivity(Intent(this, ProviderReleaseGoalsScreen::class.java))
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
}