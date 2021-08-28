package com.satrango.ui.auth.provider_signup.provider_sign_up_one

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderSignUpOneBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.LangResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProfessionResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.QualificationResponse
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
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")

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
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    response = it.data!!

                    val languagesList = ArrayList<String>()
                    for (data in response.data.language) {
                        languagesList.add(data.name)
                    }
                    val languagesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languagesList)
                    binding.languages.setAdapter(languagesAdapter)
                    binding.languages.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.languages.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.languages.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.languages.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.languages.setNachoValidator(ChipifyingNachoValidator())
                    binding.languages.enableEditChipOnTouch(true, true)

                    val qualificationList = ArrayList<String>()
                    for (data in response.data.qualification) {
                        qualificationList.add(data.qualification)
                    }
                    val qualificationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, qualificationList)
                    binding.qualification.setAdapter(qualificationAdapter)
                    binding.qualification.threshold = 1

                    val experienceList = ArrayList<String>()
                    experienceList.add("Select Experience")
                    for (data in response.data.experience) {
                        experienceList.add(data.exp)
                    }
                    val experienceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, experienceList)
                    binding.experience.adapter = experienceAdapter

                    val professionsList = ArrayList<String>()
                    for (data in response.data.list_profession) {
                        professionsList.add(data.name)
                    }
                    val professionsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, professionsList)
                    binding.profession.setAdapter(professionsAdapter)
                    binding.profession.threshold = 1

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

        if (binding.profession.text.toString().isEmpty()) {
            snackBar(binding.nextBtn, "Enter Your Profession")
        } else if (binding.qualification.text.toString().isEmpty()) {
            snackBar(binding.nextBtn, "Enter Your Qualification")
        } else if (binding.experience.selectedItemPosition == 0) {
            snackBar(binding.nextBtn, "Select Your Experience")
        } else if (binding.languages.allChips.isEmpty()) {
            snackBar(binding.nextBtn, "Enter Your Known Languages")
        } else {
            val langList = mutableListOf<LangResponse>()
            for (chip in binding.languages.allChips) {
                for (language in response.data.language) {
                    if (chip.text.toString() == language.name) {
                        langList.add(LangResponse(language.id, language.name))
                    }
                }
            }
            val profList = mutableListOf<ProfessionResponse>()
            for (pro in response.data.list_profession) {
                if (binding.profession.text.toString().trim() == pro.name) {
                    profList.add(ProfessionResponse(pro.name, pro.id))
                }
            }
            val qualList = mutableListOf<QualificationResponse>()
            for (qual in response.data.qualification) {
                if (binding.qualification.text.toString().trim() == qual.qualification) {
                    qualList.add(QualificationResponse(qual.qualification, qual.id))
                }
            }
            if (qualList.isEmpty()) {
                qualList.add(QualificationResponse(binding.qualification.text.toString().trim(), "0"))
            }
            ProviderUtils.languagesKnown = langList
            ProviderUtils.profession = profList
            ProviderUtils.qualification = qualList
            ProviderUtils.experience = binding.experience.selectedItem.toString()
            val intent = Intent(this@ProviderSignUpOne, ProviderSignUpTwo::class.java)
            intent.putExtra("profession_id", profList[0].prof_id)
            startActivity(intent)
        }
    }

}