// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : VideoActivity.kt
// Description : Takes video or grabs from gallery
//               and uploads it to server
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_video.*
import java.lang.Exception
import android.content.ContentValues
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.provider.DocumentsContract
import java.io.*
import java.net.URL


class VideoActivity : AppCompatActivity() {

    // Global variables
    private var mCurrentVideoPath = ""
    private var mCurrentVideoUri : Uri = Uri.EMPTY
    private var mCurrentPickedVideo = ""
    private var mCurrentPickedVideoName = ""
    private var saved = false
    private var userId = -1
    private var projectId = -1
    private var restart = false
    private var projectPath = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        videoView.visibility = View.INVISIBLE

        // get user data
        userId = intent.getIntExtra("userId", -1)
        projectId = intent.getIntExtra("pId",-1)
        projectPath = intent.getStringExtra("projectPath")
        val name = intent.getStringExtra("projectName")


        // Segue
        backToProject2.setOnClickListener {
            finish()
            if ((mCurrentVideoPath != "") and !saved) {
                val myFile = File(mCurrentVideoPath)
                myFile.delete()
                mCurrentVideoPath = ""
            }

            val intent = Intent(this@VideoActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectName",name)
            // start your next activity
            startActivity(intent)
        }

        // open video with intent
        openVideo.setOnClickListener {

            if ((mCurrentVideoPath != "") and !saved) {
                val myFile = File(mCurrentVideoPath)
                myFile.delete()
                mCurrentVideoPath = ""
                saved = false
            }

            mCurrentPickedVideo = ""
            mCurrentPickedVideoName = ""

            dispatchTakeVideoIntent()
        }

        // open video gallery
        openVideoGallery.setOnClickListener {

            dispatchPicVideoIntent()
        }

        // save video to gallery
        saveVideo.setOnClickListener {
            if (mCurrentVideoPath != "" && !saved) {
                galleryAddVideo()
                Toast.makeText(this, "Saved to Gallery.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nothing to Save.", Toast.LENGTH_SHORT).show()
            }
        }

        // play video
        play.setOnClickListener {
            if ((mCurrentVideoUri != Uri.EMPTY) and restart) {
                videoView.setVideoURI((mCurrentVideoUri) as Uri)
                restart = false
            }
            videoView.requestFocus()
            videoView.start()
        }
        // pause video
        pause.setOnClickListener {
            videoView.pause()
        }

        // stop video
        stop.setOnClickListener {
            videoView.stopPlayback()
            restart = true
        }

        // upload video
        uploadVideo.setOnClickListener {
            UploadFileAsync(projectPath).execute("")
        }
    }

    // takes intent to open camera to record
    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? =  try {
                    createTempVideoFile()
                } catch (t: Exception){null}
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@VideoActivity,
                        "com.example.spider.grafia", it )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    startActivityForResult(takePictureIntent, 1)
                }
            }
        }
    }
    // takes intent to open gallery
    private fun dispatchPicVideoIntent(){
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), 2)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // take video
        if (requestCode == 1 && resultCode == RESULT_OK) {
            videoView.visibility = View.VISIBLE

            val videoURI = data?.data
            if (videoURI != null) {
                mCurrentVideoUri = videoURI as Uri
                videoView.setVideoURI(videoURI)
            }

        }
        // takes video from gallery
        else if (requestCode == 2 && resultCode == RESULT_OK){
            videoView.visibility = View.VISIBLE
            if(mCurrentVideoPath != ""){
                val myFile = File(mCurrentVideoPath)
                myFile.delete()
                mCurrentVideoPath = ""
            }
            val videoURI = data?.data

            val wholeID = DocumentsContract.getDocumentId(videoURI)

            // Split at colon, use second item in the array
            val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

            val column = arrayOf(MediaStore.Images.Media.DATA)

            // where id is equal to
            val sel = MediaStore.Video.Media._ID + "=?"

            val cursor = this.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            )

            var filePath = ""
            val columnIndex = cursor.getColumnIndex(column[0])

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
            mCurrentPickedVideo = filePath

            val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
            mCurrentPickedVideoName = "VIDEO_${userId}_${timeStamp}_.mp4"

            if (videoURI != null) {
                mCurrentVideoUri = videoURI
                videoView.setVideoURI(videoURI)
            }
        }
    }


    private fun createTempVideoFile(): File {
        // Create a temporary video file
        val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(
            "VIDEO_${userId}_${timeStamp}_", /* prefix */
            ".mp4", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentVideoPath = absolutePath
        }
    }

    // save video to gallery
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
        retriever.setDataSource(this, mCurrentVideoUri)
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
            val istream = FileInputStream(mCurrentVideoPath)
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
        } catch (e: Exception) {
        }


        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))

        saved = true
    }

    // Delete Temps
    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_MOVIES))
        }
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

    // Upload Video to server
    private inner class UploadFileAsync(val projectPath: String) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {

            var path = ""
            var name = ""

            if (mCurrentVideoPath == "" && mCurrentPickedVideo != "") {
                path = mCurrentPickedVideo
                name = mCurrentPickedVideoName

            } else if (mCurrentVideoPath != "" && mCurrentPickedVideo == "") {
                path = File(mCurrentVideoPath).absolutePath
                name = path.substringAfterLast("/")
            }

            val multipart = Multipart(URL("http://54.81.239.120/fUploadAPI.php"))
            multipart.addFormField("fileType", "1")
            multipart.addFormField("path", (projectPath + "/videos/"))
            multipart.addFilePart("file", path, name, "video/mp4")

            val bool = multipart.upload()

            if (bool) {
                return "YES"
            } else {
                return "NO"
            }
        }

        override fun onPostExecute(result: String) {

            if (result == "YES") {
                Toast.makeText(this@VideoActivity, "Uploaded!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@VideoActivity, "Try Again", Toast.LENGTH_LONG).show()
            }

        }

        override fun onPreExecute() {}

        override fun onProgressUpdate(vararg values: Void) {}
    }
}