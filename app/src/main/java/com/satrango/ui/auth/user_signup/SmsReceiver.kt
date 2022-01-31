package com.satrango.ui.auth.user_signup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class SmsReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent!!.action) {
            val extras = intent.extras
            val status: Status? = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            when (status!!.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    var otp: String
                    val msgs = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                    Toast.makeText(context, msgs.toString(), Toast.LENGTH_SHORT).show()
                }
                CommonStatusCodes.TIMEOUT -> {

                }
            }
        }
    }
}