package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.text.method.KeyListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.FragmentPersonalProfileScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.UserSignUpScreenThree
import com.satrango.ui.auth.user_signup.set_password.SetPasswordScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileAddressAdapter
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileUpdateReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar

class PersonalProfileScreen : BaseFragment<ProviderProfileViewModel, FragmentPersonalProfileScreenBinding, ProviderProfileRepository>() {

    private var gender = ""
    private lateinit var userViewModel: UserProfileViewModel

    override fun getFragmentViewModel(): Class<ProviderProfileViewModel> = ProviderProfileViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonalProfileScreenBinding = FragmentPersonalProfileScreenBinding.inflate(layoutInflater)

    override fun getFragmentRepository(): ProviderProfileRepository = ProviderProfileRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory(UserProfileRepository())
        userViewModel = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]

        binding.apply {

            male.setOnClickListener {
                male.setBackgroundResource(R.drawable.provider_btn_bg)
                male.setTextColor(resources.getColor(R.color.white))
                female.setBackgroundResource(R.drawable.purple_out_line)
                female.setTextColor(resources.getColor(R.color.purple_500))
                notToMentionBtn.setBackgroundResource(R.drawable.purple_out_line)
                notToMentionBtn.setTextColor(resources.getColor(R.color.purple_500))
                gender = "male"
            }

            female.setOnClickListener {
                female.setBackgroundResource(R.drawable.provider_btn_bg)
                female.setTextColor(resources.getColor(R.color.white))
                male.setBackgroundResource(R.drawable.purple_out_line)
                male.setTextColor(resources.getColor(R.color.purple_500))
                notToMentionBtn.setBackgroundResource(R.drawable.purple_out_line)
                notToMentionBtn.setTextColor(resources.getColor(R.color.purple_500))
                gender = "female"
            }

            notToMentionBtn.setOnClickListener {
                notToMentionBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                notToMentionBtn.setTextColor(resources.getColor(R.color.white))
                male.setBackgroundResource(R.drawable.purple_out_line)
                male.setTextColor(resources.getColor(R.color.purple_500))
                female.setBackgroundResource(R.drawable.purple_out_line)
                female.setTextColor(resources.getColor(R.color.purple_500))
                gender = "others"
            }

            backBtn.setOnClickListener {
                activity?.onBackPressed()
            }

            firstName.inputType = InputType.TYPE_CLASS_TEXT
            firstName.tag = firstName.keyListener
            firstName.keyListener = null
            firstNameEdit.setOnClickListener {
                firstNameLayout.boxBackgroundColor = Color.parseColor("#ffffff")
                lastNameLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                dateOfBirthLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                emailLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                firstName.keyListener = firstName.tag as KeyListener
            }

            lastName.inputType = InputType.TYPE_CLASS_TEXT
            lastName.tag = lastName.keyListener
            lastName.keyListener = null
            lastNameEdit.setOnClickListener {
                lastNameLayout.boxBackgroundColor = Color.parseColor("#ffffff")
                dateOfBirthLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                firstNameLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                emailLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                lastName.keyListener = lastName.tag as KeyListener
            }

            dateOfBirth.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            dateOfBirth.tag = dateOfBirth.keyListener
            dateOfBirth.keyListener = null
            dateOfBirthEdit.setOnClickListener {
                dateOfBirthLayout.boxBackgroundColor = Color.parseColor("#ffffff")
                lastNameLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                firstNameLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                emailLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                dateOfBirth.keyListener = dateOfBirth.tag as KeyListener
            }
            emailId.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            emailId.tag = emailId.keyListener
            emailId.keyListener = null
            emailEdit.setOnClickListener {
                emailLayout.boxBackgroundColor = Color.parseColor("#ffffff")
                lastNameLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                dateOfBirthLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                firstNameLayout.boxBackgroundColor = Color.parseColor("#E7F0FF")
                emailId.keyListener = emailId.tag as KeyListener
            }

            applyBtn.setOnClickListener {
                val fName = firstName.text.toString().trim()
                val lName = lastName.text.toString().trim()
                val mobile = ProviderProfileScreen.binding.phoneNo.text.toString().trim()
                val email = emailId.text.toString().trim()
                val dob = dateOfBirth.text.toString().trim()
                try {
                    val year = dob.split("-")[0].toInt()
                    val month = dob.split("-")[1].toInt()
                    val day = dob.split("-")[2].toInt()
                    if (UserSignUpScreenThree.getAge(year, month, day) < 13) {
                        snackBar(binding.dateOfBirth, "Age must be greater than 13 years")
                        return@setOnClickListener
                    }
                } catch (e: java.lang.Exception) {
                    snackBar(binding.applyBtn, "Date Of Birth should be in YYYY-MM-DD")
                    return@setOnClickListener
                }

                when {
                    fName.isEmpty() -> snackBar(binding.applyBtn, "Enter First Name")
                    lName.isEmpty() -> snackBar(binding.applyBtn, "Enter Last Name")
                    mobile.isEmpty() -> snackBar(binding.applyBtn, "Enter mobile number")
                    email.isEmpty() -> snackBar(binding.applyBtn, "Enter Email Id")
                    dob.isEmpty() -> snackBar(binding.applyBtn, "Select Date od Birth")
                    gender.isEmpty() -> snackBar(binding.applyBtn, "Select Gender")
                    else -> updateUserProfileToServer()
                }
            }

        }
        showUserProfile()
    }

    private fun updateUserProfileToServer() {
        val requestBody = UserProfileUpdateReqModel(
            binding.dateOfBirth.text.toString().trim(),
            binding.emailId.text.toString().trim(),
            binding.firstName.text.toString().trim(),
            ProviderProfileScreen.selectedEncodedImage,
            binding.lastName.text.toString().trim(),
            gender,
            UserUtils.getUserId(requireContext()),
            RetrofitBuilder.USER_KEY
        )
//        Log.e("PROFILE:", Gson().toJson(requestBody))
        userViewModel.updateProfileInfo(requireContext(), requestBody).observe(requireActivity()) {
            when (it) {
                is NetworkResponse.Loading -> {
                    ProviderProfileScreen.progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    ProviderProfileScreen.progressDialog.dismiss()
                    snackBar(binding.applyBtn, it.data!!)
                    Handler().postDelayed({
                        startActivity(requireActivity().intent)
                    }, 1500)
                }
                is NetworkResponse.Failure -> {
                    snackBar(binding.applyBtn, it.message!!)
                    ProviderProfileScreen.progressDialog.dismiss()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showUserProfile() {
        userViewModel.userProfileInfo(requireContext(), UserUtils.getUserId(requireContext())).observe(requireActivity()) {
            when (it) {
                is NetworkResponse.Loading -> {
                    ProviderProfileScreen.progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    val responseData = it.data!!
                    val imageUrl = RetrofitBuilder.BASE_URL + responseData.profile_pic
                    UserUtils.saveUserProfilePic(requireContext(), imageUrl)
                    loadProfileImage(ProviderProfileScreen.binding.profilePic)
                    binding.firstName.setText(responseData.fname)
                    binding.lastName.setText(responseData.lname)
                    ProviderProfileScreen.binding.phoneNo.setText(responseData.mobile)
                    binding.emailId.setText(responseData.email_id)

                    gender = responseData.gender
                    when (responseData.gender) {
                        "male" -> {
                            binding.male.setBackgroundResource(R.drawable.provider_btn_bg)
                            binding.male.setTextColor(resources.getColor(R.color.white))
                        }
                        "female" -> {
                            binding.female.setBackgroundResource(R.drawable.provider_btn_bg)
                            binding.female.setTextColor(resources.getColor(R.color.white))
                        }
                        else -> {
                            binding.notToMentionBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                            binding.notToMentionBtn.setTextColor(resources.getColor(R.color.white))
                        }
                    }

                    binding.dateOfBirth.setText(responseData.dob)
                    ProviderProfileScreen.progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    ProviderProfileScreen.progressDialog.dismiss()
                    snackBar(binding.applyBtn, it.message!!)
                }
            }
        }
    }

}