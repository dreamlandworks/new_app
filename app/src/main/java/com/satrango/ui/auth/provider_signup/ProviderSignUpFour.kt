package com.satrango.ui.auth.provider_signup

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.satrango.R
import com.satrango.databinding.ActivityProviderSignUpFourBinding
import com.satrango.utils.PermissionUtils
import java.io.*

class ProviderSignUpFour : AppCompatActivity() {

    private val REQUEST_CAMERA: Int = 101
    private val SELECT_FILE: Int = 100
    private lateinit var binding: ActivityProviderSignUpFourBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpFourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PermissionUtils.checkAndRequestPermissions(this)

        val toolBar = findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.profile)

        binding.apply {

            uploadIdProof.setOnClickListener { selectImage() }

            backBtn.setOnClickListener { onBackPressed() }

            submitBtn.setOnClickListener { startActivity(Intent(this@ProviderSignUpFour, ProviderSignUpFive::class.java)) }

        }

    }

    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = AlertDialog.Builder(this@ProviderSignUpFour)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Take Photo") {
                cameraIntent()
            } else if (items[item] == "Choose from Library") {
                galleryIntent()
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun galleryIntent() {
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
        val destination = File(
            Environment.getExternalStorageDirectory(),
            System.currentTimeMillis().toString() + ".jpg"
        )
        val fo: FileOutputStream
        try {
            fo = FileOutputStream(destination)
            if (destination != null) {
                val filenew1 = destination.absolutePath
                binding.txtdocument.text = destination.absolutePath.toString() + ".jpg"
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
        var bm: Bitmap? = null
        if (data != null) {
            val pickedImage = data.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(pickedImage!!, filePath, null, null, null)
            cursor!!.moveToFirst()
            val destination = File(cursor.getString(cursor.getColumnIndex(filePath[0])))
            cursor.close()
            if (destination != null) {
                binding.txtdocument.text = destination.absolutePath.toString() + ".jpg"
            } else {
                Toast.makeText(this@ProviderSignUpFour, "something wrong", Toast.LENGTH_SHORT)
                    .show()
            }
            try {
                bm = MediaStore.Images.Media.getBitmap(this@ProviderSignUpFour.contentResolver,data.data)
            } catch (e: IOException) {
                e.printStackTrace()
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