package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityViewBidDetailsScreensBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.SetGoalsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.RejectJobPostStatusReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class ViewBidDetailsScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var viewModel: PostJobViewModel
    private lateinit var binding: ActivityViewBidDetailsScreensBinding
    private lateinit var progressDialog: BeautifulProgressDialog


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBidDetailsScreensBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        val factory = ViewModelFactory(PostJobRepository())
        viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        val requestBody = ViewProposalReqModel(
            intent.getStringExtra("bidId")!!.toInt(),
            RetrofitBuilder.USER_KEY,
            intent.getStringExtra("spId")!!.toInt()
        )
        viewModel.viewProposal(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()

                    binding.apply {

                        val data = it.data!!
                        Glide.with(profilePic)
                            .load(RetrofitBuilder.BASE_URL + data.bid_details.sp_profile)
                            .into(profilePic)
                        spName.text = data.bid_details.sp_fname + " " + data.bid_details.sp_lname
                        spOccupation.text = data.bid_details.profession
                        completesIn.text = data.bid_details.esimate_time + " " + data.bid_details.estimate_type
                        bid.text = data.bid_details.amount
                        proposal.text = data.bid_details.proposal
                        gender.text = data.bid_details.sp_gender

                        var languagesText = ""
                        for (language in data.language) {
                            languagesText = if (languagesText.isEmpty()) {
                                language.name
                            } else {
                                languagesText + "," + language.name
                            }
                        }
                        languages.text = languagesText
                        aboutMe.text = data.bid_details.about_me

                        qualification.text = data.bid_details.qualification

                        var skillText = ""
                        for (skill in data.skills) {
                            skillText = if (skillText.isEmpty()) {
                                skill.keyword
                            } else {
                                "$skillText, ${skill.keyword}"
                            }
                        }
                        skills.text = skillText
                        experience.text = data.bid_details.exp
                        jobsCount.text = data.bid_details.jobs_completed

                        val images = ArrayList<Attachment>()
                        for (image in data.attachments) {
                            images.add(image)
                        }
                        attachmentsRV.layoutManager = LinearLayoutManager(
                            this@ViewBidDetailsScreen,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        attachmentsRV.adapter =
                            AttachmentsAdapter(images, this@ViewBidDetailsScreen)
                        if (images.isEmpty()) {
                            attachmentsText.visibility = View.GONE
                        }

                        if (ViewBidsScreen.FROM_PROVIDER) {
                            binding.awardBtn.setOnClickListener {
                                onBackPressed()
                            }
                        } else {
                            binding.awardBtn.setOnClickListener {
                                ViewBidsScreen.bidPrice = data.bid_details.amount.toDouble()
                                ViewBidsScreen.bidId = data.bid_details.bid_id.toInt()
                                ViewBidsScreen.spId = data.bid_details.sp_id.toInt()
                                startActivity(
                                    Intent(
                                        this@ViewBidDetailsScreen,
                                        SetGoalsScreen::class.java
                                    )
                                )
                            }
                            binding.rejectBtn.setOnClickListener {
                                rejectBid()
                            }
                        }

                    }

                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.aboutMe, it.message!!)
                }
            }
        })

    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.view_proposal)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
        if (ViewBidsScreen.FROM_PROVIDER) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            binding.layout.setBackgroundResource(R.drawable.provider_btn_bg_sm)
            binding.layoutOne.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutTwo.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutThree.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutFour.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutFive.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutSix.setBackgroundResource(R.drawable.purple_out_line)
            binding.awardBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.awardBtn.setTextColor(resources.getColor(R.color.white))
            binding.awardBtn.visibility = View.GONE
            binding.rejectBtn.visibility = View.GONE
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun rejectBid() {
        val requestBody = RejectJobPostStatusReqModel(
            ViewBidsScreen.bookingId,
            RetrofitBuilder.USER_KEY,
            ViewBidsScreen.postJobId,
            ViewBidsScreen.bidId,
            29
        )
        viewModel.rejectPostJobStatus(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    snackBar(binding.aboutMe, "Bid Rejected!")
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.aboutMe, it.message!!)
                }
            }
        })
    }

    override fun deleteAttachment(position: Int, imagePath: Attachment) {

    }

    override fun onResume() {
        super.onResume()
        MyJobPostViewScreen.myJobPostViewScreen = true
    }

    override fun onPause() {
        super.onPause()
        MyJobPostViewScreen.myJobPostViewScreen = false
    }

}