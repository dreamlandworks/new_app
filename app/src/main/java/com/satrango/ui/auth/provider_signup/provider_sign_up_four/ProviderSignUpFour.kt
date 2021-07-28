package com.satrango.ui.auth.provider_signup.provider_sign_up_four

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderSignUpFourBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.provider_signup.provider_sign_up_five.ProviderSignUpFive
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProviderSignUpFourReqModel
import com.satrango.utils.*
import java.io.*

class ProviderSignUpFour : AppCompatActivity() {

    private var selectedEncodedImage = ""
    private lateinit var viewModel: ProviderSignUpFourViewModel
    private val REQUEST_CAMERA: Int = 101
    private val SELECT_FILE: Int = 100
    private lateinit var binding: ActivityProviderSignUpFourBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpFourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(ProviderSignUpFourRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderSignUpFourViewModel::class.java]

        PermissionUtils.checkAndRequestPermissions(this)

        binding.apply {

            uploadIdProof.setOnClickListener { selectImage() }

            submitBtn.setOnClickListener {
                validateFields()
            }

        }

    }

    private fun validateFields() {
        if (ProviderUtils.imagePath.isEmpty()) {
            snackBar(binding.submitBtn, "Please Select Identity Proof")
        } else {
            sendActivationRequestToServer()
        }
    }

    private fun sendActivationRequestToServer() {

        val requestBody = ProviderSignUpFourReqModel(
            RetrofitBuilder.PROVIDER_KEY,
            UserUtils.getUserId(this),
            ProviderUtils.experience,
            ProviderUtils.aboutMe,
            ProviderUtils.perHour,
            ProviderUtils.perDay,
            ProviderUtils.minCharge,
            ProviderUtils.extraCharge,
            selectedEncodedImage,
            ProviderUtils.profession!!,
            ProviderUtils.qualification!!,
            ProviderUtils.languagesKnown!!,
            ProviderUtils.keywordsSkills!!,
            ProviderUtils.slotsList!!,
        )
        viewModel.providerActivation(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    toast(this, "Loading...")
                }
                is NetworkResponse.Success -> {
                    startActivity(Intent(this, ProviderSignUpFive::class.java))
                }
                is NetworkResponse.Failure -> {
                    snackBar(binding.submitBtn, it.message!!)
                }
            }
        })
    }

    private fun selectImage() {
        val items = arrayOf<CharSequence>("Choose from Library", "Cancel")
        val builder = AlertDialog.Builder(this@ProviderSignUpFour)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Choose from Library") {
                galleryIntent()
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun galleryIntent() {
        ProviderUtils.imagePath = ""
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, SELECT_FILE)
    }

    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data)
            } else if (requestCode == REQUEST_CAMERA) onCaptureImageResult(data!!)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onCaptureImageResult(data: Intent) {
        val thumbnail = data.extras!!["data"] as Bitmap?
        val bytes = ByteArrayOutputStream()
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val destination =
            File(Environment.getExternalStorageDirectory(), System.currentTimeMillis().toString())
        val fo: FileOutputStream
        try {
            fo = FileOutputStream(destination)
            if (destination != null) {
                val filenew1 = destination.absolutePath
                binding.imagePath.text = destination.absolutePath.toString()
            } else {
                Toast.makeText(this@ProviderSignUpFour, "something wrong", Toast.LENGTH_SHORT)
                    .show()
            }
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onSelectFromGalleryResult(data: Intent?) {
        if (data != null) {
            val pickedImage = data.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(pickedImage!!, filePath, null, null, null)
            cursor!!.moveToFirst()
            val destination = File(cursor.getString(cursor.getColumnIndex(filePath[0])))
            cursor.close()
            var imageStream: InputStream? = null
            try {
                imageStream = contentResolver.openInputStream(data.data!!)
                val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
                selectedEncodedImage = UserUtils.encodeToBase64(yourSelectedImage)!!
            } catch (e: Exception) {
                snackBar(binding.submitBtn, e.message!!)
            }
            if (destination != null) {
                ProviderUtils.imagePath = destination.absolutePath.toString()
                binding.imagePath.text =
                    ProviderUtils.imagePath.split("/")[ProviderUtils.imagePath.split("/").size - 1]
            } else {
                snackBar(binding.submitBtn, "Something went Wrong, Select Image Again")
            }
        }
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
}