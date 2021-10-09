package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityMyJobPostsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models.JobPostDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models.MyJobPostReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class MyJobPostsScreen : AppCompatActivity() {

    private lateinit var binding: ActivityMyJobPostsScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyJobPostsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        updateUI("Pending")

        binding.pendingBtn.setOnClickListener {
            binding.pendingBtn.setBackgroundResource(R.drawable.user_btn_bg)
            binding.pendingBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.awardedBtn.setBackgroundResource(0)
            binding.awardedBtn.setTextColor(Color.parseColor("#000000"))
            binding.expiredBtn.setBackgroundResource(0)
            binding.expiredBtn.setTextColor(Color.parseColor("#000000"))
            updateUI("Pending")
        }
        binding.awardedBtn.setOnClickListener {
            binding.awardedBtn.setBackgroundResource(R.drawable.user_btn_bg)
            binding.awardedBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.pendingBtn.setBackgroundResource(0)
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            binding.expiredBtn.setBackgroundResource(0)
            binding.expiredBtn.setTextColor(Color.parseColor("#000000"))
            updateUI("Awarded")
        }
        binding.expiredBtn.setOnClickListener {
            binding.expiredBtn.setBackgroundResource(R.drawable.user_btn_bg)
            binding.expiredBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.pendingBtn.setBackgroundResource(0)
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            binding.awardedBtn.setBackgroundResource(0)
            binding.awardedBtn.setTextColor(Color.parseColor("#000000"))
            updateUI("Expired")
        }
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_job_posts)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withImage, resources.getString(R.string.loading))
        progressDialog.setImageLocation(resources.getDrawable(R.drawable.circlelogo))
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
    }

    private fun updateUI(status: String) {
        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        val requestBody = MyJobPostReqModel(RetrofitBuilder.USER_KEY, UserUtils.getUserId(this).toInt())
        viewModel.myJobPosts(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val list = ArrayList<JobPostDetail>()
                    for (jobDetails in it.data!!.job_post_details) {
                        if (jobDetails.booking_status == status) {
                            list.add(jobDetails)
                        }
                    }
                    binding.recyclerView.adapter = MyJobPostsAdapter(list, status)

                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })

    }
}