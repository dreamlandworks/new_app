package com.satrango.ui.user.user_dashboard.user_alerts

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentUserAlertScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ChangeExtraDemandStatusReqModel
import com.satrango.ui.user.bookings.view_booking_details.installments_request.UserInstallmentsRequestScreen
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.RescheduleStatusChangeReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.ViewBidDetailsScreen
import com.satrango.ui.user.user_dashboard.user_offers.UserOffersScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.loadProfileImage
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class UserAlertScreen :
    BaseFragment<UserAlertsViewModel, FragmentUserAlertScreenBinding, UserAlertsRepository>(),
    AlertsInterface {

    private val USER_TYPE: String = "User"
    private val ACCEPT_OR_REJECT: String = "accept/reject"
    private val CANCEL: String = "cancel"

    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeToolBar()
        initializeProgressDialog()
        loadUserAlertsScreen()

    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.alerts)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
    }

    private fun loadUserAlertsScreen() {

        if (!PermissionUtils.isNetworkConnected(requireContext())) {
            PermissionUtils.connectionAlert(requireContext()) { loadUserAlertsScreen() }
            return
        }

        loadNotActionableAlerts()

        binding.actionNeededBtn.setOnClickListener {
            loadActionableAlerts()
        }

        binding.regularBtn.setOnClickListener {
            loadNotActionableAlerts()
        }
    }

    private fun updateAlertsToRead(userType: String, lastAlertId: String) {
        viewModel.updateAlertsToRead(requireContext(), userType, lastAlertId).observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
//                    toast(requireContext(), it.data!!)
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            requireActivity(),
            BeautifulProgressDialog.withGIF,
            resources.getString(R.string.loading)
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${activity?.packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    @SuppressLint("SetTextI18n")
    private fun loadNotActionableAlerts() {
        binding.regularBtn.setBackgroundResource(R.drawable.category_bg)
        binding.regularBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(R.string.white_color)))
        binding.actionNeededBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(
                    R.string.black_color
                )))
        binding.actionNeededBtn.setBackgroundResource(R.drawable.blue_out_line)
        viewModel.getNormalAlerts(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.note.visibility = View.GONE
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    if (it.data!!.isNotEmpty()) {
                        binding.note.visibility = View.GONE
                        binding.alertsRV.adapter = RegularAlertAdapter(it.data)
                        binding.regularBadge.text = it.data.size.toString()
                        updateAlertsToRead("1", it.data.last().id)
                    } else {
                        binding.regularBadge.visibility = View.GONE
                        binding.alertsRV.adapter = UserAlertsAdapter(emptyList(), this)
                        binding.note.text = "Regular Alerts are empty"
                        binding.note.visibility = View.VISIBLE
                    }
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    binding.note.visibility = View.VISIBLE
                    binding.note.text = it.message!!
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadActionableAlerts() {
        binding.actionNeededBtn.setBackgroundResource(R.drawable.category_bg)
        binding.actionNeededBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(
                    R.string.white_color
                )))
        binding.regularBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(R.string.black_color)))
        binding.regularBtn.setBackgroundResource(R.drawable.blue_out_line)
        viewModel.getActionableAlerts(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.note.visibility = View.GONE
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    if (it.data!!.isNotEmpty()) {
                        binding.alertsRV.adapter = UserAlertsAdapter(it.data, this)
                        binding.actionNeededBadge.text = it.data.size.toString()
                        binding.note.visibility = View.GONE
                    } else {
                        binding.actionNeededBadge.visibility = View.GONE
                        binding.alertsRV.adapter =
                            UserAlertsAdapter(emptyList(), this)
                        binding.note.visibility = View.VISIBLE
                        binding.note.text = "Actionable Alerts are empty"
                    }
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    binding.note.visibility = View.VISIBLE
                    binding.note.text = it.message!!
                }
            }
        }
    }

    override fun getFragmentViewModel(): Class<UserAlertsViewModel> = UserAlertsViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserAlertScreenBinding =
        FragmentUserAlertScreenBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository(): UserAlertsRepository = UserAlertsRepository()

    override fun rescheduleUserStatusCancelDialog(
        bookingId: Int,
        categoryId: Int,
        userId: Int,
        rescheduleId: Int,
        description: String
    ) {
        fetchBookingDetails(bookingId, categoryId, userId, rescheduleId, CANCEL)
    }

    override fun rescheduleUserAcceptRejectDialog(
        bookingId: Int,
        categoryId: Int,
        userId: Int,
        rescheduleId: Int,
        description: String
    ) {
        fetchBookingDetails(bookingId, categoryId, userId, rescheduleId, ACCEPT_OR_REJECT)
    }

    private fun fetchBookingDetails(
        bookingId: Int,
        categoryId: Int,
        userId: Int,
        rescheduleId: Int,
        taskType: String
    ) {
        val requestBody = BookingDetailsReqModel(bookingId, categoryId, RetrofitBuilder.USER_KEY, userId)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                progressDialog.show()
                val response = RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(requestBody)
                if (response.status == 200)  {
                    progressDialog.dismiss()
                    if (taskType == ACCEPT_OR_REJECT) {
                        showRescheduleDialog(bookingId, response, rescheduleId, userId, taskType)
                    } else {
                        showRescheduleStatusDialog(bookingId, response, rescheduleId, userId, taskType)
                    }
                } else {
                    progressDialog.dismiss()
                    toast(requireContext(), response.message)
                }
            } catch (e: Exception) {
                toast(requireContext(), e.message!!)
            }
        }
    }

    private fun showRescheduleStatusDialog(
        bookingId: Int,
        response: BookingDetailsResModel,
        rescheduleId: Int,
        userId: Int,
        taskType: String
    ) {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.reschedule_status_change_dialog, null)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val cancelBtn = dialogView.findViewById<TextView>(R.id.cancelBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)

        title.setTextColor(resources.getColor(R.color.blue))
        cancelBtn.setTextColor(resources.getColor(R.color.blue))
        cancelBtn.setBackgroundResource(R.drawable.blue_out_line)
        closeBtn.setOnClickListener { dialog.dismiss() }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
            rescheduleStatusChangeApiCall(
                bookingId,
                rescheduleId,
                response.booking_details.sp_id.toInt(),
                12,
                userId,
                taskType
            )
        }
    }

    private fun showRescheduleDialog(
        bookingId: Int,
        response: BookingDetailsResModel,
        rescheduleId: Int,
        userId: Int,
        taskType: String
    ) {
        val dialog = BottomSheetDialog(requireContext())
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

        closeBtn.setOnClickListener { dialog.dismiss() }

        acceptBtn.setOnClickListener {
            dialog.dismiss()
            rescheduleStatusChangeApiCall(
                bookingId,
                rescheduleId,
                response.booking_details.sp_id.toInt(),
                12,
                userId,
                taskType
            )
        }

        rejectBtn.setOnClickListener {
            dialog.dismiss()
            rescheduleStatusChangeApiCall(
                bookingId,
                rescheduleId,
                response.booking_details.sp_id.toInt(),
                11,
                userId,
                taskType
            )
        }

        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun rescheduleStatusChangeApiCall(
        bookingId: Int,
        rescheduleId: Int,
        spId: Int,
        statusId: Int,
        userId: Int,
        taskType: String
    ) {
        val finalStatusId = if (taskType == ACCEPT_OR_REJECT) {
            statusId
        } else {
            24
        }
        val requestBody = RescheduleStatusChangeReqModel(
            bookingId,
            RetrofitBuilder.USER_KEY,
            rescheduleId,
            spId,
            finalStatusId,
            USER_TYPE,
            userId
        )
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitBuilder.getUserRetrofitInstance().updateRescheduleStatus(requestBody)
                val jsonResponse = JSONObject(response.string())
                if (jsonResponse.getInt("status") == 200) {
                    toast(requireContext(), jsonResponse.getString("message"))
                    loadActionableAlerts()
                } else {
                    toast(requireContext(), jsonResponse.getString("message"))
                }
            } catch (e: Exception) {
                toast(requireContext(), e.message!!)
            }
        }
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
        val requestBody = BookingDetailsReqModel(
            bookingId,
            categoryId,
            RetrofitBuilder.USER_KEY,
            userId
        )
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response =
                    RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(requestBody)
                progressDialog.show()
                if (response.status == 200) {
                    progressDialog.dismiss()
                    showExtraDemandAcceptDialog(
                        bookingId,
                        response.booking_details.material_advance,
                        response.booking_details.technician_charges,
                        response.booking_details.extra_demand_total_amount,
                        progressDialog
                    )
                } else {
                    progressDialog.dismiss()
                    toast(requireContext(), response.message)
                }
            } catch (e: Exception) {
                toast(requireContext(), e.message!!)
            }
        }
    }

    override fun divertToInstallmentsScreen(bookingId: String, postJobId: Int) {
        UserInstallmentsRequestScreen.postJobId = postJobId
        startActivity(Intent(requireContext(), UserInstallmentsRequestScreen::class.java))
    }

    override fun divertToViewBidDetailsScreen(bookingId: String, spId: Int, bidId: Int) {
        ViewBidDetailsScreen.spId = spId
        ViewBidDetailsScreen.bidId = bidId
        startActivity(Intent(requireContext(), ViewBidDetailsScreen::class.java))
    }

    override fun divertToOfferScreen() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, UserOffersScreen())
            .commit()
    }

    private fun showExtraDemandAcceptDialog(
        bookingId: Int,
        materialAdvance: String,
        technicalCharges: String,
        extraDemandTotalAmount: String,
        progressDialog: BeautifulProgressDialog
    ) {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.provider_extra_demand_accept_dialog, null)
        val materialCharges = dialogView.findViewById<TextView>(R.id.materialCharges)
        val technicianCharges = dialogView.findViewById<TextView>(R.id.technicianCharges)
        val totalCost = dialogView.findViewById<TextView>(R.id.totalCost)
        val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)

        materialCharges.text = materialAdvance
        technicianCharges.text = technicalCharges
        totalCost.text = extraDemandTotalAmount

        closeBtn.setOnClickListener { dialog.dismiss() }

        acceptBtn.setOnClickListener {
            dialog.dismiss()
            changeExtraDemandStatus(bookingId, 2, dialog, progressDialog)
        }

        rejectBtn.setOnClickListener {
            dialog.dismiss()
            changeExtraDemandStatus(bookingId, 1, dialog, progressDialog)
        }

        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun changeExtraDemandStatus(
        bookingId: Int,
        status: Int,
        dialog: BottomSheetDialog,
        progressDialog: BeautifulProgressDialog
    ) {
        val requestBody =
            ChangeExtraDemandStatusReqModel(bookingId, RetrofitBuilder.USER_KEY, status)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                progressDialog.show()
                val response =
                    RetrofitBuilder.getUserRetrofitInstance().changeExtraDemandStatus(requestBody)
                val jsonResponse = JSONObject(response.string())
                if (jsonResponse.getInt("status") == 200) {
                    dialog.dismiss()
                    progressDialog.dismiss()
                } else {
                    dialog.dismiss()
                    progressDialog.dismiss()
                    toast(requireContext(), jsonResponse.getString("status_message"))
                }
            } catch (e: Exception) {
                toast(requireContext(), e.message!!)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isProvider(requireContext(), fromProvider = false)
    }

}