package com.example.spider.grafia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_voice.*
import java.io.File
import java.io.IOException

class VoiceActivity : AppCompatActivity(){

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate (savedInstanceState)
        setContentView (R.layout.activity_voice)


        val outputFile = Environment.getExternalStorageDirectory().absolutePath + "/recording.3gp"
        println(outputFile)


        val myAudioRecorder = MediaRecorder()
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        myAudioRecorder.setOutputFile(outputFile)
        playVoice.isEnabled = false
        stopVoice.isEnabled = false

        recordVoice.setOnClickListener {

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
                stopVoice.isEnabled = true

                Toast.makeText(this@VoiceActivity, "Recording started", Toast.LENGTH_LONG).show()
            }
        }

        stopVoice.setOnClickListener {
            recordVoice.isEnabled = false
            stopVoice.isEnabled = true
            playVoice.isEnabled = true
            myAudioRecorder.stop()
            myAudioRecorder.release()

            Toast.makeText(this@VoiceActivity, "Audio Recorded successfully", Toast.LENGTH_LONG).show()
        }

//        playVoice.setOnClickListener {
//            val mediaPlayer = MediaPlayer()
//            mediaPlayer.setDataSource(outputFile);
//            mediaPlayer.prepare()
//            mediaPlayer.start()
//            Toast.makeText(this@VoiceActivity, "Playing Audio", Toast.LENGTH_LONG).show();
//        }

        val userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)

        backToProject3.setOnClickListener {
            val intent = Intent(this@VoiceActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pid", projectId)
            // start your next activity
            startActivity(intent)
        }

    }
}
