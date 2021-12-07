package com.satrango.ui.auth.provider_signup.provider_sign_up_two

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.hootsuite.nachos.chip.Chip
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderSignUpTwoBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.KeywordsResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_three.ProviderSignUpThree
import com.satrango.utils.ProviderUtils
import com.satrango.utils.snackBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProviderSignUpTwo : AppCompatActivity() {

    private var profession_Id: String = ""
    private lateinit var keywordsMList: List<com.satrango.ui.auth.provider_signup.provider_sign_up_two.models.Data>
    private lateinit var viewModel: ProviderSignUpTwoViewModel
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityProviderSignUpTwoBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(com.satrango.R.color.purple_700))
        }

        initializeProgressDialog()

        profession_Id = intent.getStringExtra("profession_id")!!

        val factory = ViewModelFactory(ProviderSignUpTwoRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderSignUpTwoViewModel::class.java]

        binding.apply {

            for ((index, profession) in ProviderUtils.profession!!.withIndex()) {
                val rdbtn = RadioButton(this@ProviderSignUpTwo)
                rdbtn.id = index
                rdbtn.text = profession.name
                rdbtn.setOnCheckedChangeListener { _, isChecked ->
                    if (!isChecked) {
                        for (chip in binding.keywordSkills.allChips) {
                            for (key in keywordsMList) {
                                var existed = false
                                val chipText = chip.text.toString()
                                if (chipText == key.keyword) {
                                    for (keyword in ProviderUtils.profession!![index].keywords_responses) {
                                        if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
                                            existed = true
                                        }
                                    }
                                    if (!existed) {
                                        ProviderUtils.profession!![index].keywords_responses.add(KeywordsResponse(key.id, key.keyword))
                                    }
                                } else {
                                    for (keyword in ProviderUtils.profession!![index].keywords_responses) {
                                        if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
                                            existed = true
                                        }
                                    }
                                    if (!existed) {
                                        ProviderUtils.profession!![index].keywords_responses.add(KeywordsResponse("0", chipText))
                                    }
                                }
                            }
                            ProviderUtils.profession!![index].keywords_responses = ProviderUtils.profession!![index].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
                        }
                        keywordSkills.setText(emptyList())
                    }
                    if (isChecked) {
                        skillsText.text = "Keywords/Skills for ${ProviderUtils.profession!![index].name} are"
                        keywordSkills.visibility = View.VISIBLE
                        if (ProviderUtils.profession!![index].keywords_responses.isNotEmpty()) {
                            val chips = ArrayList<ChipInfo>()
                            for (key in ProviderUtils.profession!![index].keywords_responses) {
                                chips.add(ChipInfo(key.name, key.keyword_id))
                                Log.e("KEYS:", "${ProviderUtils.profession!![index].name}, $index" + Gson().toJson(key))
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                keywordSkills.setTextWithChips(chips)
                            }
                        }
                    }
                }
                rdbtn.setOnClickListener {
                    if (rdbtn.isChecked) {
                        loadKeyWords(profession.prof_id)
                    }
                }
                professionRadioGroup.addView(rdbtn)
            }

            aboutMe.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    val btn = findViewById<RadioButton>(professionRadioGroup.checkedRadioButtonId)
                    if (btn != null) {
                        btn.isChecked = false
                        btn.clearFocus()
                    } else {
                        snackBar(aboutMe, "Please Select Profession For Keyword Selection")
                    }
                }
            }

            nextBtn.setOnClickListener {
                validateFields()
            }

            backBtn.setOnClickListener { onBackPressed() }

        }

    }

    private fun loadKeyWords(professionId: String) {
        viewModel.getKeywords(this, professionId).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    keywordsMList = it.data!!
                    Log.e("KEYWORDS:", Gson().toJson(keywordsMList))
                    val keywordsList = ArrayList<String>()
                    for (data in keywordsMList) {
                        keywordsList.add(data.keyword)
                    }
                    val languagesAdapter =
                        ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, keywordsList)
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

    private fun validateFields() {
        val btn = findViewById<RadioButton>(binding.professionRadioGroup.checkedRadioButtonId)
        if (btn != null) {
            btn.isChecked = false
            btn.clearFocus()
        }
        for (keyword in ProviderUtils.profession!!) {
            if (keyword.keywords_responses.isEmpty()) {
                snackBar(binding.nextBtn, "Please enter keywords for ${keyword.name} profession")
                return
            }
        }
        when {
            binding.aboutMe.text.toString().isEmpty() -> {
                snackBar(binding.nextBtn, "Enter About Me Section")
            }
            else -> {
                Log.e("PROFESSION:", Gson().toJson(ProviderUtils.profession))
                ProviderUtils.aboutMe = binding.aboutMe.text.toString().trim()
                startActivity(Intent(this@ProviderSignUpTwo, ProviderSignUpThree::class.java))
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            this, BeautifulProgressDialog.withGIF, resources.getString(
                com.satrango.R.string.loading
            )
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${com.satrango.R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(com.satrango.R.color.progressDialogColor))
    }
}