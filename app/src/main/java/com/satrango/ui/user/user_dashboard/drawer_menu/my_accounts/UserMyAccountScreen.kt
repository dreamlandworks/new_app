package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
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
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models.MyAccountDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.TransactionHistoryScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.UserPlanScreen
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

        val factory = ViewModelFactory(MyAccountRepository())
        val viewModel = ViewModelProvider(this, factory)[MyAccountViewModel::class.java]
        viewModel.myAccountDetails(this).observe(this, {
            when(it) {
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
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withImage, resources.getString(R.string.loading))
        progressDialog.setImageLocation(resources.getDrawable(R.drawable.circlelogo))
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
    }

    private fun updateUI(data: MyAccountDetailsResModel) {

        binding.apply {

            jobPosts.text = data.total_job_posts
            referrals.text = data.total_referrals
            bookings.text = data.total_bookings
            thisMonth.text = data.commission_earned.this_month.toString()
            previousMonth.text = data.commission_earned.prev_month.toString()
            change.text = data.commission_earned.change.toString()
            currentPlan.text = data.activated_plan

            changePlan.setOnClickListener {
                FROM_MY_ACCOUNT = true
                startActivity(Intent(this@UserMyAccountScreen, UserPlanScreen::class.java))
            }
            transactionHistory.setOnClickListener {
                TransactionHistoryScreen.FROM_PROVIDER = false
                startActivity(Intent(this@UserMyAccountScreen, TransactionHistoryScreen::class.java))
            }
        }

    }
}