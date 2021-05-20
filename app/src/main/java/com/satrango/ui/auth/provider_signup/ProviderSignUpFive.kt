package com.satrango.ui.auth.provider_signup

import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.satrango.R
import com.satrango.databinding.ActivityProviderSignUpFiveBinding

class ProviderSignUpFive : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var binding: ActivityProviderSignUpFiveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpFiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.profile)

        com.satrangolimitless.Vendor_UI.vendor_profile.VendorProfilFourActivity.mSurfaceHolder =
            com.satrangolimitless.Vendor_UI.vendor_profile.VendorProfilFourActivity.mSurfaceView.getHolder()
        com.satrangolimitless.Vendor_UI.vendor_profile.VendorProfilFourActivity.mSurfaceHolder.addCallback(
            this
        )
        binding.surfaceView.setType(
            SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS
        )


    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }
}