package com.satrango.ui.user.bookings.raise_ticket

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.satrango.R
import com.satrango.databinding.ActivityRaiseTicketScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.raise_ticket.models.RaiseTicketReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.FundTransferScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests.ComplaintRequestScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RaiseTicketScreen : AppCompatActivity() {

    private lateinit var binding: ActivityRaiseTicketScreenBinding

    companion object {
        var bookingId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaiseTicketScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.raise_support)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        profilePic.visibility = View.GONE

        val text = "I have complaint regarding my Booking ID: $bookingId. <br>My complaint is :-"
        binding.complaint.setText(Html.fromHtml(text))

        binding.submitBtn.setOnClickListener {
            submitTicket()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun successDialog(complaintId: String) {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.payment_success_dialog, null)
        val text = dialogView.findViewById<TextView>(R.id.text)
        val backBtn = dialogView.findViewById<TextView>(R.id.closBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        text.text = "Your ticket was raised and your complaint id is $complaintId."
        backBtn.setOnClickListener {
            startActivity(Intent(this, ComplaintRequestScreen::class.java))
        }
        closeBtn.setOnClickListener {
            startActivity(Intent(this, ComplaintRequestScreen::class.java))
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun submitTicket() {
        val userType = if (isProvider(this)) {
            2
        } else {
            1
        }
        CoroutineScope(Dispatchers.Main).launch {
            val requestBody = RaiseTicketReqModel(
                bookingId.toInt(),
                SimpleDateFormat("yyyy-mm-dd").format(Date()),
                binding.complaint.text.toString().trim(),
                RetrofitBuilder.USER_KEY,
                1,
                userType,
                UserUtils.getUserId(this@RaiseTicketScreen)
            )
            val response = RetrofitBuilder.getUserRetrofitInstance().postComplaints(requestBody)
            if (response.status == 200) {
                binding.complaint.text = null
                successDialog(response.complaint_id.toString())
            } else {
                snackBar(binding.complaint, response.message)
            }
        }
    }
}