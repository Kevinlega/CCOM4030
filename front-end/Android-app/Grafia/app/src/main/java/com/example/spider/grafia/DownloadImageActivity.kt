// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : DownloadImageActivity.kt
// Description : Fetches image from server and places it in image view

package com.example.spider.grafia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_download_image.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadImageActivity : AppCompatActivity() {

    // Global variables
    var mCurrentPath = ""
    var userId = -1
    private var saved = false
    private var location = ""

    private fun createTempFile(): File {
        // Create an image temporary file
        val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "IMAGE_${userId}_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPath = this.absolutePath
        }
    }


    // Delete Trigger
    override fun onDestroy() {
        super.onDestroy()

        deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_PICTURES))

    }

    // Delete Temporary Files
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

    // Rotate image to be viewed
    private fun rotatePic(bitmap: Bitmap): Bitmap {
        // Get the dimensions of the View

        val matrix = Matrix()

        if (bitmap.width > bitmap.height) {
            matrix.postRotate(90f)
        }

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)

        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
    }

    // Save image to gallery
    private fun galleryAddPic() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
            )
        } else {
            // Save image to gallery via bitmap
            BitmapFactory.decodeFile(mCurrentPath)?.also { bitmap ->
                MediaStore.Images.Media.insertImage(contentResolver, rotatePic(bitmap), "test", "test")
            }
             saved = true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_image)

        supportActionBar!!.title = "Images"

        // Get user info
        userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)
        location = intent.getStringExtra("projectPath")
        val name = intent.getStringExtra("projectName")

        // Execute download
        DownloadFileAsync().execute("")


        // Segue trigger
        BackToProject6.setOnClickListener {
            finish()
            val intent = Intent(this@DownloadImageActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectName",name)
            // start your next activity
            startActivity(intent)
        }

        // Save Image to Gallery
        saveImage2.setOnClickListener {
            if (mCurrentPath != "" && !saved) {
                galleryAddPic()
                saved = true
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

    // Downloads file from server
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

                BitmapFactory.decodeFile(mCurrentPath)?.also { bitmap ->
                    imageView2.setImageBitmap(rotatePic(bitmap))
                }

                Toast.makeText(this@DownloadImageActivity, "Downloaded!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@DownloadImageActivity, "Try Again", Toast.LENGTH_LONG).show()
            }
        }

        override fun onPreExecute() {}

        override fun onProgressUpdate(vararg values: Void) {}
    }
}