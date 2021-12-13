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

class SkillsProfileScreen: BaseFragment<ProviderProfileViewModel, FragmentSkillsProfileScreenBinding, ProviderProfileRepository>() {

    private lateinit var languagesMList: List<Language>
    private lateinit var experienceMList: List<Experience>
    private lateinit var professionMList: List<Profession>
    private lateinit var skillsMList: List<KeywordResponse>
    private lateinit var qualificationMList: List<Qualification>
    private lateinit var professionFList: ArrayList<ProfessionResponseX>

    private lateinit var response: ProviderOneModel
    private lateinit var experienceList: java.util.ArrayList<String>
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
        professionViewModel =
            ViewModelProvider(this, factory)[ProviderSignUpOneViewModel::class.java]

        professionFList = ArrayList()

        binding.apply {
//            profession.inputType = InputType.TYPE_CLASS_TEXT
//            profession.tag = profession.keyListener
//            profession.keyListener = null
//            profession.setOnClickListener {
//                profession.setBackgroundColor(Color.parseColor("#ffffff"))
//                skills.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                experience.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                languages.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                qualification.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                description.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                profession.keyListener = profession.tag as KeyListener
//            }

//            qualification.inputType = InputType.TYPE_CLASS_TEXT
//            qualification.tag = qualification.keyListener
//            qualification.keyListener = null
//            qualification.setOnClickListener {
//                qualification.setBackgroundColor(Color.parseColor("#ffffff"))
//                profession.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                skills.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                languages.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                description.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                experience.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                qualification.keyListener = qualification.tag as KeyListener
//            }

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

//            languages.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
//            languages.tag = languages.keyListener
//            languages.keyListener = null
//            languages.setOnClickListener {
//                languages.setBackgroundColor(Color.parseColor("#ffffff"))
//                profession.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                qualification.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                experience.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                skills.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                description.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                languages.keyListener = languages.tag as KeyListener
//            }

//            skills.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
//            skills.tag = skills.keyListener
//            skills.keyListener = null
//            skills.setOnClickListener {
//                skills.setBackgroundColor(Color.parseColor("#ffffff"))
//                profession.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                qualification.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                experience.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                languages.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                description.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                skills.keyListener = skills.tag as KeyListener
//            }

//            description.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
//            description.tag = description.keyListener
//            description.keyListener = null
//            description.setOnClickListener {
//                description.setBackgroundColor(Color.parseColor("#ffffff"))
//                profession.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                qualification.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                experience.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                languages.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                skills.setBackgroundColor(Color.parseColor("#E7F0FF"))
//                description.keyListener = description.tag as KeyListener
//            }

            updateBtn.setOnClickListener {
                validateFields()
            }
        }

        loadProviderData()
    }

    private fun validateFields() {

//        val btn =
//            binding.root.findViewById<RadioButton>(binding.professionRadioGroup.checkedRadioButtonId)
//        if (btn != null) {
//            btn.isChecked = false
//            btn.clearFocus()
//        } else {
//            snackBar(binding.backBtn, "Please Select Profession For Keyword Selection")
//            return
//        }

        binding.apply {

            val qualificationList = mutableListOf<QualificationResponse>()
//            for (chip in binding.qualification.allChips) {
                for (qualification in qualificationMList) {
                    if (binding.qualification.text.toString() == qualification.qualification) {
                        qualificationList.add(QualificationResponse(qualification.qualification, qualification.id))
//                    }
                }
            }

            val langList = mutableListOf<LangResponse>()
            for (chip in languages.allChips) {
                for (language in response.data.language) {
                    if (chip.text.toString() == language.name) {
                        langList.add(LangResponse(language.id, language.name))
                    }
                }
            }
            professionFList = ArrayList()
            if (professionOne.allChips.isNotEmpty()) {
                when {
                    extraChargeOne.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Extra Charges For First Profession")
                    }
                    minChargeOne.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Minimum Charges For First Profession")
                    }
                    perHourOne.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Per Hour Charges For First Profession")
                    }
                    perDayOne.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Per Day Charges For First Profession")
                    }
                    perDayOne.text.toString().toDouble() < perHourOne.text.toString().toDouble() -> {
                        snackBar(extraChargeOne, "Please Enter Per Hour charges less than Per Day Charges For First Profession")
                    }
                    experienceOne.selectedItemPosition == 0 -> {
                        snackBar(extraChargeOne, "Please Select Experience For First Profession")
                    }
                    else -> {
                        val keywordsOneList = ArrayList<KeywordsResponse>()
                        for (chip in binding.skillsKeywordsOne.allChips) {
                            var keyword: KeywordResponse? = null
                            for (skill in skillsMList) {
                                if (chip.text.toString() == skill.keyword) {
                                    keyword = skill
                                }
                            }
                            if (keyword != null) {
                                keywordsOneList.add(KeywordsResponse(keyword.id, keyword.keyword))
                            } else {
                                keywordsOneList.add(KeywordsResponse("0", chip.text.toString()))
                            }
                        }
                        if (keywordsOneList.isNotEmpty()) {
                            val professionOneList = ArrayList<ProfessionResponseX>()
                            var experience = ""
                            for (exp in experienceMList) {
                                if (exp.exp == binding.experienceOne.selectedItem.toString()) {
                                    experience = exp.id
                                }
                            }
                            for (chip in binding.professionOne.allChips) {
                                var profession: Profession? = null
                                for (professions in professionMList) {
                                    if (chip.text.toString() == professions.name) {
                                        profession = professions
                                    }
                                }
                                if (profession != null) {
                                    professionOneList.add(ProfessionResponseX(keywordsOneList.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name
                                    } as ArrayList<KeywordsResponse>, extraChargeOne.text.toString().trim(), minChargeOne.text.toString().trim(), perDayOne.text.toString().trim(), perHourOne.text.toString().trim(), experience, profession.name, profession.id))
                                } else {
                                    professionOneList.add(ProfessionResponseX(keywordsOneList.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name
                                    } as ArrayList<KeywordsResponse>, extraChargeOne.text.toString().trim(), minChargeOne.text.toString().trim(), perDayOne.text.toString().trim(), perHourOne.text.toString().trim(), experience, chip.text.toString(), "0"))
                                }
                            }
                            professionFList.add(professionOneList[0])
                        } else {
                            snackBar(professionOne, "Please Enter Keywords for First Profession")
                        }
                    }
                }
            }
            if (professionTwo.allChips.isNotEmpty()) {
                when {
                    extraChargeTwo.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Extra Charges For Second Profession")
                    }
                    minChargeTwo.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Minimum Charges For Second Profession")
                    }
                    perHourTwo.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Per Hour Charges For Second Profession")
                    }
                    perDayTwo.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Per Day Charges For Second Profession")
                    }
                    perDayTwo.text.toString().toDouble() < perHourTwo.text.toString().toDouble() -> {
                        snackBar(extraChargeOne, "Please Enter Per Hour charges less than Per Day Charges For Second Profession")
                    }
                    experienceTwo.selectedItemPosition == 0 -> {
                        snackBar(extraChargeOne, "Please Select Experience For Second Profession")
                    }
                    else -> {
                        val keywordsTwoList = ArrayList<KeywordsResponse>()
                        for (chip in binding.skillsKeywordsTwo.allChips) {
                            var keyword: KeywordResponse? = null
                            for (skill in skillsMList) {
                                if (chip.text.toString() == skill.keyword) {
                                    keyword = skill
                                }
                            }
                            if (keyword != null) {
                                keywordsTwoList.add(KeywordsResponse(keyword.id, keyword.keyword))
                            } else {
                                keywordsTwoList.add(KeywordsResponse("0", chip.text.toString()))
                            }
                        }
                        if (keywordsTwoList.isNotEmpty()) {
                            val professionTwoList = ArrayList<ProfessionResponseX>()
                            var experience = ""
                            for (exp in experienceMList) {
                                if (exp.exp == binding.experienceTwo.selectedItem.toString()) {
                                    experience = exp.id
                                }
                            }
                            for (chip in binding.professionTwo.allChips) {
                                var profession: Profession? = null
                                for (professions in professionMList) {
                                    if (chip.text.toString() == professions.name) {
                                        profession = professions
                                    }
                                }
                                if (profession != null) {
                                    professionTwoList.add(ProfessionResponseX(keywordsTwoList.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name
                                    } as ArrayList<KeywordsResponse>, extraChargeTwo.text.toString().trim(), minChargeTwo.text.toString().trim(), perDayTwo.text.toString().trim(), perHourTwo.text.toString().trim(), experience, profession.name, profession.id))
                                } else {
                                    professionTwoList.add(ProfessionResponseX(keywordsTwoList.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name
                                    } as ArrayList<KeywordsResponse>, extraChargeTwo.text.toString().trim(), minChargeTwo.text.toString().trim(), perDayTwo.text.toString().trim(), perHourTwo.text.toString().trim(), experience, chip.text.toString(), "0"))
                                }
                            }
                            professionFList.add(professionTwoList[0])
                        } else {
                            snackBar(professionOne, "Please Enter Keywords for Second Profession")
                        }
                    }
                }

            }
            if (professionThree.allChips.isNotEmpty()) {
                when {
                    extraChargeThree.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Extra Charges For Third Profession")
                    }
                    minChargeThree.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Minimum Charges For Third Profession")
                    }
                    perHourThree.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Per Hour Charges For Third Profession")
                    }
                    perDayThree.text.toString().isEmpty() -> {
                        snackBar(extraChargeOne, "Please Enter Per Day Charges For Third Profession")
                    }
                    perDayThree.text.toString().toDouble() < perHourThree.text.toString().toDouble() -> {
                        snackBar(extraChargeOne, "Please Enter Per Hour charges less than Per Day Charges For Third Profession")
                    }
                    experienceThree.selectedItemPosition == 0 -> {
                        snackBar(extraChargeOne, "Please Select Experience For First Profession")
                    }
                    else -> {
                        val keywordsThreeList = ArrayList<KeywordsResponse>()
                        for (chip in binding.skillsKeywordsThree.allChips) {
                            var keyword: KeywordResponse? = null
                            for (skill in skillsMList) {
                                if (chip.text.toString() == skill.keyword) {
                                    keyword = skill
                                }
                            }
                            if (keyword != null) {
                                keywordsThreeList.add(KeywordsResponse(keyword.id, keyword.keyword))
                            } else {
                                keywordsThreeList.add(KeywordsResponse("0", chip.text.toString()))
                            }
                        }
                        if (keywordsThreeList.isNotEmpty()) {
                            val professionThreeList = ArrayList<ProfessionResponseX>()
                            var experience = ""
                            for (exp in experienceMList) {
                                if (exp.exp == binding.experienceThree.selectedItem.toString()) {
                                    experience = exp.id
                                }
                            }
                            for (chip in binding.professionThree.allChips) {
                                var profession: Profession? = null
                                for (professions in professionMList) {
                                    if (chip.text.toString() == professions.name) {
                                        profession = professions
                                    }
                                }
                                if (profession != null) {
                                    professionThreeList.add(ProfessionResponseX(keywordsThreeList.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name
                                    } as ArrayList<KeywordsResponse>, extraChargeThree.text.toString().trim(), minChargeThree.text.toString().trim(), perDayThree.text.toString().trim(), perHourThree.text.toString().trim(), experience, profession.name, profession.id))
                                } else {
                                    professionThreeList.add(ProfessionResponseX(keywordsThreeList.distinctBy { keywordsResponse: KeywordsResponse -> keywordsResponse.name
                                    } as ArrayList<KeywordsResponse>, extraChargeThree.text.toString().trim(), minChargeThree.text.toString().trim(), perDayThree.text.toString().trim(), perHourThree.text.toString().trim(), experience, chip.text.toString(), "0"))
                                }
                            }
                            professionFList.add(professionThreeList[0])
                        } else {
                            snackBar(professionOne, "Please Enter Keywords for Third Profession")
                        }
                    }
                }
            }

            when {
                professionFList.isEmpty() -> {
                    snackBar(professionOne, "Enter Profession")
                }
                qualificationList.isEmpty() -> {
                    snackBar(professionOne, "Enter Qualification")
                }
                langList.isEmpty() -> {
                    snackBar(professionOne, "Enter Languages")
                }
                description.text.toString().trim().isEmpty() -> {
                    snackBar(professionOne, "Tell us something About you")
                }
                else -> {

                    val requestBody = UpdateSkillsReqModel(
                        description.text.toString().trim(),
                        UserUtils.getUserId(requireContext()),
                        RetrofitBuilder.PROVIDER_KEY,
                        professionFList.distinctBy { professionResponseX: ProfessionResponseX -> professionResponseX.name },
                        qualificationList,
                        langList.distinctBy { langResponse: LangResponse -> langResponse.name }
                    )
                    Log.e("JSON", Gson().toJson(requestBody))
                    viewModel.updateSkills(requireContext(), requestBody)
                        .observe(requireActivity(), {
                            when (it) {
                                is NetworkResponse.Loading -> {
                                    ProviderProfileScreen.progressDialog.show()
                                }
                                is NetworkResponse.Success -> {
                                    Handler().postDelayed({
                                        startActivity(requireActivity().intent)
                                    }, 1500)
                                    ProviderProfileScreen.progressDialog.dismiss()
                                    snackBar(
                                        binding.backBtn,
                                        JSONObject(it.data!!.string()).getString("message")
                                    )
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
            when (it) {
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
                    val languagesAdapter = ArrayAdapter(
                        requireContext(),
                        R.layout.simple_spinner_dropdown_item,
                        languagesList
                    )
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
                    binding.professionOne.setAdapter(professionAdapter)
                    binding.professionOne.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.professionOne.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.professionOne.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.professionOne.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.professionOne.setNachoValidator(ChipifyingNachoValidator())
                    binding.professionOne.enableEditChipOnTouch(true, true)

                    binding.professionTwo.setAdapter(professionAdapter)
                    binding.professionTwo.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.professionTwo.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.professionTwo.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.professionTwo.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.professionTwo.setNachoValidator(ChipifyingNachoValidator())
                    binding.professionTwo.enableEditChipOnTouch(true, true)

                    binding.professionThree.setAdapter(professionAdapter)
                    binding.professionThree.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.professionThree.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.professionThree.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.professionThree.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.professionThree.setNachoValidator(ChipifyingNachoValidator())
                    binding.professionThree.enableEditChipOnTouch(true, true)

                    val qualificationList = ArrayList<String>()
                    qualificationMList = response.data.qualification.distinctBy { qualification -> qualification.qualification }
                    for (data in qualificationMList) {
                        qualificationList.add(data.qualification)
                    }
                    val qualificationAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, qualificationList)
                    binding.qualification.setAdapter(qualificationAdapter)

                    experienceList = ArrayList()
                    experienceList.add("Select Experience")
                    experienceMList = response.data.experience.distinctBy { experience -> experience.exp }
                    for (data in experienceMList) {
                        experienceList.add(data.exp)
                    }
                    val experienceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, experienceList)
                    binding.experienceOne.adapter = experienceAdapter
                    binding.experienceTwo.adapter = experienceAdapter
                    binding.experienceThree.adapter = experienceAdapter

                    binding.skillsKeywordsOne.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.skillsKeywordsOne.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.skillsKeywordsOne.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.skillsKeywordsOne.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.skillsKeywordsOne.setNachoValidator(ChipifyingNachoValidator())
                    binding.skillsKeywordsOne.enableEditChipOnTouch(true, true)

                    binding.skillsKeywordsTwo.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.skillsKeywordsTwo.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.skillsKeywordsTwo.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.skillsKeywordsTwo.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.skillsKeywordsTwo.setNachoValidator(ChipifyingNachoValidator())
                    binding.skillsKeywordsTwo.enableEditChipOnTouch(true, true)

                    binding.skillsKeywordsThree.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
                    binding.skillsKeywordsThree.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.skillsKeywordsThree.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
                    binding.skillsKeywordsThree.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
                    binding.skillsKeywordsThree.setNachoValidator(ChipifyingNachoValidator())
                    binding.skillsKeywordsThree.enableEditChipOnTouch(true, true)

                    val skillsList = ArrayList<String>()
                    skillsMList = response.data.keywords.distinctBy { keywordResponse -> keywordResponse.keyword }
                    for (data in skillsMList) {
                        skillsList.add(data.keyword)
                    }
                    val skillsAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, skillsList)
                    binding.skillsKeywordsOne.setAdapter(skillsAdapter)
                    binding.skillsKeywordsTwo.setAdapter(skillsAdapter)
                    binding.skillsKeywordsThree.setAdapter(skillsAdapter)

                    if (ProviderProfileScreen.professionalDetails != null) {
                        val data = ProviderProfileScreen.professionalDetails

                        val languages = ArrayList<ChipInfo>()
                        for (language in data.language) {
                            languages.add(ChipInfo(language.name, language.language_id))
                        }
                        binding.languages.setTextWithChips(languages)
                        binding.qualification.setText(data.sp_details.qualification)
                        binding.description.setText(data.sp_details.about_me)

                        for (profession in data.profession) {
                            val skills = ArrayList<KeywordsResponse>()
                            for (pro in profession.skills) {
                                skills.add(KeywordsResponse(pro.keywords_id.toString(), pro.keyword))
                            }
                            professionFList.add(ProfessionResponseX(skills, profession.tariff_extra_charges, profession.tariff_min_charges, profession.tariff_per_day, profession.tariff_per_hour, profession.exp, profession.profession_name, profession.profession_id))
                        }

                        if (professionFList.isNotEmpty()) {
                            val chips = ArrayList<ChipInfo>()
                            chips.add(ChipInfo(professionFList[0].name, professionFList[0].prof_id))
                            binding.professionOne.setTextWithChips(chips)

                            binding.experienceOneText.text = "Experience in ${professionFList[0].name}"
                            binding.skillsKeywordsOneText.text = "Keywords for ${professionFList[0].name} Profession"
                            binding.tariffOneText.text = "Tariff for ${professionFList[0].name} Profession"

                            for (index in experienceMList.indices) {
                                if (experienceMList[index].exp == professionFList[0].experience) {
                                    binding.experienceOne.setSelection(index + 1)
                                }
                            }
                            val skillsChips = ArrayList<ChipInfo>()
                            for (keyword in professionFList[0].keywords_responses) {
                                skillsChips.add(ChipInfo(keyword.name, keyword.keyword_id))
                            }
                            binding.skillsKeywordsOne.setTextWithChips(skillsChips)
                            binding.extraChargeOne.setText(professionFList[0].tariff_extra_charges)
                            binding.minChargeOne.setText(professionFList[0].tariff_min_charges)
                            binding.perDayOne.setText(professionFList[0].tariff_per_day)
                            binding.perHourOne.setText(professionFList[0].tariff_per_hour)
                        }
                        if (professionFList.size > 1) {
                            val chips = ArrayList<ChipInfo>()
                            chips.add(ChipInfo(professionFList[1].name, professionFList[1].prof_id))
                            binding.professionTwo.setTextWithChips(chips)

                            binding.experienceTwoText.text = "Experience in ${professionFList[1].name}"
                            binding.skillsKeywordsTwoText.text = "Keywords for ${professionFList[1].name} Profession"
                            binding.tariffTwoText.text = "Tariff for ${professionFList[1].name} Profession"

                            for (index in experienceMList.indices) {
                                if (experienceMList[index].exp == professionFList[1].experience) {
                                    binding.experienceTwo.setSelection(index + 1)
                                }
                            }
                            val skillsChips = ArrayList<ChipInfo>()
                            for (keyword in professionFList[1].keywords_responses) {
                                skillsChips.add(ChipInfo(keyword.name, keyword.keyword_id))
                            }
                            binding.skillsKeywordsTwo.setTextWithChips(skillsChips)
                            binding.extraChargeTwo.setText(professionFList[1].tariff_extra_charges)
                            binding.minChargeTwo.setText(professionFList[1].tariff_min_charges)
                            binding.perDayTwo.setText(professionFList[1].tariff_per_day)
                            binding.perHourTwo.setText(professionFList[1].tariff_per_hour)
                        }
                        if (professionFList.size > 2) {
                            val chips = ArrayList<ChipInfo>()
                            chips.add(ChipInfo(professionFList[2].name, professionFList[2].prof_id))
                            binding.professionThree.setTextWithChips(chips)

                            binding.experienceThreeText.text = "Experience in ${professionFList[2].name}"
                            binding.skillsKeywordsThreeText.text = "Keywords for ${professionFList[2].name} Profession"
                            binding.tariffThreeText.text = "Tariff for ${professionFList[2].name} Profession"

                            for (index in experienceMList.indices) {
                                if (experienceMList[index].exp == professionFList[2].experience) {
                                    binding.experienceThree.setSelection(index + 1)
                                }
                            }
                            val skillsChips = ArrayList<ChipInfo>()
                            for (keyword in professionFList[2].keywords_responses) {
                                skillsChips.add(ChipInfo(keyword.name, keyword.keyword_id))
                            }
                            binding.skillsKeywordsThree.setTextWithChips(skillsChips)
                            binding.extraChargeThree.setText(professionFList[2].tariff_extra_charges)
                            binding.minChargeThree.setText(professionFList[2].tariff_min_charges)
                            binding.perDayThree.setText(professionFList[2].tariff_per_day)
                            binding.perHourThree.setText(professionFList[2].tariff_per_hour)
                        }

//                        updateSkillsAndTariff(data)
                    }
                    ProviderProfileScreen.progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    ProviderProfileScreen.progressDialog.dismiss()
                    snackBar(binding.qualification, it.message!!)
                    snackBar(binding.professionOne, "Click Reset to get language values")
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
                                for (skill in binding.skillsKeywordsOne.allChips) {
                                    for (keyword in response.data.keywords) {
                                        if (keyword.keyword == skill.text.toString()) {
                                            existed = keyword
                                        }
                                    }
                                    if (existed != null) {
                                        professionFList[index].keywords_responses.add(
                                            KeywordsResponse(existed.id, existed.keyword)
                                        )
                                    } else {
                                        professionFList[index].keywords_responses.add(
                                            KeywordsResponse("0", skill.text.toString())
                                        )
                                    }
                                }
                                professionFList[index].experience = binding.experienceOne.selectedItem.toString()
                                professionFList[index].tariff_extra_charges = binding.extraChargeOne.text.toString().trim()
                                professionFList[index].tariff_min_charges = binding.minChargeOne.text.toString().trim()
                                professionFList[index].tariff_per_day = binding.perDayOne.text.toString().trim()
                                professionFList[index].tariff_per_hour = binding.perHourOne.text.toString().trim()
                            }

                            if (isChecked) {
                                for (ind in experienceList.indices) {
                                    if (experienceList[ind] == professionFList[index].experience) {
                                        binding.experienceOne.setSelection(ind + 1)
                                    }
                                }
                                val skillsChips = ArrayList<ChipInfo>()
                                for (skill in professionFList[index].keywords_responses) {
                                    skillsChips.add(ChipInfo(skill.name, skill.keyword_id))
                                }
                                CoroutineScope(Dispatchers.Main).launch {
                                    binding.skillsKeywordsOne.setTextWithChips(skillsChips)
//                                    binding.skillsLayout.visibility = View.VISIBLE
                                    binding.perDayOne.setText(professionFList[index].tariff_per_day)
                                    binding.perHourOne.setText(professionFList[index].tariff_per_hour)
                                    binding.minChargeOne.setText(professionFList[index].tariff_min_charges)
                                    binding.extraChargeOne.setText(professionFList[index].tariff_extra_charges)
                                    binding.tariffOneText.text = "My Tariff for ${professionFList[index].name} is"
                                    binding.skillsKeywordsOneText.text = "Skills in ${professionFList[index].name} Profession"
//                                    binding.experienceOne.text = "Experience in ${professionFList[index].name} Profession"
                                    Log.e("SKILLS:", Gson().toJson(professionFList))
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
            binding.professionOne.setTextWithChips(professionChips)
            binding.description.setText(data.sp_details.about_me)
        }
    }

}