package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivitySetGoalsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.payment_screen.PaymentScreen
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.installment_payments.InstallmentPaymentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments.DataX
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments.SaveInstallmentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.setgoals.Data
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SetGoalsScreen : AppCompatActivity(), PaymentResultListener, SetGoalsListener {

    private lateinit var selectedInstallmentToUpdate: DataX
    private lateinit var viewModel: PostJobViewModel
    private var bidPrice = 0
    private lateinit var goalsList: List<Data>
    private lateinit var binding: ActivitySetGoalsScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog
    private var currentInstallment = 0

    private var installmentsList = ArrayList<DataX>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoalsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bidPrice = ViewBidsScreen.bidPrice
        binding.bidPrice.text = "Rs $bidPrice"

        initializeProgressDialog()

        val factory = ViewModelFactory(PostJobRepository())
        viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        viewModel.setGoals(this).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    goalsList = it.data!!
                    val goals = ArrayList<String>()
                    goals.add("Set Goals")
                    for (goal in goalsList) {
                        goals.add(goal.description)
                    }
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        goals
                    )
                    binding.setGoals.adapter = adapter
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.addAnotherAddressBtn, it.message!!)
                }
            }
        }

        binding.addAnotherAddressBtn.setOnClickListener {
            when {
                binding.installment.text.toString().isEmpty() -> {
                    snackBar(binding.addAnotherAddressBtn, "Enter Installment Amount")
                }
                binding.setGoals.selectedItemPosition == 0 -> {
                    snackBar(binding.addAnotherAddressBtn, "Set Goal")
                }
                else -> {
                    currentInstallment += 1
                    installmentsList.add(
                        DataX(
                            binding.installment.text.toString().toInt(),
                            goalsList[binding.setGoals.selectedItemPosition - 1].goal_id.toInt(),
                            currentInstallment
                        )
                    )
                    binding.installment.setText("")
                    binding.setGoals.setSelection(0)
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = SetGoalsAdapter(installmentsList, this)
                }
            }
        }

        binding.proceedToPayBtn.setOnClickListener {

            if (binding.proceedToPayBtn.text == "Update Installment") {

                when {
                    binding.installment.text.toString().isEmpty() -> {
                        snackBar(binding.addAnotherAddressBtn, "Enter Installment Amount")
                    }
                    binding.setGoals.selectedItemPosition == 0 -> {
                        snackBar(binding.addAnotherAddressBtn, "Set Goal")
                    }
                    else -> {
                        for (index in installmentsList.indices) {
                            if (installmentsList[index].inst_no == selectedInstallmentToUpdate.inst_no) {
                                installmentsList[index] = DataX(
                                    binding.installment.text.toString().toInt(),
                                    goalsList[binding.setGoals.selectedItemPosition - 1].goal_id.toInt(),
                                    selectedInstallmentToUpdate.inst_no
                                )
                            }
                        }
                        binding.installment.setText("")
                        binding.setGoals.setSelection(0)
                        binding.recyclerView.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        binding.recyclerView.adapter = SetGoalsAdapter(installmentsList, this)
                        binding.proceedToPayBtn.text = "Proceed to Pay"
                    }
                }
            } else {
                var installments = 0
                for (installment in installmentsList) {
                    installments += installment.amount
                }
                if (installments == bidPrice) {
                    uploadToServer()
                } else {
                    snackBar(binding.addAnotherAddressBtn, "Amount is not equal to bid price.")
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

    private fun uploadToServer() {
        toast(this, "Upload to server method called")
        val requestBody = SaveInstallmentReqModel(
            ViewBidsScreen.bookingId,
            installmentsList,
            RetrofitBuilder.USER_KEY,
            ViewBidsScreen.postJobId
        )
//        toast(this, Gson().toJson(requestBody))
        viewModel.saveInstallments(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    UserUtils.saveInstallmentDetId(this, it.data!!.installment_det_id.toString())
                    PaymentScreen.amount = bidPrice
                    toast(this, "Upload Success")
                    PaymentScreen.finalWalletBalance = it.data.wallet_balance.toDouble().toInt().toString()
                    toast(this, PaymentScreen.finalWalletBalance)
                    PaymentScreen.FROM_USER_SET_GOALS = true
                    PaymentScreen.FROM_USER_PLANS = false
                    PaymentScreen.FROM_PROVIDER_PLANS = false
                    PaymentScreen.FROM_PROVIDER_BOOKING_RESPONSE = false
                    PaymentScreen.FROM_USER_BOOKING_ADDRESS = false
                    startActivity(Intent(this, PaymentScreen::class.java))
                    toast(this, "Payment screen called")

//                    makePayment()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(this, it.message!!)
                    snackBar(binding.addAnotherAddressBtn, it.message)
                }
            }
        }
    }

    private fun makePayment() {
        Checkout.preload(applicationContext)
        val checkout = Checkout()
        checkout.setKeyID(getString(R.string.razorpay_api_key))
        try {
            val orderRequest = JSONObject()
            orderRequest.put("currency", "INR")
            orderRequest.put(
                "amount",
                bidPrice * 100
            ) // 500rs * 100 = 50000 paisa
            orderRequest.put("receipt", "order_rcptid_${System.currentTimeMillis()}")
            orderRequest.put(
                "image",
                "https://dev.satrango.com/public/assets/img/logo-black.png"
            )
            orderRequest.put("theme.color", R.color.blue)
            checkout.open(this, orderRequest)
        } catch (e: Exception) {
            toast(this, e.message!!)
        }
    }

    override fun onPaymentSuccess(response: String?) {
        updateInstallmentPaymentStatus("Success", response!!)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        updateInstallmentPaymentStatus("Failure", "")
        snackBar(binding.addAnotherAddressBtn, "Payment Failed Try again")
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateInstallmentPaymentStatus(status: String, referenceId: String) {
        val requestBody = InstallmentPaymentReqModel(
            bidPrice.toString(),
            ViewBidsScreen.bookingId,
            SimpleDateFormat("yyyy-MM-dd").format(Date()),
            RetrofitBuilder.USER_KEY,
            status,
            referenceId,
            UserUtils.getUserId(this).toInt(),
            ViewBidsScreen.bidId,
            ViewBidsScreen.spId
        )
        viewModel.installmentPayments(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    if (status == "Success") {
                        paymentSuccessDialog(this)
                    } else {
                        snackBar(binding.recyclerView, "Payment Failed")
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.addAnotherAddressBtn, it.message!!)
                }
            }
        }
    }

    private fun paymentSuccessDialog(context: Context) {
        val dialog = BottomSheetDialog(context)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.payment_success_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    override fun selectedInstallment(dataX: DataX) {
        binding.installment.setText(dataX.amount.toString())
        for (index in goalsList.indices) {
            if (dataX.goal_id == goalsList[index].goal_id.toInt()) {
                selectedInstallmentToUpdate = dataX
                binding.setGoals.setSelection(index + 1)
            }
        }
        binding.proceedToPayBtn.text = "Update Installment"
    }

}