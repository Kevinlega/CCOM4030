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

        userId = intent.getIntExtra("userId",-1)
        projectId = intent.getIntExtra("pId",-1)
        projectPath = "/var/www/projects/1/fb633b48-9850-40ca-ba37-26beb9558892"

        backToProject2.setOnClickListener {
            finish()
            if((mCurrentVideoPath != "") and !saved){
                val myFile = File(mCurrentVideoPath)
                myFile.delete()
                mCurrentVideoPath = ""
            }

            val intent = Intent(this@VideoActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pid", projectId)
            // start your next activity
            startActivity(intent)
        }

        openVideo.setOnClickListener {

            if((mCurrentVideoPath != "") and !saved){
                val myFile = File(mCurrentVideoPath)
                myFile.delete()
                mCurrentVideoPath = ""
                saved = false
            }

            mCurrentPickedVideo = ""
            mCurrentPickedVideoName = ""

            dispatchTakeVideoIntent()
        }

        openVideoGallery.setOnClickListener {

            dispatchPicVideoIntent()
        }

        saveVideo.setOnClickListener {
            if (mCurrentVideoPath != "" && !saved){
                galleryAddVideo()
                Toast.makeText(this, "Saved to Gallery.", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Nothing to Save.", Toast.LENGTH_SHORT).show()
            }
        }

        play.setOnClickListener {
            if ((mCurrentVideoUri != Uri.EMPTY) and restart) {
                videoView.setVideoURI((mCurrentVideoUri) as Uri)
                restart = false
            }
            videoView.requestFocus()
            videoView.start()
        }
        pause.setOnClickListener {
            videoView.pause()
        }

        stop.setOnClickListener {
            videoView.stopPlayback()
            restart = true
        }

        uploadVideo.setOnClickListener {
        }


    }
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
    private fun dispatchPicVideoIntent(){
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            videoView.visibility = View.VISIBLE

            val videoURI = data?.data
            if (videoURI != null) {
                mCurrentVideoUri = videoURI as Uri
                videoView.setVideoURI(videoURI)
            }

        } else if (requestCode == 2 && resultCode == RESULT_OK){
            videoView.visibility = View.VISIBLE
            if(mCurrentVideoPath != ""){
                val myFile = File(mCurrentVideoPath)
                myFile.delete()
                mCurrentVideoPath = ""
            }
            val videoURI = data?.data

            if (videoURI != null) {
                mCurrentVideoUri = videoURI
                videoView.setVideoURI(videoURI)
            }
        }
    }

    private fun createTempVideoFile(): File {
        // Create an image file name
        val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(
            "Video_${userId}_${timeStamp}_", /* prefix */
            ".mp4", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentVideoPath = absolutePath
        }
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
}
