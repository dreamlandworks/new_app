package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivitySetGoalsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.installment_payments.InstallmentPaymentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.setgoals.Data
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments.DataX
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments.SaveInstallmentReqModel
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

class SetGoalsScreen : AppCompatActivity(), PaymentResultListener {

    private lateinit var viewModel: PostJobViewModel
    private var bidPrice: Int = 0
    private lateinit var goalsList: List<Data>
    private lateinit var binding: ActivitySetGoalsScreenBinding
    private lateinit var progressDialog: ProgressDialog
    private var currentInstallment = 0

    private var installmentsList = ArrayList<DataX>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoalsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jobPostId = intent.getStringExtra("postJobId")!!
        bidPrice = intent.getStringExtra("bidPrice")!!.toInt()
        binding.bidPrice.text = "Rs ${bidPrice.toString()}"

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val factory = ViewModelFactory(PostJobRepository())
        viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        viewModel.setGoals(this).observe(this, {
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
        })

        binding.addAnotherAddressBtn.setOnClickListener {
            when {
                binding.installment.text.toString().isEmpty() -> {
                    snackBar(binding.addAnotherAddressBtn, "Enter Installment Amount")
                }
                binding.setGoals.selectedItemPosition == 0 -> {
                    snackBar(binding.addAnotherAddressBtn, "Set Goal")
                }
                else -> {
                    var installments = 0
                    for (installment in installmentsList) {
                        installments += installment.amount
                    }
                    if (installments <= bidPrice && currentInstallment <= 3) {
                        installments += binding.installment.text.toString().toInt()
                        if (currentInstallment == 2 && installments < bidPrice) {
                            snackBar(
                                binding.addAnotherAddressBtn,
                                "Please add Correct amount $installments"
                            )
                        } else {
                            installments = 0
                            for (installment in installmentsList) {
                                installments += installment.amount
                            }
                            if (installments <= bidPrice) {
                                currentInstallment += 1
                                installmentsList.add(
                                    DataX(
                                        binding.installment.text.toString().toInt(),
                                        0,
                                        goalsList[binding.setGoals.selectedItemPosition - 1].goal_id.toInt()
                                    )
                                )
                                installments -= binding.installment.text.toString().toInt()
                                binding.installment.setText("")
                                binding.setGoals.setSelection(0)
                            } else {
                                snackBar(binding.addAnotherAddressBtn, "Installment price exceeds to Bid Price")
                            }

                        }
                    } else {
                        snackBar(
                            binding.addAnotherAddressBtn,
                            "Amount is not equal to bid price or Installments exceeded."
                        )
                    }
                }
            }
        }

        binding.proceedToPayBtn.setOnClickListener {

            var installments = 0
            for (installment in installmentsList) {
                installments += installment.amount
            }
            if (installments == bidPrice) {
                toast(this, Gson().toJson(installmentsList))
                makePayment()
            } else {

                when {
                    binding.installment.text.toString().isEmpty() -> {
                        snackBar(binding.addAnotherAddressBtn, "Enter Installment Amount")
                    }
                    binding.setGoals.selectedItemPosition == 0 -> {
                        snackBar(binding.addAnotherAddressBtn, "Set Goal")
                    }
                    else -> {
                        if (installments <= bidPrice && currentInstallment <= 3) {
                            installments += binding.installment.text.toString().toInt()
                            if (currentInstallment == 2 && installments < bidPrice) {
                                snackBar(
                                    binding.addAnotherAddressBtn,
                                    "Please add Correct amount $installments"
                                )
                            } else {
                                currentInstallment += 1
                                installmentsList.add(
                                    DataX(
                                        binding.installment.text.toString().toInt(),
                                        goalsList[binding.setGoals.selectedItemPosition - 1].goal_id.toInt(),
                                        currentInstallment
                                    )
                                )
                                installments -= binding.installment.text.toString().toInt()
                                binding.installment.setText("")
                                binding.setGoals.setSelection(0)
                                installments = 0
                                for (installment in installmentsList) {
                                    installments += installment.amount
                                }
                                if (installments == bidPrice) {
                                    val requestBody = SaveInstallmentReqModel(
                                        0,
                                        installmentsList,
                                        RetrofitBuilder.USER_KEY,
                                        jobPostId.toInt()
                                    )
                                    viewModel.saveInstallments(this, requestBody).observe(this, {
                                        when(it) {
                                            is NetworkResponse.Loading -> {
                                                progressDialog.show()
                                            }
                                            is NetworkResponse.Success -> {
                                                progressDialog.dismiss()
                                                UserUtils.saveInstallmentDetId(this, it.data!!.installment_det_id.toString())
                                                makePayment()
                                            }
                                            is NetworkResponse.Failure -> {
                                                progressDialog.dismiss()
                                                snackBar(binding.addAnotherAddressBtn, it.message!!)
                                            }
                                        }
                                    })
                                }
                            }
                        } else {
                            snackBar(
                                binding.addAnotherAddressBtn,
                                "Amount is not equal to bid price or Installments exceeded."
                            )
                        }
                    }
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
            orderRequest.put("image", "https://dev.satrango.com/public/assets/img/logo-black.png")
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
            UserUtils.getInstallmentDetId(this)!!.toInt(),
            RetrofitBuilder.USER_KEY,
            status,
            referenceId,
            UserUtils.getUserId(this).toInt()
        )
        viewModel.installmentPayments(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.addAnotherAddressBtn, it.message!!)
                }
            }
        })
    }

}