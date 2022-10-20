package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderReleaseGoalsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.GoalsInstallmentsDetail
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderGoalsInstallmentsListResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderPostRequestInstallmentReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast

class ProviderReleaseGoalsScreen : AppCompatActivity(), ProviderReleaseGoalsInterface {

    private lateinit var response: ProviderGoalsInstallmentsListResModel
    private lateinit var viewModel: ProviderBookingViewModel
    private lateinit var binding: ActivityProviderReleaseGoalsScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        var userId = "0"
        var postJobId = "0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderReleaseGoalsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        }

        initializeProgressDialog()

        val factory = ViewModelFactory(ProviderBookingRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
        viewModel.getInstallmentsList(this, postJobId.toInt()).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    response = it.data!!
                    if (response.goals_installments_details.isNotEmpty()) {
                        binding.bookingId.text = response.goals_installments_details[0].booking_id
                        binding.workStartedAt.text =
                            response.goals_installments_details[0].created_dts.split(" ")[0]
                        binding.time.text =
                            response.goals_installments_details[0].created_dts.split(" ")[0]
                        binding.recyclerView.adapter = ProviderReleaseGoalsAdapter(response.goals_installments_details, this)
                        var total = 0.0
                        for (amount in response.goals_installments_details) {
                            total += amount.amount.toDouble()
                        }
                        binding.totalCost.text = total.toString()
                    } else {
                        noInstallmentsDialog()
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, "Error01:${it.message!!}")
                }
            }
        }

    }

    private fun noInstallmentsDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.no_installments_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val backBtn = dialogView.findViewById<TextView>(R.id.backBtn)
        closeBtn.setOnClickListener { dialog.dismiss() }
        backBtn.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun sendRequest(data: GoalsInstallmentsDetail) {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_release_goal_confirmation_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val backBtn = dialogView.findViewById<TextView>(R.id.backBtn)
        val confirmBtn = dialogView.findViewById<TextView>(R.id.confirmBtn)
        val percentage = dialogView.findViewById<TextView>(R.id.percent)
        val progress = dialogView.findViewById<CircularProgressIndicator>(R.id.progressBar)
        closeBtn.setOnClickListener { dialog.dismiss() }
        backBtn.setOnClickListener { dialog.dismiss() }
        confirmBtn.setOnClickListener {
            dialog.dismiss()
            showConfirmationDialog(data)
        }
        percentage.text = data.description.split(" ")[0].trim()
        progress.progress = percentage.text.toString().split("%")[0].toInt()
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showConfirmationDialog(data: GoalsInstallmentsDetail) {
        val requestBody = ProviderPostRequestInstallmentReqModel(
            data.booking_id.toInt(),
            data.inst_no.toInt(),
            RetrofitBuilder.PROVIDER_KEY,
            UserUtils.getUserId(this).toInt(),
            userId.toInt()
        )
        viewModel.postRequestInstallment(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
//                    startActivity(intent)
                    snackBar(binding.recyclerView, it.data!!.message)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }
}