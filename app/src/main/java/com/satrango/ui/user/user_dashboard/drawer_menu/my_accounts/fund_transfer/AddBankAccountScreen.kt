package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityAddBankAccountScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.MyAccountRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.MyAccountViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models.AddBankAccountReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class AddBankAccountScreen : AppCompatActivity() {

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityAddBankAccountScreenBinding

    companion object {
        var FROM_PROVIDER = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBankAccountScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgressDialog()

        if (FundTransferScreen.FROM_PROVIDER) {
            binding.imageView.setImageResource(R.drawable.provider_bg)
            binding.submitBtn.setBackgroundResource(R.drawable.provider_btn_bg)
        }

        binding.apply {

            submitBtn.setOnClickListener {
                when {
                    accountHolder.text.toString().trim().isEmpty() -> {
                        snackBar(accountHolder, "Please Enter Account Holder Name")
                    }
                    accountNo.text.toString().trim().isEmpty() -> {
                        snackBar(accountHolder, "Please Enter Account Number")
                    }
                    confirmAccountNo.text.toString().trim().isEmpty() -> {
                        snackBar(accountHolder, "Please Enter Confirm Account Number")
                    }
                    ifscCode.text.toString().trim().isEmpty() -> {
                        snackBar(accountHolder, "Please Enter IFSC Code")
                    }
                    else -> {
                        addAccountNumber()
                    }
                }
            }

        }

    }

    private fun addAccountNumber() {
        val factory = ViewModelFactory(MyAccountRepository())
        val viewModel = ViewModelProvider(this, factory)[MyAccountViewModel::class.java]
        val requestBody = AddBankAccountReqModel(
            binding.accountHolder.text.toString().trim(),
            binding.accountNo.text.toString().trim(),
            binding.ifscCode.text.toString().trim(),
            RetrofitBuilder.USER_KEY,
            UserUtils.getUserId(this).toInt()
        )
        toast(this, Gson().toJson(requestBody))
        viewModel.addBankAccount(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    successDialog()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.accountHolder, it.message!!)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun successDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.payment_success_dialog, null)
        val text = dialogView.findViewById<TextView>(R.id.text)
        val backBtn = dialogView.findViewById<TextView>(R.id.closBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        text.text = "Bank details added successfully"
        backBtn.setOnClickListener {
            startActivity(Intent(this, FundTransferScreen::class.java))
        }
        closeBtn.setOnClickListener {
            startActivity(Intent(this, FundTransferScreen::class.java))
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(
            R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}