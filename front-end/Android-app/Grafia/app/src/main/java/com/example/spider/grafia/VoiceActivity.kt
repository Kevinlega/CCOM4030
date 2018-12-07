// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : VoiceActivity.kt
// Description : Takes audio and uploads it to server
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_voice.*
import java.io.File
import java.io.IOException
import java.net.URL

class VoiceActivity : AppCompatActivity(){

    // Global varibales
    private var mCurrentVoicePath = String()
    private var userId = -1
    private var projectId = -1
    private var mPlayer: MediaPlayer? = null
    private var recording = false
    private var playing = false
    private var paused = false
    private var myAudioRecorder = MediaRecorder()
    private var projectPath = ""

    // Creates temporary audio file
    private fun createTempVoiceFile(): File {
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

        // record listener
        recordVoice.setOnClickListener {

            recording = true

            if((mCurrentVoicePath != "")){
                val myFile = File(mCurrentVoicePath)
                myFile.delete()
                mCurrentVoicePath = ""
            }
            // checks permissions
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
                // records
                val NotUsed = createTempVoiceFile()
                myAudioRecorder = MediaRecorder()
                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
                myAudioRecorder.setOutputFile(mCurrentVoicePath)


                myAudioRecorder.prepare()
                myAudioRecorder.start()
                recordVoice.isEnabled = false
                playVoice.isEnabled = false
                pauseVoice.isEnabled = false

                Toast.makeText(this@VoiceActivity, "Recording started", Toast.LENGTH_LONG).show()
            }
        }


        // stops recording or play
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

        //plays recorded audio
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

        // pause audio file
        pauseVoice.setOnClickListener {
            if (playing){
                playing = false
                paused = true

                playVoice.isEnabled = true
                recordVoice.isEnabled = true
                mPlayer?.pause()
            }
        }

        // get user data
        userId = intent.getIntExtra("userId",-1)
        projectId = intent.getIntExtra("pId",-1)
        projectPath = intent.getStringExtra("projectPath")
        val name = intent.getStringExtra("projectName")

        // upload to server
        uploadVoice.setOnClickListener {
            UploadFileAsync(projectPath).execute("")
        }

        // back to project view
        backToProject3.setOnClickListener {
            val intent = Intent(this@VoiceActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectName",name)
            finish()
            if((mCurrentVoicePath != "")){
                val myFile = File(mCurrentVoicePath)
                myFile.delete()
                mCurrentVoicePath = ""
            }

            // start your next activity
            startActivity(intent)
        }
    }

    // trigger delete files
    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS))
        }
    }

    // Deletes files
    private fun deleteTempFiles(file: File): Boolean {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    if (f.isDirectory) {
                        deleteTempFiles(f)
                    } else {
                        f.delete()
                    }
                }
            }
        }
        return file.delete()
    }

    // Upload files to server
    private inner class UploadFileAsync(val projectPath: String) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {

            var path = mCurrentVoicePath
            val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
            val name = "VOICE_${userId}_${timeStamp}_.3gp"

            val multipart = Multipart(URL("http://54.81.239.120/fUploadAPI.php"))
            multipart.addFormField("fileType", "1")
            multipart.addFormField("path", (projectPath + "/voice/"))
            multipart.addFilePart("file", path, name, "voice/3gp")

            val bool = multipart.upload()

            if (bool) {
                return "YES"
            } else {
                return "NO"
            }
        }

        override fun onPostExecute(result: String) {

            if (result == "YES") {
                Toast.makeText(this@VoiceActivity, "Uploaded!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@VoiceActivity, "Try Again", Toast.LENGTH_LONG).show()
            }

        }

        override fun onPreExecute() {}

        override fun onProgressUpdate(vararg values: Void) {}
    }
}
