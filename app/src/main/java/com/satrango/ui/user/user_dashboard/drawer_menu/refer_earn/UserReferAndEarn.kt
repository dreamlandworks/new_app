package com.satrango.ui.user.user_dashboard.drawer_menu.refer_earn

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.ClipboardManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.freshchat.consumer.sdk.beans.User
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.satrango.R
import com.satrango.databinding.ActivityUserReferAndEarnBinding
import com.satrango.utils.UserUtils
import com.satrango.utils.toast


class UserReferAndEarn : AppCompatActivity() {

    private lateinit var binding: ActivityUserReferAndEarnBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserReferAndEarnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        createReferLink()

        binding.apply {
            applyBtn.setOnClickListener { shareReferLink() }
            backBtn.setOnClickListener { onBackPressed() }
            share.setOnClickListener { shareReferLink() }
            copyLink.setOnClickListener {
                setClipboard(this@UserReferAndEarn, UserUtils.getReferralLink(this@UserReferAndEarn))
            }
        }

    }

    private fun shareReferLink() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, UserUtils.getReferralLink(this@UserReferAndEarn))
        intent.type = "text/plain"
        startActivity(intent)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setClipboard(context: Context, text: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = text
        } else {
            val clipboard =
                context.getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
        }
        toast(context,"Copied")
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.refer_amp_earn)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE
    }

    private fun createReferLink() {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("http://dev.satrango.com/userid=${UserUtils.getUserId(this@UserReferAndEarn)}")
            domainUriPrefix = "https://squill.page.link"
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
                        UserUtils.saveReferralLink(this, shortLink.toString())
                        binding.referralLink.setText(UserUtils.getReferralLink(this@UserReferAndEarn))
                        val flowchartLink: Uri? = task.result?.previewLink
                    } else {
                        Toast.makeText(this@UserReferAndEarn, "Error: ${task.exception}", Toast.LENGTH_SHORT).show()
                    }
                }
    }

}