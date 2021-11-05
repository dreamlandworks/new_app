package com.satrango.ui.service_provider.provider_dashboard.plans

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderPlansScreensBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.ProviderPaymentListener
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMemberShipPlanPaymentReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ProviderPlansScreen : AppCompatActivity(), ProviderPaymentListener, PaymentResultListener {

    private var paymentData: com.satrango.ui.service_provider.provider_dashboard.plans.models.Data? =
        null
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityProviderPlansScreensBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderPlansScreensBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgressDialog()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.purple_700))
        }

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn)
            .setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
            .setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.my_account)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        val factory = ViewModelFactory(ProviderPlansRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderPlansViewModel::class.java]
        viewModel.plans(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.recyclerView.adapter =
                        ProviderPlanAdapter(it.data!!.data, it.data.activated_plan, this)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })

    }

    private fun makePayment() {
        val checkout = Checkout()
        checkout.setKeyID(getString(R.string.razorpay_api_key))
        try {
            val orderRequest = JSONObject()
            orderRequest.put("currency", "INR")
            orderRequest.put(
                "amount",
                paymentData!!.amount.toDouble() * 100
            ) // 500rs * 100 = 50000 paisa passed
            orderRequest.put("receipt", "order_rcptid_${System.currentTimeMillis()}")
            orderRequest.put("image", "https://dev.satrango.com/public/assets/img/logo-black.png")
            orderRequest.put("theme.color", R.color.blue)
            checkout.open(this, orderRequest)
        } catch (e: Exception) {
            toast(this, e.message!!)
        }
    }

    private fun showSuccessDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.payment_success_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val homeBtn = dialogView.findViewById<TextView>(R.id.closBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        homeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    override fun onPaymentSuccess(paymentId: String?) {
        val factory = ViewModelFactory(ProviderPlansRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderPlansViewModel::class.java]
        val requestBody = ProviderMemberShipPlanPaymentReqModel(
            paymentData!!.amount.toInt(),
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            RetrofitBuilder.USER_KEY,
            "Success",
            paymentData!!.period.toInt(),
            paymentData!!.id.toInt(),
            paymentId!!,
            UserUtils.getUserId(this).toInt()
        )

        viewModel.saveMemberShip(this, requestBody).observe(this, {
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
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        snackBar(binding.recyclerView, "Payment Failed")
    }

    override fun loadPayment(data: com.satrango.ui.service_provider.provider_dashboard.plans.models.Data) {
        paymentData = data
        if (data.premium_tag == "Yes") {
            Checkout.preload(applicationContext)
            startActivity(Intent(this, UserDashboardScreen::class.java))
            makePayment()
        } else {
            showSuccessDialog()
        }
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
}