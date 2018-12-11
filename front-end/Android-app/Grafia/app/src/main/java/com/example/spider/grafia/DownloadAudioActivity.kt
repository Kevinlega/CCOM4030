// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        : DownloadAudioActivity.kt
// Description : Create, download audio notes.
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_download_audio.*
import kotlinx.android.synthetic.main.activity_download_notes.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class DownloadAudioActivity : AppCompatActivity() {

    // Global variables
    var mCurrentPath = ""
    var userId = -1
    var playing = false
    var paused = false
    private var mPlayer: MediaPlayer? = null
    private var location = ""

    // Create Audio TempFile
    private fun createTempFile(): File {
        // Create an audio file name
        val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        return File.createTempFile(
            "VOICE_${userId}_${timeStamp}_", /* prefix */
            ".3gp", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPath = this.absolutePath
        }
    }

    // Triggers delete cache
    override fun onDestroy() {
        super.onDestroy()

        deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS))

    }

    // Deletes Temporary Files
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

    // Method to show an alert dialog with yes, no and cancel button
    private fun showInternetNotification(mContext: Context, intent: Intent){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog


        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(mContext)
        // Set a title for alert dialog
        builder.setTitle("Lost Internet Connection.")

        // Set a message for alert dialog
        builder.setMessage("Do you want to log out or retry?")

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {

                    val Logout = Intent(mContext, LoginActivity::class.java)
                    Logout.putExtra("Failed",true)
                    mContext.startActivity(Logout)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    finish()
                    startActivity(intent)
                }
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Log Out",dialogClickListener)
        // Set the alert dialog negative/no button
        builder.setNegativeButton("Retry",dialogClickListener)
        // Initialize the AlertDialog using builder object
        dialog = builder.create()
        // Finally, display the alert dialog
        dialog.show()
    }

    private fun isNetworkAvailable(mContext: Context): Boolean {
        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_audio)

        supportActionBar!!.title = "Audio"

        // Receive user info
        userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)
        location = intent.getStringExtra("projectPath")
        val name = intent.getStringExtra("projectName")

        if(isNetworkAvailable(this@DownloadAudioActivity))
            DownloadFileAsync().execute("")
        else
            showInternetNotification(this@DownloadAudioActivity,intent)


        BackToProject7.setOnClickListener {
            finish()
            val intent = Intent(this@DownloadAudioActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectName",name)
            // start your next activity
            startActivity(intent)
        }


        // Disable buttons until there is a file to play
        stopVoice2.isEnabled = false
        playVoice2.isEnabled = false
        pauseVoice2.isEnabled = false

        // Stop audio and restarts
        stopVoice2.setOnClickListener {
            if (playing){
                playing = false

                playVoice2.isEnabled = true

                mPlayer?.stop()
            }
        }

        // Plays downloaded voice
        playVoice2.setOnClickListener {

            if (mCurrentPath != "" && !playing) {
                playVoice2.isEnabled = false

                playing = true
                if (!paused) {
                    mPlayer = MediaPlayer().apply {
                        try {
                            setDataSource(mCurrentPath)
                            prepare()
                            start()
                        } catch (e: IOException) {
                        }
                    }
                    Toast.makeText(this@DownloadAudioActivity, "Playing Audio", Toast.LENGTH_LONG).show()
                } else{
                    paused = false
                    mPlayer?.start()
                }
            }
        }

        // Pauses audio
        pauseVoice2.setOnClickListener {
            if (playing){
                playing = false
                paused = true
                playVoice2.isEnabled = true
                mPlayer?.pause()
            }
        }
    }

    // Receives url to a file and a destination path and copies a file to destination.
    private fun download(link: String, path: String) : Boolean {
        val input = URL(link).openStream()
        val output = FileOutputStream(File(path))
        input.use{ _ ->
            output.use { _ ->
                input.copyTo(output)
                return true
            }
        }
        return false
    }

    // Download File from server
    private inner class DownloadFileAsync : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            createTempFile()
            val result = download(
                location,
                mCurrentPath
            )

            if (result) {
                return "YES"
            } else {
                return "NO"
            }

        }

        override fun onPostExecute(result: String) {

            if (result == "YES") {

                stopVoice2.isEnabled = true
                playVoice2.isEnabled = true
                pauseVoice2.isEnabled = true

                // Was file download?
                Toast.makeText(this@DownloadAudioActivity, "Downloaded!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@DownloadAudioActivity, "Cannot Download Now Try Later.", Toast.LENGTH_LONG).show()
            }
        }

        override fun onPreExecute() {}

        override fun onProgressUpdate(vararg values: Void) {}
    }
}
