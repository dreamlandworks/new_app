package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
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
import com.satrango.databinding.ActivityMyBookingsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class UserMyBookingsScreen : AppCompatActivity() {

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var viewModel: MyBookingsViewModel
    private lateinit var binding: ActivityMyBookingsScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(MyBookingsRepository())
        viewModel = ViewModelProvider(this, factory)[MyBookingsViewModel::class.java]

        initializeToolBar()
        initializeProgressDialog()

        updateUI("Pending")
        binding.inProgressBtn.setOnClickListener {
            binding.inProgressBtn.setBackgroundResource(R.drawable.user_btn_bg)
            binding.completedBtn.setBackgroundResource(0)
            binding.pendingBtn.setBackgroundResource(0)
            binding.inProgressBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.completedBtn.setTextColor(Color.parseColor("#000000"))
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            updateUI("InProgress")
        }
        binding.pendingBtn.setOnClickListener {
            binding.pendingBtn.setBackgroundResource(R.drawable.user_btn_bg)
            binding.completedBtn.setBackgroundResource(0)
            binding.inProgressBtn.setBackgroundResource(0)
            binding.inProgressBtn.setTextColor(Color.parseColor("#000000"))
            binding.completedBtn.setTextColor(Color.parseColor("#000000"))
            binding.pendingBtn.setTextColor(Color.parseColor("#FFFFFF"))
            updateUI("Pending")
        }
        binding.completedBtn.setOnClickListener {
            binding.completedBtn.setBackgroundResource(R.drawable.user_btn_bg)
            binding.inProgressBtn.setBackgroundResource(0)
            binding.pendingBtn.setBackgroundResource(0)
            binding.inProgressBtn.setTextColor(Color.parseColor("#000000"))
            binding.completedBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            updateUI("Completed")
        }
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
    }

    private fun updateUI(status: String) {
        val requestBody = MyBookingsReqModel(RetrofitBuilder.USER_KEY, UserUtils.getUserId(this).toInt())
        viewModel.getMyBookingDetails(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val list = ArrayList<BookingDetail>()
                    for (details in it.data!!) {
                        if (status == "Completed") {
                            if (details.booking_status.equals(
                                    status,
                                    ignoreCase = true
                                ) || details.booking_status.equals(
                                    "Expired",
                                    ignoreCase = true
                                ) || details.booking_status.equals("Cancelled", ignoreCase = true)
                            ) {
                                list.add(details)
                            }
                        } else {
                            if (details.booking_status.equals(status, ignoreCase = true)) {
                                list.add(details)
                            }
                        }
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = MyBookingsAdapter(list)
                    if (list.isEmpty()) {
                        binding.note.visibility = View.VISIBLE
                    } else {
                        binding.note.visibility = View.GONE
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    binding.note.visibility = View.VISIBLE
                    binding.note.text = it.message!!
                    snackBar(binding.recyclerView, it.message)
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    override fun onBackPressed() {
        startActivity(Intent(this, UserDashboardScreen::class.java))
    }
}