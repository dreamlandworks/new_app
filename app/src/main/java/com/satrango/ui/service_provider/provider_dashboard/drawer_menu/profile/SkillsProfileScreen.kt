package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile

import android.R
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.text.method.KeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.satrango.base.BaseFragment
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.FragmentSkillsProfileScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.KeywordsResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.LangResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProfessionResponseX
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.QualificationResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOneRepository
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOneViewModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.*
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.ProviderProfileProfessionResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_skills.KeywordResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_skills.UpdateSkillsReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class SkillsProfileScreen : BaseFragment<ProviderProfileViewModel, FragmentSkillsProfileScreenBinding, ProviderProfileRepository>() {

    private lateinit var professionFList: ArrayList<ProfessionResponseX>
    private lateinit var professionMList: List<Profession>
    private lateinit var languagesMList: List<Language>
    private lateinit var skillsMList: List<KeywordResponse>
    private lateinit var qualificationMList: List<Qualification>
    private lateinit var experienceMList: List<Experience>

    private lateinit var experienceList: java.util.ArrayList<String>
    private lateinit var response: ProviderOneModel
    private lateinit var professionViewModel: ProviderSignUpOneViewModel

    override fun getFragmentViewModel(): Class<ProviderProfileViewModel> =
        ProviderProfileViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSkillsProfileScreenBinding =
        FragmentSkillsProfileScreenBinding.inflate(layoutInflater)

    override fun getFragmentRepository(): ProviderProfileRepository = ProviderProfileRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory(ProviderSignUpOneRepository())
        professionViewModel = ViewModelProvider(this, factory)[ProviderSignUpOneViewModel::class.java]

        professionFList = ArrayList()

        binding.apply {
            profession.inputType = InputType.TYPE_CLASS_TEXT
            profession.tag = profession.keyListener
            profession.keyListener = null
            profession.setOnClickListener {
                profession.setBackgroundColor(Color.parseColor("#ffffff"))
                skills.setBackgroundColor(Color.parseColor("#E7F0FF"))
                experience.setBackgroundColor(Color.parseColor("#E7F0FF"))
                languages.setBackgroundColor(Color.parseColor("#E7F0FF"))
                qualification.setBackgroundColor(Color.parseColor("#E7F0FF"))
                description.setBackgroundColor(Color.parseColor("#E7F0FF"))
                profession.keyListener = profession.tag as KeyListener
            }

            qualification.inputType = InputType.TYPE_CLASS_TEXT
            qualification.tag = qualification.keyListener
            qualification.keyListener = null
            qualification.setOnClickListener {
                qualification.setBackgroundColor(Color.parseColor("#ffffff"))
                profession.setBackgroundColor(Color.parseColor("#E7F0FF"))
                skills.setBackgroundColor(Color.parseColor("#E7F0FF"))
                languages.setBackgroundColor(Color.parseColor("#E7F0FF"))
                description.setBackgroundColor(Color.parseColor("#E7F0FF"))
                experience.setBackgroundColor(Color.parseColor("#E7F0FF"))
                qualification.keyListener = qualification.tag as KeyListener
            }

//            experience.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
//            experience.tag = experience.keyListener
//            experience.keyListener = null
//            experienceLayout.setOnClickListener {
//                experienceLayout.boxBackgroundColor = Color.parseColor("#ffffff")
//                profession.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                qualification.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                languages.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                description.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                experience.keyListener = experience.tag as KeyListener
//            }

            languages.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            languages.tag = languages.keyListener
            languages.keyListener = null
            languages.setOnClickListener {
                languages.setBackgroundColor(Color.parseColor("#ffffff"))
                profession.setBackgroundColor(Color.parseColor("#E7F0FF"))
                qualification.setBackgroundColor(Color.parseColor("#E7F0FF"))
                experience.setBackgroundColor(Color.parseColor("#E7F0FF"))
                skills.setBackgroundColor(Color.parseColor("#E7F0FF"))
                description.setBackgroundColor(Color.parseColor("#E7F0FF"))
                languages.keyListener = languages.tag as KeyListener
            }

            skills.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            skills.tag = skills.keyListener
            skills.keyListener = null
            skills.setOnClickListener {
                skills.setBackgroundColor(Color.parseColor("#ffffff"))
                profession.setBackgroundColor(Color.parseColor("#E7F0FF"))
                qualification.setBackgroundColor(Color.parseColor("#E7F0FF"))
                experience.setBackgroundColor(Color.parseColor("#E7F0FF"))
                languages.setBackgroundColor(Color.parseColor("#E7F0FF"))
                description.setBackgroundColor(Color.parseColor("#E7F0FF"))
                skills.keyListener = skills.tag as KeyListener
            }

            description.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            description.tag = description.keyListener
            description.keyListener = null
            description.setOnClickListener {
                description.setBackgroundColor(Color.parseColor("#ffffff"))
                profession.setBackgroundColor(Color.parseColor("#E7F0FF"))
                qualification.setBackgroundColor(Color.parseColor("#E7F0FF"))
                experience.setBackgroundColor(Color.parseColor("#E7F0FF"))
                languages.setBackgroundColor(Color.parseColor("#E7F0FF"))
                skills.setBackgroundColor(Color.parseColor("#E7F0FF"))
                description.keyListener = description.tag as KeyListener
            }

            updateBtn.setOnClickListener {
                validateFields()
            }
        }

        loadProviderData()
    }

    private fun validateFields() {

        val btn = binding.root.findViewById<RadioButton>(binding.professionRadioGroup.checkedRadioButtonId)
        if (btn != null) {
            btn.isChecked = false
            btn.clearFocus()
        } else {
            snackBar(binding.backBtn, "Please Select Profession For Keyword Selection")
            return
        }

        binding.apply {

            val langList = mutableListOf<LangResponse>()
            for (chip in languages.allChips) {
                for (language in response.data.language) {
                    if (chip.text.toString() == language.name) {
                        langList.add(LangResponse(language.id, language.name))
                    }
                }
            }
            val keywordsList = mutableListOf<KeywordsResponse>()
            for (chip in binding.skills.allChips) {
                for (skill in skillsMList) {
                    if (chip.text.toString() == skill.keyword) {
                        keywordsList.add(KeywordsResponse(skill.id, skill.keyword))
                    }
                }
            }

            val qualificationList = mutableListOf<QualificationResponse>()
            for (chip in binding.qualification.allChips) {
                for (qualification in qualificationMList) {
                    if (chip.text.toString() == qualification.qualification) {
                        qualificationList.add(QualificationResponse(qualification.qualification, qualification.id))
                    }
                }
            }

            for (keyword in professionFList) {
                if (keyword.keywords_responses.isEmpty()) {
                    snackBar(binding.backBtn, "Please enter keywords for ${keyword.name} profession")
                    return
                }
            }

            when {
                professionFList.isEmpty() -> {
                    snackBar(profession, "Enter Profession")
                }
                qualificationList.isEmpty() -> {
                    snackBar(profession, "Enter Qualification")
                }
                langList.isEmpty() -> {
                    snackBar(profession, "Enter Languages")
                }
                description.text.toString().trim().isEmpty() -> {
                    snackBar(profession, "Tell us something About you")
                }
                else -> {

                    val requestBody = UpdateSkillsReqModel(
                        description.text.toString().trim(),
                        UserUtils.getUserId(requireContext()),
                        RetrofitBuilder.PROVIDER_KEY,
                        professionFList,
                        qualificationList,
                        langList
                    )
                    Log.e("JSON", Gson().toJson(requestBody))
                    viewModel.updateSkills(requireContext(), requestBody).observe(requireActivity(), {
                        when(it) {
                            is NetworkResponse.Loading -> {
                                ProviderProfileScreen.progressDialog.show()
                            }
                            is NetworkResponse.Success -> {
                                Handler().postDelayed({
                                    startActivity(requireActivity().intent)
                                }, 3000)
                                ProviderProfileScreen.progressDialog.dismiss()
                                snackBar(binding.backBtn, JSONObject(it.data!!.string()).getString("message"))
                            }
                            is NetworkResponse.Failure -> {
                                ProviderProfileScreen.progressDialog.dismiss()
                                snackBar(languages, it.message!!)
                            }
                        }
                    })
                }

            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadProviderData() {
        professionViewModel.professionsList(requireContext()).observe(requireActivity(), {
            when(it) {
                is NetworkResponse.Loading -> {
                    ProviderProfileScreen.progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    response = it.data!!
                    val languagesList = ArrayList<String>()
                    languagesMList = response.data.language.distinctBy { language -> language.name }
                    for (data in languagesMList) {
                        languagesList.add(data.name)
                    }
                    val languagesAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, languagesList)
                    binding.languages.setAdapter(languagesAdapter)
                    binding.languages.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.languages.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.languages.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.languages.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.languages.setNachoValidator(ChipifyingNachoValidator())
                    binding.languages.enableEditChipOnTouch(true, true)

                    val professionList = ArrayList<String>()
                    professionMList = response.data.list_profession
//                    professionMList = response.data.list_profession.distinctBy { profession -> profession.name }
                    for (data in professionMList) {
                        professionList.add(data.name)
                    }
                    val professionAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, professionList)
                    binding.profession.setAdapter(professionAdapter)
                    binding.profession.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.profession.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.profession.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.profession.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.profession.setNachoValidator(ChipifyingNachoValidator())
                    binding.profession.enableEditChipOnTouch(true, true)

                    val qualificationList = ArrayList<String>()
                    qualificationMList = response.data.qualification.distinctBy { qualification -> qualification.qualification }
                    for (data in qualificationMList) {
                        qualificationList.add(data.qualification)
                    }
                    val qualificationAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, qualificationList)
                    binding.qualification.setAdapter(qualificationAdapter)
                    binding.qualification.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.qualification.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.qualification.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.qualification.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.qualification.setNachoValidator(ChipifyingNachoValidator())
                    binding.qualification.enableEditChipOnTouch(true, true)

                    experienceList = ArrayList()
                    experienceList.add("Select Experience")
                    experienceMList = response.data.experience.distinctBy { experience -> experience.exp }
                    for (data in experienceMList) {
                        experienceList.add(data.exp)
                    }
                    val experienceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, experienceList)
                    binding.experience.adapter = experienceAdapter

                    binding.skills.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.skills.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.skills.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.skills.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.skills.setNachoValidator(ChipifyingNachoValidator())
                    binding.skills.enableEditChipOnTouch(true, true)

                    val skillsList = ArrayList<String>()
                    skillsMList = response.data.keywords.distinctBy { keywordResponse -> keywordResponse.keyword }
                    for (data in skillsMList) {
                        skillsList.add(data.keyword)
                    }
                    val skillsAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, skillsList)
                    binding.skills.setAdapter(skillsAdapter)

                    if (ProviderProfileScreen.professionalDetails != null) {
                        val data = ProviderProfileScreen.professionalDetails

                        val qualificationChips = ArrayList<ChipInfo>()
                        for (d in qualificationMList) {
                            if (d.qualification == data.sp_details.qualification) {
                                qualificationChips.add(ChipInfo(d.qualification, d.id))
                            }
                        }
                        binding.qualification.setTextWithChips(qualificationChips)

                        val languageschips = ArrayList<ChipInfo>()
                        for (d in data.language) {
                            languageschips.add(ChipInfo(d.name, d.language_id))
                        }
                        binding.languages.setTextWithChips(languageschips)

                        for (profession in data.profession) {
                            val skills = ArrayList<KeywordsResponse>()
                            for (pro in profession.skills) {
                                skills.add(KeywordsResponse(pro.keywords_id.toString(), pro.keyword))
                            }
                            professionFList.add(ProfessionResponseX(skills, profession.tariff_extra_charges, profession.tariff_min_charges, profession.tariff_per_day, profession.tariff_per_hour, profession.exp, profession.profession_name, profession.profession_id))
                        }

                        binding.profession.setOnFocusChangeListener { v, hasFocus ->
                            if (!hasFocus) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    for (i in 0 until binding.professionRadioGroup.childCount) {
                                        binding.professionRadioGroup.removeViewAt(i)
                                    }
                                    val professions = professionFList
                                    professionFList = ArrayList()
                                    for (pro in binding.profession.allChips) {
                                        var existed = false
                                        for (prof in professions) {
                                            if (prof.name == pro.text.toString()) {
                                                professionFList.add(prof)
                                                existed = true
                                            }
                                        }
                                        if (!existed) {
                                            for (prof in professionMList) {
                                                if (pro.text.toString() == prof.name) {
                                                    professionFList.add(ProfessionResponseX(ArrayList(), "", "", "", "", "", prof.name, prof.id))
                                                }
                                            }
                                        }
                                    }
                                    toast(requireActivity(), "List: $professionFList")
                                    updateSkillsAndTariff(data)
                                }
                            }
                        }
                        updateSkillsAndTariff(data)
                    }

                    ProviderProfileScreen.progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    snackBar(binding.qualification, it.message!!)
                    ProviderProfileScreen.progressDialog.dismiss()
                    snackBar(binding.profession, "Click Reset to get language values")
                }
            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun updateSkillsAndTariff(data: ProviderProfileProfessionResModel) {
        val professionChips = ArrayList<ChipInfo>()
        for (d in professionMList) {
            for (index in professionFList.indices) {
                if (d.name == professionFList[index].name) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val rdbtn = RadioButton(requireContext())
                        rdbtn.id = d.id.toInt()
                        rdbtn.text = d.name
                        rdbtn.setOnCheckedChangeListener { _, isChecked ->
                            if (!isChecked) {
                                var existed: KeywordResponse? = null
                                professionFList[index].keywords_responses = ArrayList()
                                for (skill in binding.skills.allChips) {
                                    for (keyword in response.data.keywords) {
                                        if (keyword.keyword == skill.text.toString()) {
                                            existed = keyword
                                        }
                                    }
                                    if (existed != null) {
                                        professionFList[index].keywords_responses.add(KeywordsResponse(existed.id, existed.keyword))
                                    } else {
                                        professionFList[index].keywords_responses.add(KeywordsResponse("0", skill.text.toString()))
                                    }
                                }
                                professionFList[index].experience = binding.experience.selectedItem.toString()
                                professionFList[index].tariff_extra_charges = binding.extraCharge.text.toString().trim()
                                professionFList[index].tariff_min_charges = binding.minCharge.text.toString().trim()
                                professionFList[index].tariff_per_day = binding.perDay.text.toString().trim()
                                professionFList[index].tariff_per_hour = binding.perHour.text.toString().trim()
                            }

                            if (isChecked) {
                                for (ind in experienceList.indices) {
                                    if (experienceList[ind] == professionFList[index].experience) {
                                        binding.experience.setSelection(ind + 1)
                                    }
                                }
                                val skillsChips = ArrayList<ChipInfo>()
                                for (skill in professionFList[index].keywords_responses) {
                                    skillsChips.add(ChipInfo(skill.name, skill.keyword_id))
                                }
                                CoroutineScope(Dispatchers.Main).launch {
                                    binding.skills.setTextWithChips(skillsChips)
                                    binding.perDay.setText(professionFList[index].tariff_per_day)
                                    binding.perHour.setText(professionFList[index].tariff_per_hour)
                                    binding.minCharge.setText(professionFList[index].tariff_min_charges)
                                    binding.extraCharge.setText(professionFList[index].tariff_extra_charges)
                                    binding.tariffText.text = "My Tariff for ${professionFList[index].name} is"
                                    binding.skillsText.text = "Skills in ${professionFList[index].name} Profession"
                                    binding.experienceText.text = "Experience in ${professionFList[index].name} Profession"
                                    Log.e("SKILLS:", Gson().toJson(professionFList))
                                    binding.skillsLayout.visibility = View.VISIBLE
                                }
                            }

                        }
                        professionChips.add(ChipInfo(d.name, d.id))
                        binding.professionRadioGroup.addView(rdbtn)
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            binding.profession.setTextWithChips(professionChips)
            binding.description.setText(data.sp_details.about_me)
        }
    }

}