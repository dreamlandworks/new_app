package com.satrango.ui.auth

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.satrango.databinding.ActivityUserLoginTypeScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOne
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import androidx.core.app.ActivityCompat.startActivityForResult

import android.os.Environment
import java.io.*
import androidx.core.app.ActivityCompat.startActivityForResult

import androidx.core.content.FileProvider

import android.widget.Toast
import java.text.SimpleDateFormat


class UserLoginTypeScreen : AppCompatActivity() {

    private lateinit var pictureFile: File
    private lateinit var token: String
    private lateinit var binding: ActivityUserLoginTypeScreenBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!UserUtils.updateNewFCMToken(this)) {
            snackBar(binding.userBtn, "Please check internet connection!")
            Handler().postDelayed({
                finish()
            }, 3000)
        }

        binding.apply {

            userBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, UserDashboardScreen::class.java))
                finish()
            }
            serviceProviderBtn.setOnClickListener {
                UserUtils.saveFromFCMService(this@UserLoginTypeScreen, false)
                startActivity(Intent(this@UserLoginTypeScreen, ProviderDashboard::class.java))
                finish()
            }

        }

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            Log.e("FILE:", getPathFromUri(this, data!!.data!!)!!)
        }
        if (requestCode == 0) {
            val extras: Bundle = data!!.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            var imageStream: InputStream? = null
            try {
                imageStream = contentResolver.openInputStream(getImageUri(this, imageBitmap!!)!!)
            } catch (e: Exception) {
                snackBar(binding.userBtn, e.message!!)
            }
            if (imageStream != null) {
                val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
            }
            try {
                val imgFile = File(pictureFile.absolutePath)
//                if (imgFile.exists()) {
//                    image.setImageURI(Uri.fromFile(imgFile))
//                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun cameraIntent() {
        val cameraIntent = Intent()
        cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        startActivityForResult(cameraIntent, 0)
    }

    private fun getPathFromUri(context: Context?, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    "document" -> {
                        contentUri =
                            MediaStore.Files.getContentUri("external", java.lang.Long.valueOf(split[1]))
                    }
                }

                val selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
                val selectionArgsPdf = arrayOf(mimeType)
                val contentUri1 = ContentUris.withAppendedId(
                    Uri.parse(contentUri!!.path), java.lang.Long.valueOf(split[1])
                )
                return contentUri.toString()
            } else if (isDownloadsDocument(uri)) {
                var id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw.".toRegex(), "")
                    val file = File(id)
                    if (file.exists()) {
                        return id
                    }
                }
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context!!, contentUri, null, null)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context!!,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}