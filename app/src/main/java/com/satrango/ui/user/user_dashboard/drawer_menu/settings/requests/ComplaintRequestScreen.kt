package com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityComplaintRequestScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.SettingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.SettingsViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.UserSettingsScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class ComplaintRequestScreen : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintRequestScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintRequestScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_requests)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)

        if (UserSettingsScreen.FROM_PROVIDER) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
        }

        val factory = ViewModelFactory(SettingsRepository())
        val viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
        viewModel.complaintRequests(this).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.recyclerView.adapter = ComplaintRequestAdapter(it.data!!.complaints)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })

    }
}