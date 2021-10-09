package com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityComplaintScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesInterface
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.SettingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.SettingsViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models.ComplaintReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ComplaintScreen : AppCompatActivity(), BrowseCategoriesInterface {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var moduleNames: java.util.ArrayList<BrowserCategoryModel>
    private lateinit var binding: ActivityComplaintScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        var FROM_PROVIDER = false
        var bookingId = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        val factory = ViewModelFactory(SettingsRepository())
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
        viewModel.complaintModules(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val modules = it.data!!
                    moduleNames = ArrayList()
                    for (module in modules) {
                        moduleNames.add(BrowserCategoryModel(module.module_name, module.id, "", false))
                    }
                    binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
                    binding.recyclerView.adapter = ComplaintsAdapter(moduleNames, this)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }

            }
        })

        binding.resetBtn.setOnClickListener {
            finish()
            startActivity(intent)
        }

        binding.submitBtn.setOnClickListener {

            if (binding.complaint.text.toString().isEmpty()) {
                snackBar(binding.recyclerView, "Enter Your Complaint")
            } else {
                var selectedCount = 0
                moduleNames.forEachIndexed { index, browserCategoryModel ->
                    if (browserCategoryModel.selected) {
                        selectedCount += 1
                    }
                }
                if (selectedCount > 0) {
                    submitComplaintToServer()
                } else {
                    snackBar(binding.recyclerView, "Select Category")
                }
            }

        }


    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.raise_a_complaint)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE
        if (FROM_PROVIDER) {
            toolBar.setBackgroundResource(R.color.purple_500)
            binding.resetBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.resetBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.submitBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.complaintBox.boxStrokeColor = resources.getColor(R.color.purple_500)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun submitComplaintToServer() {

        var moduleId = 0
        moduleNames.forEach { browserCategoryModel ->
            if (browserCategoryModel.selected) {
                moduleId = browserCategoryModel.id.toInt()
            }
        }

        val requestBody = ComplaintReqModel(
            SimpleDateFormat("yyyy-MM-dd").format(Date()),
            binding.complaint.text.toString().trim(),
            RetrofitBuilder.USER_KEY,
            moduleId,
            UserUtils.getUserId(this),
            0
        )

        viewModel.postComplaint(this, requestBody).observe(this, {

            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    binding.complaint.setText("")
                    snackBar(binding.submitBtn, it.data!!.message)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.recyclerView, it.message!!)
                }

            }


        })

    }

    override fun selectedCategory(categoryId: String, position: Int) {
        val tempList = ArrayList<BrowserCategoryModel>()
        moduleNames.forEachIndexed { index, browserCategoryModel ->
            if (position == index) {
                tempList.add(
                    BrowserCategoryModel(
                        browserCategoryModel.category,
                        browserCategoryModel.id,
                        browserCategoryModel.image,
                        true
                    )
                )
            } else {
                tempList.add(
                    BrowserCategoryModel(
                        browserCategoryModel.category,
                        browserCategoryModel.id,
                        browserCategoryModel.image,
                        false
                    )
                )
            }
        }
        if (position == moduleNames.size - 1) {
            binding.recyclerView.scrollToPosition(moduleNames.size - 1)
        }
        moduleNames = tempList
        binding.recyclerView.adapter = ComplaintsAdapter(tempList, this)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withImage, resources.getString(R.string.loading))
        progressDialog.setImageLocation(resources.getDrawable(R.drawable.circlelogo))
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
    }
}