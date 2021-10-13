package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderProfileScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.set_password.SetPasswordScreen
import com.satrango.ui.service_provider.provider_dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.ProviderProfileProfessionResModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ProviderProfileScreen : AppCompatActivity() {

    private val GALLERY_REQUEST = 100
    private val CAMERA_REQUEST: Int = 101

    companion object {
        lateinit var professionalDetails: ProviderProfileProfessionResModel
        lateinit var binding: ActivityProviderProfileScreenBinding
        lateinit var progressDialog: ProgressDialog
        var selectedEncodedImage = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener {
            startActivity(Intent(this, ProviderDashboard::class.java))
        }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener {
            startActivity(Intent(this, ProviderDashboard::class.java))
        }
        toolBar.findViewById<CircleImageView>(R.id.toolBarImage).visibility = View.GONE
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_profile)

        initializeProgressDialog()
        loadFragment(PersonalProfileScreen())
        loadProfessionalDetails()

        binding.apply {

            changePwd.setOnClickListener {
                startActivity(Intent(this@ProviderProfileScreen, SetPasswordScreen::class.java))
            }

            profileUploadBtn.setOnClickListener { openImagePicker() }

            personalBtn.setOnClickListener {
                personalBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                personalBtn.setTextColor(resources.getColor(R.color.white))
                skillsBtn.setBackgroundResource(R.drawable.purple_out_line)
                skillsBtn.setTextColor(resources.getColor(R.color.purple_500))
                tariffBtn.setBackgroundResource(R.drawable.purple_out_line)
                tariffBtn.setTextColor(resources.getColor(R.color.purple_500))
                loadFragment(PersonalProfileScreen())
            }

            skillsBtn.setOnClickListener {
                skillsBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                skillsBtn.setTextColor(resources.getColor(R.color.white))
                personalBtn.setBackgroundResource(R.drawable.purple_out_line)
                personalBtn.setTextColor(resources.getColor(R.color.purple_500))
                tariffBtn.setBackgroundResource(R.drawable.purple_out_line)
                tariffBtn.setTextColor(resources.getColor(R.color.purple_500))
                loadFragment(SkillsProfileScreen())
            }

            tariffBtn.setOnClickListener {
                tariffBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                tariffBtn.setTextColor(resources.getColor(R.color.white))
                skillsBtn.setBackgroundResource(R.drawable.purple_out_line)
                skillsBtn.setTextColor(resources.getColor(R.color.purple_500))
                personalBtn.setBackgroundResource(R.drawable.purple_out_line)
                personalBtn.setTextColor(resources.getColor(R.color.purple_500))
                progressDialog.show()
                loadFragment(TariffTimingsProfileScreen())
            }

        }

    }

    private fun loadProfessionalDetails() {
        val factory = ViewModelFactory(ProviderProfileRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderProfileViewModel::class.java]
        val requestBody =
            ProviderBookingReqModel(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(this).toInt())
        viewModel.professionalDetails(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    professionalDetails = it.data!!
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.changePwd, it.message!!)
                }
            }
        })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var imageStream: InputStream? = null
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            binding.profilePic.setImageURI(selectedImage)
            try {
                imageStream = contentResolver.openInputStream(data.data!!)
            } catch (e: Exception) {
                snackBar(binding.changePwd, e.message!!)
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            binding.profilePic.setImageBitmap(imageBitmap)
            try {
                imageStream = contentResolver.openInputStream(getImageUri(this, imageBitmap!!)!!)
            } catch (e: Exception) {
                snackBar(binding.changePwd, e.message!!)
            }
        }
        if (imageStream != null) {
            val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
            selectedEncodedImage = UserUtils.encodeToBase64(yourSelectedImage)!!
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading Profile...")
    }
}