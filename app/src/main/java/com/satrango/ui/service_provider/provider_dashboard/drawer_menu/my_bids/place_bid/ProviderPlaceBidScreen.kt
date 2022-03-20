package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderPlaceBidScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.ProviderMyBidsRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.ProviderMyBidsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.ProviderMyBidsViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderBidEditReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderDeleteBidAttachmentReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderPostBidReqModel
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class ProviderPlaceBidScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var progressDialog: BeautifulProgressDialog
    private val CAMERA_REQUEST: Int = 101
    private val GALLERY_REQUEST: Int = 100
    companion object {
        var bookingId = 0
        var postJobId = 0
        var FROM_EDIT_BID = false
        var FROM_AWARDED = false
        var EDIT_BID_ID = ""
    }
    private lateinit var binding: ActivityProviderPlaceBidScreenBinding
    var estimateTimeType = 0
    var submissionType = -1

    lateinit var imagePathList: ArrayList<com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment>
    lateinit var encodedImages: ArrayList<Attachment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderPlaceBidScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_post)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.statusBarColor = resources.getColor(R.color.purple_700)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        initializeProgressDialog()

        binding.expiresOn.text = intent.getStringExtra("expiresIn")!!
        binding.bidRanges.text = intent.getStringExtra("bidRanges")!!
        binding.title.text = intent.getStringExtra("title")!!

        encodedImages = ArrayList()

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

        if (FROM_EDIT_BID) {
            updateUIWithBidDetails()
        }

    }

    private fun updateUIWithBidDetails() {
        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        val requestBody1 = ViewProposalReqModel(EDIT_BID_ID.toInt(), RetrofitBuilder.USER_KEY, 2)
        viewModel.viewProposal(this, requestBody1).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    updateBidDetails(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.bidAmount, it.message!!)
                }
            }
        }
    }

    private fun updateBidDetails(data: ViewProposalResModel) {
        binding.apply {
            bidAmount.setText(data.bid_details.amount)
            estimateTime.setText(data.bid_details.esimate_time)
            if (data.bid_details.estimate_type == "Hours") {
                hoursBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                hoursBtn.setTextColor(resources.getColor(R.color.white))
                estimateTimeType = 1
            } else {
                daysBtn.setBackgroundResource(R.drawable.provider_btn_bg)
                daysBtn.setTextColor(resources.getColor(R.color.white))
                estimateTimeType = 2
            }
            proposalDescription.setText(data.bid_details.proposal)
            attachmentsRV.layoutManager = LinearLayoutManager(this@ProviderPlaceBidScreen, LinearLayoutManager.HORIZONTAL, false)
            attachmentsRV.adapter = AttachmentsAdapter(data.attachments as ArrayList<com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment>, this@ProviderPlaceBidScreen)
        }
    }

    private fun resetFields() {
        if (FROM_EDIT_BID) {
            updateUIWithBidDetails()
        } else {
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
                submissionType == -1 -> {
                    snackBar(bidAmount, "Select Submission Type")
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

        if (FROM_EDIT_BID) {
            val requestBody = ProviderBidEditReqModel(
                binding.bidAmount.text.toString().trim(),
                encodedImages,
                EDIT_BID_ID.toInt(),
                submissionType,
                binding.estimateTime.text.toString().toInt(),
                estimateTimeType,
                RetrofitBuilder.PROVIDER_KEY,
                binding.proposalDescription.text.toString().trim()
            )
            viewModel.editBid(this, requestBody).observe(this) {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        successDialog("Bid updated successful")
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.bidAmount, it.message!!)
                    }
                }
            }
        } else {
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
                UserUtils.getUserId(this).toInt(),
                binding.title.text.toString().trim()
            )
            viewModel.postBid(this, requestBody).observe(this) {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        successDialog("Your bid is successfully submitted. You can view the status in My Bids.")
                        resetFields()
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.bidAmount, it.message!!)
                    }
                }
            }
        }
    }

    private fun successDialog(message: String) {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_edit_bid_success_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val messageText = dialogView.findViewById<TextView>(R.id.message)
        val homeBtn = dialogView.findViewById<TextView>(R.id.homeBtn)
        val myBidsBtn = dialogView.findViewById<TextView>(R.id.myBidsBtn)
        messageText.text = message
        closeBtn.setOnClickListener { dialog.dismiss() }
        homeBtn.setOnClickListener {
            UserUtils.saveFromFCMService(this, false)
//            ProviderDashboard.FROM_FCM_SERVICE = false
            startActivity(Intent(this, ProviderDashboard::class.java))
        }
        myBidsBtn.setOnClickListener { startActivity(Intent(this, ProviderMyBidsScreen::class.java)) }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
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
                        ,"")
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
                        ,""
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
        val factory = ViewModelFactory(ProviderMyBidsRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderMyBidsViewModel::class.java]
        viewModel.deleteBidAttachment(this, ProviderDeleteBidAttachmentReqModel(imagePath.bid_attach_id.toInt(), RetrofitBuilder.PROVIDER_KEY)).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    toast(this, it.message!!)
                    imagePathList.removeAt(position)
                    binding.attachmentsRV.adapter!!.notifyItemRemoved(position)
                    Handler().postDelayed({
                        binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
                        encodedImages.remove(encodedImages[position])
                    }, 500)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.bidAmount, it.message!!)
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}