package com.satrango.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.satrango.databinding.ActivityUserLoginTypeScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*


class UserLoginTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginTypeScreenBinding

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
                startActivity(Intent(this@UserLoginTypeScreen, ProviderDashboard::class.java))
                finish()
            }

        }

//        val projection = arrayOf(
//            MediaStore.Files.FileColumns._ID,
//            MediaStore.Files.FileColumns.MIME_TYPE,
//            MediaStore.Files.FileColumns.DATE_ADDED,
//            MediaStore.Files.FileColumns.DATE_MODIFIED,
//            MediaStore.Files.FileColumns.DISPLAY_NAME,
//            MediaStore.Files.FileColumns.TITLE,
//            MediaStore.Files.FileColumns.SIZE
//        )
//
//        val mimeType = "application/pdf"
//
//        val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN ('" + mimeType + "')"
//        val orderBy = MediaStore.Files.FileColumns.SIZE + " DESC"
//        val cursor: Cursor? = contentResolver.query(
//            MediaStore.Files.getContentUri("external"),
//            projection,
//            whereClause,
//            null,
//            null
//        )
//
//        val idCol: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
//        val mimeCol: Int = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
//        val addedCol: Int = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
//        val modifiedCol: Int =
//            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
//        val nameCol: Int = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
//        val titleCol: Int = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
//        val sizeCol: Int = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
//
//        toast(this, cursor.count.toString())
//
//        if (cursor.moveToFirst()) {
//            do {
//                val fileUri: Uri = Uri.withAppendedPath(
//                    MediaStore.Files.getContentUri("external"),
//                    cursor.getString(idCol)
//                )
//                val mimeType = cursor.getString(mimeCol)
//                val dateAdded = cursor.getLong(addedCol)
//                val dateModified = cursor.getLong(modifiedCol)
//                val name = cursor.getString(nameCol)
//                Log.e("PDF", name)
//            } while (cursor.moveToNext())
//        }

//        MaterialFilePicker()
//            // Pass a source of context. Can be:
//            //    .withActivity(Activity activity)
//            //    .withFragment(Fragment fragment)
//            //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
//            .withActivity(this)
//            // With cross icon on the right side of toolbar for closing picker straight away
//            .withCloseMenu(true)
//            // Entry point path (user will start from it)
////            .withPath(alarmsFolder.absolutePath)
//            // Root path (user won't be able to come higher than it)
//            .withRootPath(Environment.getExternalStorageDirectory().absolutePath)
//            // Showing hidden files
//            .withHiddenFiles(true)
//            // Want to choose only jpg images
//            .withFilter(Pattern.compile(".*\\.PDF$"))
//            // Don't apply filter to directories names
//            .withFilterDirectories(false)
//            .withTitle("Sample title")
//            .withRequestCode(0)
//            .start()

    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }


}