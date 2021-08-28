package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.satrango.R
import com.satrango.databinding.ActivityUserMyAccountScreenBinding
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.TransactionHistoryScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.UserPlanScreen

class UserMyAccountScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserMyAccountScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMyAccountScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.account)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE

        binding.apply {

            changePlan.setOnClickListener { startActivity(Intent(this@UserMyAccountScreen, UserPlanScreen::class.java)) }
            transactionHistory.setOnClickListener { startActivity(Intent(this@UserMyAccountScreen, TransactionHistoryScreen::class.java)) }

        }

    }
}