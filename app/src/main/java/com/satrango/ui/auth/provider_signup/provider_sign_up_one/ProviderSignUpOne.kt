package com.satrango.ui.auth.provider_signup.provider_sign_up_one

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderSignUpOneBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.*
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.Profession
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_two.ProviderSignUpTwo
import com.satrango.utils.ProviderUtils
import com.satrango.utils.snackBar
import java.util.*
import kotlin.collections.ArrayList

class ProviderSignUpOne : AppCompatActivity() {

    private lateinit var response: ProviderOneModel
    private lateinit var viewModel: ProviderSignUpOneViewModel
    private lateinit var binding: ActivityProviderSignUpOneBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.purple_700))
        }

        initializeProgressDialog()

        val factory = ViewModelFactory(ProviderSignUpOneRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderSignUpOneViewModel::class.java]

        loadProviderData()

        binding.apply {

            nextBtn.setOnClickListener {
                validateFields()
            }

            resetBtn.setOnClickListener {
                refreshScreen()
            }

        }

    }

    private fun loadProviderData() {
        viewModel.professionsList(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    response = it.data!!

                    val languagesList = ArrayList<String>()
                    for (data in response.data.language) {
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

                    val qualificationList = ArrayList<String>()
                    for (data in response.data.qualification) {
                        qualificationList.add(data.qualification)
                    }
                    val qualificationAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        qualificationList
                    )
                    binding.qualification.setAdapter(qualificationAdapter)
                    binding.qualification.threshold = 1

                    val professionsList = ArrayList<String>()
                    for (data in response.data.list_profession) {
                        professionsList.add(data.name)
                    }
                    val professionsAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        professionsList
                    )
                    binding.profession.setAdapter(professionsAdapter)
                    binding.profession.threshold = 1

                    ProviderUtils.experience = response.data.experience

                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.qualification, it.message!!)
                    snackBar(binding.nextBtn, "Click Reset to get language values")
                }
            }
        })

    }

    private fun refreshScreen() {
        finish()
        startActivity(intent)
    }

    private fun validateFields() {

//        val specialCharactersString = "!@#$%&*()'+,-./:;<=>?[]^_`{|}"
        when {
            binding.profession.allChips.isEmpty() -> {
                snackBar(binding.nextBtn, "Please Enter Your Profession")
            }
//            binding.profession.text.toString().contains(specialCharactersString) -> {
//                snackBar(binding.nextBtn, "Please avoid special characters in profession")
//            }
            binding.qualification.text.toString().isEmpty() -> {
                snackBar(binding.nextBtn, "Please Enter Your Qualification")
            }
//            binding.qualification.text.toString().contains(specialCharactersString) -> {
//                snackBar(binding.nextBtn, "Please avoid special characters in qualification")
//            }
//            binding.experience.selectedItemPosition == 0 -> {
//                snackBar(binding.nextBtn, "Please Select Your Experience")
//            }
            binding.languages.allChips.isEmpty() -> {
                snackBar(binding.nextBtn, "Please Enter Your Known Languages")
            }
//            binding.languages.text.toString().contains(specialCharactersString) -> {
//                snackBar(binding.nextBtn, "Please avoid special characters in languages")
//            }
            else -> {
                val langList = mutableListOf<LangResponse>()
                for (chip in binding.languages.allChips) {
                    for (language in response.data.language) {
                        if (chip.text.toString() == language.name) {
                            langList.add(LangResponse(language.id, language.name))
                        }
                    }
                }

                val profList = mutableListOf<ProfessionResponseX>()
                for (chip in binding.profession.allChips) {
                    var profesion: Profession? = null
                    for (profession in response.data.list_profession) {
                        if (chip.text.toString() == profession.name) {
                            profesion = profession
                        }
                    }
                    if (profesion != null) {
                        profList.add(
                            ProfessionResponseX(
                                ArrayList(),
                                "",
                                "",
                                "",
                                "",
                                "",
                                profesion.name,
                                profesion.id
                            )
                        )
                    } else {
                        profList.add(
                            ProfessionResponseX(
                                ArrayList(),
                                "",
                                "",
                                "",
                                "",
                                "",
                                chip.text.toString(),
                                "0"
                            )
                        )
                    }
                }
                val qualList = mutableListOf<QualificationResponse>()
                for (qual in response.data.qualification) {
                    if (binding.qualification.text.toString().trim() == qual.qualification) {
                        qualList.add(QualificationResponse(qual.qualification, qual.id))
                    }
                }
                if (qualList.isEmpty()) {
                    qualList.add(
                        QualificationResponse(
                            binding.qualification.text.toString().trim(),
                            "0"
                        )
                    )
                }
                ProviderUtils.languagesKnown = langList
                ProviderUtils.profession = profList
                ProviderUtils.qualification = qualList
                Log.e("PROFESSIONS:", Gson().toJson(profList))
//                ProviderUtils.experience = binding.experience.selectedItem.toString()
                val intent = Intent(this@ProviderSignUpOne, ProviderSignUpTwo::class.java)
                intent.putExtra("profession_id", profList[0].prof_id)
                startActivity(intent)
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
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}