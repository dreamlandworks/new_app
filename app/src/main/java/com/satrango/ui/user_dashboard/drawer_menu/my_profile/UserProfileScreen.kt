package com.satrango.ui.user_dashboard.drawer_menu.my_profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.databinding.ActivityUserProfileScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.SetPasswordScreen
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.BrowseCategoryReqModel
import com.satrango.ui.user_dashboard.drawer_menu.my_profile.models.UserProfileAddressInterface
import com.satrango.ui.user_dashboard.drawer_menu.my_profile.models.UserProfileUpdateReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.SocketTimeoutException

class UserProfileScreen : AppCompatActivity(), UserProfileAddressInterface {

    private val GALLERY_REQUEST = 100
    private val CAMERA_REQUEST: Int = 100
    private var selectedEncodedImage = ""
    private lateinit var binding: ActivityUserProfileScreenBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_profile)

        initializeProgressDialog()
        showUserProfile()

        binding.apply {

            applyBtn.setOnClickListener {
                val fName = firstName.text.toString().trim()
                val lName = lastName.text.toString().trim()
                val mobile = phoneNo.text.toString().trim()
                val email = emailId.text.toString().trim()
                val dob = dateOfBirth.text.toString().trim()

                when {
                    fName.isEmpty() -> toast(this@UserProfileScreen, "Enter First Name")
                    lName.isEmpty() -> toast(this@UserProfileScreen, "Enter Last Name")
                    mobile.isEmpty() -> toast(this@UserProfileScreen, "Enter mobile number")
                    email.isEmpty() -> toast(this@UserProfileScreen, "Enter Email Id")
                    dob.isEmpty() -> toast(this@UserProfileScreen, "Select Date od Birth")
//                    selectedEncodedImage.isEmpty() -> toast(this@UserProfileScreen, "Select Image")
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
        startActivityForResult(
            cameraIntent,
            CAMERA_REQUEST
        )
    }

    private fun getImageFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    private fun updateUserProfileToServer() {
        progressDialog.show()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = UserProfileUpdateReqModel(
                    binding.dateOfBirth.text.toString().trim(),
                    binding.emailId.text.toString().trim(),
                    binding.firstName.text.toString().trim(),
                    selectedEncodedImage,
                    binding.lastName.text.toString().trim(),
                    UserUtils.getUserId(this@UserProfileScreen)
                )
                val response = RetrofitBuilder.getRetrofitInstance().updateUserProfile(requestBody)
                val jsonResponse = JSONObject(response.string())
                progressDialog.dismiss()
                if (jsonResponse.getInt("status") == 200) {
                    toast(this@UserProfileScreen, "Profile Updated!")
                } else {
                    Snackbar.make(binding.applyBtn, "Something went wrong!", Snackbar.LENGTH_SHORT)
                        .show()
                }
                onBackPressed()
            } catch (e: HttpException) {
                progressDialog.dismiss()
                Snackbar.make(binding.applyBtn, "Server Busy", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                progressDialog.dismiss()
                Snackbar.make(binding.applyBtn, "Something Went Wrong", Snackbar.LENGTH_SHORT)
                    .show()
            } catch (e: SocketTimeoutException) {
                progressDialog.dismiss()
                Snackbar.make(
                    binding.applyBtn,
                    "Please check internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showUserProfile() {
        progressDialog.show()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = BrowseCategoryReqModel(UserUtils.getUserId(this@UserProfileScreen))
                val response = RetrofitBuilder.getRetrofitInstance().getUserProfile(requestBody)
                val responseData = response.data
                if (response.status == 200) {
                    val imageUrl = RetrofitBuilder.BASE_URL + responseData.profile_pic
                    Glide.with(binding.profilePic).load(imageUrl).into(binding.profilePic)
                    binding.firstName.setText(responseData.fname)
                    binding.lastName.setText(responseData.lname)
                    binding.phoneNo.setText(responseData.mobile)
                    binding.emailId.setText(responseData.email_id)
                    binding.dateOfBirth.setText(responseData.dob)
                    if (responseData.address != null) {
                        val layoutManager = LinearLayoutManager(this@UserProfileScreen)
                        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                        binding.addressRv.layoutManager = layoutManager
                        binding.addressRv.adapter = UserProfileAddressAdapter(responseData.address, this@UserProfileScreen)
                        binding.addressRv.visibility = View.VISIBLE
                    } else {
                        binding.addressText.text = "No Addresses Found!"
                        binding.addressRv.visibility = View.GONE
                    }
                    progressDialog.dismiss()
                } else {
                    Snackbar.make(binding.applyBtn, "Something went wrong!", Snackbar.LENGTH_SHORT)
                        .show()
                    onBackPressed()
                }
            } catch (e: HttpException) {
                progressDialog.dismiss()
                Snackbar.make(binding.applyBtn, "Server Busy", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                progressDialog.dismiss()
                Snackbar.make(binding.applyBtn, "Something Went Wrong", Snackbar.LENGTH_SHORT)
                    .show()
            } catch (e: SocketTimeoutException) {
                progressDialog.dismiss()
                Snackbar.make(
                    binding.applyBtn,
                    "Please check internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            binding.profilePic.setImageURI(selectedImage)
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val bitmap = data.extras!!["data"] as Bitmap?
            binding.profilePic.setImageBitmap(bitmap)
        }
        var imageStream: InputStream? = null
        try {
            imageStream = contentResolver.openInputStream(data!!.data!!)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
        selectedEncodedImage = UserUtils.encodeToBase64(yourSelectedImage)!!
    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
    }

    override fun deleteAddress(addressId: String) {
        if (binding.addressRv.childCount > 1) {
            deleteAddressOnServer(addressId)
        } else {
            toast(this, "You have only Primary Address")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun deleteAddressOnServer(addressId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = BrowseCategoryReqModel(addressId)
                val response = RetrofitBuilder.getRetrofitInstance().deleteUserAddress(requestBody)
                val jsonResponse = JSONObject(response.string())
                if (jsonResponse.getInt("status") == 200) {
                    toast(this@UserProfileScreen, jsonResponse.toString())
                    finish();
                    startActivity(intent);
                } else {
                    Snackbar.make(binding.applyBtn, "Something went wrong!", Snackbar.LENGTH_SHORT).show()
                    onBackPressed()
                }
            } catch (e: HttpException) {
                Snackbar.make(binding.applyBtn, "Server Busy", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                Snackbar.make(binding.applyBtn, "Something Went Wrong", Snackbar.LENGTH_SHORT).show()
            } catch (e: SocketTimeoutException) {
                Snackbar.make(binding.applyBtn, "Please check internet Connection", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}