package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ktx.database
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderProfileScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.set_password.SetPasswordScreen
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.ProviderProfileProfessionResModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*


class ProviderProfileScreen : AppCompatActivity() {

    private val GALLERY_REQUEST = 100
    private val CAMERA_REQUEST: Int = 101

    companion object {
        lateinit var professionalDetails: ProviderProfileProfessionResModel
        lateinit var binding: ActivityProviderProfileScreenBinding
        @SuppressLint("StaticFieldLeak")
        lateinit var progressDialog: BeautifulProgressDialog
        var selectedEncodedImage = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        }

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener {
            UserUtils.saveFromFCMService(this, false)
            startActivity(Intent(this, ProviderDashboard::class.java))
        }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener {
            UserUtils.saveFromFCMService(this, false)
            startActivity(Intent(this, ProviderDashboard::class.java))
        }
        toolBar.findViewById<CircleImageView>(R.id.toolBarImage).visibility = View.GONE
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.my_profile)

        initializeProgressDialog()
        loadProfessionalDetails()
        loadFragment(PersonalProfileScreen())

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
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody =
                    ProviderBookingReqModel(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(this@ProviderProfileScreen).toInt())
                val response = RetrofitBuilder.getServiceProviderRetrofitInstance().getProfessionalDetails(requestBody)
//                toast(this@ProviderProfileScreen, response.string())
                val data = Gson().fromJson(response.string(), ProviderProfileProfessionResModel::class.java)
                if (data.status == 200) {
                    professionalDetails = data
                    createQRLinkLink()
                }
            } catch (e: Exception) {
                snackBar(binding.changePwd, e.message!!)
            }
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

    @SuppressLint("SimpleDateFormat")
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, timeStamp, null)
        return Uri.parse(path)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ProviderDashboard::class.java))
    }

    private fun createQRLinkLink() {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("http://dev.satrango.com/userid=SP${UserUtils.getUserId(this@ProviderProfileScreen)}|${Gson().toJson(professionalDetails.profession)}")
            domainUriPrefix = "https://squill.page.link"
            androidParameters {
                this.build()
            }
        }

        val dynamicLinkUri = dynamicLink.uri
        val shortLinkTask: Task<ShortDynamicLink> =
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLinkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        val shortLink: Uri? = task.result?.shortLink
                        val flowchartLink: Uri? = task.result?.previewLink
                        generateQRCode(shortLink.toString())
                        shortLink.toString()
                    } else {
                        Toast.makeText(this@ProviderProfileScreen, "QR Code Error:${task.exception}", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun generateQRCode(data: String) {
        val qrWriter = QRCodeWriter()
        try {
            val bitMatrix = qrWriter.encode(data, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            binding.qrCode.setImageBitmap(bitmap)

        } catch (e: WriterException) {
            toast(this, e.message!!)
        }
    }
}