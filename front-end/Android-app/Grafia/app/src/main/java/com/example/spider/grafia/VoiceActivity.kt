package com.example.spider.grafia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.activity_voice.*
import java.io.File
import java.io.IOException

class VoiceActivity : AppCompatActivity(){

    private var mCurrentVoicePath = String()
    private var userId = -1
    private var projectId = -1
    private var mPlayer: MediaPlayer? = null
    private var recording = false
    private var playing = false
    private var paused = false
    private var myAudioRecorder = MediaRecorder()


    private fun createTempVoiceFile(): File {
        // Create an voice file name
        val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return File.createTempFile(
            "Voice_${userId}_${timeStamp}_", /* prefix */
            ".3gp", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentVoicePath = absolutePath
        }
    }

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate (savedInstanceState)
        setContentView (R.layout.activity_voice)




        recordVoice.setOnClickListener {

            recording = true

            if((mCurrentVoicePath != "")){
                val myFile = File(mCurrentVoicePath)
                myFile.delete()
                mCurrentVoicePath = ""
            }

            val NotUsed = createTempVoiceFile()
            myAudioRecorder = MediaRecorder()
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            myAudioRecorder.setOutputFile(mCurrentVoicePath)

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO), 1
                )
            } else {


                myAudioRecorder.prepare()
                myAudioRecorder.start()
                recordVoice.isEnabled = false
                playVoice.isEnabled = false
                pauseVoice.isEnabled = false

                Toast.makeText(this@VoiceActivity, "Recording started", Toast.LENGTH_LONG).show()
            }
        }

        stopVoice.setOnClickListener {

            if (recording) {
                recording = false
                recordVoice.isEnabled = true
                playVoice.isEnabled = true
                pauseVoice.isEnabled = true

                myAudioRecorder.stop()
                myAudioRecorder.release()
                Toast.makeText(this@VoiceActivity, "Audio Recorded successfully", Toast.LENGTH_LONG).show()
            } else if (playing){
                playing = false

                recordVoice.isEnabled = true
                playVoice.isEnabled = true

                mPlayer?.stop()
            }

        }

        playVoice.setOnClickListener {

            if (mCurrentVoicePath != "" && !playing) {
                recordVoice.isEnabled = false
                playVoice.isEnabled = false

                playing = true
                if (!paused) {
                    mPlayer = MediaPlayer().apply {
                        try {
                            setDataSource(mCurrentVoicePath)
                            prepare()
                            start()
                        } catch (e: IOException) {
                        }
                    }
                    Toast.makeText(this@VoiceActivity, "Playing Audio", Toast.LENGTH_LONG).show()
                } else{
                    paused = false
                    mPlayer?.start()
                }
            }
        }

        pauseVoice.setOnClickListener {
            if (playing){
                playing = false
                paused = true

                playVoice.isEnabled = true
                recordVoice.isEnabled = true
                mPlayer?.pause()
            }
        }
        userId = intent.getIntExtra("userId",-1)
        projectId = intent.getIntExtra("pId",-1)

        backToProject3.setOnClickListener {
            val intent = Intent(this@VoiceActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pid", projectId)

            if((mCurrentVoicePath != "")){
                val myFile = File(mCurrentVoicePath)
                myFile.delete()
                mCurrentVoicePath = ""
            }

            // start your next activity
            startActivity(intent)
        }
    }
}
