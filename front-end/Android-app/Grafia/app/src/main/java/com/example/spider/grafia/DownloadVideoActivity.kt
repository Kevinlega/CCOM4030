// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : DownloadVideoActivity.kt
// Description : Downloads video from server
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_download_video.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URL
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.support.v7.app.AlertDialog
import android.view.SurfaceView


class DownloadVideoActivity : AppCompatActivity() {

    private var mCurrentPath = ""
    private var userId = -1
    private var restart = true
    private var saved = false
    private var location = ""
    private var downloaded = false

    private fun createTempFile(): File {
        // Create a temporary video file
        val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_MOVIES)

        return File.createTempFile(
            "VIDEO_${userId}_${timeStamp}_", /* prefix */
            ".mp4", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPath = this.absolutePath
        }
    }

    // Triggers Delete
    override fun onDestroy() {
        super.onDestroy()
        deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_MOVIES))
    }

    // Delete temporary files
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

    // Save video to gallery
    private fun galleryAddVideo() = if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
        )
    } else {
        // Save video to gallery

        val retriever = MediaMetadataRetriever()
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this, Uri.fromFile(File(mCurrentPath)))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillisec = time.toLong()
        retriever.release()

        // Save the name and description of a video in a ContentValues map.
        val values = ContentValues(6)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Video.Media.DURATION, timeInMillisec)

        // Add a new record (identified by uri) without the video, but with the values just set.

        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)

        // Now get a handle to the file for that record, and save the data into it.
        try {
            val istream = FileInputStream(mCurrentPath)
            val os = contentResolver.openOutputStream(uri!!)
            val buffer = ByteArray(4096) // tweaking this number may increase performance
            var len = istream.read(buffer)
            while (len != -1) {
                os!!.write(buffer, 0, len)
                len = istream.read(buffer)
            }
            os!!.flush()
            istream.close()
            os.close()
        } catch (e: Exception) {}

        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))

        saved = true
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
                    startActivity(Logout)
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
        setContentView(R.layout.activity_download_video)

        supportActionBar!!.title = "Video"

        videoView2.visibility = View.INVISIBLE

        // Get user data
        userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)
        location = intent.getStringExtra("projectPath")
        val name = intent.getStringExtra("projectName")

        if(isNetworkAvailable(this@DownloadVideoActivity))
            DownloadFileAsync().execute("")
        else
            showInternetNotification(this@DownloadVideoActivity,intent)

        // Segues
        BackToProject8.setOnClickListener {
            // Delete temporary file
            finish()
            val intent = Intent(this@DownloadVideoActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectName",name)
            // start your next activity
            startActivity(intent)
        }


        // play video
        playVideo2.setOnClickListener {
            if (restart) {
                val video = Uri.fromFile(File(mCurrentPath))

                videoView2.setVideoURI(video)
                restart = false
            }
            videoView2.requestFocus()
            videoView2.start()
        }
        // pause video
        pauseVideo2.setOnClickListener {
            videoView2.pause()
        }

        // stop video
        stopVideo2.setOnClickListener {
            videoView2.stopPlayback()
            restart = true
        }

        // save video to gallery
        saveVideo2.setOnClickListener {
            if (mCurrentPath != "" && !saved && downloaded) {
                galleryAddVideo()
                Toast.makeText(this, "Saved to Gallery.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nothing to Save.", Toast.LENGTH_SHORT).show()
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

    // Download file from server
    private inner class DownloadFileAsync : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            createTempFile()
            val result = download(location,mCurrentPath)

            if(result){
                return "YES"
            } else{
                return "NO"
            }

        }

        override fun onPostExecute(result: String) {

            if (result == "YES") {

                videoView2.visibility = View.VISIBLE
                downloaded = true

                Toast.makeText(this@DownloadVideoActivity, "Downloaded!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@DownloadVideoActivity, "Try Again", Toast.LENGTH_LONG).show()
            }
        }
        override fun onPreExecute() {}

        override fun onProgressUpdate(vararg values: Void) {}
    }


}
