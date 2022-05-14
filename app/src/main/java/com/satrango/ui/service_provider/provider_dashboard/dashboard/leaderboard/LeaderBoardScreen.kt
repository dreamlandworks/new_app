package com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityLeaderBoardScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.models.Data
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.ProviderMyTrainingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.ProviderMyTrainingViewModel
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast

class LeaderBoardScreen : AppCompatActivity() {

    private lateinit var viewModel: ProviderMyTrainingViewModel
    private lateinit var binding: ActivityLeaderBoardScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog
    val points =
        "1 x positive review - 5 points <br/> 1 x overall rating (> 4.5 Stars) - 2 Points <br/>1 x overall rating (> 3.5 Stars and < 4.5 Stars) - 1 Point <br/>1 x Job Completed - 3 Points <br/>1 x (5 Stars in Professionalism) - 3 Points <br/>1 x bid submitted - 1 Point <br/>1 x Training video watched - 1 Point <br/>3 x Timely Submissions - 3 Points"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderBoardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeToolBar()
        initializeProgressDialog()

        val factory = ViewModelFactory(ProviderMyTrainingRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderMyTrainingViewModel::class.java]

        viewModel.getCitiesList(this).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()

                    val cities = it.data!!.data
                    val cityNames = ArrayList<String>()
                    for (city in cities) {
                        cityNames.add(city.city)
                    }
                    val adapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cityNames)
                    binding.citySpinner.adapter = adapter
                    binding.citySpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                loadLeaderShipDetails(cities[position].id)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.audience, it.message!!)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun loadLeaderShipDetails(cityId: String) {
        viewModel.getLeaderboardList(this, cityId).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val data = it.data!!

                    binding.apply {
                        rank.text = "#${data.sp_data.rank}"
                        pointsCount.text = "${data.sp_data.points_count} Points"
                        loadProfileImage(profileImage)
                        spName.text = "${data.sp_data.fname} ${data.sp_data.lname}"
                        professionName.text = data.sp_data.profession
                        leaderBoardPoints.text = Html.fromHtml(points)
                        rating.text = data.sp_data.rating
                        audience.text = data.sp_data.total_people
                        topPerformersRV.adapter = LeaderboardAdapter(data.data)
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.audience, it.message!!)
                }
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            this,
            BeautifulProgressDialog.withGIF,
            resources.getString(R.string.loading)
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.leaderboard)
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