package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityPostJobDescriptionScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.PostJobAttachmentsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.Data
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar

class PostJobDescriptionScreen : AppCompatActivity() {

    private lateinit var bidRanges: List<Data>
    private lateinit var binding: ActivityPostJobDescriptionScreenBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobDescriptionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.post_a_job)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

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
                        startActivity(Intent(this@PostJobDescriptionScreen, PostJobAttachmentsScreen::class.java))
                    }
                }
            }

            backBtn.setOnClickListener {
                onBackPressed()
            }

            perHour.setOnClickListener {
                perHour.setBackgroundResource(R.drawable.btn_bg)
                perDay.setBackgroundResource(R.drawable.blue_out_line)
                perJob.setBackgroundResource(R.drawable.blue_out_line)
                perHour.setTextColor(Color.parseColor("#FFFFFF"))
                perDay.setTextColor(Color.parseColor("#0A84FF"))
                perJob.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bid_per = 1
            }

            perDay.setOnClickListener {
                perDay.setBackgroundResource(R.drawable.btn_bg)
                perHour.setBackgroundResource(R.drawable.blue_out_line)
                perJob.setBackgroundResource(R.drawable.blue_out_line)
                perDay.setTextColor(Color.parseColor("#FFFFFF"))
                perHour.setTextColor(Color.parseColor("#0A84FF"))
                perJob.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bid_per = 2
            }

            perJob.setOnClickListener {
                perJob.setBackgroundResource(R.drawable.btn_bg)
                perDay.setBackgroundResource(R.drawable.blue_out_line)
                perHour.setBackgroundResource(R.drawable.blue_out_line)
                perJob.setTextColor(Color.parseColor("#FFFFFF"))
                perDay.setTextColor(Color.parseColor("#0A84FF"))
                perHour.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bid_per = 4
            }

            hours.setOnClickListener {
                hours.setBackgroundResource(R.drawable.btn_bg)
                days.setBackgroundResource(R.drawable.blue_out_line)
                hours.setTextColor(Color.parseColor("#FFFFFF"))
                days.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.estimateTypeId = 1
            }

            days.setOnClickListener {
                days.setBackgroundResource(R.drawable.btn_bg)
                hours.setBackgroundResource(R.drawable.blue_out_line)
                days.setTextColor(Color.parseColor("#FFFFFF"))
                hours.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.estimateTypeId = 2
            }

        }

        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        viewModel.bidRanges(this).observe(this, {
            when(it) {
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
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bidRangesArray)
                    binding.bidRangeSpinner.adapter = adapter
                    binding.bidRangeSpinner.setOnItemClickListener { parent, view, position, id -> UserUtils.bid_range_id = bidRanges[position + 1].bid_range_id.toInt() }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.backBtn, it.message!!)
                }
            }
        })

    }

}