package com.satrango.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.satrango.R

object PermissionUtils {

    private lateinit var gpsAlertDialog: AlertDialog.Builder
    val PERMISSIONS_CODE: Int = 101

    fun connectionAlert(context: Context, task: () -> Unit) {
        AlertDialog.Builder(context).setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again")
            .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                task()
                dialogInterface.dismiss()
                }
            .setIcon(android.R.drawable.ic_dialog_alert).setCancelable(false).show()
    }

    @SuppressLint("ServiceCast")
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    fun checkAndRequestPermissions(context: Activity) {
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

    fun checkGPSStatus(context: Activity): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
        }

        if (!gpsEnabled && !networkEnabled) {
            gpsAlertDialog = AlertDialog.Builder(context)
            gpsAlertDialog.setMessage(R.string.gps_network_not_enabled)
            gpsAlertDialog.setPositiveButton(R.string.open_location_settings) { _, _ ->
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.setNegativeButton(R.string.Cancel) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    checkAndRequestPermissions(context)
                    context.startActivity(context.intent)
                }
            gpsAlertDialog.setCancelable(false)
            gpsAlertDialog.create().show()
            return false
        }
        return true
    }

    fun dismissGpsDialog() {
        gpsAlertDialog.setOnDismissListener { dialogInterface: DialogInterface ->
            dialogInterface.dismiss()
        }
    }

}