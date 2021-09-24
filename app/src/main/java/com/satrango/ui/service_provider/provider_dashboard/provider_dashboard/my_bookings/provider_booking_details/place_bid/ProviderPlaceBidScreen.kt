package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.place_bid

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderPlaceBidScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.ProviderMyBidsRepository
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.ProviderMyBidsViewModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.place_bid.models.ProviderPostBidReqModel
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class ProviderPlaceBidScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var progressDialog: ProgressDialog
    private val CAMERA_REQUEST: Int = 101
    private val GALLERY_REQUEST: Int = 100
    companion object {
        var bookingId = 0
    }
    private var postJobId = 0
    private lateinit var binding: ActivityProviderPlaceBidScreenBinding
    var estimateTimeType = 0
    var submissionType = 0

    lateinit var imagePathList: ArrayList<com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment>
    lateinit var encodedImages: ArrayList<Attachment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderPlaceBidScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.view_post)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        binding.expiresOn.text = intent.getStringExtra("expiresIn")!!
        binding.bidRanges.text = intent.getStringExtra("bidRanges")!!
        binding.title.text = intent.getStringExtra("title")!!
        postJobId = intent.getStringExtra("postJobId")!!.toInt()

        binding.apply {

            hoursBtn.setOnClickListener {
                estimateTimeType = 1
                hoursBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                hoursBtn.setTextColor(resources.getColor(R.color.white))
                daysBtn.setBackgroundResource(R.drawable.purple_out_line)
                daysBtn.setTextColor(resources.getColor(R.color.purple_500))
            }
            daysBtn.setOnClickListener {
                estimateTimeType = 2
                daysBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                daysBtn.setTextColor(resources.getColor(R.color.white))
                hoursBtn.setBackgroundResource(R.drawable.purple_out_line)
                hoursBtn.setTextColor(resources.getColor(R.color.purple_500))
            }

            openBtn.setOnClickListener {
                submissionType = 0
                openBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                openBtn.setTextColor(resources.getColor(R.color.white))
                sealedBtn.setBackgroundResource(R.drawable.purple_out_line)
                sealedBtn.setTextColor(resources.getColor(R.color.purple_500))
            }

            sealedBtn.setOnClickListener {
                submissionType = 1
                sealedBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                sealedBtn.setTextColor(resources.getColor(R.color.white))
                openBtn.setBackgroundResource(R.drawable.purple_out_line)
                openBtn.setTextColor(resources.getColor(R.color.purple_500))
            }

            attachments.setOnClickListener {
                getImageFromGallery()
            }

            submitBtn.setOnClickListener {
                validateFields()
            }

            resetBtn.setOnClickListener {
                resetFields()
            }

        }

    }

    private fun resetFields() {
        binding.apply {
            bidAmount.setText("")
            estimateTime.setText("")
            estimateTimeType = 0
            hoursBtn.setBackgroundResource(R.drawable.purple_out_line)
            hoursBtn.setTextColor(resources.getColor(R.color.purple_500))
            daysBtn.setBackgroundResource(R.drawable.purple_out_line)
            daysBtn.setTextColor(resources.getColor(R.color.purple_500))
            proposalDescription.setText("")
            imagePathList.clear()
            encodedImages.clear()
            attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this@ProviderPlaceBidScreen)
            openBtn.setBackgroundResource(R.drawable.purple_out_line)
            openBtn.setTextColor(resources.getColor(R.color.purple_500))
            sealedBtn.setBackgroundResource(R.drawable.purple_out_line)
            sealedBtn.setTextColor(resources.getColor(R.color.purple_500))
        }
    }

    private fun validateFields() {
        binding.apply {

            when {
                bidAmount.text.toString().isEmpty() -> {
                    snackBar(bidAmount, "Enter Bid Amount")
                }
                estimateTime.text.toString().isEmpty() -> {
                    snackBar(bidAmount, "Enter Estimate Time")
                }
                estimateTimeType == 0 -> {
                    snackBar(bidAmount, "Select Estimate Time Type")
                }
                proposalDescription.text.toString().isEmpty() -> {
                    snackBar(bidAmount, "Enter Proposal (Why Choose Me?)")
                }
                estimateTimeType == 0 -> {
                    snackBar(bidAmount, "Select Estimate Time Type")
                }
                else -> {
                    submitToServer()
                }
            }

        }
    }

    private fun submitToServer() {
        val factory = ViewModelFactory(ProviderMyBidsRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderMyBidsViewModel::class.java]
        val requestBody = ProviderPostBidReqModel(
            binding.bidAmount.text.toString().trim(),
            encodedImages,
            submissionType,
            bookingId,
            binding.estimateTime.text.toString().toInt(),
            estimateTimeType,
            RetrofitBuilder.PROVIDER_KEY,
            postJobId,
            binding.proposalDescription.text.toString().trim(),
            UserUtils.getUserId(this).toInt()
        )
        viewModel.postBid(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    snackBar(binding.bidAmount, it.data!!.message)
                    resetFields()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.bidAmount, it.message!!)
                }
            }
        })
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
        imagePathList = ArrayList()
        encodedImages = ArrayList()
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData != null) {
                val count: Int = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imagePathList.add(
                        com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment(
                            "",
                            getImageFilePath(imageUri),
                            ""
                        )
                    )
                    encodedImages.add(
                        Attachment(
                            encodeToBase64FromUri(
                                imageUri
                            )
                        )
                    )
                }
            } else if (data.data != null) {
                val imageUri = data.data
                imagePathList.add(
                    com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment(
                        "",
                        getImageFilePath(imageUri!!),
                        ""
                    )
                )
                encodedImages.add(Attachment(encodeToBase64FromUri(imageUri)))
            }
            binding.attachmentsRV.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.attachmentsRV.adapter =
                AttachmentsAdapter(imagePathList, this)
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            getImageFilePath(data.data!!)
            val bitmap = data.extras!!["data"] as Bitmap?
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
        imagePathList.remove(imagePath)
        binding.attachmentsRV.adapter!!.notifyItemRemoved(position)
        Handler().postDelayed({
            binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
            encodedImages.remove(encodedImages[position])
        }, 500)
    }

}