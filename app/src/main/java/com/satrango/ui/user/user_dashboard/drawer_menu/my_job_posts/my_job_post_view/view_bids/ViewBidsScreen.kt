package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityViewBidsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.BidDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.ViewBidsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.ViewBidsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.utils.loadProfileImage
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class ViewBidsScreen : AppCompatActivity() {

    private lateinit var binding: ActivityViewBidsScreenBinding
    private lateinit var progressDialog: ProgressDialog

    companion object {
        var spId = 0
        var bidId = 0
        var categoryId = 0
        var bookingId = 0
        var postJobId = 0
        var bidPrice = 0.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBidsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_bids)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        binding.title.text = intent.getStringExtra("title")!!
        binding.expiresOn.text = intent.getStringExtra("expiresIn")!!
        binding.bidRanges.text = intent.getStringExtra("bidRanges")!!

        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]

        val requestBody = ViewBidsReqModel(RetrofitBuilder.USER_KEY, postJobId)
        viewModel.viewBids(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val list = it.data!!
                    val filteredList = ArrayList<BidDetail>()
                    for (bid in list.bid_details) {
                        if (bid.bid_type == "0") {
                          filteredList.add(bid)
                        }
                    }
                    binding.recyclerView.adapter = ViewBidsAdapter(filteredList)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                }
            }
        })

    }
}