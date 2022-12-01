package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserMyAccountScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.FundTransferScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models.MyAccountDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.TransactionHistoryScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.UserPlanScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.snackBar

class UserMyAccountScreen : AppCompatActivity() {

    companion object {
        var FROM_MY_ACCOUNT = false
    }

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityUserMyAccountScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMyAccountScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        binding.withDrawBtn.setOnClickListener {
            isProvider(this, false)
            startActivity(Intent(this, FundTransferScreen::class.java))
        }
    }

    private var connectionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                disconnected()
            } else {
                connected()
            }
        }
    }

    private fun connected() {
        loadScreen()
    }

    @SuppressLint("SetTextI18n")
    private fun disconnected() {
        binding.noteText.visibility = View.VISIBLE
        binding.noteText.text = "Internet Connection Lost"
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(connectionReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectionReceiver)
    }

    private fun loadScreen() {
        binding.noteText.visibility = View.GONE
        val factory = ViewModelFactory(MyAccountRepository())
        val viewModel = ViewModelProvider(this, factory)[MyAccountViewModel::class.java]
        viewModel.myAccountDetails(this).observe(this) {
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
        }
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.account)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE
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

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: MyAccountDetailsResModel) {

        binding.apply {

            jobPosts.text = data.total_job_posts
            referrals.text = data.total_referrals
            bookings.text = data.total_bookings
            thisMonth.text = data.commission_earned.this_month.toString()
            previousMonth.text = data.commission_earned.prev_month.toString()
            modBalance.text = "Rs " + "%.2f".format(data.wallet_blocked_amount)
            change.text = data.commission_earned.change.toString()
            availableBalance.text = "Rs " + "%.2f".format(data.wallet_balance)
            FundTransferScreen.availableBalance = data.wallet_balance

            changePlan.setOnClickListener {
                FROM_MY_ACCOUNT = true
                startActivity(Intent(this@UserMyAccountScreen, UserPlanScreen::class.java))
            }
            transactionHistory.setOnClickListener {
                isProvider(this@UserMyAccountScreen, false)
                startActivity(
                    Intent(
                        this@UserMyAccountScreen,
                        TransactionHistoryScreen::class.java
                    )
                )
            }
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this, UserDashboardScreen::class.java))
    }
}