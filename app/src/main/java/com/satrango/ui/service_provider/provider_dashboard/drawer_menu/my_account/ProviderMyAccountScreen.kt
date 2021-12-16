package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account

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
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderMyAccountScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMyAccountResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews.ProviderReviewScreen
import com.satrango.ui.service_provider.provider_dashboard.plans.ProviderPlansScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.FundTransferScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.TransactionHistoryScreen
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class ProviderMyAccountScreen : AppCompatActivity() {

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityProviderMyAccountScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderMyAccountScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        val factory = ViewModelFactory(ProviderMyAccountRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderMyAccountViewModel::class.java]
        viewModel.myAccount(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    updateUI(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.applyBtn, it.message!!)
                }

            }
        })

        binding.changePlan.setOnClickListener {
            startActivity(Intent(this, ProviderPlansScreen::class.java))
        }

        binding.withDrawBtn.setOnClickListener {
            FundTransferScreen.FROM_PROVIDER = true
            startActivity(Intent(this, FundTransferScreen::class.java))
        }

    }

    private fun initializeToolBar() {
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
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: ProviderMyAccountResModel) {

        binding.apply {
            tasksCount.text = "${data.total_completed_bookings}/${data.total_bookings}"
            bidsCount.text = "${data.total_completed_bids}/${data.total_bids}"
            referralsCount.text = data.total_referrals
            earningsCount.text = "Rs ${data.commission_earned.this_month}"
            thisMonth.text = "Rs ${data.commission_earned.this_month}"
            previousMonth.text = "Rs ${data.commission_earned.prev_month}"
            change.text = "Rs ${data.commission_earned.change}"
            reviewsCount.text = data.total_reviews
            currentPlan.text = data.activated_plan
            earningsCount.text = "Rs ${data.wallet_balance}"
            FundTransferScreen.availableBalance = data.wallet_balance

            transactionHistory.setOnClickListener {
                TransactionHistoryScreen.FROM_PROVIDER = true
                startActivity(Intent(this@ProviderMyAccountScreen, TransactionHistoryScreen::class.java))
            }
            myReviews.setOnClickListener {
                startActivity(Intent(this@ProviderMyAccountScreen, ProviderReviewScreen::class.java))
            }
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this, ProviderDashboard::class.java))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }
}