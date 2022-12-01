package com.satrango

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.utils.PermissionUtils


class GpsLocationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action!! == "android.location.PROVIDERS_CHANGED") {
            if (PermissionUtils.checkGPSStatus(context!! as Activity)) {
                ProviderDashboard.fetchLocation(context)
                PermissionUtils.dismissGpsDialog()
            }
        }
    }
}