package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityMyJobPostViewScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.ProviderPlaceBidScreen
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.DiscussionBoardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobDateTimeScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class MyJobPostViewScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var viewModel: PostJobViewModel
    private lateinit var binding: ActivityMyJobPostViewScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        var myJobPostViewScreen = false
        var FROM_PROVIDER = false
        var bookingId: Int = 0
        var categoryId: Int = 0
        var postJobId: Int = 0
        var userId: Int = 0
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyJobPostViewScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        val factory = ViewModelFactory(PostJobRepository())
        viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.view_post)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
        if (FROM_PROVIDER) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            binding.card.setBackgroundResource(R.drawable.provider_btn_bg_sm)
            binding.layoutOne.setBackgroundResource(R.drawable.purple_out_line)
            binding.layoutTwo.setBackgroundResource(R.drawable.purple_out_line)

            binding.editPostBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.viewBidsBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.discussionBoardBtn.setBackgroundResource(R.drawable.purple_out_line)

            binding.editPostBtn.setTextColor(resources.getColor(R.color.white))
            binding.discussionBoardBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.viewBidsBtn.setTextColor(resources.getColor(R.color.purple_500))

            if (ProviderPlaceBidScreen.FROM_EDIT_BID) {
                binding.editPostBtn.text = "Edit Bid"
            } else if (ProviderPlaceBidScreen.FROM_AWARDED) {
                binding.editPostBtn.visibility = View.GONE
            } else {
                binding.editPostBtn.text = "Place Bid"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: MyJobPostViewResModel) {
        binding.title.text = data.job_post_details.title
        binding.bidRanges.text = data.job_post_details.range_slots
        binding.expiresOn.text = data.job_post_details.expires_in
        binding.scheduleDate.text = data.job_post_details.scheduled_date
        binding.estimateTime.text =
            "${data.job_post_details.estimate_time} ${data.job_post_details.estimate_type}"

        var languages = ""
        for (language in data.languages) {
            if (languages.isNotEmpty()) {
                languages = "$languages,${language}"
            } else {
                languages = language
            }

        }
        binding.languages.text = languages

        if (data.job_details[0].locality.isNullOrBlank()) {
            if (data.job_details[0].city.isNullOrBlank()) {
                binding.jobLocation.visibility = View.GONE
                binding.jobLocationText.visibility = View.GONE
            } else {
                binding.jobLocation.text =
                    data.job_details[0].city + ", " + data.job_details[0].state + ", " + data.job_details[0].country + ", " + data.job_details[0].zipcode
            }
        } else if (!data.job_details[0].locality.isNullOrBlank()) {
            binding.jobLocation.text =
                data.job_details[0].locality + ", " + data.job_details[0].city + ", " + data.job_details[0].state + ", " + data.job_details[0].country + ", " + data.job_details[0].zipcode
        }

        binding.jobDescription.text = data.job_details[0].job_description

        val images = ArrayList<Attachment>()
        for (image in data.attachments) {
            images.add(image)
        }
        if (images.isEmpty()) {
            binding.attachmentsLayout.visibility = View.GONE
        } else {
            binding.attachmentsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.attachmentsRV.adapter = AttachmentsAdapter(images, this)
            toast(this, images.size.toString())
        }

        binding.bidCount.text = data.job_post_details.total_bids.toString()
        binding.avgAmount.text = data.job_post_details.average_bids_amount

        if (FROM_PROVIDER) {
            binding.viewBidsBtn.setOnClickListener {
                ViewBidsScreen.bookingId = bookingId
                ViewBidsScreen.FROM_PROVIDER = FROM_PROVIDER
                ViewBidsScreen.postJobId = data.job_post_details.post_job_id.toInt()
                val intent = Intent(this@MyJobPostViewScreen, ViewBidsScreen::class.java)
                intent.putExtra("expiresIn", data.job_post_details.expires_in)
                intent.putExtra("bidRanges", data.job_post_details.range_slots)
                intent.putExtra("title", data.job_post_details.title)
                startActivity(intent)
            }

            binding.discussionBoardBtn.setOnClickListener {
                val intent = Intent(this, DiscussionBoardScreen::class.java)
                intent.putExtra("postJobId", data.job_post_details.post_job_id)
                intent.putExtra("expiresIn", data.job_post_details.expires_in)
                intent.putExtra("bidRanges", data.job_post_details.range_slots)
                intent.putExtra("title", data.job_post_details.title)
                startActivity(intent)
            }

            binding.editPostBtn.setOnClickListener {
                val intent = Intent(this, ProviderPlaceBidScreen::class.java)
                intent.putExtra("expiresIn", data.job_post_details.expires_in)
                intent.putExtra("bidRanges", data.job_post_details.range_slots)
                intent.putExtra("title", data.job_post_details.title)
                intent.putExtra("postJobId", data.job_post_details.post_job_id)
                ProviderPlaceBidScreen.bookingId = bookingId
                startActivity(intent)
            }

        } else {
            binding.viewBidsBtn.setOnClickListener {
                ViewBidsScreen.FROM_PROVIDER = FROM_PROVIDER
                ViewBidsScreen.bookingId = bookingId
                ViewBidsScreen.postJobId = data.job_post_details.post_job_id.toInt()
                val intent = Intent(this@MyJobPostViewScreen, ViewBidsScreen::class.java)
                intent.putExtra("expiresIn", data.job_post_details.expires_in)
                intent.putExtra("bidRanges", data.job_post_details.range_slots)
                intent.putExtra("title", data.job_post_details.title)
                startActivity(intent)
            }

            binding.discussionBoardBtn.setOnClickListener {
                val intent = Intent(this, DiscussionBoardScreen::class.java)
                intent.putExtra("postJobId", data.job_post_details.post_job_id)
                intent.putExtra("expiresIn", data.job_post_details.expires_in)
                intent.putExtra("bidRanges", data.job_post_details.range_slots)
                intent.putExtra("title", data.job_post_details.title)
                startActivity(intent)
            }

            binding.editPostBtn.setOnClickListener {
                UserUtils.EDIT_MY_JOB_POST = true
                ViewBidsScreen.bookingId = bookingId
                ViewBidsScreen.categoryId = categoryId
                ViewBidsScreen.postJobId = data.job_post_details.post_job_id.toInt()
                startActivity(Intent(this, PostJobDateTimeScreen::class.java))
            }
        }



    }

    private fun connected() {
        binding.noteLayout.visibility = View.GONE
        loadScreen()
    }

    private fun loadScreen() {
        val requestBody = MyJobPostViewReqModel(bookingId, categoryId, RetrofitBuilder.USER_KEY, postJobId, UserUtils.getUserId(this).toInt())
        Log.e("JOB POST:", Gson().toJson(requestBody))
        viewModel.myJobPostsViewDetails(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.dataLayout.visibility = View.GONE
                    binding.noteLayout.visibility = View.GONE
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.dataLayout.visibility = View.VISIBLE
                    updateUI(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    disconnected()
                    binding.note.text = it.message!!
                }

            }
        })
    }

    private fun disconnected() {
        binding.noteLayout.visibility = View.VISIBLE
        binding.note.text = getString(R.string.no_internet_connection)
        binding.retryBtn.setOnClickListener {
            connected()
        }
    }


    override fun deleteAttachment(position: Int, imagePath: Attachment) {

    }

    override fun onResume() {
        super.onResume()
        myJobPostViewScreen = true
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        myJobPostViewScreen = false
        unregisterReceiver(broadcastReceiver)
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected =
                intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                disconnected()
            } else {
                connected()
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