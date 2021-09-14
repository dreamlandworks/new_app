package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityMyJobPostViewScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.DiscussionBoardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.SetGoalsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobDateTimeScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import de.hdodenhof.circleimageview.CircleImageView

class MyJobPostViewScreen : AppCompatActivity(), AttachmentsListener {

    private var categoryId: Int = 0
    private var bookingId: Int = 0
    private lateinit var viewModel: PostJobViewModel
    private lateinit var binding: ActivityMyJobPostViewScreenBinding
    private lateinit var progressDialog: ProgressDialog

    companion object {
        var myJobPostViewScreen = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyJobPostViewScreenBinding.inflate(layoutInflater)
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

        val factory = ViewModelFactory(PostJobRepository())
        viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        bookingId = intent.getStringExtra("booking_id")!!.toInt()
        categoryId = intent.getStringExtra("category_id")!!.toInt()
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
        }
        binding.attachmentsRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.attachmentsRV.adapter = AttachmentsAdapter(images, this)

        binding.bidCount.text = data.job_post_details.total_bids.toString()
        binding.avgAmount.text = data.job_post_details.average_bids_amount

        binding.viewBidsBtn.setOnClickListener {
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

//        binding.awardBtn.setOnClickListener {
//            val intent = Intent(this, SetGoalsScreen::class.java)
//            intent.putExtra("postJobId", data.job_post_details.post_job_id)
//            intent.putExtra("bidId", data.job_post_details.bid_id)
//            intent.putExtra("bidPrice", data.job_post_details.average_bids_amount)
//            startActivity(intent)
//        }

        binding.editPostBtn.setOnClickListener {
            UserUtils.EDIT_MY_JOB_POST = true
            ViewBidsScreen.bookingId = bookingId
            ViewBidsScreen.categoryId = categoryId
            ViewBidsScreen.postJobId = data.job_post_details.post_job_id.toInt()
            startActivity(Intent(this, PostJobDateTimeScreen::class.java))
        }

    }

    private fun connected() {
        binding.noteLayout.visibility = View.GONE
        loadScreen()
    }

    private fun loadScreen() {
        val requestBody = MyJobPostViewReqModel(
            bookingId,
            categoryId,
            RetrofitBuilder.USER_KEY,
            intent.getStringExtra("post_job_id")!!.toInt(),
            UserUtils.getUserId(this).toInt(),
        )
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

}