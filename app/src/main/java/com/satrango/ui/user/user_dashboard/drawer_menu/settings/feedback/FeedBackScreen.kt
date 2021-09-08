package com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityFeedBackScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.SettingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.SettingsViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models.FeedbackReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import java.text.SimpleDateFormat
import java.util.*

class FeedBackScreen : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBackScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBackScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.suggestions_feed_back)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE

        binding.submitBtn.setOnClickListener {

            if (binding.feedBack.text.toString().isEmpty()) {
                snackBar(binding.feedBack, "Enter Feedback")
            } else {
                submitFeedBackToServer()
            }

        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun submitFeedBackToServer() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val factory = ViewModelFactory(SettingsRepository())
        val viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
        val requestBody = FeedbackReqModel(
            SimpleDateFormat("yyyy-MM-dd").format(Date()),
            binding.feedBack.text.toString().trim(),
            RetrofitBuilder.USER_KEY,
            UserUtils.getUserId(this)
        )
        viewModel.postFeedback(this, requestBody).observe(this, {
            when(it) {
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
        })
    }
}