package com.satrango.ui.auth.provider_signup.provider_sign_up_two

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.hootsuite.nachos.NachoTextView.OnChipClickListener
import com.hootsuite.nachos.chip.Chip
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
import com.satrango.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import android.widget.Toast

import android.widget.AdapterView.OnItemClickListener
import androidx.core.view.allViews
import androidx.core.view.children
import com.google.gson.Gson
import kotlin.collections.ArrayList


class ProviderSignUpTwo : AppCompatActivity() {

    private var profession_Id: String = ""
    private lateinit var keywordsOneMList: List<Data>
    private lateinit var keywordsTwoMList: List<Data>
    private lateinit var keywordsThreeMList: List<Data>
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
            window.statusBarColor = resources.getColor(com.satrango.R.color.purple_700)
        }

        initializeProgressDialog()

        profession_Id = intent.getStringExtra("profession_id")!!

        val factory = ViewModelFactory(ProviderSignUpTwoRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderSignUpTwoViewModel::class.java]

        when (ProviderUtils.profession!!.size) {
            1 -> {
                binding.profOneSkillsText.text = "Keywords for ${ProviderUtils.profession!![0].name} are"
                binding.layoutTwo.visibility = View.GONE
                binding.layoutThree.visibility = View.GONE
            }
            2 -> {
                binding.profOneSkillsText.text = "Keywords for ${ProviderUtils.profession!![0].name} are"
                binding.profTwoSkillsText.text = "Keywords for ${ProviderUtils.profession!![1].name} are"
                binding.layoutThree.visibility = View.GONE
            }
            3 -> {
                binding.profOneSkillsText.text = "Keywords for ${ProviderUtils.profession!![0].name} are"
                binding.profTwoSkillsText.text = "Keywords for ${ProviderUtils.profession!![1].name} are"
                binding.profThreeSkillsText.text = "Keywords for ${ProviderUtils.profession!![2].name} are"
            }
        }

//        Log.e("PROFESSION:", Gson().toJson(ProviderUtils.profession!!))
        binding.autoCompleteOne.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                loadKeyWords(ProviderUtils.profession!![0].prof_id, 0)
            } else {
                if (binding.chipGroupOne.childCount != 0) {
                    ProviderUtils.profession!![0].keywords_responses = ArrayList()
                    val chipIds = binding.chipGroupOne.children
                    for (chipId in chipIds) {
                        val chip = binding.chipGroupOne.findViewById(chipId.id) as com.google.android.material.chip.Chip
                        if (keywordsOneMList.isNotEmpty()) {
                            for (key in keywordsOneMList) {
                                var existed = false
                                val chipText = chip.text.toString().trim()
                                if (chipText.lowercase(Locale.getDefault()) == key.keyword.lowercase(Locale.getDefault()).trim()) {
                                    for (keyword in ProviderUtils.profession!![0].keywords_responses) {
//                                        Log.e("CHECK:", "${key.keyword} | ${keyword.name}")
                                        if (key.keyword.lowercase(Locale.getDefault()).trim() == keyword.name.lowercase(Locale.getDefault()).trim() && key.keyword_id == keyword.keyword_id) {
                                            existed = true
                                        }
                                    }
                                    if (!existed) {
                                        ProviderUtils.profession!![0].keywords_responses.add(KeywordsResponse(key.keyword_id, key.keyword))
                                        ProviderUtils.profession!![0].category_id = keywordsOneMList[0].category_id
                                        ProviderUtils.profession!![0].subcategory_id = keywordsOneMList[0].subcategory_id
                                    }
                                } else {
                                    for (keyword in ProviderUtils.profession!![0].keywords_responses) {
                                        if (key.keyword.trim() == keyword.name.trim() && key.keyword_id == keyword.keyword_id) {
                                            existed = true
                                        }
                                    }
                                    if (!existed) {
                                        ProviderUtils.profession!![0].keywords_responses.add(KeywordsResponse("0", chipText))
                                        ProviderUtils.profession!![0].category_id = "0"
                                        ProviderUtils.profession!![0].subcategory_id = "0"
                                    }
                                }
                            }
                        } else {
                            ProviderUtils.profession!![0].keywords_responses.add(KeywordsResponse("0", chip.text.toString()))
                            ProviderUtils.profession!![0].category_id = "0"
                            ProviderUtils.profession!![0].subcategory_id = "0"
                        }
                        ProviderUtils.profession!![0].keywords_responses = ProviderUtils.profession!![0].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
                    }
                }
            }
        }

//        binding.profOnekeywordSkills.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                loadKeyWords(ProviderUtils.profession!![0].prof_id, 0)
//            } else {
//                if (binding.profOnekeywordSkills.allChips.isNotEmpty()) {
//                    ProviderUtils.profession!![0].keywords_responses = ArrayList()
//                    for (chip in binding.profOnekeywordSkills.allChips) {
//                        if (keywordsOneMList.isNotEmpty()) {
//                            for (key in keywordsOneMList) {
//                                var existed = false
//                                val chipText = chip.text.toString()
//                                if (chipText.lowercase(Locale.getDefault()).trim() == key.keyword.lowercase(Locale.getDefault()).trim()) {
//                                    for (keyword in ProviderUtils.profession!![0].keywords_responses) {
//                                        if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
//                                            existed = true
//                                        }
//                                    }
//                                    if (!existed) {
//                                        ProviderUtils.profession!![0].keywords_responses.add(
//                                            KeywordsResponse(key.id, key.keyword)
//                                        )
//                                    }
//                                } else {
//                                    for (keyword in ProviderUtils.profession!![0].keywords_responses) {
//                                        if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
//                                            existed = true
//                                        }
//                                    }
//                                    if (!existed) {
//                                        ProviderUtils.profession!![0].keywords_responses.add(
//                                            KeywordsResponse("0", chipText)
//                                        )
//                                    }
//                                }
//                            }
//                        } else {
////                            Log.e("ONECOMPARE:", "${chip.text} | NEW KEYWORD")
//                            ProviderUtils.profession!![0].keywords_responses.add(
//                                KeywordsResponse(
//                                    "0",
//                                    chip.text.toString()
//                                )
//                            )
//                        }
//                        ProviderUtils.profession!![0].keywords_responses =
//                            ProviderUtils.profession!![0].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
//                    }
//                }
//            }
//        }

        binding.autoCompleteTwo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                loadKeyWords(ProviderUtils.profession!![1].prof_id, 1)
            } else {
                if (binding.chipGroupTwo.childCount > 0) {
                    ProviderUtils.profession!![1].keywords_responses = ArrayList()
                    val chipIds = binding.chipGroupTwo.children
                    for (chipId in chipIds) {
                        val chip = binding.chipGroupTwo.findViewById(chipId.id) as com.google.android.material.chip.Chip
                        if (keywordsTwoMList.isNotEmpty()) {
                            for (key in keywordsTwoMList) {
                                var existed = false
                                val chipText = chip.text.toString().trim()
                                if (chipText.lowercase(Locale.getDefault()).trim() == key.keyword.lowercase(Locale.getDefault()).trim()) {
                                    for (keyword in ProviderUtils.profession!![1].keywords_responses) {
                                        if (key.keyword.trim().equals(keyword.name.trim(), ignoreCase = true) && key.keyword_id == keyword.keyword_id) {
                                            existed = true
                                        }
                                    }
                                    if (!existed) {
                                        ProviderUtils.profession!![1].keywords_responses.add(KeywordsResponse(key.keyword_id, key.keyword))
                                        ProviderUtils.profession!![1].category_id = key.category_id
                                        ProviderUtils.profession!![1].subcategory_id = key.subcategory_id
                                    }
                                } else {
                                    for (keyword in ProviderUtils.profession!![1].keywords_responses) {
                                        if (key.keyword.lowercase().trim() == keyword.name.lowercase().trim() && key.keyword_id == keyword.keyword_id) {
                                            existed = true
                                        }
                                    }
                                    if (!existed) {
                                        ProviderUtils.profession!![1].keywords_responses.add(KeywordsResponse("0", chipText))
                                        ProviderUtils.profession!![1].category_id = "0"
                                        ProviderUtils.profession!![1].subcategory_id = "0"
                                    }
                                }
                            }
                        } else {
                            ProviderUtils.profession!![1].keywords_responses.add(KeywordsResponse("0", chip.text.toString()))
                            ProviderUtils.profession!![1].category_id = "0"
                            ProviderUtils.profession!![1].subcategory_id = "0"
                        }

                        ProviderUtils.profession!![1].keywords_responses = ProviderUtils.profession!![1].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
                    }
                }
            }
        }

//        binding.profTwokeywordSkills.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                loadKeyWords(ProviderUtils.profession!![1].prof_id, 1)
//            } else {
//                if (binding.profTwokeywordSkills.allChips.isNotEmpty()) {
//                    ProviderUtils.profession!![1].keywords_responses = ArrayList()
//                    for (chip in binding.profTwokeywordSkills.allChips) {
//                        if (keywordsTwoMList.isNotEmpty()) {
//                            for (key in keywordsTwoMList) {
//                                var existed = false
//                                val chipText = chip.text.toString()
////                                Log.e("TWOCOMPARE:", "$chipText | ${key.keyword}")
//                                if (chipText.lowercase(Locale.getDefault())
//                                        .trim() == key.keyword.lowercase(Locale.getDefault()).trim()
//                                ) {
//                                    for (keyword in ProviderUtils.profession!![1].keywords_responses) {
//                                        if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
//                                            existed = true
//                                        }
//                                    }
//                                    if (!existed) {
//                                        ProviderUtils.profession!![1].keywords_responses.add(
//                                            KeywordsResponse(key.id, key.keyword)
//                                        )
//                                    }
//                                } else {
//                                    for (keyword in ProviderUtils.profession!![1].keywords_responses) {
//                                        if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
//                                            existed = true
//                                        }
//                                    }
//                                    if (!existed) {
//                                        ProviderUtils.profession!![1].keywords_responses.add(
//                                            KeywordsResponse("0", chipText)
//                                        )
//                                    }
//                                }
//                            }
//                        } else {
//                            ProviderUtils.profession!![1].keywords_responses.add(
//                                KeywordsResponse(
//                                    "0",
//                                    chip.text.toString()
//                                )
//                            )
//                        }
//
//                        ProviderUtils.profession!![1].keywords_responses =
//                            ProviderUtils.profession!![1].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
//                    }
//                }
//            }
//        }

        binding.autoCompleteOne.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val chip = com.google.android.material.chip.Chip(this@ProviderSignUpTwo)
                chip.text = binding.autoCompleteOne.text.toString().trim()
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    binding.chipGroupOne.removeView(chip)
                }
                binding.chipGroupOne.addView(chip)
                binding.autoCompleteOne.text = null
            }
            false
        }

        binding.autoCompleteTwo.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val chip = com.google.android.material.chip.Chip(this@ProviderSignUpTwo)
                chip.text = binding.autoCompleteTwo.text.toString().trim()
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    binding.chipGroupTwo.removeView(chip)
                }
                binding.chipGroupTwo.addView(chip)
                binding.autoCompleteTwo.text = null
            }
            false
        }

        binding.autoCompleteThree.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val chip = com.google.android.material.chip.Chip(this@ProviderSignUpTwo)
                chip.text = binding.autoCompleteThree.text.toString().trim()
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    binding.chipGroupThree.removeView(chip)
                }
                binding.chipGroupThree.addView(chip)
                binding.autoCompleteThree.text = null
            }
            false
        }

        binding.autoCompleteThree.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                loadKeyWords(ProviderUtils.profession!![2].prof_id, 2)
            } else {
                if (ProviderUtils.profession!!.size > 2) {
                    ProviderUtils.profession!![2].keywords_responses = ArrayList()
                    if (binding.chipGroupThree.childCount > 0) {
                        val chipIds = binding.chipGroupThree.children
                        for (chipId in chipIds) {
                            val chip = binding.chipGroupThree.findViewById(chipId.id) as com.google.android.material.chip.Chip
                            if (keywordsThreeMList.isNotEmpty()) {
                                for (key in keywordsThreeMList) {
                                    var existed = false
                                    val chipText = chip.text.toString()
                                    if (chipText.lowercase(Locale.getDefault()).trim() == key.keyword.lowercase(Locale.getDefault()).trim()) {
                                        for (keyword in ProviderUtils.profession!![2].keywords_responses) {
                                            if (key.keyword.lowercase(Locale.getDefault()).trim() == keyword.name.lowercase(Locale.getDefault()).trim() && key.keyword_id == keyword.keyword_id) {
                                                existed = true
                                            }
                                        }
                                        if (!existed) {
                                            ProviderUtils.profession!![2].category_id = key.category_id
                                            ProviderUtils.profession!![2].subcategory_id = key.subcategory_id
                                            ProviderUtils.profession!![2].keywords_responses.add(KeywordsResponse(key.keyword_id, key.keyword))
                                        }
                                    } else {
                                        for (keyword in ProviderUtils.profession!![2].keywords_responses) {
                                            if (key.keyword.lowercase().trim() == keyword.name.lowercase().trim() && key.keyword_id == keyword.keyword_id) {
                                                existed = true
                                            }
                                        }
                                        if (!existed) {
                                            ProviderUtils.profession!![2].keywords_responses.add(KeywordsResponse("0", chipText))
                                            ProviderUtils.profession!![2].category_id = "0"
                                            ProviderUtils.profession!![2].subcategory_id = "0"
                                        }
                                    }
                                }
                            } else {
                                ProviderUtils.profession!![2].keywords_responses.add(KeywordsResponse("0", chip.text.toString()))
                                ProviderUtils.profession!![2].category_id = "0"
                                ProviderUtils.profession!![2].subcategory_id = "0"
                            }
                            ProviderUtils.profession!![2].keywords_responses = ProviderUtils.profession!![2].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
                        }
                    }
                }
            }
        }
//        binding.profThreekeywordSkills.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                loadKeyWords(ProviderUtils.profession!![2].prof_id, 2)
//            } else {
//                if (ProviderUtils.profession!!.size > 2) {
//                    ProviderUtils.profession!![2].keywords_responses = ArrayList()
//                    if (binding.profThreekeywordSkills.allChips.isNotEmpty()) {
//                        for (chip in binding.profThreekeywordSkills.allChips) {
//                            if (keywordsThreeMList.isNotEmpty()) {
//                                for (key in keywordsThreeMList) {
//                                    var existed = false
//                                    val chipText = chip.text.toString()
////                                    Log.e("THREECOMPARE:", "${chip.text} | ${key.keyword}")
//                                    if (chipText.lowercase(Locale.getDefault())
//                                            .trim() == key.keyword.lowercase(Locale.getDefault())
//                                            .trim()
//                                    ) {
//                                        for (keyword in ProviderUtils.profession!![2].keywords_responses) {
//                                            if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
//                                                existed = true
//                                            }
//                                        }
//                                        if (!existed) {
//                                            ProviderUtils.profession!![2].keywords_responses.add(
//                                                KeywordsResponse(key.id, key.keyword)
//                                            )
//                                        }
//                                    } else {
//                                        for (keyword in ProviderUtils.profession!![2].keywords_responses) {
//                                            if (key.keyword == keyword.name && key.id == keyword.keyword_id) {
//                                                existed = true
//                                            }
//                                        }
//                                        if (!existed) {
//                                            ProviderUtils.profession!![2].keywords_responses.add(
//                                                KeywordsResponse("0", chipText)
//                                            )
//                                        }
//                                    }
//                                }
//                            } else {
//                                ProviderUtils.profession!![2].keywords_responses.add(
//                                    KeywordsResponse("0", chip.text.toString())
//                                )
//                            }
//                            ProviderUtils.profession!![2].keywords_responses = ProviderUtils.profession!![2].keywords_responses.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name } as ArrayList<KeywordsResponse>
//                        }
//                    }
//                }
//
//            }
//        }

        binding.apply {
            nextBtn.setOnClickListener { validateFields() }
            backBtn.setOnClickListener { onBackPressed() }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadKeyWords(professionId: String, index: Int) {
        viewModel.getKeywords(this, professionId).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    when (index) {
                        0 -> {
                            keywordsOneMList = it.data!!
                            updateUIOne()
                        }
                        1 -> {
                            keywordsTwoMList = it.data!!
                            updateUITwo()
                        }
                        2 -> {
                            keywordsThreeMList = it.data!!
                            updateUIThree()
                        }
                    }
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUIThree() {
        var keywordsList = ArrayList<String>()
        for (data in keywordsThreeMList) {
            keywordsList.add(data.keyword)
        }

        CoroutineScope(Dispatchers.Main).launch {
            var adapter = ArrayAdapter(this@ProviderSignUpTwo, R.layout.simple_list_item_1, keywordsList)
            binding.autoCompleteThree.threshold = 1
            binding.autoCompleteThree.setAdapter(adapter)
            binding.autoCompleteThree.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                val chip = com.google.android.material.chip.Chip(this@ProviderSignUpTwo)
                chip.text = adapter.getItem(position)
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    binding.chipGroupThree.removeView(chip)
                }
                binding.chipGroupThree.addView(chip)
                binding.autoCompleteThree.text = null
                for (keyword in keywordsThreeMList) {
                    if (keywordsList[position] == keyword.keyword) {
                        val temp = ArrayList<Data>()
                        for (key in keywordsThreeMList) {
                            if (key.profession_id == keyword.profession_id) {
                                temp.add(key)
                            }
                        }
                        keywordsThreeMList = ArrayList()
                        keywordsThreeMList = temp
                        keywordsList = ArrayList()
                        for (data in keywordsThreeMList) {
                            keywordsList.add(data.keyword)
                        }
                        adapter = ArrayAdapter(this@ProviderSignUpTwo, R.layout.simple_list_item_1, keywordsList)
                        binding.autoCompleteThree.threshold = 1
                        binding.autoCompleteThree.setAdapter(adapter)
                        return@OnItemClickListener
                    }
                }
            }
//            binding.profThreekeywordSkills.setAdapter(languagesAdapter)
//            binding.profThreekeywordSkills.threshold = 1
//            binding.profThreekeywordSkills.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
//            binding.profThreekeywordSkills.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
//            binding.profThreekeywordSkills.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
//            binding.profThreekeywordSkills.setNachoValidator(ChipifyingNachoValidator())
//            binding.profThreekeywordSkills.enableEditChipOnTouch(true, true)
            progressDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUITwo() {
        var keywordsList = ArrayList<String>()
        for (data in keywordsTwoMList) {
            keywordsList.add(data.keyword)
        }
        CoroutineScope(Dispatchers.Main).launch {
            var adapter = ArrayAdapter(this@ProviderSignUpTwo, R.layout.simple_list_item_1, keywordsList)
            binding.autoCompleteTwo.threshold = 1
            binding.autoCompleteTwo.setAdapter(adapter)
            binding.autoCompleteTwo.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                val chip = com.google.android.material.chip.Chip(this@ProviderSignUpTwo)
                chip.text = adapter.getItem(position)
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    binding.chipGroupTwo.removeView(chip)
                }
                binding.chipGroupTwo.addView(chip)
                binding.autoCompleteTwo.text = null
                for (keyword in keywordsTwoMList) {
                    if (keywordsList[position] == keyword.keyword) {
                        val temp = ArrayList<Data>()
                        for (key in keywordsTwoMList) {
                            if (key.profession_id == keyword.profession_id) {
                                temp.add(key)
                            }
                        }
                        keywordsTwoMList = ArrayList()
                        keywordsTwoMList = temp
                        keywordsList = ArrayList()
                        for (data in keywordsTwoMList) {
                            keywordsList.add(data.keyword)
                        }
                        adapter = ArrayAdapter(this@ProviderSignUpTwo, R.layout.simple_list_item_1, keywordsList)
                        binding.autoCompleteTwo.threshold = 1
                        binding.autoCompleteTwo.setAdapter(adapter)
                        return@OnItemClickListener
                    }
                }
            }
//            binding.profTwokeywordSkills.setAdapter(languagesAdapter)
//            binding.profTwokeywordSkills.threshold = 1
//            binding.profTwokeywordSkills.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
//            binding.profTwokeywordSkills.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
//            binding.profTwokeywordSkills.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
//            binding.profTwokeywordSkills.setNachoValidator(ChipifyingNachoValidator())
//            binding.profTwokeywordSkills.enableEditChipOnTouch(true, true)
            if (ProviderUtils.profession!!.size == 2) {
                binding.profThreeSkillsText.visibility = View.GONE
                binding.profThreekeywordSkills.visibility = View.GONE
            }
            progressDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUIOne() {
        var keywordsList = ArrayList<String>()
        for (data in keywordsOneMList) {
            keywordsList.add(data.keyword)
        }
        CoroutineScope(Dispatchers.Main).launch {
            var adapter = ArrayAdapter(this@ProviderSignUpTwo, R.layout.simple_list_item_1, keywordsList)
            binding.autoCompleteOne.threshold = 1
            binding.autoCompleteOne.setAdapter(adapter)
            binding.autoCompleteOne.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                val chip = com.google.android.material.chip.Chip(this@ProviderSignUpTwo)
                chip.text = adapter.getItem(position)
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    binding.chipGroupOne.removeView(chip)
                }
                binding.chipGroupOne.addView(chip)
                binding.autoCompleteOne.text = null
                for (keyword in keywordsOneMList) {
                    if (keywordsList[position] == keyword.keyword) {
                        val temp = ArrayList<Data>()
                        for (key in keywordsOneMList) {
                            if (key.profession_id == keyword.profession_id) {
                                temp.add(key)
                            }
                        }
                        keywordsOneMList = ArrayList()
                        keywordsOneMList = temp
                        keywordsList = ArrayList()
                        for (data in keywordsOneMList) {
                            keywordsList.add(data.keyword)
                        }
                        adapter = ArrayAdapter(this@ProviderSignUpTwo, R.layout.simple_list_item_1, keywordsList)
                        binding.autoCompleteOne.threshold = 1
                        binding.autoCompleteOne.setAdapter(adapter)
                        return@OnItemClickListener
                    }
                }
            }
//            binding.profOnekeywordSkills.setAdapter(languagesAdapter)
//            binding.profOnekeywordSkills.threshold = 1
//            binding.profOnekeywordSkills.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
//            binding.profOnekeywordSkills.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
//            binding.profOnekeywordSkills.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
//            binding.profOnekeywordSkills.setNachoValidator(ChipifyingNachoValidator())
//            binding.profOnekeywordSkills.enableEditChipOnTouch(true, true)

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
                ProviderUtils.aboutMe = binding.aboutMe.text.toString().trim()
//                Log.e("KEYWORDS:", Gson().toJson(ProviderUtils.profession))
                startActivity(Intent(this@ProviderSignUpTwo, ProviderSignUpThree::class.java))
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(com.satrango.R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${com.satrango.R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(com.satrango.R.color.progressDialogColor))
    }
}