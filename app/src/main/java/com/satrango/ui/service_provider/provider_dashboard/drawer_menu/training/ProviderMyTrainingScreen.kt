package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderMyTrainingBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.ProviderMyTrainingResModel
import com.satrango.utils.snackBar

class ProviderMyTrainingScreen : AppCompatActivity() {

    private lateinit var data: ProviderMyTrainingResModel
    private lateinit var binding: ActivityProviderMyTrainingBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderMyTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        binding.recentlyAddedRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recommendedForYouRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.watchAgainRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val factory = ViewModelFactory(ProviderMyTrainingRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderMyTrainingViewModel::class.java]
        viewModel.getTrainingList(this).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    data = it.data!!
                    binding.recentlyAddedRV.adapter =
                        ProviderMyTrainingOneAdapter(data.recent_videos)
                    binding.recommendedForYouRV.adapter =
                        ProviderMyTrainingTwoAdapter(data.recommended_videos)
                    binding.watchAgainRV.adapter =
                        ProviderMyTrainingThreeAdapter(data.watched_videos)
                    binding.videosCount.text = "${data.watched_videos_count}/${data.total_videos}"
                    binding.pointsEarned.text = "${data.points_earned}/${data.total_points}"
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.pointsEarned, it.message!!)
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.training_videos)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ProviderDashboard::class.java))
    }

}