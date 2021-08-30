package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts

import android.app.ProgressDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.satrango.R
import com.satrango.databinding.ActivityMyJobPostsScreenBinding
import com.satrango.utils.loadProfileImage
import de.hdodenhof.circleimageview.CircleImageView

class MyJobPostsScreen : AppCompatActivity() {

    private lateinit var binding: ActivityMyJobPostsScreenBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyJobPostsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_job_posts)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)


        binding.pendingBtn.setOnClickListener {
            binding.pendingBtn.setBackgroundResource(R.drawable.btn_bg)
            binding.pendingBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.awardedBtn.setBackgroundResource(0)
            binding.awardedBtn.setTextColor(Color.parseColor("#000000"))
            binding.expiredBtn.setBackgroundResource(0)
            binding.expiredBtn.setTextColor(Color.parseColor("#000000"))
//            updateUI("InProgress")
        }
        binding.awardedBtn.setOnClickListener {
            binding.awardedBtn.setBackgroundResource(R.drawable.btn_bg)
            binding.awardedBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.pendingBtn.setBackgroundResource(0)
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            binding.expiredBtn.setBackgroundResource(0)
            binding.expiredBtn.setTextColor(Color.parseColor("#000000"))
//            updateUI("Pending")
        }
        binding.expiredBtn.setOnClickListener {
            binding.expiredBtn.setBackgroundResource(R.drawable.btn_bg)
            binding.expiredBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.pendingBtn.setBackgroundResource(0)
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            binding.awardedBtn.setBackgroundResource(0)
            binding.awardedBtn.setTextColor(Color.parseColor("#000000"))
//            updateUI("Completed")
        }
    }
}