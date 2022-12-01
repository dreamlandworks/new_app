package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews

import android.app.ProgressDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderReviewScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.ProviderMyAccountRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.ProviderMyAccountViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.MyAccountRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.MyAccountViewModel
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class ProviderReviewScreen : AppCompatActivity() {

    private lateinit var binding: ActivityProviderReviewScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderReviewScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.purple_700))
        }

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn)
            .setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
            .setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.reviews)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val factory = ViewModelFactory(ProviderMyAccountRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderMyAccountViewModel::class.java]
        viewModel.reviews(this).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.recyclerView.adapter = ProviderReviewAdapter(it.data!!.sp_reviews)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        }


    }
}