package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityMyJobPostEditScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsAdapter
import com.satrango.ui.user.bookings.booking_attachments.AttachmentsListener
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.AttachmentDeleteReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.DiscussionBoardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.SetGoalsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import de.hdodenhof.circleimageview.CircleImageView

class MyJobPostEditScreen : AppCompatActivity(), AttachmentsListener {

    private lateinit var images: java.util.ArrayList<String>
    private lateinit var displayData: MyJobPostViewResModel
    private lateinit var binding: ActivityMyJobPostEditScreenBinding
    private lateinit var viewModel: PostJobViewModel
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyJobPostEditScreenBinding.inflate(layoutInflater)
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

    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun connected() {
        binding.noteLayout.visibility = View.GONE
        loadScreen()
    }

    override fun onPause() {
        super.onPause()
        MyJobPostViewScreen.myJobPostViewScreen = false
        unregisterReceiver(broadcastReceiver)
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                disconnected()
            } else {
                connected()
            }
        }
    }

    private fun loadScreen() {
        val requestBody = MyJobPostViewReqModel(
            ViewBidsScreen.bookingId,
            ViewBidsScreen.categoryId,
            RetrofitBuilder.USER_KEY,
            ViewBidsScreen.postJobId,
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
                    displayData = it.data!!
                    updateUI(displayData)
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

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: MyJobPostViewResModel) {
        binding.title.text = data.job_post_details.title
        binding.bidRanges.setText(data.job_post_details.range_slots)
        binding.expiresOn.text = data.job_post_details.expires_in
        binding.scheduleDate.setText(data.job_post_details.scheduled_date)
        binding.estimateTime.setText("${data.job_post_details.estimate_time} ${data.job_post_details.estimate_type}")

        var languages = ""
        for (language in data.languages) {
            if (languages.isNotEmpty()) {
                languages = "$languages,${language}"
            } else {
                languages = language
            }

        }
        binding.languages.setText(languages)

        if (data.job_details[0].locality.isNullOrBlank()) {
            if (data.job_details[0].city.isNullOrBlank()) {
                binding.jobLocation.visibility = View.GONE
                binding.jobLocationText.visibility = View.GONE
            } else {
                binding.jobLocation.setText(data.job_details[0].city + ", " + data.job_details[0].state + ", " + data.job_details[0].country + ", " + data.job_details[0].zipcode)
            }
        } else if (!data.job_details[0].locality.isNullOrBlank()) {
            binding.jobLocation.setText(data.job_details[0].locality + ", " + data.job_details[0].city + ", " + data.job_details[0].state + ", " + data.job_details[0].country + ", " + data.job_details[0].zipcode)
        }

        binding.jobDescription.setText(data.job_details[0].job_description)

        images = ArrayList<String>()
        for (image in data.attachments) {
            images.add(RetrofitBuilder.BASE_URL + image.file_name.substring(1))
        }
        binding.attachmentsRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.attachmentsRV.adapter = AttachmentsAdapter(images, this)
        binding.bidCount.text = data.job_post_details.total_bids.toString()
        binding.avgAmount.text = data.job_post_details.average_bids_amount


    }

    override fun deleteAttachment(position: Int, imagePath: String) {
        viewModel.deleteAttachment(this, AttachmentDeleteReqModel(displayData.attachments[position].id.toInt(), RetrofitBuilder.USER_KEY)).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.attachmentsRV.adapter!!.notifyItemRemoved(position)
                    Handler().postDelayed({
                        images.remove(imagePath)
                        binding.attachmentsRV.adapter = AttachmentsAdapter(images, this)
//                        encodedImages.remove(encodedImages[position])
                    }, 500)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                }
            }
        })

    }

}