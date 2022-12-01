package com.satrango.base

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.satrango.R
import com.satrango.utils.UserUtils
import com.zoho.commons.Fonts
import com.zoho.commons.InitConfig
import com.zoho.livechat.android.listeners.InitListener
import com.zoho.salesiqembed.ZohoSalesIQ


class MyApp: Application(), LifecycleObserver {

    companion object {
        lateinit var instance: MyApp
    }

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate() {
        super.onCreate()
        instance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        FirebaseApp.initializeApp(this)
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url)).child(getString(R.string.users))
//        ZohoSalesIQ.init(this, "ZqUGlb8vQgTD%2B2nQ29l8D8omc3VIw5JY7e%2F5zKbaWIlaHOWhQkgKjONc01%2F8BrdODDQan%2BMS85znVdDi3X4fqb0Jg1lKFfev_in", "xfuLcMoNAd0Fsx6u3Jr7h57LR6dqWDHACHgTdHZkQpexceH4uHQFBbtcHz%2Bw2zHOU5MY68vPnvd04ktPS8%2Bq%2Bo%2BUjY93hdvudrkbCbaAfxY66iHDOw81Yi6tqHFhW0wU4mSp9seXEj%2FtjLNtagh%2BvuXCkQb0krleRxTIyYyhFJwKn9iU1KaanHoXu8Z4WViD")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
//        Log.d("MyApp", "App in background")
        databaseReference.child(UserUtils.getUserId(this)).child(getString(R.string.online_status)).setValue(getString(R.string.offline))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {
//        Log.d("MyApp", "App in foreground")
        databaseReference.child(UserUtils.getUserId(this)).child(getString(R.string.online_status)).setValue(getString(R.string.online))
    }

    fun getInstance(): MyApp {
        return instance
    }

    override fun attachBaseContext(base: Context?) {
        MultiDex.install(this)
        super.attachBaseContext(base)
    }

}