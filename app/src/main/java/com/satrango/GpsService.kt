package com.satrango

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.utils.PermissionUtils
import com.satrango.utils.networkAvailable
import com.satrango.utils.toast

class GpsService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Toast.makeText(this, "Entered into Service", Toast.LENGTH_SHORT).show()

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}