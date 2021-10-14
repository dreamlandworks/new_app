package com.satrango.ui.auth.provider_signup.provider_sign_up_two

import android.R
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderSignUpTwoBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.KeywordsResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_three.ProviderSignUpThree
import com.satrango.utils.ProviderUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast

class ProviderSignUpTwo : AppCompatActivity() {

    private var profession_Id: String = ""
    private lateinit var keywordsMList: List<com.satrango.ui.auth.provider_signup.provider_sign_up_two.models.Data>
    private lateinit var viewModel: ProviderSignUpTwoViewModel
    private lateinit var binding: ActivityProviderSignUpTwoBinding
    private lateinit var progressDialog: BeautifulProgressDialog

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

        loadKeyWords()

        binding.apply {

            keywordSkills.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
            keywordSkills.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
            keywordSkills.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)

            nextBtn.setOnClickListener {
                validateFields()
            }

            backBtn.setOnClickListener { onBackPressed() }

        }

    }

    private fun loadKeyWords() {
        viewModel.getKeywords(this, profession_Id).observe(this, {
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
                    val languagesAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, keywordsList)
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
        if (binding.keywordSkills.allChips.isEmpty()) {
            snackBar(binding.nextBtn, "Enter Keywords/Skills")
        } else if (binding.aboutMe.text.toString().isEmpty()) {
            snackBar(binding.nextBtn, "Enter About Me Section")
        } else {
            val keysList = mutableListOf<KeywordsResponse>()
            for (chip in binding.keywordSkills.allChips) {
                for (key in keywordsMList) {
                    val chipText = chip.text.toString()
                    if (chipText == key.keyword) {
                        keysList.add(KeywordsResponse(key.id, key.keyword))
                    } else {
                        keysList.add(KeywordsResponse("0", chipText))
                    }
                }
            }
            ProviderUtils.keywordsSkills = keysList
            ProviderUtils.aboutMe = binding.aboutMe.text.toString().trim()
            startActivity(Intent(this@ProviderSignUpTwo, ProviderSignUpThree::class.java))
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(
            com.satrango.R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${com.satrango.R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(com.satrango.R.color.progressDialogColor))
    }
}