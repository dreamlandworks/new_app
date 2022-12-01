package com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityFeedBackScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.SettingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.SettingsViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.UserSettingsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models.FeedbackReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import java.text.SimpleDateFormat
import java.util.*

class FeedBackScreen : AppCompatActivity() {

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityFeedBackScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBackScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        binding.submitBtn.setOnClickListener {

            if (binding.feedBack.text.toString().isEmpty()) {
                snackBar(binding.feedBack, "Enter Feedback")
            } else {
                submitFeedBackToServer()
            }

        }

    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.suggestions_feed_back)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE
        if (UserUtils.isProvider(this)) {
            binding.resetBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.resetBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.submitBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.setStatusBarColor(resources.getColor(R.color.purple_700))
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withImage, resources.getString(R.string.loading))
        if (UserUtils.isProvider(this)) {
            progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        } else {
            progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        }
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
    }

    @SuppressLint("SimpleDateFormat")
    private fun submitFeedBackToServer() {

        val factory = ViewModelFactory(SettingsRepository())
        val viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
        val userType = if (UserUtils.isProvider(this)) {
            "2"
        } else {
            "1"
        }
        val requestBody = FeedbackReqModel(
            SimpleDateFormat("yyyy-MM-dd").format(Date()),
            binding.feedBack.text.toString().trim(),
            RetrofitBuilder.USER_KEY,
            UserUtils.getUserId(this),
            userType
        )
        viewModel.postFeedback(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.feedBack.setText("")
                    snackBar(binding.feedBack, it.data!!.message + "... Thanks for your Feedback")
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.feedBack, it.message!!)
                }
            }
        }
    }
}