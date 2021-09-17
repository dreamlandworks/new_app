package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityPostJobAttachmentsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOneRepository
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOneViewModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.UserMyAccountScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.AttachmentDeleteReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.blue_collar.MyJobPostEditBlueCollarReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobAddressScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Data
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_blue_collar.PostJobBlueCollarReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.KeywordsResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.LangResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.UserPlanScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostJobAttachmentsScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var data: MyJobPostViewResModel
    private lateinit var binding: ActivityPostJobAttachmentsScreenBinding
    private lateinit var viewModel: ProviderSignUpOneViewModel
    private lateinit var progressDialog: ProgressDialog

    private lateinit var responseLanguages: ProviderOneModel
    private lateinit var keywordsMList: List<Data>

    private val CAMERA_REQUEST: Int = 100
    private val GALLERY_REQUEST = 100

    companion object {
        lateinit var imagePathList: ArrayList<com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment>
        lateinit var encodedImages: ArrayList<Attachment>

        lateinit var finalKeywords: java.util.ArrayList<KeywordsResponse>
        lateinit var finalLanguages: java.util.ArrayList<LangResponse>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobAttachmentsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.post_a_job)

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")

        imagePathList = ArrayList()
        encodedImages = ArrayList()

        binding.apply {

            backBtn.setOnClickListener {
                onBackPressed()
            }

            nextBtn.setOnClickListener {

                finalLanguages = ArrayList()
                for (lang in languages.allChips) {
                    for (lan in responseLanguages.data.language) {
                        if (lang.text.toString().equals(lan.name, ignoreCase = true)) {
                            finalLanguages.add(LangResponse(lan.id, lan.name))
                        }
                    }
                }
                finalKeywords = ArrayList()
                for (key in keywordSkills.allChips) {
                    for (k in keywordsMList) {
                        if (key.text.toString().equals(k.keyword, ignoreCase = true)) {
                            finalKeywords.add(KeywordsResponse(k.keyword_id, k.keyword))
                        }
                    }
                }

                when {
                    finalLanguages.size == 0 -> {
                        snackBar(nextBtn, "Select Languages")
                    }
                    finalKeywords.size == 0 -> {
                        snackBar(nextBtn, "Select Skills / Keywords")
                    }
                    UserUtils.bids_period == 0 -> {
                        snackBar(nextBtn, "Select Accept Bid Per")
                    }
                    else -> {
                        if (UserUtils.getFromJobPostMultiMove(this@PostJobAttachmentsScreen) || UserUtils.getFromJobPostSingleMove(this@PostJobAttachmentsScreen)) {
                            startActivity(Intent(this@PostJobAttachmentsScreen, PostJobAddressScreen::class.java))
                        } else if (UserUtils.getFromJobPostBlueCollar(this@PostJobAttachmentsScreen)) {
                            if (UserUtils.EDIT_MY_JOB_POST) {
                                updatePostJobBlueCollar()
                            } else {
                                postJobBlueCollar()
                            }

                        }
                    }
                }

            }

            oneDay.setOnClickListener {
                oneDay.setBackgroundResource(R.drawable.user_btn_bg)
                threeDays.setBackgroundResource(R.drawable.blue_out_line)
                sevenDays.setBackgroundResource(R.drawable.blue_out_line)
                oneDay.setTextColor(Color.parseColor("#FFFFFF"))
                threeDays.setTextColor(Color.parseColor("#0A84FF"))
                sevenDays.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 1
            }

            threeDays.setOnClickListener {
                threeDays.setBackgroundResource(R.drawable.user_btn_bg)
                oneDay.setBackgroundResource(R.drawable.blue_out_line)
                sevenDays.setBackgroundResource(R.drawable.blue_out_line)
                threeDays.setTextColor(Color.parseColor("#FFFFFF"))
                oneDay.setTextColor(Color.parseColor("#0A84FF"))
                sevenDays.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 3
            }

            sevenDays.setOnClickListener {
                sevenDays.setBackgroundResource(R.drawable.user_btn_bg)
                oneDay.setBackgroundResource(R.drawable.blue_out_line)
                threeDays.setBackgroundResource(R.drawable.blue_out_line)
                sevenDays.setTextColor(Color.parseColor("#FFFFFF"))
                threeDays.setTextColor(Color.parseColor("#0A84FF"))
                oneDay.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 7
            }

            attachments.setOnClickListener {
                getImageFromGallery()
            }

        }

        val factory = ViewModelFactory(ProviderSignUpOneRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderSignUpOneViewModel::class.java]

        loadLanguages()
        loadkeyWords()

        if (UserUtils.EDIT_MY_JOB_POST) {
            updateUI()
        }
    }

    private fun updatePostJobBlueCollar() {
        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        val requestBody = MyJobPostEditBlueCollarReqModel(
            encodedImages,
            UserUtils.bid_per,
            UserUtils.bid_range_id,
            UserUtils.bids_period,
            data.job_post_details.booking_id,
            data.job_post_details.post_created_on,
            UserUtils.estimate_time,
            UserUtils.estimateTypeId,
            UserUtils.job_description,
            RetrofitBuilder.USER_KEY,
            finalKeywords,
            finalLanguages,
            data.job_post_details.post_job_id.toInt(),
            UserUtils.scheduled_date,
            UserUtils.time_slot_from,
            UserUtils.title,
            UserUtils.getUserId(this).toInt()
        )
        viewModel.updateBlueCollarMyJobPost(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    snackBar(binding.nextBtn, it.data!!.message)
                    Handler().postDelayed({
                        startActivity(Intent(this, UserDashboardScreen::class.java))
                    }, 3000)
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        })
    }

    private fun updateUI() {
        data = Gson().fromJson(UserUtils.EDIT_MY_JOB_POST_DETAILS, MyJobPostViewResModel::class.java)
        val chips = ArrayList<ChipInfo>()
        for (language in data.languages) {
            chips.add(ChipInfo(language, language))
        }
        binding.languages.setTextWithChips(chips)
        val keywords = ArrayList<ChipInfo>()
        for (keyword in data.keywords) {
            keywords.add(ChipInfo(keyword, keyword))
        }
        binding.keywordSkills.setTextWithChips(keywords)

        when(data.job_post_details.bids_period) {
            "1" -> {
                binding.oneDay.setBackgroundResource(R.drawable.user_btn_bg)
                binding.threeDays.setBackgroundResource(R.drawable.blue_out_line)
                binding.sevenDays.setBackgroundResource(R.drawable.blue_out_line)
                binding.oneDay.setTextColor(Color.parseColor("#FFFFFF"))
                binding.threeDays.setTextColor(Color.parseColor("#0A84FF"))
                binding.sevenDays.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 1
            }
            "3" -> {
                binding.threeDays.setBackgroundResource(R.drawable.user_btn_bg)
                binding.oneDay.setBackgroundResource(R.drawable.blue_out_line)
                binding.sevenDays.setBackgroundResource(R.drawable.blue_out_line)
                binding.threeDays.setTextColor(Color.parseColor("#FFFFFF"))
                binding.oneDay.setTextColor(Color.parseColor("#0A84FF"))
                binding.sevenDays.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 3
            }
            "5" -> {
                binding.sevenDays.setBackgroundResource(R.drawable.user_btn_bg)
                binding.oneDay.setBackgroundResource(R.drawable.blue_out_line)
                binding.threeDays.setBackgroundResource(R.drawable.blue_out_line)
                binding.sevenDays.setTextColor(Color.parseColor("#FFFFFF"))
                binding.threeDays.setTextColor(Color.parseColor("#0A84FF"))
                binding.oneDay.setTextColor(Color.parseColor("#0A84FF"))
                UserUtils.bids_period = 7
            }
        }

        imagePathList = data.attachments as ArrayList<com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment>
        binding.attachmentsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
    }

    @SuppressLint("SimpleDateFormat")
    private fun postJobBlueCollar() {

        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        val requestBody = PostJobBlueCollarReqModel(
            encodedImages,
            UserUtils.bid_per,
            UserUtils.bid_range_id,
            UserUtils.bids_period,
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            UserUtils.estimate_time,
            UserUtils.estimateTypeId,
            UserUtils.job_description,
            RetrofitBuilder.USER_KEY,
            finalKeywords,
            finalLanguages,
            UserUtils.scheduled_date,
            UserUtils.time_slot_from,
            UserUtils.title,
            UserUtils.getUserId(this).toInt()
        )

        viewModel.postJobBlueCollar(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    if (it.data!!.user_plan_id != "0") {
                        showSuccessDialog()
                    } else {
                        UserMyAccountScreen.FROM_MY_ACCOUNT = false
                        startActivity(Intent(this, UserPlanScreen::class.java))
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
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

    private fun loadLanguages() {
        viewModel.professionsList(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    responseLanguages = it.data!!

                    val languagesList = ArrayList<String>()
                    for (data in responseLanguages.data.language) {
                        languagesList.add(data.name)
                    }
                    val languagesAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        languagesList
                    )
                    binding.languages.setAdapter(languagesAdapter)
                    binding.languages.addChipTerminator(
                        '\n',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL
                    )
                    binding.languages.addChipTerminator(
                        ' ',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
                    )
                    binding.languages.addChipTerminator(
                        ',',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
                    )
                    binding.languages.addChipTerminator(
                        ';',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN
                    )
                    binding.languages.setNachoValidator(ChipifyingNachoValidator())
                    binding.languages.enableEditChipOnTouch(true, true)

                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.keywordSkills, it.message!!)
                    snackBar(binding.keywordSkills, "Click Reset to get language values")
                }
            }
        })
    }

    private fun loadkeyWords() {
        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        viewModel.skills(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    keywordsMList = it.data!!
                    val keywordsList = ArrayList<String>()
                    for (data in keywordsMList) {
                        keywordsList.add(data.keyword)
                    }
                    val languagesAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        keywordsList
                    )
                    binding.keywordSkills.setAdapter(languagesAdapter)
                    binding.keywordSkills.addChipTerminator(
                        '\n',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL
                    )
                    binding.keywordSkills.addChipTerminator(
                        ' ',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
                    )
                    binding.keywordSkills.addChipTerminator(
                        ',',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
                    )
                    binding.keywordSkills.addChipTerminator(
                        ';',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN
                    )
                    binding.keywordSkills.setNachoValidator(ChipifyingNachoValidator())
                    binding.keywordSkills.enableEditChipOnTouch(true, true)
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
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
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData != null) {
                val count: Int = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imagePathList.add(com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment("", getImageFilePath(imageUri), ""))
                    encodedImages.add(Attachment(encodeToBase64FromUri(imageUri)))
                }
            } else if (data.data != null) {
                val imageUri = data.data
                imagePathList.add(com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment("", getImageFilePath(imageUri!!), ""))
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
        if (imagePath.id.isNotEmpty()) {
            val factory = ViewModelFactory(PostJobRepository())
            val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
            viewModel.deleteAttachment(this, AttachmentDeleteReqModel(imagePath.id.toInt(), RetrofitBuilder.USER_KEY)).observe(this, {
                when(it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        binding.attachmentsRV.adapter!!.notifyItemRemoved(position)
                        Handler().postDelayed({
                            imagePathList.remove(imagePath)
                            binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
                        }, 500)
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                    }
                }
            })
        } else {
            imagePathList.remove(imagePath)
            binding.attachmentsRV.adapter!!.notifyItemRemoved(position)
            Handler().postDelayed({
                binding.attachmentsRV.adapter = AttachmentsAdapter(imagePathList, this)
                encodedImages.remove(encodedImages[position])
            }, 500)
        }

    }

}