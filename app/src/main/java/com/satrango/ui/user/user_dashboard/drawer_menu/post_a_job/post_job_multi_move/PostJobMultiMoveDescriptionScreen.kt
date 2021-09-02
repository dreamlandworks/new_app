package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.post_job_multi_move

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityPostJobMultiMoveDescriptionScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOneRepository
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOneViewModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel
import com.satrango.ui.user.bookings.booking_address.models.Attachment
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobAddressScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Data
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.KeywordsResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.LangResponse
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class PostJobMultiMoveDescriptionScreen : AppCompatActivity() {

    private lateinit var binding: ActivityPostJobMultiMoveDescriptionScreenBinding
    private lateinit var viewModel: ProviderSignUpOneViewModel
    private lateinit var progressDialog: ProgressDialog

    private lateinit var responseLanguages: ProviderOneModel
    private lateinit var keywordsMList: List<Data>
    private lateinit var bidRanges: List<com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.Data>

    companion object {

        lateinit var finalKeywords: java.util.ArrayList<KeywordsResponse>
        lateinit var finalLanguages: java.util.ArrayList<LangResponse>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobMultiMoveDescriptionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.post_a_job)

        val factory = ViewModelFactory(ProviderSignUpOneRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderSignUpOneViewModel::class.java]

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")

        loadLanguages()
        loadKeyWords()

        binding.apply {

            nextBtn.setOnClickListener {

                finalLanguages = ArrayList()
                for (lang in languages.allChips) {
                    for (lan in responseLanguages.data.language) {
                        if (lang.text.toString().equals(lan.name, ignoreCase = true)) {
                            finalLanguages.add(LangResponse(lan.id, lan.name))
                        }
                    }
                }
                finalKeywords = ArrayList()
                for (key in keywordSkills.allChips) {
                    for (k in keywordsMList) {
                        if (key.text.toString().equals(k.keyword, ignoreCase = true)) {
                            finalKeywords.add(KeywordsResponse(k.keyword_id, k.keyword))
                        }
                    }
                }

                when {
                    title.text.toString().isEmpty() -> {
                        snackBar(title, "Enter Title")
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
                    finalLanguages.size == 0 -> {
                        snackBar(nextBtn, "Select Languages")
                    }
                    finalKeywords.size == 0 -> {
                        snackBar(nextBtn, "Select Skills / Keywords")
                    }
                    UserUtils.bids_period == 0 -> {
                        snackBar(nextBtn, "Select Accept Bid Per")
                    }
                    else -> {
                        UserUtils.title = title.text.toString().trim()
                        if (UserUtils.estimateTypeId == 1) {
                            UserUtils.estimate_time = estimateTime.text.toString().toInt()
                        } else {
                            UserUtils.estimate_time = estimateTime.text.toString().toInt() * 24
                        }
                        startActivity(Intent(this@PostJobMultiMoveDescriptionScreen, PostJobAddressScreen::class.java))
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

            oneDay.setOnClickListener {
                oneDay.setBackgroundResource(R.drawable.btn_bg)
                threeDays.setBackgroundResource(R.drawable.blue_out_line)
                sevenDays.setBackgroundResource(R.drawable.blue_out_line)
                oneDay.setTextColor(Color.parseColor("#FFFFFF"))
                threeDays.setTextColor(Color.parseColor("#0A84FF"))
                sevenDays.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 1
            }

            threeDays.setOnClickListener {
                threeDays.setBackgroundResource(R.drawable.btn_bg)
                oneDay.setBackgroundResource(R.drawable.blue_out_line)
                sevenDays.setBackgroundResource(R.drawable.blue_out_line)
                threeDays.setTextColor(Color.parseColor("#FFFFFF"))
                oneDay.setTextColor(Color.parseColor("#0A84FF"))
                sevenDays.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 3
            }

            sevenDays.setOnClickListener {
                sevenDays.setBackgroundResource(R.drawable.btn_bg)
                oneDay.setBackgroundResource(R.drawable.blue_out_line)
                threeDays.setBackgroundResource(R.drawable.blue_out_line)
                sevenDays.setTextColor(Color.parseColor("#FFFFFF"))
                threeDays.setTextColor(Color.parseColor("#0A84FF"))
                oneDay.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 7
            }
        }

        val bidFactory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, bidFactory)[PostJobViewModel::class.java]
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
                    binding.bidRangeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            UserUtils.bid_range_id = bidRanges[position + 1].bid_range_id.toInt()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.backBtn, it.message!!)
                }
            }
        })

    }

    private fun loadLanguages() {
        viewModel.professionsList(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    responseLanguages = it.data!!

                    val languagesList = ArrayList<String>()
                    for (data in responseLanguages.data.language) {
                        languagesList.add(data.name)
                    }
                    val languagesAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        languagesList
                    )
                    binding.languages.setAdapter(languagesAdapter)
                    binding.languages.addChipTerminator(
                        '\n',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL
                    )
                    binding.languages.addChipTerminator(
                        ' ',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
                    )
                    binding.languages.addChipTerminator(
                        ',',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
                    )
                    binding.languages.addChipTerminator(
                        ';',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN
                    )
                    binding.languages.setNachoValidator(ChipifyingNachoValidator())
                    binding.languages.enableEditChipOnTouch(true, true)

                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.keywordSkills, it.message!!)
                    snackBar(binding.keywordSkills, "Click Reset to get language values")
                }
            }
        })
    }

    private fun loadKeyWords() {
        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        viewModel.skills(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    keywordsMList = it.data!!
                    val keywordsList = ArrayList<String>()
                    for (data in keywordsMList) {
                        keywordsList.add(data.keyword)
                    }
                    val languagesAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        keywordsList
                    )
                    binding.keywordSkills.setAdapter(languagesAdapter)
                    binding.keywordSkills.addChipTerminator(
                        '\n',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL
                    )
                    binding.keywordSkills.addChipTerminator(
                        ' ',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
                    )
                    binding.keywordSkills.addChipTerminator(
                        ',',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
                    )
                    binding.keywordSkills.addChipTerminator(
                        ';',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN
                    )
                    binding.keywordSkills.setNachoValidator(ChipifyingNachoValidator())
                    binding.keywordSkills.enableEditChipOnTouch(true, true)
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        })
    }

}