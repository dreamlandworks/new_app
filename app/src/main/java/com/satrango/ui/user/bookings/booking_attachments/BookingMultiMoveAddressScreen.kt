package com.satrango.ui.user.bookings.booking_attachments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.razorpay.Checkout
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityBookingMultiMoveAddressScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.payment_screen.PaymentScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_attachments.models.Addresses
import com.satrango.ui.user.bookings.booking_attachments.models.MultiMoveReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

class BookingMultiMoveAddressScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseDatabaseRef: DatabaseReference
    private lateinit var data: Data
    private var addressIndex: Int = 0
    private lateinit var binding: ActivityBookingMultiMoveAddressScreenBinding
    lateinit var imagePathList: ArrayList<com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment>
    lateinit var encodedImages: ArrayList<Attachment>
    private lateinit var viewModel: BookingViewModel
    private lateinit var waitingDialog: BottomSheetDialog
    private lateinit var progressDialog: BeautifulProgressDialog
    private val GALLERY_REQUEST = 100
    private val CAMERA_REQUEST: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingMultiMoveAddressScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            data = Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java)
            updateUI(data)
        }

        val bookingFactory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, bookingFactory)[BookingViewModel::class.java]

        database = Firebase.database
        firebaseDatabaseRef = database.getReference(UserUtils.getFCMToken(this))
        
        imagePathList = ArrayList()
        encodedImages = ArrayList()

        if (UserUtils.addressList.isNotEmpty()) {
            loadAddressOnUI()
        }

        binding.apply {

            attachments.setOnClickListener {
                openImagePicker()
            }

            backBtn.setOnClickListener { onBackPressed() }

            UserUtils.finalAddressList = ArrayList()

            nextBtn.setOnClickListener {

                val description = discription.text.toString().trim()
                if (description.isEmpty()) {
                    snackBar(nextBtn, "Enter Description")
                } else {
                    UserUtils.job_description = description
                    var address = ""
                    var city = ""
                    var state = ""
                    var country = ""
                    var postalCode = ""
                    var latitude = ""
                    var longitude = ""
                    if (UserUtils.addressList[addressIndex].day == "0") {
                        address = UserUtils.getAddress(this@BookingMultiMoveAddressScreen)
                        city = UserUtils.getCity(this@BookingMultiMoveAddressScreen)
                        state = UserUtils.getState(this@BookingMultiMoveAddressScreen)
                        country = UserUtils.getCountry(this@BookingMultiMoveAddressScreen)
                        postalCode = UserUtils.getPostalCode(this@BookingMultiMoveAddressScreen)
                        latitude = UserUtils.getLatitude(this@BookingMultiMoveAddressScreen)
                        longitude = UserUtils.getLongitude(this@BookingMultiMoveAddressScreen)
                    }
                    UserUtils.finalAddressList.add(
                        Addresses(
                            UserUtils.addressList[addressIndex].day.toInt(),
                            UserUtils.job_description,
                            addressIndex + 1,
                            2,
                            address,
                            city,
                            state,
                            country,
                            postalCode,
                            latitude,
                            longitude
                        )
                    )
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

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.booking)
    }

    private fun loadAddressOnUI() {
        binding.discription.setText("")
        binding.addressText.text = UserUtils.addressList[addressIndex].month
    }

    @SuppressLint("SimpleDateFormat")
    private fun bookMultiMoveServiceProvider() {
        var finalAmount = 0
        var spId = "0"
        var cgst = "0"
        var sgst = "0"
        if (data != null) {
            finalAmount = data.final_amount
            spId = data.users_id
            cgst = data.CGST_amount
            sgst = data.SGST_amount
        }

        val requestBody = MultiMoveReqModel(
            UserUtils.finalAddressList,
            finalAmount.toString(),
            encodedImages,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            1,
            1,
            RetrofitBuilder.USER_KEY,
            UserUtils.scheduled_date,
            spId.toInt(),
            UserUtils.time_slot_from,
            UserUtils.time_slot_from,
            UserUtils.time_slot_to,
            UserUtils.getUserId(this).toInt(),
            sgst,
            cgst,
            data!!.profession_id
        )
        Log.e("MULTI MOVE:", Gson().toJson(requestBody))
        viewModel.multiMoveBooking(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    showWaitingForSPConfirmationDialog()
                    if (UserUtils.getFromInstantBooking(this)) {
                        if (PermissionUtils.isNetworkConnected(this)) {
                            UserUtils.sendFCMtoAllServiceProviders(
                                this,
                                UserUtils.getBookingId(this),
                                "user",
                                "accepted|${UserUtils.bookingType}"
                            )
                        } else {
                            snackBar(binding.nextBtn, "No Internet Connection!")
                        }
                    } else {
                        UserUtils.sendFCMtoSelectedServiceProvider(
                            this,
                            UserUtils.getBookingId(this),
                            "user"
                        )
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, "MULTI MOVE:" + it.message!!)
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
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun showWaitingForSPConfirmationDialog() {
        waitingDialog = BottomSheetDialog(this)
        waitingDialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.waiting_for_sp_confirmation_dialog, null)
        val progressBar = dialogView.findViewById<CircularProgressIndicator>(R.id.progressBar)
        val time = dialogView.findViewById<TextView>(R.id.time)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            UserUtils.sendFCMtoAllServiceProviders(this, UserUtils.getBookingId(this), "accepted", "accepted|${UserUtils.bookingType}")
            finish()
            startActivity(intent)
            waitingDialog.dismiss()
        }
        var minutes = 2
        var seconds = 59
        val mainHandler = Handler(Looper.getMainLooper())
        var progressTime = 180
        mainHandler.post(object : Runnable {
            override fun run() {
                if (seconds < 10) {
                    time.text = "0$minutes:0$seconds"
                } else {
                    time.text = "0$minutes:$seconds"
                }

                progressTime -= 1
                progressBar.progress = progressTime

                seconds -= 1
                if (minutes == 0 && seconds == 0) {
                    UserUtils.sendFCMtoAllServiceProviders(this@BookingMultiMoveAddressScreen, "accepted", "accepted", "accepted|${UserUtils.bookingType}")
                    waitingDialog.dismiss()
                    try {
                        weAreSorryDialog()
                    } catch (e: java.lang.Exception) {
                    }
                    Checkout.preload(applicationContext)
                    weAreSorryDialog()
//                    startActivity(Intent(this@BookingAddressScreen, UserDashboardScreen::class.java)
                }
                if (seconds == 0) {
                    seconds = 59
                    minutes -= 1
                }
                if (UserUtils.getProviderAction(this@BookingMultiMoveAddressScreen).split("|")[0].isNotEmpty()) {
                    waitingDialog.dismiss()
                    if (UserUtils.getProviderAction(this@BookingMultiMoveAddressScreen)
                            .split("|")[0].trim() == "accept"
                    ) {
                        serviceProviderAcceptDialog(this@BookingMultiMoveAddressScreen)
                        UserUtils.sendFCMtoAllServiceProviders(this@BookingMultiMoveAddressScreen, "accepted", "accepted", "accepted|${UserUtils.bookingType}")
                    } else {
                        serviceProviderRejectDialog(this@BookingMultiMoveAddressScreen)
                        UserUtils.sendFCMtoAllServiceProviders(this@BookingMultiMoveAddressScreen, "accepted", "accepted", "accepted|${UserUtils.bookingType}")
                    }
                }
                mainHandler.postDelayed(this, 1000)
            }
        })
        waitingDialog.setContentView(dialogView)
        waitingDialog.show()
    }

    private fun serviceProviderRejectDialog(context: Context) {
        val dialog = BottomSheetDialog(context)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.rejected_by_service_provider_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
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
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun serviceProviderAcceptDialog(context: Context) {
        val dialog = BottomSheetDialog(context)
        dialog.setCancelable(false)
        val dialogView = layoutInflater.inflate(R.layout.sp_accepted_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        val finalAmount = if (data != null) {
            data.final_amount           
        } else {
            0
        }
        val finalUserId = if (data != null) {
            data.users_id
        } else {
            "0"
        }
        Handler().postDelayed({
//            makePayment()
            PaymentScreen.FROM_USER_BOOKING_ADDRESS = true
            PaymentScreen.FROM_USER_PLANS = false
            PaymentScreen.FROM_PROVIDER_PLANS = false
            PaymentScreen.FROM_PROVIDER_BOOKING_RESPONSE = false
            PaymentScreen.FROM_USER_SET_GOALS = false
            PaymentScreen.amount = finalAmount
            PaymentScreen.userId = finalUserId.toInt()
            startActivity(Intent(this, PaymentScreen::class.java))
        }, 3000)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun weAreSorryDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.no_service_provider_found, null)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
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
        dialog.setCancelable(false)
        if (!(this as Activity).isFinishing) {
            dialog.show()
        }
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

    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            if (data.clipData != null) {
                val count: Int = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imagePathList.add(
                        com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment(
                            "",
                            getImageFilePath(imageUri),
                            "",
                            ""
                        )
                    )
                    encodedImages.add(Attachment(encodeToBase64FromUri(imageUri)))
                }
            } else if (data.data != null) {
                val imageUri = data.data
                imagePathList.add(
                    com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment(
                        "",
                        getImageFilePath(imageUri!!),
                        "",
                        ""
                    )
                )
                encodedImages.add(Attachment(encodeToBase64FromUri(imageUri)))
            }
            binding.attachmentsRV.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            val storageRef = FirebaseStorage.getInstance().reference
            val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            val profilePicStorageRef = storageRef.child("images/$timeStamp.jpg")
            profilePicStorageRef.putFile(getImageUri(this, imageBitmap!!)!!).addOnFailureListener {
                toast(this, it.message!!)
            }.addOnSuccessListener {
                profilePicStorageRef.downloadUrl.addOnSuccessListener { uri ->
                    val url = uri.toString()
                    firebaseDatabaseRef.child(timeStamp).setValue(url)
                    progressDialog.dismiss()
                    val menuListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            dataSnapshot.children.forEach { data ->
                                val image_url = data.value.toString()
                                val image_key = data.key.toString()
                                Log.e("SNAPSHOT:", image_url)
                                var existed = false
                                for (image in imagePathList) {
                                    if (com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment("", image_url, "", image_key).file_name == image_url) {
                                        existed = true
                                    }
                                }
                                if (!existed) {
                                    imagePathList.add(com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment("", image_url, "", image_key))
                                    encodedImages.add(Attachment(image_url))
                                }
                            }
                            binding.attachmentsRV.layoutManager = LinearLayoutManager(this@BookingMultiMoveAddressScreen, LinearLayoutManager.HORIZONTAL, false)
                            binding.attachmentsRV.adapter = AttachmentsAdapter(
                                imagePathList, this@BookingMultiMoveAddressScreen)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    }
                    firebaseDatabaseRef = database.getReference(UserUtils.getFCMToken(this))
                    firebaseDatabaseRef.addListenerForSingleValueEvent(menuListener)
                }
            }
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
    
    override fun deleteAttachment(
        position: Int,
        imagePath: com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment
    ) {
        firebaseDatabaseRef.child(imagePath.id).removeValue()
        imagePathList.remove(imagePath)
        binding.attachmentsRV.adapter!!.notifyItemRemoved(position)
        Handler().postDelayed({
            binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
            if (encodedImages.isNotEmpty()) {
                encodedImages.remove(encodedImages[position])
            }
        }, 500)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, timeStamp, null)
        return Uri.parse(path)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: Data) {
        binding.userName.text = "${data.fname} ${data.lname}"
        binding.occupation.text = data.profession
        binding.costPerHour.text = "Rs. ${round(data.final_amount.toDouble()).toInt()}/-"
        Glide.with(this).load(RetrofitBuilder.BASE_URL + data.profile_pic).into(binding.profilePic)
    }

}