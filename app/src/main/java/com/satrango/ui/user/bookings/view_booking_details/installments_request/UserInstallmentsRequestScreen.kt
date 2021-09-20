package com.satrango.ui.user.bookings.view_booking_details.installments_request

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserInstallmentsRequestScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.GoalsInstallmentsDetail
import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.PostApproveRejectReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class UserInstallmentsRequestScreen : AppCompatActivity(), UserInstallmentsRequestInterface {

    companion object {
        var postJobId = 0
    }

    private lateinit var viewModel: BookingViewModel
    private lateinit var binding: ActivityUserInstallmentsRequestScreenBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInstallmentsRequestScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.installment_requests)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        val factory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        viewModel.getInstallmentsList(this, postJobId).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.recyclerView.adapter = UserInstallmentsRequestAdapter(it.data!!.goals_installments_details, this)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })

    }

    override fun acceptInstallment(data: GoalsInstallmentsDetail) {
        val requestBody = PostApproveRejectReqModel(
            data.booking_id.toInt(),
            data.inst_no.toInt(),
            RetrofitBuilder.PROVIDER_KEY,
            UserUtils.spid.toInt(),
            34,
            UserUtils.getUserId(this).toInt()
        )
        viewModel.postApproveReject(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }

    override fun rejectInstallment(data: GoalsInstallmentsDetail) {
        val requestBody = PostApproveRejectReqModel(
            data.booking_id.toInt(),
            data.inst_no.toInt(),
            RetrofitBuilder.PROVIDER_KEY,
            UserUtils.spid.toInt(),
            35,
            UserUtils.getUserId(this).toInt()
        )
        viewModel.postApproveReject(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }
}