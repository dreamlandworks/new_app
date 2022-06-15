package com.satrango.ui.service_provider.provider_dashboard.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.FragmentProviderHomeScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.alerts.ProviderAlertRepository
import com.satrango.ui.service_provider.provider_dashboard.alerts.ProviderAlertsScreen
import com.satrango.ui.service_provider.provider_dashboard.alerts.ProviderAlertsViewModel
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard.Companion.fetchLocation
import com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.LeaderBoardScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.ProviderMyBidsRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.ProviderPlaceBidScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.ProviderMyTrainingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.ProviderMyTrainingViewModel
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.bookings.view_booking_details.models.RescheduleStatusChangeReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.ui.user.user_dashboard.user_alerts.models.Action
import com.satrango.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class ProviderHomeScreen : Fragment() {

    private lateinit var viewModel: ProviderMyTrainingViewModel
    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        lateinit var binding: FragmentProviderHomeScreenBinding
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProviderHomeScreenBinding.inflate(inflater, container, false)

        initializeProgressDialog()

        binding.leaderBoard.setOnClickListener {
            startActivity(Intent(requireContext(), LeaderBoardScreen::class.java))
        }

        val factory = ViewModelFactory(ProviderMyTrainingRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderMyTrainingViewModel::class.java]

        viewModel.getCitiesList("0").observe(requireActivity()) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val cities = it.data!!.data
                    for (city in cities) {
                        if (UserUtils.getCity(requireContext()).lowercase(Locale.getDefault()) == city.city.trim().lowercase(Locale.getDefault())) {
                            loadProviderDashboardDetails(city.id)
                        }
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.bidCount, it.message!!)
                }
            }
        }
        updateUpcomingLayout("Pending")
        updateNewJobs()
        showPendingActionableAlerts()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun updateNewJobs() {
        val requestBody = ProviderBookingReqModel(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(requireContext()).toInt())
        CoroutineScope(Dispatchers.Main).launch {
            val response = RetrofitBuilder.getServiceProviderRetrofitInstance().getBidJobsList(requestBody)
            if (response.status == 200) {
                if (response.job_post_details.isNotEmpty()) {
                    val data = response.job_post_details[0]
                    binding.newJobExpiresIn.text = data.expires_in
                    binding.newJobCostPerHour.text = "Rs.${data.amount}/-"
                    binding.newJobLocation.text = data.job_post_description[0].city
                    binding.newJobDescription.text = data.job_post_description[0].job_description
                    binding.newJobCallBtn.setOnClickListener {
                        UserUtils.makePhoneCall(requireContext(), data.mobile)
                    }
                    binding.newJobsCard.setOnClickListener {
                        ProviderPlaceBidScreen.FROM_AWARDED = false
                        ProviderPlaceBidScreen.FROM_EDIT_BID = false
                        MyJobPostViewScreen.bookingId = data.booking_id.toInt()
                        MyJobPostViewScreen.categoryId = data.category_id.toInt()
                        MyJobPostViewScreen.userId = data.booking_user_id.toInt()
                        UserUtils.savePostJobId(requireContext(), data.post_job_id.toInt())
                        val intent = Intent(binding.root.context, MyJobPostViewScreen::class.java)
                        binding.root.context.startActivity(intent)
                    }
                    binding.newJobsLayout.visibility = View.VISIBLE
                    binding.newJobsCard.visibility = View.VISIBLE
                } else {
                    binding.noNewJobs.visibility = View.VISIBLE
                    binding.rightArrowTwo.visibility = View.GONE
                }
            } else {
                binding.noNewJobs.visibility = View.VISIBLE
                binding.rightArrowTwo.visibility = View.GONE
//                toast(requireContext(), Gson().toJson(response.message))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUpcomingLayout(status: String) {
        val factory = ViewModelFactory(ProviderBookingRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
        val requestBody = ProviderBookingReqModel(RetrofitBuilder.PROVIDER_KEY, UserUtils.getUserId(requireContext()).toInt())
        viewModel.bookingListWithDetails(requireContext(), requestBody).observe(requireActivity()) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val list = ArrayList<com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.BookingDetail>()
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
                    if (list.isNotEmpty()) {
                        val data = list[0]
                        binding.scheduleIn.text = data.scheduled_date
                        binding.location.text = data.details[0].city
                        binding.description.text = data.details[0].job_description
                        binding.costPerHour.text = "Rs.${data.amount}/-"
                        binding.callBtn.setOnClickListener {
                            UserUtils.makePhoneCall(requireContext(), data.mobile)
                        }
                        binding.upcomingCard.setOnClickListener {
                            val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen:: class.java)
                            UserUtils.spid = data.sp_id
                            intent.putExtra(binding.root.context.getString(R.string.user_id), data.users_id)
                            intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                            intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                            ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                            binding.root.context.startActivity(intent)
                        }
                        binding.upcomingCard.visibility = View.VISIBLE
                        binding.upcomingLayout.visibility = View.VISIBLE
                    } else {
                        binding.noUpcomingBookings.visibility = View.VISIBLE
                        binding.rightArrowOne.visibility = View.GONE
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    binding.noUpcomingBookings.visibility = View.VISIBLE
                    binding.rightArrowOne.visibility = View.GONE
//                    snackBar(binding.bidCount, it.message!!)
                }
            }
        }
    }

    private fun showPendingActionableAlerts() {
        val factory = ViewModelFactory(ProviderAlertRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderAlertsViewModel::class.java]
        viewModel.getActionableAlerts(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    if (it.data!!.isNotEmpty()) {
                        for (data in it.data) {
                            if (data.type_id == "9" && data.status == "2") {
                                showPendingActionableAlertsDialog(data)
                            }
                        }
                    }
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                }
            }
        }
    }

    private fun showPendingActionableAlertsDialog(data: Action) {
        val dialog = Dialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.show_pending_actionable_alerts_dialog, null)
        val profilePic = dialogView.findViewById<CircleImageView>(R.id.profilePic)
        val description = dialogView.findViewById<TextView>(R.id.description)
        val skipBtn = dialogView.findViewById<TextView>(R.id.skipBtn)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
        Glide.with(requireContext()).load(data.profile_pic).error(R.drawable.images).into(profilePic)
        description.text = data.description
        title.setTextColor(resources.getColor(R.color.purple_500))
        acceptBtn.setBackgroundColor(resources.getColor(R.color.purple_500))
        skipBtn.setOnClickListener { dialog.dismiss() }
        acceptBtn.setOnClickListener {
            dialog.dismiss()
            rescheduleStatusChangeApiCall(
                data.booking_id.toInt(),
                data.reschedule_id.toInt(),
                data.sp_id.toInt(),
                12,
                data.user_id.toInt()
            )
        }
        rejectBtn.setOnClickListener {
            dialog.dismiss()
            rescheduleStatusChangeApiCall(
                data.booking_id.toInt(),
                data.reschedule_id.toInt(),
                data.sp_id.toInt(),
                11,
                data.user_id.toInt()
            )
        }
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private fun rescheduleStatusChangeApiCall(
        bookingId: Int,
        rescheduleId: Int,
        spId: Int,
        statusId: Int,
        userId: Int
    ) {
        val requestBody = RescheduleStatusChangeReqModel(
            bookingId,
            RetrofitBuilder.USER_KEY,
            rescheduleId,
            spId,
            statusId,
            ProviderAlertsScreen.USER_TYPE,
            userId
        )
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitBuilder.getUserRetrofitInstance().updateRescheduleStatus(requestBody)
                val jsonResponse = JSONObject(response.string())
                if (jsonResponse.getInt("status") == 200) {
                    toast(requireContext(), jsonResponse.getString("message"))

                } else {
                    toast(requireContext(), jsonResponse.getString("message"))
                }
            } catch (e: Exception) {
                toast(requireContext(), e.message!!)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadProviderDashboardDetails(cityId: String) {
        viewModel.providerDashboardDetails(requireContext(), cityId).observe(requireActivity()) {
            when (it) {
                is NetworkResponse.Loading -> {
//                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val data = it.data!!
//                    toast(requireContext(), Gson().toJson(data))
                    binding.bookingCount.text = "${data.bookings_completed}/${data.total_bookings}"
                    binding.bidCount.text = "${data.bids_awarded}/${data.total_bids}"
                    binding.earningText.text = data.earnings
                    binding.commissionCount.text = data.commission
                    binding.myRank.text = "#${data.sp_rank}"
                    binding.ratingCount.text = data.sp_rating
                    binding.pointsCount.text = data.sp_points
                    binding.cName.text = data.competitor_name
                    binding.cRank.text = data.competitor_rank
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.bidCount, it.message!!)
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (PermissionUtils.checkGPSStatus(requireActivity()) && networkAvailable(requireContext())) {
            fetchLocation(requireContext())
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(requireActivity(), BeautifulProgressDialog.withGIF, resources.getString(
            R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${activity?.packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}