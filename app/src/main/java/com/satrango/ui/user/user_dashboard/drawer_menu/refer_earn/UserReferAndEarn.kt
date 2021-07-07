package com.satrango.ui.user.user_dashboard.drawer_menu.refer_earn

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.satrango.R
import com.satrango.databinding.ActivityUserReferAndEarnBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.utils.UserUtils
import com.satrango.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserReferAndEarn : AppCompatActivity() {

    private lateinit var binding: ActivityUserReferAndEarnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserReferAndEarnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.refer_amp_earn)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE

        binding.apply {
            applyBtn.setOnClickListener { createReferLink() }


        }

    }

    private fun createReferLink() {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("http://dev.satrango.com/userid=${UserUtils.getUserId(this@UserReferAndEarn)}")
            domainUriPrefix = "https://satrango.page.link"
            androidParameters {
                this.build()
            }
        }

        val dynamicLinkUri = dynamicLink.uri
        val shortLinkTask: Task<ShortDynamicLink> =
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLinkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        val shortLink: Uri? = task.result?.shortLink
                        val flowchartLink: Uri? = task.result?.previewLink
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                        intent.type = "text/plain"
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@UserReferAndEarn, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
    }

}