package com.satrango.ui.user.bookings.booking_attachments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityBookingAttachmentsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_address.BookingAddressScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_address.models.Attachment
import com.satrango.ui.user.bookings.booking_address.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.booking_attachments.models.Addresses
import com.satrango.ui.user.bookings.booking_attachments.models.MultiMoveReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingAttachmentsScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var viewModel: BookingViewModel
    private lateinit var binding: ActivityBookingAttachmentsScreenBinding
    private lateinit var progressDialog: ProgressDialog
    private val GALLERY_REQUEST = 100
    private val CAMERA_REQUEST: Int = 100
    private lateinit var data: Data
    private var addressIndex = 0

    companion object {
        lateinit var imagePathList: ArrayList<String>
        lateinit var encodedImages: ArrayList<Attachment>
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingAttachmentsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)

        val factory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        initializeProgressDialog()
        data = intent.getSerializableExtra(getString(R.string.service_provider)) as Data
        updateUI(data)
        if (UserUtils.addressList.isNotEmpty()) {
            loadAddressOnUI()
        }

        imagePathList = ArrayList()
        encodedImages = ArrayList()

        binding.apply {

            attachments.setOnClickListener {
                openImagePicker()
            }

            backBtn.setOnClickListener {
                onBackPressed()
            }

            nextBtn.setOnClickListener {
                val description = discription.text.toString().trim()
                if (description.isEmpty()) {
                    snackBar(nextBtn, "Enter Description")
                } else {
                    UserUtils.job_description = description

                    when (data.category_id) {
                        "1" -> {
                            val intent = Intent(this@BookingAttachmentsScreen, BookingAddressScreen::class.java)
                            intent.putExtra(getString(R.string.service_provider), data)
                            startActivity(intent)
                        }
                        "2" -> {
                            bookBlueCollarServiceProvider()
                        }
                        "3" -> {
                            UserUtils.finalAddressList = ArrayList()
                            UserUtils.finalAddressList.add(Addresses(UserUtils.addressList[addressIndex].day.toInt(), UserUtils.job_description, addressIndex + 1, 2))
                            addressIndex += 1
                            if (addressIndex != UserUtils.addressList.size) {
                                loadAddressOnUI()
                            } else {
                                bookMultiMoveServiceProvider()
                            }
                        }
                    }
                }
            }

        }
    }

    private fun bookMultiMoveServiceProvider() {
        val requestBody = MultiMoveReqModel(
            UserUtils.finalAddressList,
            data.per_hour,
            encodedImages,
            currentDateAndTime(),
            1,
            1,
            RetrofitBuilder.USER_KEY,
            UserUtils.scheduled_date,
            data.users_id.toInt(),
            UserUtils.started_at,
            UserUtils.time_slot_from,
            UserUtils.time_slot_to,
            UserUtils.getUserId(this).toInt()
        )
        viewModel.multiMoveBooking(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    showWaitingForSPConfirmationDialog()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        })
    }

    private fun loadAddressOnUI() {
        binding.at.visibility = View.VISIBLE
        binding.addressText.visibility = View.VISIBLE
        binding.discription.setText("")
        binding.addressText.text = UserUtils.addressList[addressIndex].month
    }

    private fun openImagePicker() {
        val options = resources.getStringArray(R.array.imageSelections)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select image")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> getImageFromGallery()
                    1 -> capturePictureFromCamera()
                }
            }
        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: Data) {
        binding.userName.text = "${data.fname} ${data.lname}"
        binding.occupation.text = data.profession
        binding.costPerHour.text = data.per_hour
    }

    private fun capturePictureFromCamera() {
        val cameraIntent = Intent()
        cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    private fun getImageFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData != null) {
                val count: Int = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imagePathList.add(getImageFilePath(imageUri))
                    encodedImages.add(Attachment(encodeToBase64FromUri(imageUri)))
                }
            } else if (data.data != null) {
                val imageUri = data.data
                imagePathList.add(getImageFilePath(imageUri!!))
                encodedImages.add(Attachment(encodeToBase64FromUri(imageUri)))
            }
            binding.attachmentsRV.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            getImageFilePath(data.data!!)
            val bitmap = data.extras!!["data"] as Bitmap?
            binding.profilePic.setImageBitmap(bitmap)
        }

    }

    private fun encodeToBase64FromUri(imageUri: Uri): String {
        var imageStream: InputStream? = null
        try {
            imageStream = contentResolver.openInputStream(imageUri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
        return UserUtils.encodeToBase64(yourSelectedImage)!!
    }

    private fun getImageFilePath(uri: Uri): String {
        val file = File(uri.path!!)
        val filePath: List<String> = file.path.split(":")
        val image_id = filePath[filePath.size - 1]
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(image_id),
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            Log.e("IMAGES PATH: ", imagePath)
            cursor.close()
            return imagePath
        }
        return ""
    }

    override fun deleteAttachment(position: Int, imagePath: String) {
        imagePathList.remove(imagePath)
        binding.attachmentsRV.adapter!!.notifyItemRemoved(position)
        Handler().postDelayed({
            binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
            encodedImages.remove(encodedImages[position])
        }, 500)
    }

    @SuppressLint("SimpleDateFormat")
    private fun bookBlueCollarServiceProvider() {
        val requestBody = BlueCollarBookingReqModel(
            data.per_hour,
            encodedImages,
            currentDateAndTime(),
            1,
            1,
            UserUtils.job_description,
            RetrofitBuilder.USER_KEY,
            UserUtils.scheduled_date,
            data.users_id.toInt(),
            UserUtils.started_at,
            UserUtils.time_slot_from,
            UserUtils.time_slot_to,
            UserUtils.getUserId(this).toInt()
        )
        Log.e("BLUE COLLAR MOVE", Gson().toJson(requestBody))
        viewModel.blueCollarBooking(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    showWaitingForSPConfirmationDialog()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun currentDateAndTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
    }

    @SuppressLint("SetTextI18n")
    private fun showWaitingForSPConfirmationDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.waiting_for_sp_confirmation_dialog, null)
        val progressBar = dialogView.findViewById<CircularProgressIndicator>(R.id.progressBar)
        val time = dialogView.findViewById<TextView>(R.id.time)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        var minutes = 2
        var seconds = 59
        val mainHandler = Handler(Looper.getMainLooper())
        var progressTime = 180
        mainHandler.post(object : Runnable {
            override fun run() {
                time.text = "$minutes:$seconds"
                progressTime -= 1
                progressBar.progress = progressTime

                seconds -= 1
                if (minutes == 0 && seconds == 0) {
                    dialog.dismiss()
                    weAreSorryDialog()
                }
                if (seconds == 0) {
                    seconds = 59
                    minutes -= 1
                }
                mainHandler.postDelayed(this, 1000)
            }
        })
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun weAreSorryDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.no_service_provider_found, null)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesBtn.setOnClickListener {
            snackBar(yesBtn, "Post the Job")
            dialog.dismiss()
            finish()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            showWaitingForSPConfirmationDialog()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }


}