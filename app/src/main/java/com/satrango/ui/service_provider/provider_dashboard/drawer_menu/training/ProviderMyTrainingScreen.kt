package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderMyTrainingBinding
import com.satrango.remote.NetworkResponse
import com.satrango.utils.snackBar

class ProviderMyTrainingScreen : AppCompatActivity() {

    private lateinit var binding: ActivityProviderMyTrainingBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderMyTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recentlyAddedRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recommendedForYouRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.watchAgainRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        initializeProgressDialog()

        val factory = ViewModelFactory(ProviderMyTrainingRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderMyTrainingViewModel::class.java]
        viewModel.getTrainingList(this, "").observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val data = it.data!!
                    binding.recentlyAddedRV.adapter = ProviderMyTrainingOneAdapter(data.recent_videos)
                    binding.recommendedForYouRV.adapter = ProviderMyTrainingTwoAdapter(data.recommended_videos)
                    binding.watchAgainRV.adapter = ProviderMyTrainingThreeAdapter(data.watched_videos)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.pointsEarned, it.message!!)
                }
            }
        })
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}