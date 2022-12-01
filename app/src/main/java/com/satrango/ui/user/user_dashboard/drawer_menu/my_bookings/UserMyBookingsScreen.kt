package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityMyBookingsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.RescheduleStatusChangeReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.chats.ChatScreen
import com.satrango.ui.user.user_dashboard.chats.models.ChatModel
import com.satrango.ui.user.user_dashboard.chats.models.ChatsModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.AlertsInterface
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.getPhoneNo
import com.satrango.utils.UserUtils.getUserId
import com.satrango.utils.UserUtils.isCompleted
import com.satrango.utils.UserUtils.isPending
import com.satrango.utils.UserUtils.isProgress
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class UserMyBookingsScreen : AppCompatActivity(), AlertsInterface, BookingInterface {

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

        binding.inProgressBtn.setOnClickListener {
            binding.inProgressBtn.setBackgroundResource(R.drawable.user_btn_bg)
            binding.completedBtn.setBackgroundResource(0)
            binding.pendingBtn.setBackgroundResource(0)
            binding.inProgressBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.completedBtn.setTextColor(Color.parseColor("#000000"))
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            isProgress(this, true)
            isPending(this, false)
            isCompleted(this, false)
            updateUI("InProgress")
        }
        binding.pendingBtn.setOnClickListener {
            updatePendingUI()
        }
        binding.completedBtn.setOnClickListener {
            binding.completedBtn.setBackgroundResource(R.drawable.user_btn_bg)
            binding.inProgressBtn.setBackgroundResource(0)
            binding.pendingBtn.setBackgroundResource(0)
            binding.inProgressBtn.setTextColor(Color.parseColor("#000000"))
            binding.completedBtn.setTextColor(Color.parseColor("#FFFFFF"))
            binding.pendingBtn.setTextColor(Color.parseColor("#000000"))
            isCompleted(this, true)
            isProgress(this, false)
            isPending(this, false)
            updateUI("Completed")
        }

    }

    private fun updatePendingUI() {
        binding.pendingBtn.setBackgroundResource(R.drawable.user_btn_bg)
        binding.completedBtn.setBackgroundResource(0)
        binding.inProgressBtn.setBackgroundResource(0)
        binding.inProgressBtn.setTextColor(Color.parseColor("#000000"))
        binding.completedBtn.setTextColor(Color.parseColor("#000000"))
        binding.pendingBtn.setTextColor(Color.parseColor("#FFFFFF"))
        isPending(this, true)
        isProgress(this, false)
        isCompleted(this, false)
        updateUI("Pending")
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
//                    progressDialog.show()
                    binding.shimmerLayout.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.shimmerLayout.startShimmerAnimation()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val list = ArrayList<BookingDetail>()
                    for (details in it.data!!) {
                        if (status == "Completed") {
                            if (details.booking_status.equals(
                                    status,
                                    ignoreCase = true) || details.booking_status.equals("Expired", ignoreCase = true) || details.booking_status.equals("Cancelled", ignoreCase = true)) {
                                list.add(details)
                            }
                        } else {
                            if (details.booking_status.equals(status, ignoreCase = true)) {
                                list.add(details)
                            }
                        }
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = MyBookingsAdapter(list, this, this)
                    binding.shimmerLayout.visibility = View.GONE
                    binding.shimmerLayout.stopShimmerAnimation()
                    binding.recyclerView.visibility = View.VISIBLE
                    if (list.isEmpty()) {
                        binding.note.visibility = View.VISIBLE
                    } else {
                        binding.note.visibility = View.GONE
                    }
                }
                is NetworkResponse.Failure -> {
                    binding.shimmerLayout.visibility = View.GONE
                    binding.shimmerLayout.stopShimmerAnimation()
                    binding.recyclerView.visibility = View.VISIBLE
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updatePendingUI()
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

    override fun rescheduleUserStatusCancelDialog(
        bookingId: Int,
        categoryId: Int,
        userId: Int,
        rescheduleId: Int,
        description: String
    ) {

    }

    override fun rescheduleUserAcceptRejectDialog(
        bookingId: Int,
        categoryId: Int,
        userId: Int,
        rescheduleId: Int,
        description: String
    ) {
        fetchBookingDetails(bookingId, categoryId, rescheduleId.toString(), description)
    }

    override fun rescheduleSPStatusCancelDialog(
        bookingId: Int,
        categoryId: Int,
        userId: Int,
        rescheduleId: Int,
        description: String
    ) {

    }

    override fun rescheduleSPAcceptRejectDialog(
        bookingId: Int,
        categoryId: Int,
        userId: Int,
        rescheduleId: Int,
        description: String
    ) {

    }

    override fun extraDemandDialog(bookingId: Int, categoryId: Int, userId: Int) {

    }

    override fun divertToInstallmentsScreen(bookingId: String, postJobId: Int) {

    }

    override fun divertToViewBidDetailsScreen(bookingId: String, spId: Int, bidId: Int) {

    }

    override fun divertToOfferScreen() {

    }

    private fun fetchBookingDetails(bookingId: Int, categoryId: Int, rescheduleId: String, description: String) {
        val requestBody = BookingDetailsReqModel(bookingId, categoryId, RetrofitBuilder.USER_KEY, UserUtils.getUserId(this).toInt())
        CoroutineScope(Dispatchers.Main).launch {
            try {
                progressDialog.show()
                val response = RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(requestBody)
                if (response.status == 200)  {
                    progressDialog.dismiss()
                    showRescheduleDialog(bookingId, response, rescheduleId, description)
                } else {
                    progressDialog.dismiss()
                    toast(this@UserMyBookingsScreen, response.message)
                }
            } catch (e: Exception) {
                toast(this@UserMyBookingsScreen, e.message!!)
            }
        }
    }

    private fun rescheduleStatusChangeApiCall(
        bookingId: Int,
        rescheduleId: Int,
        spId: Int,
        statusId: Int
    ) {
        val requestBody = RescheduleStatusChangeReqModel(
            bookingId,
            RetrofitBuilder.USER_KEY,
            rescheduleId,
            spId,
            statusId,
            UserAlertScreen.USER_TYPE,
            UserUtils.getUserId(this).toInt()
        )
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitBuilder.getUserRetrofitInstance().updateRescheduleStatus(requestBody)
                val jsonResponse = JSONObject(response.string())
                toast(this@UserMyBookingsScreen, jsonResponse.getString("message"))
                finish()
                startActivity(intent)
            } catch (e: Exception) {
                toast(this@UserMyBookingsScreen, e.message!!)
            }
        }
    }

    private fun showRescheduleDialog(
        bookingId: Int,
        response: BookingDetailsResModel,
        rescheduleId: String,
        description: String
    ) {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.reschedule_status_change_dialog, null)
        val noteText = dialogView.findViewById<TextView>(R.id.noteText)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)

        noteText.setTextColor(resources.getColor(R.color.blue))
        rejectBtn.setTextColor(resources.getColor(R.color.blue))
        title.setTextColor(resources.getColor(R.color.blue))
        rejectBtn.setBackgroundResource(R.drawable.blue_out_line)
        acceptBtn.setTextColor(resources.getColor(R.color.white))
        acceptBtn.setBackgroundResource(R.drawable.category_bg)
        noteText.text = description

        closeBtn.setOnClickListener { dialog.dismiss() }

        acceptBtn.setOnClickListener {
            dialog.dismiss()
            rescheduleStatusChangeApiCall(
                bookingId,
                rescheduleId.toInt(),
                response.booking_details.sp_id.toInt(),
                12
            )
        }

        rejectBtn.setOnClickListener {
            dialog.dismiss()
            rescheduleStatusChangeApiCall(
                bookingId,
                rescheduleId.toInt(),
                response.booking_details.sp_id.toInt(),
                11
            )
        }

        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    override fun startUserMessaging(bookingDetails: BookingDetail) {
        val branch = if (UserUtils.getUserId(this).toInt() > bookingDetails.sp_id.toInt()) {
            bookingDetails.sp_id + "|" + UserUtils.getUserId(this)
        } else {
            UserUtils.getUserId(this) + "|" + bookingDetails.sp_id
        }
        val databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val datetime = Date().time
                val userChatReference = databaseReference.child(getString(R.string.users)).child(getUserId(this@UserMyBookingsScreen)).child(getString(R.string.chat)).child(branch)
                userChatReference.child(getString(R.string.chat_branch)).child(branch)
                userChatReference.child(getString(R.string.users_id)).setValue(bookingDetails.sp_id)
                userChatReference.child(getString(R.string.user_name)).setValue(bookingDetails.sp_fname + " " + bookingDetails.sp_lname)
                userChatReference.child(getString(R.string.profile_image)).setValue(bookingDetails.sp_profile_pic)
                userChatReference.child(getString(R.string.last_message)).setValue("No Messages Yet")
                userChatReference.child(getString(R.string.sent_by)).setValue(UserUtils.getUserId(this@UserMyBookingsScreen))
                userChatReference.child(getString(R.string.date_time)).setValue(datetime)

                val spDatabaseReference = databaseReference.child(getString(R.string.users)).child(bookingDetails.sp_id).child(getString(R.string.chat)).child(branch)
                spDatabaseReference.child(getString(R.string.chat_branch)).setValue(branch)
                spDatabaseReference.child(getString(R.string.users_id)).setValue(UserUtils.getUserId(this@UserMyBookingsScreen))
                spDatabaseReference.child(getString(R.string.user_name)).setValue(UserUtils.getUserName(this@UserMyBookingsScreen))
                spDatabaseReference.child(getString(R.string.profile_image)).setValue(UserUtils.getUserProfilePic(this@UserMyBookingsScreen))
                spDatabaseReference.child(getString(R.string.last_message)).setValue("No Messages Yet")
                spDatabaseReference.child(getString(R.string.sent_by)).setValue(getUserId(this@UserMyBookingsScreen))
                spDatabaseReference.child(getString(R.string.date_time)).setValue(datetime)
                val chatsModel = ChatsModel(branch, bookingDetails.sp_profile_pic, bookingDetails.sp_id,
                    bookingDetails.sp_fname + " " + bookingDetails.sp_lname, "","",
                    getUserId(this@UserMyBookingsScreen), datetime)
                UserUtils.selectedChat(this@UserMyBookingsScreen, Gson().toJson(chatsModel))
                startActivity(Intent(this@UserMyBookingsScreen, ChatScreen::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                toast(this@UserMyBookingsScreen, error.message)
            }

        })
    }

    override fun startServiceProviderMessaging(bookingDetails: com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.BookingDetail) {

    }
}