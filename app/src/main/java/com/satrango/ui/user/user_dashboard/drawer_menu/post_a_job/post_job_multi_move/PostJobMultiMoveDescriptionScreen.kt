package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.post_job_multi_move

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityPostJobMultiMoveDescriptionScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.KeywordsResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.LangResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOneRepository
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOneViewModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobAddressScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Data
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar

class PostJobMultiMoveDescriptionScreen : AppCompatActivity() {

    private lateinit var binding: ActivityPostJobMultiMoveDescriptionScreenBinding
    private lateinit var viewModel: ProviderSignUpOneViewModel
    private lateinit var progressDialog: BeautifulProgressDialog

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

        initializeToolBar()
        initializeProgressDialog()

        val factory = ViewModelFactory(ProviderSignUpOneRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderSignUpOneViewModel::class.java]

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

            oneDay.setOnClickListener {
                oneDay.setBackgroundResource(R.drawable.user_btn_bg)
                threeDays.setBackgroundResource(R.drawable.blue_out_line)
                sevenDays.setBackgroundResource(R.drawable.blue_out_line)
                oneDay.setTextColor(Color.parseColor("#FFFFFF"))
                threeDays.setTextColor(Color.parseColor("#0A84FF"))
                sevenDays.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 1
            }

            threeDays.setOnClickListener {
                threeDays.setBackgroundResource(R.drawable.user_btn_bg)
                oneDay.setBackgroundResource(R.drawable.blue_out_line)
                sevenDays.setBackgroundResource(R.drawable.blue_out_line)
                threeDays.setTextColor(Color.parseColor("#FFFFFF"))
                oneDay.setTextColor(Color.parseColor("#0A84FF"))
                sevenDays.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 3
            }

            sevenDays.setOnClickListener {
                sevenDays.setBackgroundResource(R.drawable.user_btn_bg)
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
    }

    private fun updateUI() {

        val data = Gson().fromJson(UserUtils.EDIT_MY_JOB_POST_DETAILS, MyJobPostViewResModel::class.java)
        binding.title.setText(data.job_post_details.title)

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

        val chips = ArrayList<ChipInfo>()
        for (language in data.languages) {
            chips.add(ChipInfo(language, language))
        }
        binding.languages.setTextWithChips(chips)

        val keywords = ArrayList<ChipInfo>()
        for (keyword in data.keywords) {
            keywords.add(ChipInfo(keyword, keyword))
        }
        binding.keywordSkills.setTextWithChips(keywords)

        when(data.job_post_details.bids_period) {
            "1" -> {
                binding.oneDay.setBackgroundResource(R.drawable.user_btn_bg)
                binding.threeDays.setBackgroundResource(R.drawable.blue_out_line)
                binding.sevenDays.setBackgroundResource(R.drawable.blue_out_line)
                binding.oneDay.setTextColor(Color.parseColor("#FFFFFF"))
                binding.threeDays.setTextColor(Color.parseColor("#0A84FF"))
                binding.sevenDays.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 1
            }
            "3" -> {
                binding.threeDays.setBackgroundResource(R.drawable.user_btn_bg)
                binding.oneDay.setBackgroundResource(R.drawable.blue_out_line)
                binding.sevenDays.setBackgroundResource(R.drawable.blue_out_line)
                binding.threeDays.setTextColor(Color.parseColor("#FFFFFF"))
                binding.oneDay.setTextColor(Color.parseColor("#0A84FF"))
                binding.sevenDays.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 3
            }
            "5" -> {
                binding.sevenDays.setBackgroundResource(R.drawable.user_btn_bg)
                binding.oneDay.setBackgroundResource(R.drawable.blue_out_line)
                binding.threeDays.setBackgroundResource(R.drawable.blue_out_line)
                binding.sevenDays.setTextColor(Color.parseColor("#FFFFFF"))
                binding.threeDays.setTextColor(Color.parseColor("#0A84FF"))
                binding.oneDay.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 7
            }
        }


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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withImage, resources.getString(R.string.loading))
        progressDialog.setImageLocation(resources.getDrawable(R.drawable.circlelogo))
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
    }

}