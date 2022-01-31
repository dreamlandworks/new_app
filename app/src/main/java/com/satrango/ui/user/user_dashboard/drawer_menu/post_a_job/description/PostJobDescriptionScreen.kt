package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityPostJobDescriptionScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.PostJobAttachmentsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.Data
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class PostJobDescriptionScreen : AppCompatActivity() {

    private lateinit var bidRanges: List<Data>
    private lateinit var binding: ActivityPostJobDescriptionScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobDescriptionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        UserUtils.bid_per = 0
        UserUtils.estimateTypeId = 0

        binding.apply {

            nextBtn.setOnClickListener {

                when {
                    title.text.toString().isEmpty() -> {
                        snackBar(discription, "Enter Title")
                    }
                    discription.text.toString().isEmpty() -> {
                        snackBar(discription, "Enter work description")
                    }
                    UserUtils.bid_per == 0 -> {
                        snackBar(bidRangeSpinner, "Select Bid per")
                    }
                    bidRangeSpinner.selectedItemPosition == 0 -> {
                        snackBar(bidRangeSpinner, "Select Bid Range")
                    }
                    estimateTime.text.toString().isEmpty() -> {
                        snackBar(bidRangeSpinner, "Enter Estimate Time")
                    }
                    UserUtils.estimateTypeId == 0 -> {
                        snackBar(estimateTime, "Select Estimate time type")
                    }
                    else -> {
                        UserUtils.title = title.text.toString().trim()
                        UserUtils.job_description = discription.text.toString().trim()
                        if (UserUtils.estimateTypeId == 1) {
                            UserUtils.estimate_time = estimateTime.text.toString().toInt()
                        } else {
                            UserUtils.estimate_time = estimateTime.text.toString().toInt() * 24
                        }
                        startActivity(
                            Intent(
                                this@PostJobDescriptionScreen,
                                PostJobAttachmentsScreen::class.java
                            )
                        )
                    }
                }
            }

            backBtn.setOnClickListener {
                onBackPressed()
            }

            perHour.setOnClickListener {
                perHour.setBackgroundResource(R.drawable.user_btn_bg)
                perDay.setBackgroundResource(R.drawable.blue_out_line)
                perJob.setBackgroundResource(R.drawable.blue_out_line)
                perHour.setTextColor(Color.parseColor("#FFFFFF"))
                perDay.setTextColor(Color.parseColor("#0A84FF"))
                perJob.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bid_per = 1
            }

            perDay.setOnClickListener {
                perDay.setBackgroundResource(R.drawable.user_btn_bg)
                perHour.setBackgroundResource(R.drawable.blue_out_line)
                perJob.setBackgroundResource(R.drawable.blue_out_line)
                perDay.setTextColor(Color.parseColor("#FFFFFF"))
                perHour.setTextColor(Color.parseColor("#0A84FF"))
                perJob.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bid_per = 2
            }

            perJob.setOnClickListener {
                perJob.setBackgroundResource(R.drawable.user_btn_bg)
                perDay.setBackgroundResource(R.drawable.blue_out_line)
                perHour.setBackgroundResource(R.drawable.blue_out_line)
                perJob.setTextColor(Color.parseColor("#FFFFFF"))
                perDay.setTextColor(Color.parseColor("#0A84FF"))
                perHour.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bid_per = 4
            }

            hours.setOnClickListener {
                hours.setBackgroundResource(R.drawable.user_btn_bg)
                days.setBackgroundResource(R.drawable.blue_out_line)
                hours.setTextColor(Color.parseColor("#FFFFFF"))
                days.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.estimateTypeId = 1
            }

            days.setOnClickListener {
                days.setBackgroundResource(R.drawable.user_btn_bg)
                hours.setBackgroundResource(R.drawable.blue_out_line)
                days.setTextColor(Color.parseColor("#FFFFFF"))
                hours.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.estimateTypeId = 2
            }

        }

        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        viewModel.bidRanges(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    bidRanges = it.data!!
                    val bidRangesArray = ArrayList<String>()
                    bidRangesArray.add("Select Bid Range")
                    for (bid in bidRanges) {
                        bidRangesArray.add(bid.range_slots)
                    }
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        bidRangesArray
                    )
                    binding.bidRangeSpinner.adapter = adapter
                    binding.bidRangeSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                UserUtils.bid_range_id =
                                    bidRanges[position + 1].bid_range_id.toInt()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                        }
                    if (UserUtils.EDIT_MY_JOB_POST) {
                        updateUI()
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.backBtn, it.message!!)
                }
            }
        })

    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.post_a_job)
        val image = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(image)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun updateUI() {
        val data =
            Gson().fromJson(UserUtils.EDIT_MY_JOB_POST_DETAILS, MyJobPostViewResModel::class.java)
        binding.title.setText(data.job_post_details.title)
        binding.discription.setText(data.job_details[0].job_description)
        when (data.job_post_details.bid_per) {
            "1" -> {
                binding.perHour.setBackgroundResource(R.drawable.user_btn_bg)
                binding.perDay.setBackgroundResource(R.drawable.blue_out_line)
                binding.perJob.setBackgroundResource(R.drawable.blue_out_line)
                binding.perHour.setTextColor(Color.parseColor("#FFFFFF"))
                binding.perDay.setTextColor(Color.parseColor("#0A84FF"))
                binding.perJob.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bid_per = 1
            }
            "2" -> {
                binding.perDay.setBackgroundResource(R.drawable.user_btn_bg)
                binding.perHour.setBackgroundResource(R.drawable.blue_out_line)
                binding.perJob.setBackgroundResource(R.drawable.blue_out_line)
                binding.perDay.setTextColor(Color.parseColor("#FFFFFF"))
                binding.perHour.setTextColor(Color.parseColor("#0A84FF"))
                binding.perJob.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bid_per = 2
            }
            "4" -> {
                binding.perJob.setBackgroundResource(R.drawable.user_btn_bg)
                binding.perDay.setBackgroundResource(R.drawable.blue_out_line)
                binding.perHour.setBackgroundResource(R.drawable.blue_out_line)
                binding.perJob.setTextColor(Color.parseColor("#FFFFFF"))
                binding.perDay.setTextColor(Color.parseColor("#0A84FF"))
                binding.perHour.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bid_per = 4
            }
        }
        for (index in bidRanges.indices) {
            if (bidRanges[index].range_slots == data.job_post_details.range_slots) {
                binding.bidRangeSpinner.setSelection(index + 1)
            }
        }
        binding.estimateTime.setText(data.job_post_details.estimate_time)
        when (data.job_post_details.estimate_type) {
            "Hours" -> {
                binding.hours.setBackgroundResource(R.drawable.user_btn_bg)
                binding.days.setBackgroundResource(R.drawable.blue_out_line)
                binding.hours.setTextColor(Color.parseColor("#FFFFFF"))
                binding.days.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.estimateTypeId = 1
            }
            "Days" -> {
                binding.days.setBackgroundResource(R.drawable.user_btn_bg)
                binding.hours.setBackgroundResource(R.drawable.blue_out_line)
                binding.days.setTextColor(Color.parseColor("#FFFFFF"))
                binding.hours.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.estimateTypeId = 2
            }

        }


    }

}