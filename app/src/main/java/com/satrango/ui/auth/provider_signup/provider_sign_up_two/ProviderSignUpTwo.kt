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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderSignUpTwoBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.KeywordsResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_three.ProviderSignUpThree
import com.satrango.ui.auth.provider_signup.provider_sign_up_two.models.Data
import com.satrango.utils.ProviderUtils
import com.satrango.utils.snackBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class ProviderSignUpTwo : AppCompatActivity() {

    private var profession_Id: String = ""
    private lateinit var keywordsOneMList: List<Data>
    private lateinit var keywordsTwoMList: List<Data>
    private lateinit var keywordsThreeMList: List<Data>
    private lateinit var keywordsMList: ArrayList<List<Data>>
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
        keywordsMList = ArrayList()

        profession_Id = intent.getStringExtra("profession_id")!!

        val factory = ViewModelFactory(ProviderSignUpTwoRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderSignUpTwoViewModel::class.java]

        when (ProviderUtils.profession!!.size) {
            1 -> {
                binding.profOneSkillsText.text =
                    "Keywords for ${ProviderUtils.profession!![0].name} are"
                binding.layoutTwo.visibility = View.GONE
                binding.layoutThree.visibility = View.GONE
            }
            2 -> {
                binding.profOneSkillsText.text =
                    "Keywords for ${ProviderUtils.profession!![0].name} are"
                binding.profTwoSkillsText.text =
                    "Keywords for ${ProviderUtils.profession!![1].name} are"
                binding.layoutThree.visibility = View.GONE
            }
            3 -> {
                binding.profOneSkillsText.text =
                    "Keywords for ${ProviderUtils.profession!![0].name} are"
                binding.profTwoSkillsText.text =
                    "Keywords for ${ProviderUtils.profession!![1].name} are"
                binding.profThreeSkillsText.text =
                    "Keywords for ${ProviderUtils.profession!![2].name} are"
            }
        }

//        Log.e("PROFESSION:", Gson().toJson(ProviderUtils.profession!!))
        binding.profOnekeywordSkills.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                loadKeyWords(ProviderUtils.profession!![0].prof_id, 0)
            } else {

                if (binding.profOnekeywordSkills.allChips.isNotEmpty()) {
                    ProviderUtils.profession!![0].keywords_responses = ArrayList()
                    for (chip in binding.profOnekeywordSkills.allChips) {
                        for (key in keywordsOneMList) {
                            Log.e("COMPARE:", "${chip.text} | ${key.keyword}")
                            var existed = false
                            val chipText = chip.text.toString()
                            if (chipText.lowercase(Locale.getDefault()).trim() == key.keyword.lowercase(
                                    Locale.getDefault()
                                )
                                    .trim()) {
                                for (keyword in ProviderUtils.profession!![0].keywords_responses) {
                                    if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
                                        existed = true
                                    }
                                }
                                if (!existed) {
                                    ProviderUtils.profession!![0].keywords_responses.add(
                                        KeywordsResponse(key.id, key.keyword)
                                    )
                                }
                            } else {
                                for (keyword in ProviderUtils.profession!![0].keywords_responses) {
                                    if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
                                        existed = true
                                    }
                                }
                                if (!existed) {
                                    ProviderUtils.profession!![0].keywords_responses.add(
                                        KeywordsResponse("0", chipText)
                                    )
                                }
                            }
                        }
                        ProviderUtils.profession!![0].keywords_responses =
                            ProviderUtils.profession!![0].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
                    }
                }
            }
        }
        binding.profTwokeywordSkills.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                loadKeyWords(ProviderUtils.profession!![1].prof_id, 1)
            } else {
                if (binding.profTwokeywordSkills.allChips.isNotEmpty()) {
                    ProviderUtils.profession!![1].keywords_responses = ArrayList()
                    for (chip in binding.profTwokeywordSkills.allChips) {
                        for (key in keywordsTwoMList) {
                            var existed = false
                            val chipText = chip.text.toString()
                            if (chipText.lowercase(Locale.getDefault()).trim() == key.keyword.lowercase(Locale.getDefault()).trim()) {
                                for (keyword in ProviderUtils.profession!![1].keywords_responses) {
                                    if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
                                        existed = true
                                    }
                                }
                                if (!existed) {
                                    ProviderUtils.profession!![1].keywords_responses.add(
                                        KeywordsResponse(key.id, key.keyword)
                                    )
                                }
                            } else {
                                for (keyword in ProviderUtils.profession!![1].keywords_responses) {
                                    if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
                                        existed = true
                                    }
                                }
                                if (!existed) {
                                    ProviderUtils.profession!![1].keywords_responses.add(
                                        KeywordsResponse("0", chipText)
                                    )
                                }
                            }
                        }
                        ProviderUtils.profession!![1].keywords_responses =
                            ProviderUtils.profession!![1].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
                    }
                }
            }
        }
        binding.profThreekeywordSkills.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                loadKeyWords(ProviderUtils.profession!![2].prof_id, 2)
            } else {
                if (ProviderUtils.profession!!.size > 2) {
                    ProviderUtils.profession!![2].keywords_responses = ArrayList()
                    if (binding.profThreekeywordSkills.allChips.isNotEmpty()) {
                        for (chip in binding.profThreekeywordSkills.allChips) {
                            for (key in keywordsThreeMList) {
                                var existed = false
                                val chipText = chip.text.toString()
                                if (chipText.lowercase(Locale.getDefault()).trim() == key.keyword.lowercase(Locale.getDefault()).trim()) {
                                    for (keyword in ProviderUtils.profession!![2].keywords_responses) {
                                        if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
                                            existed = true
                                        }
                                    }
                                    if (!existed) {
                                        ProviderUtils.profession!![2].keywords_responses.add(
                                            KeywordsResponse(key.id, key.keyword)
                                        )
                                    }
                                } else {
                                    for (keyword in ProviderUtils.profession!![2].keywords_responses) {
                                        if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
                                            existed = true
                                        }
                                    }
                                    if (!existed) {
                                        ProviderUtils.profession!![2].keywords_responses.add(
                                            KeywordsResponse("0", chipText)
                                        )
                                    }
                                }
                            }
                            ProviderUtils.profession!![2].keywords_responses =
                                ProviderUtils.profession!![2].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
                        }
                    }
                }

            }
        }
//        for (index in ProviderUtils.profession!!.indices) {
//            Log.e("LOG:", ProviderUtils.profession!![index].prof_id)
//            loadKeyWords(ProviderUtils.profession!![index].prof_id, index)
//        }

        binding.apply {

//            for ((index, profession) in ProviderUtils.profession!!.withIndex()) {
//                val rdbtn = RadioButton(this@ProviderSignUpTwo)
//                rdbtn.id = index
//                rdbtn.text = profession.name
//                rdbtn.setOnCheckedChangeListener { _, isChecked ->
//                    if (!isChecked) {
//                        for (chip in binding.keywordSkills.allChips) {
//                            for (key in keywordsMList) {
//                                var existed = false
//                                val chipText = chip.text.toString()
//                                if (chipText == key.keyword) {
//                                    for (keyword in ProviderUtils.profession!![index].keywords_responses) {
//                                        if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
//                                            existed = true
//                                        }
//                                    }
//                                    if (!existed) {
//                                        ProviderUtils.profession!![index].keywords_responses.add(
//                                            KeywordsResponse(key.id, key.keyword)
//                                        )
//                                    }
//                                } else {
//                                    for (keyword in ProviderUtils.profession!![index].keywords_responses) {
//                                        if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
//                                            existed = true
//                                        }
//                                    }
//                                    if (!existed) {
//                                        ProviderUtils.profession!![index].keywords_responses.add(
//                                            KeywordsResponse("0", chipText)
//                                        )
//                                    }
//                                }
//                            }
//                            ProviderUtils.profession!![index].keywords_responses =
//                                ProviderUtils.profession!![index].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
//                        }
//                        keywordSkills.setText(emptyList())
//                    }
//                    if (isChecked) {
//                        skillsText.text =
//                            "Keywords/Skills for ${ProviderUtils.profession!![index].name} are"
//                        keywordSkills.visibility = View.VISIBLE
//                        if (ProviderUtils.profession!![index].keywords_responses.isNotEmpty()) {
//                            val chips = ArrayList<ChipInfo>()
//                            for (key in ProviderUtils.profession!![index].keywords_responses) {
//                                chips.add(ChipInfo(key.name, key.keyword_id))
//                                Log.e(
//                                    "KEYS:",
//                                    "${ProviderUtils.profession!![index].name}, $index" + Gson().toJson(
//                                        key
//                                    )
//                                )
//                            }
//                            CoroutineScope(Dispatchers.Main).launch {
//                                keywordSkills.setTextWithChips(chips)
//                            }
//                        }
//                    }
//                }
//                rdbtn.setOnClickListener {
//                    if (rdbtn.isChecked) {
//                        loadKeyWords(profession.prof_id)
//                    }
//                }
//                professionRadioGroup.addView(rdbtn)
//            }

//            aboutMe.setOnFocusChangeListener { v, hasFocus ->
//                if (hasFocus) {
//                    val btn = findViewById<RadioButton>(professionRadioGroup.checkedRadioButtonId)
//                    if (btn != null) {
//                        btn.isChecked = false
//                        btn.clearFocus()
//                    } else {
//                        snackBar(aboutMe, "Please Select Profession For Keyword Selection")
//                    }
//                }
//            }

            nextBtn.setOnClickListener {
                validateFields()
            }

            backBtn.setOnClickListener { onBackPressed() }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadKeyWords(professionId: String, index: Int) {
        Log.e("ONE:", "Gson().toJson(apiData)")
        viewModel.getKeywords(this, professionId).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    when (index) {
                        0 -> {
                            keywordsOneMList = it.data!!
                            updateUIOne()
                            Log.e("RESPONSE:$index:", Gson().toJson(keywordsOneMList))
                        }
                        1 -> {
                            keywordsTwoMList = it.data!!
                            updateUITwo()
                            Log.e("RESPONSE:$index:", Gson().toJson(keywordsTwoMList))
                        }
                        2 -> {
                            keywordsThreeMList = it.data!!
                            updateUIThree()
                            Log.e("RESPONSE:$index:", Gson().toJson(keywordsThreeMList))
                        }
                    }
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUIThree() {
        val keywordsList = ArrayList<String>()
        for (data in keywordsThreeMList) {
            keywordsList.add(data.keyword)
        }
        Log.e("THREE:", keywordsOneMList.toString())
        Log.e("THREE:", keywordsList.toString())
        CoroutineScope(Dispatchers.Main).launch {
            val languagesAdapter = ArrayAdapter(
                this@ProviderSignUpTwo,
                R.layout.simple_spinner_dropdown_item,
                keywordsList
            )
            binding.profThreekeywordSkills.setAdapter(languagesAdapter)
            binding.profThreekeywordSkills.threshold = 1
            binding.profThreekeywordSkills.addChipTerminator(
                '\n',
                ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL
            )
            binding.profThreekeywordSkills.addChipTerminator(
                ',',
                ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
            )
            binding.profThreekeywordSkills.addChipTerminator(
                ';',
                ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN
            )
            binding.profThreekeywordSkills.setNachoValidator(ChipifyingNachoValidator())
            binding.profThreekeywordSkills.enableEditChipOnTouch(true, true)
            progressDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUITwo() {
        val keywordsList = ArrayList<String>()
        for (data in keywordsTwoMList) {
            keywordsList.add(data.keyword)
        }
        Log.e("TWO:", keywordsOneMList.toString())
        Log.e("TWO:", keywordsList.toString())
        CoroutineScope(Dispatchers.Main).launch {
            val languagesAdapter = ArrayAdapter(
                this@ProviderSignUpTwo,
                R.layout.simple_spinner_dropdown_item,
                keywordsList
            )
            binding.profTwokeywordSkills.setAdapter(languagesAdapter)
            binding.profTwokeywordSkills.threshold = 1
            binding.profTwokeywordSkills.addChipTerminator(
                '\n',
                ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL
            )
            binding.profTwokeywordSkills.addChipTerminator(
                ',',
                ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
            )
            binding.profTwokeywordSkills.addChipTerminator(
                ';',
                ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN
            )
            binding.profTwokeywordSkills.setNachoValidator(ChipifyingNachoValidator())
            binding.profTwokeywordSkills.enableEditChipOnTouch(true, true)
            if (ProviderUtils.profession!!.size == 2) {
                binding.profThreeSkillsText.visibility = View.GONE
                binding.profThreekeywordSkills.visibility = View.GONE
            }
            progressDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUIOne() {
        val keywordsList = ArrayList<String>()
        for (data in keywordsOneMList) {
            keywordsList.add(data.keyword)
        }
        Log.e("ONE:", keywordsOneMList.toString())
        Log.e("ONE:", keywordsList.toString())
        CoroutineScope(Dispatchers.Main).launch {
            val languagesAdapter = ArrayAdapter(
                this@ProviderSignUpTwo,
                R.layout.simple_spinner_dropdown_item,
                keywordsList
            )
            binding.profOnekeywordSkills.setAdapter(languagesAdapter)
            binding.profOnekeywordSkills.threshold = 1
            binding.profOnekeywordSkills.addChipTerminator(
                '\n',
                ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL
            )
            binding.profOnekeywordSkills.addChipTerminator(
                ',',
                ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
            )
            binding.profOnekeywordSkills.addChipTerminator(
                ';',
                ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN
            )
            binding.profOnekeywordSkills.setNachoValidator(ChipifyingNachoValidator())
            binding.profOnekeywordSkills.enableEditChipOnTouch(true, true)
            if (ProviderUtils.profession!!.size == 1) {
                binding.profTwoSkillsText.visibility = View.GONE
                binding.profThreeSkillsText.visibility = View.GONE
                binding.profTwokeywordSkills.visibility = View.GONE
                binding.profThreekeywordSkills.visibility = View.GONE
            }
            progressDialog.dismiss()
        }
    }

    private fun validateFields() {

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