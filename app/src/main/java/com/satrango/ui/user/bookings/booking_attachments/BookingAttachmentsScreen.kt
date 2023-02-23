package com.satrango.ui.user.bookings.booking_attachments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityBookingAttachmentsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMService
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.bookings.booking_address.BookingAddressScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_address.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.provider_response.PaymentConfirmReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.ProviderResponseReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobTypeScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


class BookingAttachmentsScreen : AppCompatActivity(), AttachmentsListener
//,
//    PaymentResultListener
{

    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseDatabaseRef: DatabaseReference
    private lateinit var viewModel: BookingViewModel
    private lateinit var binding: ActivityBookingAttachmentsScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog
    private val GALLERY_REQUEST = 100
    private val CAMERA_REQUEST: Int = 101
    private var data: Data? = null
    private var addressIndex = 0

    private lateinit var mGetContent: ActivityResultLauncher<String>
    private lateinit var mGetPermission: ActivityResultLauncher<Intent>

    companion object {
        lateinit var imagePathList: ArrayList<com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment>
        lateinit var encodedImages: ArrayList<Attachment>
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingAttachmentsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()

        database = Firebase.database
        firebaseDatabaseRef = database.getReference(UserUtils.getFCMToken(this))

        val factory = ViewModelFactory(BookingRepository())
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        initializeProgressDialog()

        if (UserUtils.getBookingType(this) != "instant") {
            initializeProgressDialog()
            if (UserUtils.addressList.isNotEmpty()) {
                loadAddressOnUI()
            }
        }
        if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            data = Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java)
            updateUI(data!!)
        } else {
            binding.spCard.visibility = View.GONE
        }

        imagePathList = ArrayList()
        encodedImages = ArrayList()

        binding.apply {

            attachments.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(intent, GALLERY_REQUEST)
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
                    if (UserUtils.getBookingType(this@BookingAttachmentsScreen) != "instant") {
                        when (data!!.category_id) {
                            "1" -> {
                                UserUtils.saveSelectedSPDetails(
                                    this@BookingAttachmentsScreen,
                                    Gson().toJson(data)
                                )
                                val intent = Intent(
                                    this@BookingAttachmentsScreen,
                                    BookingAddressScreen::class.java
                                )
                                startActivity(intent)
                            }
                            "2" -> {
                                bookBlueCollarServiceProvider()
                            }
                        }
                    } else {
                        if (UserUtils.getSelectedSPDetails(this@BookingAttachmentsScreen)
                                .isNotEmpty()
                        ) {
                            UserUtils.saveSelectedSPDetails(
                                this@BookingAttachmentsScreen,
                                Gson().toJson(data)
                            )
                        }
                        if (data != null) {
                            if (data!!.category_id == "2") {
                                UserUtils.scheduled_date =
                                    SimpleDateFormat("yyyy-MM-dd").format(Date())
                                val startedAt = SimpleDateFormat("hh:mm:ss").format(Date())
                                val hour = startedAt.split(":")[0].toInt() + 1
                                if (hour < 10) {
                                    UserUtils.started_at = "0$hour:00:00"
                                    UserUtils.time_slot_from = "0$hour:00:00"
                                } else {
                                    UserUtils.started_at = "$hour:00:00"
                                    UserUtils.time_slot_from = "$hour:00:00"
                                }
                                if (hour + 1 < 10) {
                                    UserUtils.time_slot_to = "0${hour + 1}:00:00"
                                } else {
                                    UserUtils.time_slot_to = "${hour + 1}:00:00"
                                }
                                bookBlueCollarServiceProvider()
                            } else {
                                startActivity(
                                    Intent(
                                        this@BookingAttachmentsScreen,
                                        BookingAddressScreen::class.java
                                    )
                                )
                            }
                        } else if (UserUtils.getSelectedKeywordCategoryId(this@BookingAttachmentsScreen) == "2") {
                            UserUtils.scheduled_date = SimpleDateFormat("yyyy-MM-dd").format(Date())
                            val startedAt = SimpleDateFormat("hh:mm:ss").format(Date())
                            val hour = startedAt.split(":")[0].toInt() + 1
                            if (hour < 10) {
                                UserUtils.started_at = "0$hour:00:00"
                                UserUtils.time_slot_from = "0$hour:00:00"
                            } else {
                                UserUtils.started_at = "$hour:00:00"
                                UserUtils.time_slot_from = "$hour:00:00"
                            }
                            if (hour + 1 < 10) {
                                UserUtils.time_slot_to = "0${hour + 1}:00:00"
                            } else {
                                UserUtils.time_slot_to = "${hour + 1}:00:00"
                            }
                            bookBlueCollarServiceProvider()
                        } else {
                            startActivity(
                                Intent(
                                    this@BookingAttachmentsScreen,
                                    BookingAddressScreen::class.java
                                )
                            )
                        }
                    }
                }
            }
        }

        mGetContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) toast(this, it.path!!)
        }

        mGetPermission =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    toast(this, "Permission Given In Android 11")
                } else {
                    toast(this, "Permission Denied")
                }
            }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mGetContent.launch("pdf/*")
                toast(this, "Permission Granted")
            }
        }
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener {
            onBackPressed()
        }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener {
            onBackPressed()
        }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).error(R.drawable.images)
            .into(profilePic)
    }

    private fun loadAddressOnUI() {
        binding.at.visibility = View.VISIBLE
        binding.addressText.visibility = View.VISIBLE
        binding.discription.setText("")
        binding.addressText.text = UserUtils.addressList[addressIndex].month
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: Data) {
        binding.userName.text = "${data.fname} ${data.lname}"
        binding.occupation.text = data.profession
        binding.costPerHour.text = "Rs. ${round(data.final_amount.toDouble()).toInt()}/-"
        Glide.with(this).load(data.profile_pic).error(R.drawable.images).into(binding.profilePic)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            if (data.clipData != null) {
                val count: Int = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val uri = data.clipData!!.getItemAt(i).uri
                    val encodedImage = UserUtils.getBase64FromFile(this, uri)!!
                    val fileExtension = UserUtils.getFileExtension(this, uri)
                    imagePathList.add(
                        com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment(
                            "",
                            encodedImage,
                            "",
                            fileExtension,
                            ""
                        )
                    )
                    encodedImages.add(Attachment(encodedImage, fileExtension))
                }
            } else if (data.data != null) {
                val uri = data.data
                val encodedImage = UserUtils.getBase64FromFile(this, uri)!!
                val fileExtension = UserUtils.getFileExtension(this, uri!!)
                imagePathList.add(
                    com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment(
                        "",
                        encodedImage,
                        "",
                        fileExtension,
                        ""
                    )
                )
                encodedImages.add(Attachment(encodedImage, UserUtils.getFileExtension(this, uri)))
            }
            binding.attachmentsRV.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
//            Log.e("IMAGES:", Gson().toJson(encodedImages))
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            val storageRef = FirebaseStorage.getInstance().reference
            val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            val profilePicStorageRef = storageRef.child("images/$timeStamp.jpg")
            val imageProgressDialog = ProgressDialog(this)
            imageProgressDialog.setMessage("Uploading Image...")
            imageProgressDialog.setCancelable(false)
            imageProgressDialog.show()
            profilePicStorageRef.putFile(getImageUri(this, imageBitmap!!)!!).addOnFailureListener {
                imageProgressDialog.dismiss()
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
                                var existed = false
                                for (image in imagePathList) {
                                    if (com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment(
                                            "",
                                            image_url,
                                            "",
                                            image_key,
                                            ""
                                        ).file_name == image_url
                                    ) {
                                        existed = true
                                    }
                                }
                                if (!existed) {
                                    imagePathList.add(
                                        com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment(
                                            "",
                                            image_url,
                                            "",
                                            image_key,
                                            ""
                                        )
                                    )
                                    encodedImages.add(
                                        Attachment(
                                            image_url,
                                            UserUtils.getFileExtension(
                                                this@BookingAttachmentsScreen,
                                                uri
                                            )
                                        )
                                    )
                                }
                            }
                            binding.attachmentsRV.layoutManager = LinearLayoutManager(
                                this@BookingAttachmentsScreen,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            binding.attachmentsRV.adapter =
                                AttachmentsAdapter(imagePathList, this@BookingAttachmentsScreen)
                            imageProgressDialog.dismiss()
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
    private fun bookBlueCollarServiceProvider() {

        var finalAmount = 0
        var spId = "0"
        var cgst = "0"
        var sgst = "0"
        if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            spId = Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).users_id
            finalAmount =
                Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).final_amount
            cgst =
                Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).CGST_amount
            sgst =
                Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).SGST_amount
        }

        if (UserUtils.getBookingType(this) == "instant") {
            val calender = Calendar.getInstance()
            val hour = calender.get(Calendar.HOUR_OF_DAY)
            UserUtils.time_slot_from = "${hour + 1}:00:00"
            UserUtils.scheduled_date = SimpleDateFormat("yyyy-MM-dd").format(Date())
        }

        val requestBody = BlueCollarBookingReqModel(
            finalAmount.toString(),
            encodedImages,
            currentDateAndTime(),
            1,
            1,
            UserUtils.job_description,
            RetrofitBuilder.USER_KEY,
            UserUtils.scheduled_date,
            spId.toInt(),
            UserUtils.time_slot_from,
            UserUtils.time_slot_from,
            UserUtils.time_slot_to.replace("\n", ""),
            UserUtils.getUserId(this).toInt(),
            cgst,
            sgst,
            UserUtils.getProfessionIdForBookInstant(this)
        )
        viewModel.blueCollarBooking(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
//                    val jsonResponse = JSONObject(it.data!!)
                    if (UserUtils.getBookingType(this) == "instant") {
                        // Book instantly...
                        val hasToken = UserUtils.sendFCMtoAllServiceProviders(
                            this@BookingAttachmentsScreen,
                            UserUtils.getBookingId(this@BookingAttachmentsScreen),
                            "user",
                            "accepted|${UserUtils.getBookingType(this@BookingAttachmentsScreen)}"
                        )
                        if (hasToken.isNotEmpty()) {
                            toast(this@BookingAttachmentsScreen, hasToken)
                        } else {
                            showWaitingForSPConfirmationDialog()
                        }
                    } else {
                        val hasToken = UserUtils.sendFCMtoSelectedServiceProvider(
                            this,
                            UserUtils.getBookingId(this),
                            "user"
                        )
                        if (hasToken.isNotEmpty()) {
                            toast(this, hasToken)
                        } else {
                            showWaitingForSPConfirmationDialog()
                        }
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, "Blue Collar: " + it.message!!)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun currentDateAndTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
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
            @SuppressLint("SimpleDateFormat")
            override fun run() {
                time.text = "$minutes:$seconds"
                progressTime -= 1
                progressBar.progress = progressTime

                seconds -= 1
                if (minutes == 0 && seconds == 0) {
                    dialog.dismiss()
                    UserUtils.sendFCMtoAllServiceProviders(
                        this@BookingAttachmentsScreen,
                        "accepted",
                        "accepted",
                        UserUtils.getBookingType(this@BookingAttachmentsScreen)!!
                    )
                    val bookingFactory = ViewModelFactory(BookingRepository())
                    val bookingViewModel = ViewModelProvider(
                        this@BookingAttachmentsScreen,
                        bookingFactory
                    )[BookingViewModel::class.java]
                    val requestBody = ProviderResponseReqModel(
                        data!!.final_amount.toString(),
                        UserUtils.getBookingId(this@BookingAttachmentsScreen).toInt(),
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                        "",
                        RetrofitBuilder.USER_KEY,
                        data!!.users_id.toInt(),
                        6,
                        UserUtils.getUserId(this@BookingAttachmentsScreen).toInt()
                    )
                    bookingViewModel.setProviderResponse(this@BookingAttachmentsScreen, requestBody)
                        .observe(this@BookingAttachmentsScreen) {
                            when (it) {
                                is NetworkResponse.Loading -> {
                                    progressDialog.show()
                                }
                                is NetworkResponse.Success -> {
                                    progressDialog.dismiss()
                                    UserUtils.saveFromFCMService(
                                        this@BookingAttachmentsScreen,
                                        false
                                    )
                                    if (FCMService.notificationManager != null) {
                                        FCMService.notificationManager.cancelAll()
                                    }
                                    ProviderDashboard.bookingId = "0"
                                    if (ProviderDashboard.bottomSheetDialog != null) {
                                        if (ProviderDashboard.bottomSheetDialog!!.isShowing) {
                                            ProviderDashboard.countDownTimer.cancel()
                                            ProviderDashboard.bottomSheetDialog!!.dismiss()
                                        }
                                    }
                                    weAreSorryDialog()
                                }
                                is NetworkResponse.Failure -> {
                                    progressDialog.dismiss()
                                    toast(this@BookingAttachmentsScreen, it.message!!)
                                }
                            }
                        }
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
            dialog.dismiss()
            finish()
            startActivity(Intent(this, PostJobTypeScreen::class.java))
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

//    override fun onPaymentSuccess(paymentResponse: String?) {
//        updateStatusInServer(paymentResponse, "Success")
//    }

//    private fun updateStatusInServer(paymentResponse: String?, status: String) {
//        var finalWalletAmount = Gson().fromJson(
//            UserUtils.getSelectedAllSPDetails(this),
//            SearchServiceProviderResModel::class.java
//        ).wallet_balance
//        if (finalWalletAmount == 0) {
//            finalWalletAmount = 0
//        }
//        val requestBody = PaymentConfirmReqModel(
//            data!!.final_amount.toString(),
//            UserUtils.getBookingId(this),
//            UserUtils.scheduled_date,
//            RetrofitBuilder.USER_KEY,
//            data!!.users_id.toInt(),
//            UserUtils.time_slot_from,
//            UserUtils.getUserId(this).toInt(),
//            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).CGST_amount,
//            Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java).SGST_amount,
//            UserUtils.getOrderId(this),
//            finalWalletAmount.toString()
//        )
//        viewModel.confirmPayment(this, requestBody).observe(this) {
//            when (it) {
//                is NetworkResponse.Loading -> {
//                    progressDialog.show()
//                }
//                is NetworkResponse.Success -> {
//                    progressDialog.dismiss()
//                    finish()
//                    startActivity(Intent(this, UserDashboardScreen::class.java))
//                }
//                is NetworkResponse.Failure -> {
//                    progressDialog.dismiss()
//                    snackBar(binding.nextBtn, it.message!!)
//                }
//            }
//        }
//    }

//    override fun onPaymentError(p0: Int, paymentError: String?) {
//        updateStatusInServer("", "failure")
//        snackBar(binding.nextBtn, "Payment Failed. Please Try Again!")
//    }

    override fun onBackPressed() {
        if (UserUtils.getBookingType(this) == "instant") {
            startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
        } else {
            when (data!!.category_id) {
                "3" -> {
                    finish()
                    startActivity(Intent(this, BookingAddressScreen::class.java))
                }
                else -> {
                    finish()
                    isProvider(this, false)
                    startActivity(Intent(this, BookingDateAndTimeScreen::class.java))
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, timeStamp, null)
        return Uri.parse(path)
    }

}