package com.satrango.ui.service_provider.provider_dashboard.alerts

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.FragmentProviderAlertsScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.offers.ProviderOffersScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.RescheduleStatusChangeReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.AlertsInterface
import com.satrango.ui.user.user_dashboard.user_alerts.RegularAlertAdapter
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsAdapter
import com.satrango.utils.PermissionUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class ProviderAlertsScreen : BaseFragment<ProviderAlertsViewModel, FragmentProviderAlertsScreenBinding, ProviderAlertRepository>(), AlertsInterface {

    private val USER_TYPE: String = "SP"
    private val CANCEL: String = "cancel"
    private val ACCEPT_OR_REJECT: String = "accept/reject"
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun getFragmentViewModel(): Class<ProviderAlertsViewModel> = ProviderAlertsViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProviderAlertsScreenBinding = FragmentProviderAlertsScreenBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository(): ProviderAlertRepository = ProviderAlertRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeProgressDialog()

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn)
            .setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
            .setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.notifications)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
        loadProviderAlertsScreen()
    }

    private fun loadProviderAlertsScreen() {

        if (!PermissionUtils.isNetworkConnected(requireContext())) {
            PermissionUtils.connectionAlert(requireContext()) { loadProviderAlertsScreen() }
            return
        }

        loadNotActionableAlerts()

        binding.apply {

            actionNeededBtn.setOnClickListener {
                loadActionableAlerts()
            }

            regularBtn.setOnClickListener {
                loadNotActionableAlerts()
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadNotActionableAlerts() {
        binding.regularBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        binding.regularBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(R.string.white_color)))
        binding.actionNeededBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(
                    R.string.black_color
                )))
        binding.actionNeededBtn.setBackgroundResource(R.drawable.purple_out_line)
        viewModel.getNormalAlerts(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.alertsRV.adapter = RegularAlertAdapter(it.data!!)
                    if (it.data.isEmpty()) {
                        binding.note.visibility = View.VISIBLE
                        binding.note.text = "Alerts not found"
                    } else {
                        binding.regularBadge.text = it.data.size.toString()
                        binding.note.visibility = View.GONE
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    binding.alertsRV.adapter = UserAlertsAdapter(emptyList(),  this)
                    binding.note.visibility = View.VISIBLE
//                    toast(requireContext(), it.message!!)
                }
            }
        }
        updateAlertsToRead("1")
    }

    private fun updateAlertsToRead(alertType: String) {
        viewModel.updateAlertsToRead(requireContext(), alertType).observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.actionNeededBadge, it.message!!)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadActionableAlerts() {
        binding.actionNeededBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        binding.actionNeededBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(
                    R.string.white_color
                )))
        binding.regularBtn.setTextColor(Color.parseColor(requireActivity().resources.getString(R.string.black_color)))
        binding.regularBtn.setBackgroundResource(R.drawable.purple_out_line)
        viewModel.getActionableAlerts(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val data = it.data!!
                    binding.actionNeededBadge.text = it.data.size.toString()
                    if (data.isNotEmpty()) {
                        binding.alertsRV.adapter = UserAlertsAdapter(it.data,  this)
                        if (it.data.isEmpty()) {
                            binding.note.visibility = View.VISIBLE
                            binding.note.text = "Alerts not found"
                        } else {
                            binding.note.visibility = View.GONE
                            binding.alertsRV.visibility = View.VISIBLE
                        }
                    } else {
                        binding.note.visibility = View.VISIBLE
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    binding.alertsRV.adapter = UserAlertsAdapter(emptyList(),  this)
                    binding.note.visibility = View.VISIBLE
                }
            }
        }
        updateAlertsToRead("2")
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(requireActivity(), BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${activity?.packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    override fun rescheduleUserStatusCancelDialog(bookingId: Int, categoryId: Int, userId: Int, rescheduleId: Int) {

    }

    override fun rescheduleUserAcceptRejectDialog(
        bookingId: Int,
        categoryId: Int,
        userId: Int,
        rescheduleId: Int
    ) {
        fetchBookingDetails(bookingId, categoryId, userId, rescheduleId, ACCEPT_OR_REJECT)
    }

    override fun rescheduleSPStatusCancelDialog(bookingId: Int, categoryId: Int, userId: Int, rescheduleId: Int) {
        fetchBookingDetails(bookingId, categoryId, userId, rescheduleId, CANCEL)
    }

    override fun rescheduleSPAcceptRejectDialog(
        bookingId: Int,
        categoryId: Int,
        userId: Int,
        rescheduleId: Int
    ) {

    }

    override fun extraDemandDialog(bookingId: Int, categoryId: Int, userId: Int) {

    }

    override fun divertToInstallmentsScreen(bookingId: String, postJobId: Int) {

    }

    override fun divertToViewBidDetailsScreen(bookingId: String, spId: Int, bidId: Int) {

    }

    override fun divertToOfferScreen() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, ProviderOffersScreen())
            .commit()
    }

    private fun fetchBookingDetails(
        bookingId: Int,
        categoryId: Int,
        userId: Int,
        rescheduleId: Int,
        taskType: String
    ) {
        val requestBody = BookingDetailsReqModel(
            bookingId,
            categoryId,
            RetrofitBuilder.USER_KEY,
            userId
        )
        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        viewModel.viewBookingDetails(requireContext(), requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val response = it.data!!
                    if (taskType == ACCEPT_OR_REJECT) {
                        showRescheduleDialog(bookingId, response, rescheduleId, userId, taskType)
                    } else {
                        showRescheduleStatusDialog(
                            bookingId,
                            response,
                            rescheduleId,
                            userId,
                            taskType
                        )
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(requireContext(), it.message!!)
                }
            }
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
//        val noteText = dialogView.findViewById<TextView>(R.id.noteText)
//        val title = dialogView.findViewById<TextView>(R.id.title)
        val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)

        closeBtn.setOnClickListener { dialog.dismiss() }

        acceptBtn.setOnClickListener {
            rescheduleStatusChangeApiCall(
                bookingId,
                rescheduleId,
                response.booking_details.sp_id.toInt(),
                12,
                userId,
                taskType
            )
            dialog.dismiss()
        }

        rejectBtn.setOnClickListener {
            rescheduleStatusChangeApiCall(
                bookingId,
                rescheduleId,
                response.booking_details.sp_id.toInt(),
                11,
                userId,
                taskType
            )
            dialog.dismiss()
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
        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        val finalStatusId = if (taskType == ACCEPT_OR_REJECT) {
            statusId
        } else {
            25
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
        viewModel.rescheduleStatusChange(requireContext(), requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    snackBar(binding.actionNeededBadge, it.data!!)
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(requireContext(), it.message!!)
                }
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
//        val noteText = dialogView.findViewById<TextView>(R.id.noteText)
//        val title = dialogView.findViewById<TextView>(R.id.title)
        val cancelBtn = dialogView.findViewById<TextView>(R.id.cancelBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)

        closeBtn.setOnClickListener { dialog.dismiss() }

        cancelBtn.setOnClickListener {
            rescheduleStatusChangeApiCall(bookingId, rescheduleId, response.booking_details.sp_id.toInt(), 12, userId, taskType)
        }
    }

}