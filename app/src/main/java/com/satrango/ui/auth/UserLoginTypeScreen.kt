package com.satrango.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.satrango.databinding.ActivityUserLoginTypeScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant.now
import java.util.*
import kotlin.concurrent.thread
import android.content.ContentUris
import android.os.*
import android.webkit.MimeTypeMap

import android.os.Environment

import android.os.Build
import java.io.File


class UserLoginTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginTypeScreenBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (PermissionUtils.isNetworkConnected(this)) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM ERROR:", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                CoroutineScope(Dispatchers.Main).launch {
                    val response = RetrofitBuilder.getUserRetrofitInstance().updateFCMToken(
                        FCMReqModel(
                            token, RetrofitBuilder.USER_KEY, UserUtils.getUserId(
                                this@UserLoginTypeScreen
                            )
                        )
                    )
                    val responses = RetrofitBuilder.getUserRetrofitInstance().updateFCMToken(
                        FCMReqModel(
                            token,
                            RetrofitBuilder.USER_KEY,
                            "2"
                        )
                    )
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") != 200) {
                        snackBar(binding.userBtn, "Please check internet connection!")
                        Handler().postDelayed({
                            finish()
                        }, 3000)
                    }
                }
                Log.e("FCM TOKEN", token)
            })
        } else {
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
                ProviderDashboard.FROM_FCM_SERVICE = false
                startActivity(Intent(this@UserLoginTypeScreen, ProviderDashboard::class.java))
                finish()
            }

        }
        // 59, 55

//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "application/pdf"
//        startActivityForResult(intent, 1)

//        val intent = Intent()
//        intent.action = Intent.ACTION_GET_CONTENT
//        intent.type = "application/pdf"
//        startActivityForResult(intent,1)

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            Log.e("FILE:", getPathFromUri(this, data!!.data!!)!!)
            toast(this, getPathFromUri(this, data.data!!)!!)
        }
    }

//    fun dumpImageMetaData(uri: Uri) {
//
//        val contentResolver = applicationContext.contentResolver
//        val cursor: Cursor? = contentResolver.query(
//            uri, null, null, null, null, null)
//
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val displayName: String = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                toast(this, displayName)
//
//                val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
//                val size: String = if (!it.isNull(sizeIndex)) {
//                    it.getString(sizeIndex)
//                } else {
//                    "Unknown"
//                }
//                toast(this, size)
//            }
//        }
//    }

//    fun getPDFPath(uri: Uri?): String? {
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(uri!!, projection, null, null, null)
//        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        cursor.moveToFirst()
//        return cursor.getString(column_index)
//    }

//    private fun getRealPathFromURI(contentURI: Uri): String? {
//        val result: String?
//        val cursor = contentResolver.query(contentURI, null, null, null, null)
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = contentURI.path
//        } else {
//            cursor.moveToFirst()
//            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            result = cursor.getString(idx)
//            cursor.close()
//        }
//        return result
//    }
//
//    private fun getRealPath(uri: Uri): String {
//        val docId = DocumentsContract.getDocumentId(uri)
//        val split = docId.split(":")
//        val type = split[0]
//        var contentUri: Uri? = null
//        contentUri = when (type) {
//            "image" -> {
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//            }
//            "video" -> {
//                MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//            }
//            "audio" -> {
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//            }
//            else -> {
//                MediaStore.Files.getContentUri("external");
//            }
//        }
//        val selection = "_id=?";
//        val selectionArgs = arrayOf(split[1])
//        return getDataColumn(this, contentUri, selection, selectionArgs)!!
//    }

//    private fun getDataColumn(
//        context: Context,
//        uri: Uri?,
//        selection: String?,
//        selectionArgs: Array<String>
//    ): String? {
//        var cursor: Cursor? = null
//        val column = "_data"
//        val projection = arrayOf(
//            column
//        )
//        try {
//            cursor =
//                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
//            if (cursor != null && cursor.moveToFirst()) {
//                val column_index: Int = cursor.getColumnIndexOrThrow(column)
//                val value: String = cursor.getString(column_index)
//                return if (value.startsWith("content://") || !value.startsWith("/") && !value.startsWith("file://")) {
//                    null
//                } else value
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//        return null
//    }

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
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                } else if ("document" == type) {
                    contentUri =
                        MediaStore.Files.getContentUri("external", java.lang.Long.valueOf(split[1]))
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

//    fun getPathFromUri(context: Context, uri: Uri): String? {
//        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
//
//        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//            if (isExternalStorageDocument(uri)) {
//                val docId = DocumentsContract.getDocumentId(uri)
//                val split = docId.split(":".toRegex()).toTypedArray()
//                val type = split[0]
//                if ("primary".equals(type, ignoreCase = true)) {
//                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
//                }
//
//            } else if (isDownloadsDocument(uri)) {
//                val id = DocumentsContract.getDocumentId(uri)
//                val contentUri = ContentUris.withAppendedId(
//                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
//                )
//                return getDataColumn(context, contentUri, null, null)
//            } else if (isMediaDocument(uri)) {
//                val docId = DocumentsContract.getDocumentId(uri)
//                val split = docId.split(":".toRegex()).toTypedArray()
//                val type = split[0]
//                var contentUri: Uri? = null
//                if ("image" == type) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                } else if ("video" == type) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                } else if ("audio" == type) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//                }
//                val selection = "_id=?"
//                val selectionArgs = arrayOf(
//                    split[1]
//                )
//                return getDataColumn(context, contentUri, selection, selectionArgs)
//            }
//        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
//
//            // Return the remote address
//            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
//                context,
//                uri,
//                null,
//                null
//            )
//        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
//            return uri.path
//        }
//        return null
//    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
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