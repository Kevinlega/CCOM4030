package com.example.spider.grafia

import android.Manifest
import android.content.ContentValues
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

class DownloadVideoActivity : AppCompatActivity() {

    private var projectPath = ""
    private var mCurrentPath = ""
    private var userId = -1
    private var restart = true
    private var saved = false
    private var location = "http://54.81.239.120/projects/1/fb633b48-9850-40ca-ba37-26beb9558892/videos/VIDEO_1_20181204_161332_.mp4"


    private fun createTempFile(): File {
        // Create an image file name
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

    override fun onDestroy() {
        super.onDestroy()

        deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_MOVIES))

    }

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_video)

        supportActionBar!!.title = "Video"

        videoView2.visibility = View.INVISIBLE


        userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)
        projectPath = intent.getStringExtra("projectPath")
        val name = intent.getStringExtra("projectName")

        DownloadFileAsync(projectPath).execute("")

        BackToProject8.setOnClickListener {
            finish()
            val intent = Intent(this@DownloadVideoActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectName",name)
            // start your next activity
            startActivity(intent)
        }

        playVideo2.setOnClickListener {
            if (restart) {
                val video = Uri.fromFile(File(mCurrentPath))

                videoView2.setVideoURI(video)
                restart = false
            }
            videoView2.requestFocus()
            videoView2.start()
        }
        pauseVideo2.setOnClickListener {
            videoView2.pause()
        }

        stopVideo2.setOnClickListener {
            videoView2.stopPlayback()
            restart = true
        }

        saveVideo2.setOnClickListener {
            if (mCurrentPath != "" && !saved) {
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


    private inner class DownloadFileAsync(val projectPath: String) : AsyncTask<String, Void, String>() {

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

                Toast.makeText(this@DownloadVideoActivity, "Downloaded!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@DownloadVideoActivity, "Try Again", Toast.LENGTH_LONG).show()
            }
        }

        override fun onPreExecute() {}

        override fun onProgressUpdate(vararg values: Void) {}
    }


}
