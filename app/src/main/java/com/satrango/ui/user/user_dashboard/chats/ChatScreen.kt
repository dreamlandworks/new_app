package com.satrango.ui.user.user_dashboard.chats

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ActivityChatScreenBinding
import com.satrango.ui.user.user_dashboard.chats.models.ChatMessageModel
import com.satrango.ui.user.user_dashboard.chats.models.ChatsModel
import com.satrango.utils.UserUtils
import com.satrango.utils.toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ChatScreen : AppCompatActivity(), ChatInterface {

    private val PDF_PICKER_REQUEST_CODE = 102
    private val GALLERY_REQUEST = 100
    private val CAMERA_REQUEST: Int = 101
    private lateinit var userData: ChatsModel
    private lateinit var userStatusValueEventListener: ValueEventListener
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: ActivityChatScreenBinding
    private lateinit var chats: ArrayList<ChatMessageModel>
    private lateinit var branch: String
    private lateinit var progressDialog: BeautifulProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProgressDialog()

        userData = Gson().fromJson(UserUtils.getSelectedChat(this), ChatsModel::class.java)
        Glide.with(this).load(userData.profile_image).error(R.drawable.images).into(binding.toolBarImage)
        binding.toolBarBackBtn.setOnClickListener { onBackPressed() }
        binding.toolBarTitle.text = userData.username

        branch = if (UserUtils.getUserId(this).toInt() > userData.user_id.toInt()) {
            userData.user_id + "|" + UserUtils.getUserId(this@ChatScreen)
        } else {
            UserUtils.getUserId(this@ChatScreen) + "|" + userData.user_id
        }

        binding.sendBtn.setOnClickListener {
            sendMessage()
        }

        binding.cameraBtn.setOnClickListener {
            openImagePicker()
        }

        binding.pinBtn.setOnClickListener {
            openPdfPicker()
        }

    }

    private fun openPdfPicker() {
        val pdfIntent = Intent()
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        pdfIntent.type = "application/pdf"
        startActivityForResult(pdfIntent, PDF_PICKER_REQUEST_CODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            uploadImageFile(null, data.data, getString(R.string.image))
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            uploadImageFile(imageBitmap!!, null, getString(R.string.image))
        } else if (requestCode == PDF_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            uploadImageFile(null, data.data!!, getString(R.string.pdf))
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, timeStamp, null)
        return Uri.parse(path)
    }

    private fun uploadImageFile(imageBitmap: Bitmap?, imageUri: Uri?, fileType: String) {
        progressDialog.show()
        val storageRef = FirebaseStorage.getInstance().reference
        val childPath = if (fileType == getString(R.string.image)) {
            "chats/$branch/images/${Date().time}.jpg"
        } else {
            "chats/$branch/pdfs/${Date().time}.pdf"
        }
        val profilePicStorageRef = storageRef.child(childPath)
        val finalUri: Uri = if (imageBitmap == null) {
            imageUri!!
        } else {
            getImageUri(this, imageBitmap)!!
        }
        profilePicStorageRef.putFile(finalUri).addOnFailureListener {
            toast(this, it.message!!)
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss()
            }
        }.addOnSuccessListener {
            profilePicStorageRef.downloadUrl.addOnSuccessListener { uri ->
                val url = uri.toString()
                sendToBranch(url, fileType)
                progressDialog.dismiss()
            }
        }
    }

    private fun sendMessage() {
        binding.apply {
            val message = message.text.toString().trim()
            if (message.isNotEmpty()) {
                databaseReference.child(getString(R.string.chat))
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            sendToBranch(message, getString(R.string.text))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            toast(this@ChatScreen, error.message)
                        }

                    })
            }
        }
    }

    private fun sendToBranch(message: String, messageType: String) {
        val timestamp = Date().time.toString()
        databaseReference.child(timestamp).child(getString(R.string.message_sm)).setValue(message)
        databaseReference.child(timestamp).child(getString(R.string.sent_by)).setValue(UserUtils.getUserId(this))
        databaseReference.child(timestamp).child(getString(R.string.message_type)).setValue(messageType)
        databaseReference.child(timestamp).child(getString(R.string.unseen)).setValue(getString(R.string._0))
        val datetime = Date().time
        val lastMessageReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
            .child(getString(R.string.users))
        val userChatReference = lastMessageReference.child(UserUtils.getUserId(this@ChatScreen))
            .child(getString(R.string.chat)).child(branch)
        userChatReference.child(getString(R.string.last_message)).setValue(message)
        userChatReference.child(getString(R.string.message_type)).setValue(messageType)
        userChatReference.child(getString(R.string.sent_by)).setValue(UserUtils.getUserId(this@ChatScreen))
        userChatReference.child(getString(R.string.date_time)).setValue(datetime)

        val spDatabaseReference = lastMessageReference.child(userData.user_id).child(getString(R.string.chat)).child(branch)
        spDatabaseReference.child(getString(R.string.last_message)).setValue(message)
        spDatabaseReference.child(getString(R.string.sent_by)).setValue(UserUtils.getUserId(this@ChatScreen))
        spDatabaseReference.child(getString(R.string.message_type)).setValue(messageType)
        spDatabaseReference.child(getString(R.string.date_time)).setValue(datetime)

        binding.message.setText("")
    }

    override fun onPause() {
        super.onPause()
        databaseReference.removeEventListener(valueEventListener)
        databaseReference.removeEventListener(userStatusValueEventListener)
    }

    override fun onResume() {
        super.onResume()
        loadChatMessages()
        loadUserStatus()
    }

    private fun loadUserStatus() {
        val databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
            .child(getString(R.string.users)).child(userData.user_id)
        userStatusValueEventListener = databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userStatus = snapshot.child(getString(R.string.online_status)).value.toString()
                binding.toolBarSubTitle.text = userStatus
            }

            override fun onCancelled(error: DatabaseError) {
                toast(this@ChatScreen, error.message)
            }

        })
    }

    private fun downloadPdfOrImage(url: String, fileType: String) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val uri: Uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setDescription("Downloading...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setVisibleInDownloadsUi(true)
        val dateTime = Date().time.toString()
        if (fileType == getString(R.string.image)) {
            request.setTitle("Image_$dateTime")
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS.toString(), "$dateTime.jpg")
        } else {
            request.setTitle("Pdf_$dateTime")
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS.toString(), "$dateTime.pdf")
        }
        downloadManager!!.enqueue(request)
        toast(this, "Downloading...")
    }

    private fun loadChatMessages() {
        databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
            .child(getString(R.string.chat)).child(branch)
        valueEventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0) {
                    chats = ArrayList()
                    for (snap in snapshot.children) {
                        val dateTime = snap.key.toString()
                        val message = snap.child(getString(R.string.message_sm)).value.toString()
                        val sentBy = snap.child(getString(R.string.sent_by)).value.toString()
                        val type = snap.child(getString(R.string.message_type)).value.toString()
                        val unseen = snap.child(getString(R.string.unseen)).value.toString()
                        val chat = ChatMessageModel(message, dateTime, sentBy, type, unseen)
                        chats.add(chat)
                    }
                    val layoutManager = LinearLayoutManager(this@ChatScreen)
                    layoutManager.reverseLayout = true
                    binding.recyclerView.layoutManager = layoutManager
                    binding.recyclerView.adapter = ChatMessageAdapter(chats.reversed(), this@ChatScreen)
                    for (chat in chats) {
                        if (chat.sentBy != UserUtils.getUserId(this@ChatScreen) && chat.unseen == getString(R.string._0)) {
                            databaseReference.child(chat.datetime).child(getString(R.string.unseen)).setValue(getString(R.string.one))
                        }
                    }
                } else {
                    toast(this@ChatScreen, "Messages not Found!")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toast(this@ChatScreen, error.details)
            }

        })
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

    override fun downloadFile(url: String, fileType: String) {
        downloadPdfOrImage(url, fileType)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun showFile(url: String, fileType: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.view_image_or_pdf_in_chat)
        val imageView = dialog.findViewById<ImageView>(R.id.imageView)
        val closeBtn = dialog.findViewById<TextView>(R.id.closeBtn)
        val downloadBtn = dialog.findViewById<TextView>(R.id.downloadBtn)
        if (fileType == getString(R.string.image)) {
            imageView.visibility = View.VISIBLE
            Glide.with(this).load(url).error(R.drawable.images).into(imageView)
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }
            downloadBtn.setOnClickListener {
                downloadPdfOrImage(url, fileType)
            }
        }
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.show()
    }

}