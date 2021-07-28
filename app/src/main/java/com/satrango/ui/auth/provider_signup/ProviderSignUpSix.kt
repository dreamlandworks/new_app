package com.satrango.ui.auth.provider_signup

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderSignUpSixBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.provider_signup.provider_sign_up_five.ProviderSignUpFiveRepository
import com.satrango.ui.auth.provider_signup.provider_sign_up_five.ProviderSignUpFiveViewModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProviderSignUpSix : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var binding: ActivityProviderSignUpSixBinding

    private lateinit var videoPath: String
    private var mOutputFile: File? = null
    private lateinit var mMediaRecorder: MediaRecorder
    private lateinit var mServiceCamera: Camera
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var progressDialog: ProgressDialog
    private var timerApp: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpSixBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Video Uploading...")

        val factory = ViewModelFactory(ProviderSignUpFiveRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderSignUpFiveViewModel::class.java]

        surfaceHolder = binding.surfaceView.holder
        surfaceHolder.addCallback(this)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        binding.apply {

            var playing = false

            startBtn.setOnClickListener {
                if (playing) {
                    binding.startBtn.setImageResource(R.drawable.ic_play_svg)
                    timerApp?.onFinish()
                    timerApp?.cancel()
                    stopRecording()
                    playing = false
                } else if (!playing) {
                    startBtn.setImageResource(R.drawable.ic_baseline_stop_circle_24)
                    timerApp = null
                    startRecording()
                    playing = true
                }
            }

            restartBtn.setOnClickListener {
                startRecording()
            }

            nextBtn.setOnClickListener {
                stopRecording()

                val videoFile = File(videoPath)
                val userId = RequestBody.create(
                    MultipartBody.FORM,
                    UserUtils.getUserId(this@ProviderSignUpSix)
                )
                val videoNo = RequestBody.create(MultipartBody.FORM, "2")
                val key = RequestBody.create(MultipartBody.FORM, RetrofitBuilder.PROVIDER_KEY)
                viewModel.uploadVideo(
                    this@ProviderSignUpSix,
                    userId,
                    videoNo,
                    key,
                    MultipartBody.Part.createFormData(
                        "video_record",
                        videoFile.name,
                        videoFile.asRequestBody("video/*".toMediaType())
                    )
                ).observe(this@ProviderSignUpSix, {
                    when (it) {
                        is NetworkResponse.Loading -> {
                            progressDialog.show()
                        }
                        is NetworkResponse.Success -> {
                            progressDialog.dismiss()
                            toast(this@ProviderSignUpSix, it.data!!)
                            startActivity(
                                Intent(
                                    this@ProviderSignUpSix,
                                    ProviderSignUpSeven::class.java
                                )
                            )
                        }
                        is NetworkResponse.Failure -> {
                            progressDialog.dismiss()
                            snackBar(nextBtn, it.message!!)
                        }
                    }
                })
            }

            videoPreviewBtn.setOnClickListener {
                surfaceView.visibility = View.GONE
                videoPlayer.setVideoPath(videoPath)
                videoPlayer.start()
                videoPlayer.visibility = View.VISIBLE
            }

        }
    }

    private fun startRecording(): Boolean {
        return try {
            binding.videoPreviewBtn.visibility = View.GONE
            binding.recordImage.visibility = View.VISIBLE
            binding.timerText.visibility = View.VISIBLE
            binding.surfaceView.visibility = View.VISIBLE

            timer()

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
//            println("Video PAth>>>>>>  " + mOutputFile!!.absolutePath)
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
//                Toast.makeText(
//                    applicationContext,
//                    "Press Next button to continue",
//                    Toast.LENGTH_SHORT
//                ).show()
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
            ).toString() + "/Video Recorder/RECORDING_" + dateFormat.format(Date()) + ".mp4"
        )
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

    override fun onPause() {
        super.onPause()
        timerApp?.onFinish()
        timerApp?.cancel()
    }

}