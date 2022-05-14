package com.satrango.ui.user.bookings.booking_attachments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityViewFilesScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_attachments.models.Attachments
import com.satrango.ui.user.bookings.booking_attachments.models.ViewFilesResModel
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*
import java.util.*


class ViewFilesScreen : AppCompatActivity() {

    private lateinit var reference: DatabaseReference
    private val FILE_PICKER_REQUEST_CODE = 100
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityViewFilesScreenBinding
    private lateinit var response: BookingDetailsResModel

    companion object {
        var userId: Int = 0
        var categoryId: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFilesScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProgressDialog()
        initializeToolBar()

        binding.apply {

            attachmentsBtn.setOnClickListener {
                if (description.text.toString().trim().isNotEmpty()) {
                    openFilesPicker()
                } else {
                    snackBar(description, getString(R.string.please_enter_description))
                }
            }

        }

        val requestBody = BookingDetailsReqModel(
            UserUtils.getBookingId(this).toInt(),
            categoryId,
            RetrofitBuilder.USER_KEY,
            userId
        )
        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        viewModel.viewBookingDetails(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    response = it.data!!
                    updateUI(response)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.attachmentsBtn, it.message!!)
                }
            }
        }

        loadFiles()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: BookingDetailsResModel) {
        binding.date.text = response.booking_details.scheduled_date
        binding.amount.text = "Rs ${response.booking_details.amount}"
        binding.time.text = response.booking_details.from
        ViewUserBookingDetailsScreen.FCM_TOKEN = response.booking_details.fcm_token
        if (UserUtils.isProvider(this)) {
            binding.userName.text =
                "${response.booking_details.fname} ${response.booking_details.lname}"
            binding.occupation.text = "User"
            Glide.with(binding.profilePic)
                .load(RetrofitBuilder.BASE_URL + response.booking_details.user_profile_pic)
                .error(R.drawable.images)
                .into(binding.profilePic)
            if (!UserUtils.isPending(this)) {
                binding.otpText.text = resources.getString(R.string.time_lapsed)
                binding.otp.text = response.booking_details.time_lapsed
            } else {
                if (UserUtils.isProvider(this)) {
                    binding.otpText.text = resources.getString(R.string.starts_in)
                    binding.otp.text =
                        "${response.booking_details.remaining_days_to_start}D ${response.booking_details.remaining_hours_to_start}H ${response.booking_details.remaining_minutes_to_start}M"
                } else {
                    binding.otpText.text = resources.getString(R.string.otp)
                    binding.otp.text = response.booking_details.otp
                }
            }
        } else {
            binding.userName.text =
                "${response.booking_details.sp_fname} ${response.booking_details.sp_lname}"
            binding.occupation.text = response.booking_details.sp_profession
            Glide.with(binding.profilePic)
                .load(RetrofitBuilder.BASE_URL + response.booking_details.sp_profile_pic)
                .error(R.drawable.images)
                .into(binding.profilePic)
            if (!UserUtils.isPending(this)) {
                binding.otpText.text = resources.getString(R.string.time_lapsed)
                binding.otp.text = response.booking_details.time_lapsed
            } else {
                binding.otpText.text = resources.getString(R.string.otp)
                binding.otp.text = response.booking_details.otp
            }
        }
        binding.bookingIdText.text = UserUtils.getBookingId(this)
    }

    private fun openFilesPicker() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val database = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
            .child(getString(R.string.bookings_sm)).child("10")
        reference = database.child(Date().time.toString())
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (null != data) {
                if (null != data.clipData) {
                    for (i in 0 until data.clipData!!.itemCount) {
                        val uri = data.clipData!!.getItemAt(i).uri
                        uploadImageFile(
                            uri,
                            getFileExtension(uri),
                            binding.description.text.toString().trim()
                        )
                    }
                } else {
                    val uri = data.data
                    uploadImageFile(
                        uri!!,
                        getFileExtension(uri),
                        binding.description.text.toString().trim()
                    )
                }
            }
        }
    }

    private fun uploadImageFile(uri: Uri, fileType: String, description: String) {
        progressDialog.show()
        val storageRef = FirebaseStorage.getInstance().reference
        val childPath = "blue_collar/$fileType/${Date().time}.$fileType"
        val profilePicStorageRef = storageRef.child(childPath)
        profilePicStorageRef.putFile(uri).addOnFailureListener {
            toast(this, it.message!!)
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loadFiles()
                progressDialog.dismiss()
            }
        }.addOnSuccessListener {
            profilePicStorageRef.downloadUrl.addOnSuccessListener { uri ->
                val url = uri.toString()
                val datetime = Date().time
                reference.child("description").setValue(description)
                reference.child("sent_by").setValue(UserUtils.getUserId(this))
                reference.child("profile_pic").setValue(UserUtils.getUserProfilePic(this))
                reference.child("username").setValue(UserUtils.getUserName(this))
                reference.child("datetime").setValue(datetime)
                val attachmentsReference = reference.child("attachments").child(datetime.toString())
                attachmentsReference.child("url").setValue(url)
                attachmentsReference.child("type").setValue(fileType)
                binding.description.setText("")
                loadFiles()
                progressDialog.dismiss()
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

    private fun getFileExtension(uri: Uri): String {
        return if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
                .toString()
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path!!)).toString())
        }
    }

    private fun loadFiles() {
        val databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
        databaseReference.child(getString(R.string.bookings_sm)).child("10")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val files = ArrayList<ViewFilesResModel>()
                    for (snap in snapshot.children) {
                        val attachments = ArrayList<Attachments>()
                        for (sn in snap.child("attachments").children) {
                            attachments.add(
                                Attachments(
                                    sn.child("type").value.toString(),
                                    sn.child("url").value.toString()
                                )
                            )
                        }
                        val model = ViewFilesResModel(
                            attachments,
                            snap.child("description").value.toString(),
                            snap.child("sent_by").value.toString(),
                            snap.child("profile_pic").value.toString(),
                            snap.child("username").value.toString(),
                            snap.child("datetime").value.toString()
                        )
                        files.add(model)
                    }
                    val adapter = ViewFilesAdapter(files)
                    binding.userFilesRv.layoutManager = LinearLayoutManager(this@ViewFilesScreen)
                    binding.userFilesRv.adapter = adapter
//                    toast(this@ViewFilesScreen, "Files Uploaded")
//                    Log.e("FILES", Gson().toJson(files))
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.view_files)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
        if (isProvider(this)) {
            binding.layoutOne.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutTwo.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutThree.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutFour.setBackgroundResource(R.drawable.purple_out_line)
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            binding.card.setCardBackgroundColor(resources.getColor(R.color.purple_500))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = resources.getColor(R.color.purple_700)
            }
        } else {
            toolBar.setBackgroundColor(resources.getColor(R.color.blue))
        }
    }

}