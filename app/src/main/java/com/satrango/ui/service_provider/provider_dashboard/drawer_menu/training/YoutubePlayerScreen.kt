package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityYoutubePlayerScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.RecentVideo
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.RecommendedVideo
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.WatchedVideo
import com.satrango.utils.toast
import java.util.regex.Pattern

class YoutubePlayerScreen : AppCompatActivity() {

    private lateinit var binding: ActivityYoutubePlayerScreenBinding

    companion object {
        var recentVideoDetails: RecentVideo? = null
        var watchedVideoDetails: WatchedVideo? = null
        var recommendedVideoDetails: RecommendedVideo? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubePlayerScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when {
            recentVideoDetails != null -> {
                playVideo(recentVideoDetails!!.url, recentVideoDetails!!.id, recentVideoDetails!!.points)
            }
            watchedVideoDetails != null -> {
                playVideo(watchedVideoDetails!!.url, watchedVideoDetails!!.id, watchedVideoDetails!!.points)
            }
            recommendedVideoDetails != null -> {
                playVideo(recommendedVideoDetails!!.url, recommendedVideoDetails!!.id, recommendedVideoDetails!!.points)
            }
        }
    }

    private fun playVideo(videoUrl: String, videoId: String, points: String) {
        val youtubeVideoId = extractYouTubeId(videoUrl)
        if (youtubeVideoId == "error") {
            toast(this, "Something went wrong, Please Try again!")
            onBackPressed()
        } else {
            lifecycle.addObserver(binding.youtubePlayer)
            binding.youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(youtubeVideoId, 0f)
                    updateYoutubeVideoPoints(videoId, points)
                }
            })
        }
    }

    private fun updateYoutubeVideoPoints(videoId: String, points: String) {
        val factory = ViewModelFactory(ProviderMyTrainingRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderMyTrainingViewModel::class.java]
        viewModel.submitYoutubePoints(this, videoId, points).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {

                }
                is NetworkResponse.Success -> {
                    toast(this, it.data!!)
                }
                is NetworkResponse.Failure -> {
                    toast(this, it.message!!)
                }
            }
        })
    }


    private fun extractYouTubeId(youTubeUrl: String): String {
        val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(youTubeUrl)
        return if (matcher.find()) {
            matcher.group()
        } else {
            "error"
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ProviderMyTrainingScreen::class.java))
    }
}