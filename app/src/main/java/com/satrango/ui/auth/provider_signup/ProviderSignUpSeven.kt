package com.satrango.ui.auth.provider_signup

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Camera
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.satrango.R
import com.satrango.databinding.ActivityProviderSignUpSevenBinding
import com.satrango.ui.provider_dashboard.ProviderDashboard
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProviderSignUpSeven : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var binding: ActivityProviderSignUpSevenBinding

    private lateinit var videoPath: String
    private var mOutputFile: File? = null
    private lateinit var mMediaRecorder: MediaRecorder
    private lateinit var mServiceCamera: Camera
    private lateinit var surfaceHolder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpSevenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.profile)

        surfaceHolder = binding.surfaceView.holder
        surfaceHolder.addCallback(this)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        binding.apply {

            startBtn.setOnClickListener {
                startBtn.setImageResource(R.drawable.ic_baseline_stop_circle_24)
                timer()
                startRecording()
                startBtn.isClickable = false
                restartBtn.isClickable = false
            }

            nextBtn.setOnClickListener {
                stopRecording()
                startActivity(Intent(this@ProviderSignUpSeven, ProviderDashboard::class.java))
            }

        }

    }

    private fun startRecording(): Boolean {
        return try {
            binding.recordImage.visibility = View.VISIBLE
            mServiceCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
            mServiceCamera.setDisplayOrientation(90)
            val params: Camera.Parameters = mServiceCamera.parameters
            mServiceCamera.parameters = params
            val p: Camera.Parameters = mServiceCamera.parameters
            val listPreviewSize = p.supportedPreviewSizes
            val previewSize = listPreviewSize[0]
            p.setPreviewSize(previewSize.width, previewSize.height)
            mServiceCamera.parameters = p
            try {
                mServiceCamera.setPreviewDisplay(surfaceHolder)
                mServiceCamera.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mServiceCamera.unlock()
            mMediaRecorder = MediaRecorder()
            mMediaRecorder.setCamera(mServiceCamera)
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mOutputFile = getOutputFile()
            mOutputFile?.parentFile?.mkdirs()
            mMediaRecorder.setOutputFile(mOutputFile!!.absolutePath)
            mMediaRecorder.setPreviewDisplay(surfaceHolder.surface)
            println("Video PAth>>>>>>  " + mOutputFile!!.absolutePath)
            videoPath = mOutputFile!!.absolutePath
            mMediaRecorder.prepare()
            try {
                mMediaRecorder.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


    private fun timer() {
        var counter = 0
        object : CountDownTimer(31000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.timerText.text = "REC 00:00:" + counter.toString()
                counter++
            }

            override fun onFinish() {
                Toast.makeText(
                    applicationContext,
                    "Press Next button to continue",
                    Toast.LENGTH_SHORT
                ).show()
                binding.startBtn.setImageResource(R.drawable.ic_play_svg)
                stopRecording()
                binding.startBtn.isClickable = true
                binding.restartBtn.isClickable = true
            }
        }.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    private fun getOutputFile(): File {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.ITALY)
        return File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).toString() + "/Video Recorder/RECORDING_" + dateFormat.format(Date()) + ".mp4")
    }

    fun stopRecording() {
        try {
            binding.recordImage.visibility = View.GONE
            binding.timerText.visibility = View.GONE
            mMediaRecorder.stop()
            mMediaRecorder.reset()
            mServiceCamera.stopPreview()
            mMediaRecorder.release()
            mServiceCamera.release()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        Toast.makeText(this, videoPath, Toast.LENGTH_SHORT).show()
    }

}