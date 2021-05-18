package com.satrango.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.satrango.R

object PermissionUtils {

    val PERMISSIONS_CODE: Int = 101

    fun checkAndRequestPermissions(context: Context) {
        when {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestForAllPermissions(context)
            }
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestForAllPermissions(context)
            }
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestForAllPermissions(context)
            }
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestForAllPermissions(context)
            }
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestForAllPermissions(context)
            }
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestForAllPermissions(context)
            }
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestForAllPermissions(context)
            }
            else -> {
                checkGPSStatus(context)
            }
        }
    }

    private fun requestForAllPermissions(context: Context) {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        ActivityCompat.requestPermissions(context as Activity, permissions, PERMISSIONS_CODE)
    }

    private fun checkGPSStatus(context: Context) {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (e: Exception) {
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (e: Exception) {
        }

        if (!gpsEnabled && !networkEnabled) {
            AlertDialog.Builder(context)
                .setMessage(R.string.gps_network_not_enabled)
                .setPositiveButton(R.string.open_location_settings) { _, _ ->
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.setNegativeButton(R.string.Cancel) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    checkAndRequestPermissions(context)
                }
                .setCancelable(false)
                .show()
        }
    }

}