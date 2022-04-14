package com.satrango.base

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.satrango.R
import com.satrango.utils.UserUtils


class MyApp: Application(), LifecycleObserver {

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url)).child(getString(R.string.users)).child(UserUtils.getPhoneNo(this))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("MyApp", "App in background")
        databaseReference.child(getString(R.string.online_status)).setValue(getString(R.string.offline))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {
        Log.d("MyApp", "App in foreground")
        databaseReference.child(getString(R.string.online_status)).setValue(getString(R.string.online))
    }

}