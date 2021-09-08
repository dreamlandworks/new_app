package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityViewBidDetailsScreensBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.SetGoalsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class ViewBidDetailsScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var binding: ActivityViewBidDetailsScreensBinding
    private lateinit var progressDialog: ProgressDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBidDetailsScreensBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_proposal)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        binding.awardBtn.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val dialogView = layoutInflater.inflate(R.layout.payment_type_dialog, null)
            val installmentBtn = dialogView.findViewById<TextView>(R.id.installmentBtn)
            val singlePaymentBtn = dialogView.findViewById<TextView>(R.id.singlePaymentBtn)
            installmentBtn.setOnClickListener {
                dialog.dismiss()
            }
            singlePaymentBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setContentView(dialogView)
            dialog.show()
        }

        binding.rejectBtn.setOnClickListener {
        
        }

        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        val requestBody = ViewProposalReqModel(intent.getStringExtra("bidId")!!.toInt(), RetrofitBuilder.USER_KEY, intent.getStringExtra("spId")!!.toInt())
        viewModel.viewProposal(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()

                    binding.apply {

                        val data = it.data!!
                        Glide.with(profilePic).load(RetrofitBuilder.BASE_URL + data.bid_details.sp_profile).into(profilePic)
                        spName.text = data.bid_details.sp_fname + " " + data.bid_details.sp_lname
                        spOccupation.text = data.bid_details.profession
                        completesIn.text = data.bid_details.esimate_time + " " + data.bid_details.estimate_type
                        bid.text = data.bid_details.amount

                        proposal.text = data.bid_details.proposal

                        gender.text = data.bid_details.sp_gender
//                        myLocation.text = data.bid_details.ci

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

                        val images = ArrayList<String>()
                        for (image in data.attachments) {
                            images.add(RetrofitBuilder.BASE_URL + image.file_name.substring(1))
                        }
                        attachmentsRV.layoutManager = LinearLayoutManager(this@ViewBidDetailsScreen, LinearLayoutManager.HORIZONTAL, false)
                        attachmentsRV.adapter = AttachmentsAdapter(images, this@ViewBidDetailsScreen)
                        if (images.isEmpty()) {
                            attachmentsText.visibility = View.GONE
                        }

                        awardBtn.setOnClickListener {
                            ViewBidsScreen.bidPrice = data.bid_details.amount.toDouble()
                            startActivity(Intent(this@ViewBidDetailsScreen, SetGoalsScreen::class.java))
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

    override fun deleteAttachment(position: Int, imagePath: String) {

    }

    override fun onResume() {
        super.onResume()
        MyJobPostViewScreen.myJobPostViewScreen = true
    }

    override fun onPause() {
        super.onPause()
        MyJobPostViewScreen.myJobPostViewScreen = false
    }

    @SuppressLint("SetTextI18n")
    private fun showPaymentTypeDialog() {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.satrango.R.layout.search_type_dialog)
        val window = dialog.window
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val viewResults = dialog.findViewById<TextView>(com.satrango.R.id.viewResults)
        val bookInstantly = dialog.findViewById<TextView>(com.satrango.R.id.bookInstantly)
        val question = dialog.findViewById<TextView>(com.satrango.R.id.question)
        question.text = "Select Payment Type"
        viewResults.text = "Installments"
        bookInstantly.text = "Spot"

        viewResults.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, SetGoalsScreen::class.java)
            intent.putExtra("postJobId", ViewBidsScreen.bookingId)
            intent.putExtra("bidPrice", binding.bid.text.toString().trim())
            startActivity(intent)
        }
        bookInstantly.setOnClickListener {
            dialog.dismiss()

        }
        dialog.show()
    }

}