package com.satrango.ui.auth.provider_signup.provider_sign_up_four

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderSignUpFourBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.provider_signup.provider_sign_up_five.ProviderSignUpFive
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.*
import com.satrango.utils.*
import org.intellij.lang.annotations.Language
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

class ProviderSignUpFour: AppCompatActivity() {

    private lateinit var progressDialog: BeautifulProgressDialog
    private var selectedEncodedImage = ""
    private val REQUEST_CAMERA: Int = 101
    private val SELECT_FILE: Int = 100
    private lateinit var binding: ActivityProviderSignUpFourBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpFourBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProgressDialog()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        }

        PermissionUtils.checkAndRequestPermissions(this)

        binding.apply {

            uploadIdProof.setOnClickListener { selectImage() }

            submitBtn.setOnClickListener {
                validateFields()
            }

        }

    }

    private fun validateFields() {
        if (selectedEncodedImage.isEmpty()) {
            snackBar(binding.submitBtn, "Please Select Identity Proof")
        } else {
            sendActivationRequestToServer()
        }
    }

    private fun sendActivationRequestToServer() {
        val requestBody = ProviderIdProofReqModel(selectedEncodedImage, RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(this))
//        toast(this, requestBody.toString())
        val factory = ViewModelFactory(ProviderSignUpFourRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderSignUpFourViewModel::class.java]
        viewModel.uploadIdProof(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    toast(this, it.data!!)
                    startActivity(Intent(this, ProviderSignUpFive::class.java))
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.submitBtn, it.message!!)
                }
            }
        }

    }

    private fun selectImage() {
        val items = arrayOf<CharSequence>("Choose from Library", "Capture with Camera")
        val builder = AlertDialog.Builder(this@ProviderSignUpFour)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            when (item) {
                0 -> galleryIntent()
                1 -> cameraIntent()
            }
        }
        builder.show()
    }

    private fun galleryIntent() {
        ProviderUtils.imagePath = ""
        val intent = Intent()
        val mineType = arrayOf("image/jpeg", "image/jpg", "image/png")
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mineType)
        startActivityForResult(intent, SELECT_FILE)
    }

    private fun cameraIntent() {
        val cameraIntent = Intent()
        cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var imageStream: InputStream? = null
        if (requestCode == SELECT_FILE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                imageStream = contentResolver.openInputStream(data.data!!)
                binding.imagePath.text = data.data!!.path
                ProviderUtils.imagePath = data.data!!.path!!
            } catch (e: Exception) {
                snackBar(binding.submitBtn, e.message!!)
            }
        } else if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            try {
                imageStream = contentResolver.openInputStream(getImageUri(this, imageBitmap!!)!!)
                binding.imagePath.text = "Captured by Camera"
            } catch (e: Exception) {
                snackBar(binding.checkIcon, e.message!!)
            }
        }
        if (imageStream != null) {
            val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
            selectedEncodedImage = UserUtils.encodeToBase64(yourSelectedImage)!!
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.PERMISSIONS_CODE == requestCode && grantResults.isNotEmpty()) {
            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    PermissionUtils.checkAndRequestPermissions(this)
                }
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
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}