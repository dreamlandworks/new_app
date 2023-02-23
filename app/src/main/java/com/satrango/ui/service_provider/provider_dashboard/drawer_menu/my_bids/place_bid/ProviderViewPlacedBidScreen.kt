package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderViewPlacedBidScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class ProviderViewPlacedBidScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var viewModel: PostJobViewModel
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityProviderViewPlacedBidScreenBinding
    private var categoryId = 0
    private var postJobId = 0
    private var bookingId = 0
    private var bidId = 0
    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderViewPlacedBidScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_post)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.statusBarColor = resources.getColor(R.color.purple_700)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        loadProfileImage(profilePic)

        initializeProgressDialog()
//        toast(this, UserUtils.getPostJobId(this).toString())
        val factory = ViewModelFactory(PostJobRepository())
        viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        bookingId = intent.getStringExtra("booking_id")!!.toInt()
        categoryId = intent.getStringExtra("category_id")!!.toInt()
        postJobId = intent.getStringExtra("post_job_id")!!.toInt()
//        bidId = intent.getStringExtra("bid_id")!!.toInt()
        userId = intent.getStringExtra("user_id")!!.toInt()
        binding.title.text = intent.getStringExtra("title")!!
        binding.expiresOn.text = intent.getStringExtra("expiresIn")!!
        binding.bidRanges.text = intent.getStringExtra("bidRange")!!

        val requestBody = MyJobPostViewReqModel(
            bookingId,
            categoryId,
            RetrofitBuilder.USER_KEY,
            postJobId,
            UserUtils.getUserId(this).toInt()
        )
        viewModel.myJobPostsViewDetails(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    updatePostDetails(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.viewPostBtn, it.message!!)
                }
            }
        }

        val requestBody1 = ViewProposalReqModel(bidId, RetrofitBuilder.USER_KEY, UserUtils.getUserId(this).toInt())
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
                    snackBar(binding.viewPostBtn, it.message!!)
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateBidDetails(data: ViewProposalResModel) {
        binding.apply {
            bidAmount.text = "Rs ${data.bid_details.amount}"
            estimateTime.text = "${data.bid_details.estimate_time} ${data.bid_details.estimate_type}"
            proposal.text = data.bid_details.proposal
            attachmentsRV1.layoutManager = LinearLayoutManager(
                this@ProviderViewPlacedBidScreen,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            attachmentsRV1.adapter = AttachmentsAdapter(
                data.attachments as ArrayList<Attachment>,
                this@ProviderViewPlacedBidScreen
            )

            openBidBtn.setOnClickListener {
                isProvider(this@ProviderViewPlacedBidScreen, true)
                ProviderPlaceBidScreen.FROM_EDIT_BID = true
                MyJobPostViewScreen.bookingId = bookingId
                MyJobPostViewScreen.categoryId = categoryId
                MyJobPostViewScreen.userId = userId
                UserUtils.savePostJobId(this@ProviderViewPlacedBidScreen, postJobId)
                val intent = Intent(binding.root.context, MyJobPostViewScreen::class.java)
                binding.root.context.startActivity(intent)
            }

            editBidBtn.setOnClickListener {
                ProviderPlaceBidScreen.FROM_EDIT_BID = true
                val intent = Intent(this@ProviderViewPlacedBidScreen, ProviderPlaceBidScreen::class.java)
                intent.putExtra("expiresIn", binding.expiresOn.text.toString().trim())
                intent.putExtra("bidRanges", binding.bidRanges.text.toString().trim())
                intent.putExtra("title", binding.title.text.toString().trim())
//                ProviderPlaceBidScreen.postJobId = postJobId
                ProviderPlaceBidScreen.bookingId = bookingId
                startActivity(intent)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePostDetails(data: MyJobPostViewResModel) {
        binding.apply {
            scheduleDate.text = data.job_post_details.scheduled_date
            estimateTime.text =
                "${data.job_post_details.estimate_time} ${data.job_post_details.estimate_type}"
            if (data.job_details.isNotEmpty()) {
                jobLocation.text = "${data.job_details[0].locality}, ${data.job_details[0].city}"
            } else {
                jobLocation.text = "Not Available"
            }
            attachmentsRV.layoutManager = LinearLayoutManager(
                this@ProviderViewPlacedBidScreen,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            attachmentsRV.adapter = AttachmentsAdapter(
                data.attachments as ArrayList<Attachment>,
                this@ProviderViewPlacedBidScreen
            )
        }
    }

    override fun deleteAttachment(position: Int, imagePath: Attachment) {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }
}