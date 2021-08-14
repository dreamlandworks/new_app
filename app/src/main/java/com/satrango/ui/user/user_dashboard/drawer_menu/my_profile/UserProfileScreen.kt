package com.satrango.ui.user.user_dashboard.drawer_menu.my_profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.text.InputType
import android.text.method.KeyListener
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserProfileScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.UserSignUpScreenThree
import com.satrango.ui.auth.user_signup.set_password.SetPasswordScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileAddressInterface
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileUpdateReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*


class UserProfileScreen : AppCompatActivity(), UserProfileAddressInterface {

    private var selectedAge = 0
    private val GALLERY_REQUEST = 100
    private val CAMERA_REQUEST: Int = 101
    private var selectedEncodedImage = ""
    private lateinit var binding: ActivityUserProfileScreenBinding
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var progressDialog: ProgressDialog

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<CircleImageView>(R.id.toolBarImage).visibility = View.GONE
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.my_profile)

        val factory = ViewModelFactory(UserProfileRepository())
        viewModel = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]

        initializeProgressDialog()
        showUserProfile()

        binding.apply {

            applyBtn.setOnClickListener {
                val fName = firstName.text.toString().trim()
                val lName = lastName.text.toString().trim()
                val mobile = phoneNo.text.toString().trim()
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

                    else -> updateUserProfileToServer()
                }
            }

            backBtn.setOnClickListener {
                onBackPressed()
            }

            changePwd.setOnClickListener {
                startActivity(Intent(this@UserProfileScreen, SetPasswordScreen::class.java))
            }

            profileUploadBtn.setOnClickListener { openImagePicker() }

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
//            dateOfBirth.setOnTouchListener(OnTouchListener { v, event ->
//                val DRAWABLE_LEFT = 0
//                val DRAWABLE_TOP = 1
//                val DRAWABLE_RIGHT = 2
//                val DRAWABLE_BOTTOM = 3
//                if (event.action == MotionEvent.ACTION_UP) {
//                    if (event.rawX >= dateOfBirth.right - dateOfBirth.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
//                        dateOfBirth.keyListener = dateOfBirth.tag as KeyListener
//                        return@OnTouchListener true
//                    }
//                }
//                false
//            })
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
//            emailId.setOnTouchListener(OnTouchListener { v, event ->
//                val DRAWABLE_LEFT = 0
//                val DRAWABLE_TOP = 1
//                val DRAWABLE_RIGHT = 2
//                val DRAWABLE_BOTTOM = 3
//                if (event.action == MotionEvent.ACTION_UP) {
//                    if (event.rawX >= emailId.right - emailId.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
//                    ) {
//                        emailId.keyListener = emailId.tag as KeyListener
//                        return@OnTouchListener true
//                    }
//                }
//                false
//            })
        }

    }

    private fun openImagePicker() {
        val options = resources.getStringArray(R.array.imageSelections)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select image ")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> getImageFromGallery()
                    1 -> capturePictureFromCamera()
                }
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun capturePictureFromCamera() {
        val cameraIntent = Intent()
        cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    private fun getImageFromGallery() {
        val intent = Intent()
        val mineType = arrayOf("image/jpeg", "image/jpg", "image/png")
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mineType)
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    private fun updateUserProfileToServer() {
        val requestBody = UserProfileUpdateReqModel(
            binding.dateOfBirth.text.toString().trim(),
            binding.emailId.text.toString().trim(),
            binding.firstName.text.toString().trim(),
            selectedEncodedImage,
            binding.lastName.text.toString().trim(),
            UserUtils.getUserId(this@UserProfileScreen),
            RetrofitBuilder.USER_KEY
        )

        viewModel.updateProfileInfo(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    snackBar(binding.applyBtn, it.data!!)
                    Handler().postDelayed({
                        showUserProfile()
                    }, 1500)
                }
                is NetworkResponse.Failure -> {
                    snackBar(binding.applyBtn, it.message!!)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showUserProfile() {
        viewModel.userProfileInfo(this, UserUtils.getUserId(this)).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    val responseData = it.data!!
                    val imageUrl = RetrofitBuilder.BASE_URL + responseData.profile_pic
                    UserUtils.saveUserProfilePic(this@UserProfileScreen, imageUrl)
                    loadProfileImage(binding.profilePic)
                    binding.firstName.setText(responseData.fname)
                    binding.lastName.setText(responseData.lname)
                    binding.phoneNo.setText(responseData.mobile)
                    binding.emailId.setText(responseData.email_id)
                    binding.dateOfBirth.setText(responseData.dob)
                    val layoutManager = LinearLayoutManager(this@UserProfileScreen)
                    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    binding.addressRv.layoutManager = layoutManager
                    binding.addressRv.adapter =
                        UserProfileAddressAdapter(responseData.address, this@UserProfileScreen)
                    binding.addressRv.visibility = View.VISIBLE
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    snackBar(binding.applyBtn, it.message!!)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var imageStream: InputStream? = null
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            binding.profilePic.setImageURI(selectedImage)
            try {
                imageStream = contentResolver.openInputStream(data.data!!)
            } catch (e: Exception) {
                snackBar(binding.applyBtn, e.message!!)
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            binding.profilePic.setImageBitmap(imageBitmap)
            try {
                imageStream = contentResolver.openInputStream(getImageUri(this, imageBitmap!!)!!)
            } catch (e: Exception) {
                snackBar(binding.applyBtn, e.message!!)
            }
        }
        if (imageStream != null) {
            val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
            selectedEncodedImage = UserUtils.encodeToBase64(yourSelectedImage)!!
        }
    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading Profile...")
    }

    override fun deleteAddress(addressId: String) {
        if (binding.addressRv.childCount > 1) {
            deleteAddressOnServer(addressId)
        } else {
            snackBar(binding.applyBtn, "You have only Primary Address")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun deleteAddressOnServer(addressId: String) {
        val requestBody = BrowseCategoryReqModel(addressId, RetrofitBuilder.USER_KEY)
        viewModel.deleteUserAddress(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    snackBar(binding.applyBtn, "Deleting Address...")
                }
                is NetworkResponse.Success -> {
                    showUserProfile()
                    snackBar(binding.applyBtn, it.data!!)
                }
                is NetworkResponse.Failure -> {
                    snackBar(binding.applyBtn, it.message!!)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun openDateOfBirthDialog() {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR] // current year
        val mMonth = c[Calendar.MONTH] // current month
        val mDay = c[Calendar.DAY_OF_MONTH] // current day

        val datePickerDialog = DatePickerDialog(
            this@UserProfileScreen, { _, year, monthOfYear, dayOfMonth ->
                binding.dateOfBirth.setText(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth)
                binding.dateOfBirth.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_greencheck,
                    0
                )
                selectedAge = UserSignUpScreenThree.getAge(year, monthOfYear + 1, dayOfMonth)
                if (selectedAge < 13) {
                    snackBar(binding.applyBtn, "Age must be greater than 13 years")
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

}