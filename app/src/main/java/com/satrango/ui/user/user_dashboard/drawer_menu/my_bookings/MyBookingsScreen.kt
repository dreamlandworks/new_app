package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import android.app.ProgressDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityMyBookingsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class MyBookingsScreen : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var viewModel: MyBookingsViewModel
    private lateinit var binding: ActivityMyBookingsScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(MyBookingsRepository())
        viewModel = ViewModelProvider(this, factory)[MyBookingsViewModel::class.java]

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.my_booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)


        updateUI("InProgress")
        binding.inProgressBtn.setOnClickListener {
            binding.inProgressBtn.setBackgroundResource(R.drawable.btn_bg)
            binding.completedBtn.setBackgroundResource(0)
            binding.pendingBtn.setBackgroundResource(0)
            binding.inProgressBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.completedBtn.setTextColor(Color.parseColor("#000000"))
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            updateUI("InProgress")
        }
        binding.pendingBtn.setOnClickListener {
            binding.pendingBtn.setBackgroundResource(R.drawable.btn_bg)
            binding.completedBtn.setBackgroundResource(0)
            binding.inProgressBtn.setBackgroundResource(0)
            binding.inProgressBtn.setTextColor(Color.parseColor("#000000"))
            binding.completedBtn.setTextColor(Color.parseColor("#000000"))
            binding.pendingBtn.setTextColor(Color.parseColor("#FFFFFF"))
            updateUI("Pending")
        }
        binding.completedBtn.setOnClickListener {
            binding.completedBtn.setBackgroundResource(R.drawable.btn_bg)
            binding.inProgressBtn.setBackgroundResource(0)
            binding.pendingBtn.setBackgroundResource(0)
            binding.inProgressBtn.setTextColor(Color.parseColor("#000000"))
            binding.completedBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            updateUI("Completed")
        }
    }

    private fun updateUI(status: String) {
        val requestBody = MyBookingsReqModel(RetrofitBuilder.USER_KEY, UserUtils.getUserId(this).toInt())

        viewModel.getMyBookingDetails(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val list = ArrayList<BookingDetail>()
                    for (details in it.data!!) {
                        if (details.booking_status.equals(status, ignoreCase = true)) {
                            list.add(details)
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
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        })
    }
}