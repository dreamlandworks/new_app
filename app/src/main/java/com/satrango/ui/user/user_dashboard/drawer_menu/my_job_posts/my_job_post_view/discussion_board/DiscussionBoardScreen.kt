package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityDiscussionBoardScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.ProviderMyBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models.DiscussionBoardMessageReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models.DiscussionListReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models.LikePostDescussionReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DiscussionBoardScreen : AppCompatActivity(), DiscussionBoardInterface {

    private lateinit var viewModel: PostJobViewModel
    private var postJobId: Int = 0
    private val PERMISSIONS_REQUEST_CODE: Int = 103
    private lateinit var binding: ActivityDiscussionBoardScreenBinding
    private val GALLERY_REQUEST = 100
    private val CAMERA_REQUEST: Int = 101
    private val PDF_REQUEST = 102
    private var attachmentType = ""

    companion object {
        lateinit var imagePathList: ArrayList<String>
        lateinit var encodedImages: ArrayList<Attachment>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscussionBoardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.view_post)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)


        if (MyJobPostViewScreen.FROM_PROVIDER) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            binding.layout.setBackgroundResource(R.drawable.provider_btn_bg_sm)
            binding.layoutOne.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutTwo.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutThree.setBackgroundResource(R.drawable.provider_chat_edit_text_bg)
            binding.sendBtn.setImageResource(R.drawable.ic_round_send_purple_24)
        }

        postJobId = intent.getStringExtra("postJobId")!!.toInt()
        binding.title.text = intent.getStringExtra("title")!!
        binding.bidRanges.text = intent.getStringExtra("bidRanges")!!
        binding.expiresOn.text = intent.getStringExtra("expiresIn")!!

        val factory = ViewModelFactory(PostJobRepository())
        viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        imagePathList = ArrayList()
        encodedImages = ArrayList()

        binding.cameraBtn.setOnClickListener {
            capturePictureFromCamera()
        }

        binding.pinBtn.setOnClickListener {
            if (binding.pinLayout.visibility == View.VISIBLE) {
                binding.pinLayout.visibility = View.GONE
            } else {
                binding.pinLayout.visibility = View.VISIBLE
            }
        }

        binding.imageBtn.setOnClickListener {
            binding.pinLayout.visibility = View.GONE
            getImageFromGallery()
        }

        binding.pdfBtn.setOnClickListener {
            binding.pinLayout.visibility = View.GONE
            snackBar(binding.recyclerView, "This Feature is in development stage")
//            getPdfFromFileStorage()
        }

        binding.sendBtn.setOnClickListener {
            if (binding.message.text.toString().isNotEmpty()) {
                sendMessageToServer()
            } else {
                toast(this, "Enter Message to send")
            }
        }

        binding.message.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.pinLayout.visibility = View.GONE
            }
        }

        loadDiscussionList()

    }

    private fun loadDiscussionList() {

        val requestBody = DiscussionListReqModel(RetrofitBuilder.USER_KEY, postJobId)
        viewModel.discussionList(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {

                }
                is NetworkResponse.Success -> {
                    val layoutManager =
                        LinearLayoutManager(this)
                    layoutManager.stackFromEnd = true
                    layoutManager.isSmoothScrollbarEnabled = true
                    binding.recyclerView.layoutManager = layoutManager
                    binding.recyclerView.adapter = DiscussionListAdapter(it.data!!, this, this)
                }
                is NetworkResponse.Failure -> {
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun sendMessageToServer() {
        val requestBody = DiscussionBoardMessageReqModel(
            attachmentType,
            encodedImages,
            binding.message.text.toString().trim(),
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            RetrofitBuilder.USER_KEY,
            postJobId,
            UserUtils.getUserId(this).toInt()
        )
        viewModel.discussionMessage(this, requestBody).observe(this, {
            when (it) {
                is com.satrango.remote.NetworkResponse.Loading -> {
                    binding.sendBtn.visibility = View.GONE
                    binding.message.isClickable = false
                    binding.message.isFocusable = false
                    binding.pinBtn.isClickable = false
                    binding.cameraBtn.isClickable = false
                    binding.progressBar.visibility = View.VISIBLE
                    binding.pinLayout.visibility = View.GONE
                }
                is com.satrango.remote.NetworkResponse.Success -> {
                    binding.message.isClickable = true
                    binding.message.isFocusable = true
                    binding.pinBtn.isClickable = true
                    binding.cameraBtn.isClickable = true
                    encodedImages = ArrayList()
                    imagePathList = ArrayList()
                    binding.message.setText("")
                    binding.progressBar.visibility = View.GONE
                    binding.sendBtn.visibility = View.VISIBLE
                    loadDiscussionList()
                }
                is com.satrango.remote.NetworkResponse.Failure -> {
                    binding.message.isClickable = true
                    binding.message.isFocusable = true
                    binding.pinBtn.isClickable = true
                    binding.cameraBtn.isClickable = true
                    binding.progressBar.visibility = View.GONE
                    binding.sendBtn.visibility = View.VISIBLE
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }

    private fun getPdfFromFileStorage() {
        val permissionGranted =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
            } else {
                false
            }

        if (permissionGranted) {

            val mimeTypes = arrayOf("application/pdf")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
                if (mimeTypes.isNotEmpty()) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
            } else {
                var mimeTypesStr = ""
                for (mimeType in mimeTypes) {
                    mimeTypesStr += "$mimeType|"
                }
                intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
            }
            startActivityForResult(intent, PDF_REQUEST)

//            val intent = Intent(this, FilePickerActivity::class.java)
//            startActivityForResult(intent, PDF_REQUEST)
        } else {
            if (if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)
                } else {
                    false
                }
            ) {
                snackBar(binding.recyclerView, "Enable Storage Permission")
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(READ_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUEST_CODE
                    )
                }
            }
        }

    }

    private fun capturePictureFromCamera() {
        val cameraIntent = Intent()
        cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    private fun getImageFromGallery() {

//        val mimeTypes = arrayOf("image/*", "application/pdf")
        val mimeTypes = arrayOf("image/*")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        } else {
            var mimeTypesStr = ""
            for (mimeType in mimeTypes) {
                mimeTypesStr += "$mimeType|"
            }
            intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
        }
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagePathList = ArrayList()
        encodedImages = ArrayList()
        var imageStream: InputStream? = null
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData != null) {
                val count: Int = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imagePathList.add(getImageFilePath(imageUri))
                    encodedImages.add(Attachment(encodeToBase64FromUri(imageUri)))
                    attachmentType = "image"
                }
            } else if (data.data != null) {
                val imageUri = data.data
                imagePathList.add(getImageFilePath(imageUri!!))
                encodedImages.add(Attachment(encodeToBase64FromUri(imageUri)))
                attachmentType = "image"
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            try {
                imageStream = contentResolver.openInputStream(getImageUri(this, imageBitmap!!)!!)
                if (imageStream != null) {
                    val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
                    encodedImages.add(Attachment(UserUtils.encodeToBase64(yourSelectedImage)!!))
                    attachmentType = "image"
                }
            } catch (e: Exception) {
                snackBar(binding.recyclerView, e.message!!)
            }
        } else if (requestCode == PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            try {
                attachmentType = "pdf"
//                val cursor = contentResolver.query(data.data!!, null, null, null, null, null)
//                if (cursor != null) {
//                    if (cursor.moveToFirst()) {
//                        val displayName: String =
//                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                        toast(
//                            this,
//                            Environment.getExternalStorageDirectory().absolutePath + "/" + data.data!!.path!!.split(
//                                ":"
//                            )[1]
//                        )
//                    }
//                }
                val file = File(
                    Environment.getExternalStorageDirectory().absolutePath + "/" + data.data!!.path!!.split(
                        ":"
                    )[1]
                )
//                val encodedString = encodeFileToBase64Binary(file)
//                encodedImages.add(Attachment(encodedString!!))
            } catch (e: Exception) {
                toast(this, e.message!!)
            }
        }

    }

    private fun encodeFileToBase64Binary(yourFile: File): String? {
        val size = yourFile.length().toInt()
        val bytes = ByteArray(size)
        try {
            val buf = BufferedInputStream(FileInputStream(yourFile))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
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
            arrayOf(
                image_id
            ),
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

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    @SuppressLint("SimpleDateFormat")
    override fun likeClicked(id: String, position: Int) {
        val requestBody = LikePostDescussionReqModel(
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            id.toInt(),
            RetrofitBuilder.USER_KEY,
            UserUtils.getUserId(this).toInt()
        )
        viewModel.likeClicked(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {

                }
                is NetworkResponse.Success -> {
                    binding.recyclerView.adapter!!.notifyItemChanged(position)
                    loadDiscussionList()
                }
                is NetworkResponse.Failure -> {
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })

    }

}