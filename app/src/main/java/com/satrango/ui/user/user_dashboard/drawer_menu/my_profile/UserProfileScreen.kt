package com.satrango.ui.user.user_dashboard.drawer_menu.my_profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.text.InputType
import android.text.method.KeyListener
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserProfileScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.UserSignUpScreenThree
import com.satrango.ui.auth.user_signup.set_password.SetPasswordScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileAddressInterface
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileUpdateReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class UserProfileScreen : AppCompatActivity(), UserProfileAddressInterface {

    private var gender = ""
    private var selectedAge = 0
    private val GALLERY_REQUEST = 100
    private val CAMERA_REQUEST: Int = 101
    private var selectedEncodedImage = ""
    private lateinit var binding: ActivityUserProfileScreenBinding
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var progressDialog: BeautifulProgressDialog

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeToolBar()
        initializeProgressDialog()
    }

    private var connectionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected =
                intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                disconnected()
            } else {
                connected()
            }
        }
    }

    private fun connected() {
        loadScreen()
    }

    @SuppressLint("SetTextI18n")
    private fun disconnected() {
        binding.noteText.visibility = View.VISIBLE
        binding.noteText.text = "Internet Connection Lost"
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(connectionReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectionReceiver)
    }

    private fun loadScreen() {
        binding.noteText.visibility = View.GONE

        val factory = ViewModelFactory(UserProfileRepository())
        viewModel = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]
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
                    fName.isEmpty() -> snackBar(binding.applyBtn, "Please Enter First Name")
                    lName.isEmpty() -> snackBar(binding.applyBtn, "Please Enter Last Name")
                    mobile.isEmpty() -> snackBar(binding.applyBtn, "Please Enter mobile number")
                    email.isEmpty() -> snackBar(binding.applyBtn, "Please Enter Email Id")
                    !email.contains("@") || !email.contains(".") -> snackBar(
                        binding.applyBtn,
                        "Please Enter Valid Email Id"
                    )
                    dob.isEmpty() -> snackBar(binding.applyBtn, "Please Select Date of Birth")
                    gender.isEmpty() -> snackBar(binding.applyBtn, "Please Select Gender")
                    else -> {
                        updateUserProfileToServer()
                    }
                }
            }

            male.setOnClickListener {
                male.setBackgroundResource(R.drawable.category_bg)
                male.setTextColor(resources.getColor(R.color.white))
                female.setBackgroundResource(R.drawable.blue_out_line)
                female.setTextColor(resources.getColor(R.color.blue))
                notToMentionBtn.setBackgroundResource(R.drawable.blue_out_line)
                notToMentionBtn.setTextColor(resources.getColor(R.color.blue))
                gender = "male"
            }

            female.setOnClickListener {
                female.setBackgroundResource(R.drawable.category_bg)
                female.setTextColor(resources.getColor(R.color.white))
                male.setBackgroundResource(R.drawable.blue_out_line)
                male.setTextColor(resources.getColor(R.color.blue))
                notToMentionBtn.setBackgroundResource(R.drawable.blue_out_line)
                notToMentionBtn.setTextColor(resources.getColor(R.color.blue))
                gender = "female"
            }

            notToMentionBtn.setOnClickListener {
                notToMentionBtn.setBackgroundResource(R.drawable.category_bg)
                notToMentionBtn.setTextColor(resources.getColor(R.color.white))
                male.setBackgroundResource(R.drawable.blue_out_line)
                male.setTextColor(resources.getColor(R.color.blue))
                female.setBackgroundResource(R.drawable.blue_out_line)
                female.setTextColor(resources.getColor(R.color.blue))
                gender = "others"
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
        }
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<CircleImageView>(R.id.toolBarImage).visibility = View.GONE
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.my_profile)
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
        val reg = "[^A-Za-z0-9 ]".toRegex()
        val requestBody = UserProfileUpdateReqModel(
            binding.dateOfBirth.text.toString().trim(),
            binding.emailId.text.toString().trim(),
            reg.replace(binding.firstName.text.toString().trim(), ""),
            selectedEncodedImage,
            reg.replace(binding.lastName.text.toString().trim(), ""),
            gender,
            UserUtils.getUserId(this@UserProfileScreen),
            RetrofitBuilder.USER_KEY
        )
//        Log.e("JSON", Gson().toJson(requestBody))
        viewModel.updateProfileInfo(this, requestBody).observe(this) {
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
                    progressDialog.dismiss()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showUserProfile() {
        val requestBody = UserProfileReqModel(
            RetrofitBuilder.USER_KEY,
            UserUtils.getUserId(this@UserProfileScreen).toInt(),
            UserUtils.getCity(this@UserProfileScreen)
        )
        viewModel.userProfileInfo(this, requestBody).observe(this) {
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

                    gender = responseData.gender
                    when (responseData.gender) {
                        "male" -> {
                            binding.male.setBackgroundResource(R.drawable.category_bg)
                            binding.male.setTextColor(resources.getColor(R.color.white))
                        }
                        "female" -> {
                            binding.female.setBackgroundResource(R.drawable.category_bg)
                            binding.female.setTextColor(resources.getColor(R.color.white))
                        }
                        else -> {
                            binding.notToMentionBtn.setBackgroundResource(R.drawable.category_bg)
                            binding.notToMentionBtn.setTextColor(resources.getColor(R.color.white))
                        }
                    }

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
                    progressDialog.dismiss()
                    snackBar(binding.applyBtn, it.message!!)
                }
            }
        }
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
            progressDialog.show()
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            val storageRef = FirebaseStorage.getInstance().reference
            val profilePicStorageRef =
                storageRef.child("images/profile_pic_with_user_id_${UserUtils.getUserId(this)}.jpg")
            profilePicStorageRef.putFile(getImageUri(this, imageBitmap!!)!!).addOnFailureListener {
                toast(this, it.message!!)
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                }
            }.addOnSuccessListener {
                profilePicStorageRef.downloadUrl.addOnSuccessListener { uri ->
                    val url = uri.toString()
                    val database = Firebase.database
                    val myRef = database.getReference(UserUtils.getFCMToken(this))
                    myRef.child("profile_pic_path").setValue(url)
                    Glide.with(this).load(url).into(binding.profilePic)
                    progressDialog.dismiss()
                }
            }
            try {
                imageStream = contentResolver.openInputStream(getImageUri(this, imageBitmap)!!)
            } catch (e: Exception) {
                snackBar(binding.applyBtn, e.message!!)
            }
            binding.profilePic.setImageBitmap(data.extras!!.get("data") as Bitmap?)
        }
        if (imageStream != null) {
            val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
            selectedEncodedImage = UserUtils.encodeToBase64(yourSelectedImage)!!
            updateUserProfileToServer()
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
        viewModel.deleteUserAddress(this, requestBody).observe(this) {
            when (it) {
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
        }
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

    @SuppressLint("SimpleDateFormat")
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, timeStamp, null)
        return Uri.parse(path)
    }

}