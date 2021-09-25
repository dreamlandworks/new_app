package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.post_job_multi_move

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityPostJobMultiMoveAddressScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.bookings.booking_attachments.models.Addresses
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.UserMyAccountScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.multi_move.Addresse
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.multi_move.MyJobPostMultiMoveEditReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_multi_move.PostJobMultiMoveReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.UserPlanScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostJobMultiMoveAddressScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var data: MyJobPostViewResModel
    private lateinit var viewModel: PostJobViewModel
    private lateinit var binding: ActivityPostJobMultiMoveAddressScreenBinding
    private lateinit var progressDialog: ProgressDialog
    private val GALLERY_REQUEST = 100
    private var addressIndex = 0

    companion object {
        lateinit var imagePathList: ArrayList<com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment>
        lateinit var encodedImages: ArrayList<Attachment>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobMultiMoveAddressScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.post_a_job)

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")

        val factory = ViewModelFactory(PostJobRepository())
        viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        if (UserUtils.addressList.isNotEmpty()) {
            loadAddressOnUI()
        }

        imagePathList = ArrayList()
        encodedImages = ArrayList()

        binding.apply {

            attachments.setOnClickListener {
                getImageFromGallery()
            }

            backBtn.setOnClickListener { onBackPressed() }

            UserUtils.finalAddressList = ArrayList()

            nextBtn.setOnClickListener {

                val description = discription.text.toString().trim()
                if (description.isEmpty()) {
                    snackBar(nextBtn, "Enter Description")
                } else {
                    UserUtils.job_description = description
                    UserUtils.finalAddressList.add(
                        Addresses(
                            UserUtils.addressList[addressIndex].day.toInt(),
                            UserUtils.job_description,
                            addressIndex + 1,
                            2
                        )
                    )
                    addressIndex += 1
                    if (addressIndex != UserUtils.addressList.size) {
                        loadAddressOnUI()
                    } else {
                        if (UserUtils.EDIT_MY_JOB_POST) {
                            updatePostJobMultiMove()
                        } else {
                            postJobMultiMove()
                        }
                    }
                }

            }

        }

    }

    private fun updatePostJobMultiMove() {

        val addressList = ArrayList<Addresse>()
        for (address in UserUtils.finalAddressList) {
            var id = 0
            for (add in data.job_details) {
                if (add.address_id.toInt() == address.address_id) {
                    id = add.id.toInt()
                }
            }
            addressList.add(Addresse(
                address.address_id,
                id,
                address.job_description,
                address.sequence_no,
                address.weight_type
            ))
        }

        val requestBody = MyJobPostMultiMoveEditReqModel(
            addressList,
            encodedImages,
            UserUtils.bid_per,
            UserUtils.bid_range_id,
            UserUtils.bids_period,
            data.job_post_details.booking_id,
            data.job_post_details.post_created_on,
            UserUtils.estimate_time,
            UserUtils.estimateTypeId,
            RetrofitBuilder.USER_KEY,
            PostJobMultiMoveDescriptionScreen.finalKeywords,
            PostJobMultiMoveDescriptionScreen.finalLanguages,
            data.job_post_details.post_job_id.toInt(),
            UserUtils.scheduled_date,
            UserUtils.time_slot_from,
            UserUtils.title,
            UserUtils.getUserId(this).toInt()
        )

        viewModel.updateMultiMoveMyJobPost(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    snackBar(binding.addressText, it.data!!.message)
                    Handler().postDelayed({
                        startActivity(Intent(this, UserDashboardScreen::class.java))
                    }, 3000)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.addressText, it.message!!)
                }
            }
        })

    }


    @SuppressLint("SimpleDateFormat")
    private fun postJobMultiMove() {

        val requestBody = PostJobMultiMoveReqModel(
            UserUtils.finalAddressList,
            encodedImages,
            UserUtils.bid_per,
            UserUtils.bid_range_id,
            UserUtils.bids_period,
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            UserUtils.estimate_time,
            UserUtils.estimateTypeId,
            RetrofitBuilder.USER_KEY,
            PostJobMultiMoveDescriptionScreen.finalKeywords,
            PostJobMultiMoveDescriptionScreen.finalLanguages,
            UserUtils.scheduled_date,
            UserUtils.time_slot_from,
            UserUtils.title,
            UserUtils.getUserId(this).toInt()
        )
        Log.e("MULTI MOVE", Gson().toJson(requestBody))
        viewModel.postJobMultiMove(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    if (it.data!!.user_plan_id == "1") {
                        showSuccessDialog()
                    } else {
                        UserMyAccountScreen.FROM_MY_ACCOUNT = false
                        startActivity(Intent(this, UserPlanScreen::class.java))
                    }

                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.addressText, it.message!!)
                }
            }
        })
    }

    private fun showSuccessDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.payment_success_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val homeBtn = dialogView.findViewById<TextView>(R.id.closBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        homeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun loadAddressOnUI() {
        binding.discription.setText("")
        binding.addressText.text = UserUtils.addressList[addressIndex].month

        if (UserUtils.EDIT_MY_JOB_POST) {
            data = Gson().fromJson(UserUtils.EDIT_MY_JOB_POST_DETAILS, MyJobPostViewResModel::class.java)
            for (add in data.job_details) {
                if (add.address_id == UserUtils.addressList[addressIndex].day) {
                    binding.discription.setText(add.job_description)
                }
            }
        }
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
                    imagePathList.add(com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment("", getImageFilePath(imageUri), "",""))
                    encodedImages.add(Attachment(encodeToBase64FromUri(imageUri)))
                }
            } else if (data.data != null) {
                val imageUri = data.data
                imagePathList.add(com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment("", getImageFilePath(imageUri!!), "",""))
                encodedImages.add(Attachment(encodeToBase64FromUri(imageUri)))
            }
            binding.attachmentsRV.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
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

    override fun deleteAttachment(position: Int, imagePath: com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment) {
        imagePathList.remove(imagePath)
        binding.attachmentsRV.adapter!!.notifyItemRemoved(position)
        Handler().postDelayed({
            binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
            encodedImages.remove(encodedImages[position])
        }, 500)
    }


}