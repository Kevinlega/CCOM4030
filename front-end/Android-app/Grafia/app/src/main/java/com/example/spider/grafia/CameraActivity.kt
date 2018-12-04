package com.example.spider.grafia

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_camera.*
import java.lang.Exception
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.widget.Toast
import android.Manifest.permission
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import java.io.*
import android.os.AsyncTask
import java.net.*
import android.provider.DocumentsContract

class CameraActivity : AppCompatActivity() {

    private var mCurrentPhotoPath = ""
    private var mCurrentPickedPicture = ""
    private var mCurrentPickedPictureName = ""
    private var saved = false
    private var userId = -1
    private var projectId = -1
    private var projectPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        userId = intent.getIntExtra("userId", -1)
        projectId = intent.getIntExtra("pId",-1)
        projectPath = intent.getStringExtra("projectPath")
        val name = intent.getStringExtra("projectName")


        backToProject1.setOnClickListener {
            finish()
            if ((mCurrentPhotoPath != "") and !saved) {
                val myFile = File(mCurrentPhotoPath)
                myFile.delete()
                mCurrentPhotoPath = ""
            }

            val intent = Intent(this@CameraActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectName",name)
            // start your next activity
            startActivity(intent)
        }

        openCamera.setOnClickListener {

            if ((mCurrentPhotoPath != "") and !saved) {
                val myFile = File(mCurrentPhotoPath)
                myFile.delete()
                mCurrentPhotoPath = ""
                saved = false
            }
            mCurrentPickedPicture = ""
            mCurrentPickedPictureName = ""

            dispatchTakePictureIntent()
        }

        openGallery.setOnClickListener {

            dispatchPicPictureIntent()
        }

        saveImage.setOnClickListener {
            if (mCurrentPhotoPath != "" && !saved) {
                galleryAddPic()
                Toast.makeText(this, "Saved to Gallery.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nothing to Save.", Toast.LENGTH_SHORT).show()
            }
        }

        uploadImage.setOnClickListener {

            UploadFileAsync(projectPath).execute("")
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createTempImageFile()
                } catch (t: Exception) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@CameraActivity,
                        "com.example.spider.grafia", photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    startActivityForResult(takePictureIntent, 1)
                }
            }
        }
    }

    private fun dispatchPicPictureIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {

            BitmapFactory.decodeFile(mCurrentPhotoPath)?.also { bitmap ->
                imageView.setImageBitmap(rotatePic(bitmap))
            }

            val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
            mCurrentPickedPictureName = "IMAGE_${userId}_${timeStamp}_.jpg"

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != "") {
                val myFile = File(mCurrentPhotoPath)
                myFile.delete()
                mCurrentPhotoPath = ""
            }
            val photoUri = data?.data

            val wholeID = DocumentsContract.getDocumentId(photoUri)

            // Split at colon, use second item in the array
            val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

            val column = arrayOf(MediaStore.Images.Media.DATA)

            // where id is equal to
            val sel = MediaStore.Images.Media._ID + "=?"

            val cursor = this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            )

            var filePath = ""
            val columnIndex = cursor.getColumnIndex(column[0])

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
            mCurrentPickedPicture = filePath

            val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
            mCurrentPickedPictureName = "IMAGE_${userId}_${timeStamp}_.jpg"

            if (photoUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
                imageView.setImageBitmap(rotatePic(bitmap))
            }
        }
    }

    private fun createTempImageFile(): File {
        // Create an image file name
        val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "IMAGE_${userId}_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = this.absolutePath
        }
    }

    private fun rotatePic(bitmap: Bitmap): Bitmap {
        // Get the dimensions of the View

        val matrix = Matrix()

        if (bitmap.width > bitmap.height) {
            matrix.postRotate(90f)
        }

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)

        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
    }

    private fun galleryAddPic() {

        if (ContextCompat.checkSelfPermission(
                this,
                permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.WRITE_EXTERNAL_STORAGE), 1
            )
        } else {
            // Save image to gallery via bitmap
            BitmapFactory.decodeFile(mCurrentPhotoPath)?.also { bitmap ->
                MediaStore.Images.Media.insertImage(contentResolver, rotatePic(bitmap), "test", "test")
            }
            saved = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_PICTURES))
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

    private inner class UploadFileAsync(val projectPath: String) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {

            var path = ""
            var name = mCurrentPickedPictureName

            if (mCurrentPhotoPath == "" && mCurrentPickedPicture != "") {
                path = mCurrentPickedPicture

            } else if (mCurrentPhotoPath != "" && mCurrentPickedPicture == "") {
                path = mCurrentPhotoPath
            }

            val multipart = Multipart(URL("http://54.81.239.120/fUploadAPI.php"))
            multipart.addFormField("fileType", "1")
            multipart.addFormField("path", (projectPath + "/images/"))
            multipart.addFilePart("file", path, name, "image/jpg")

            val bool = multipart.upload()

            if (bool) {
                return "YES"
            } else {
                return "NO"
            }

        }

        override fun onPostExecute(result: String) {

            if (result == "YES") {
                Toast.makeText(this@CameraActivity, "Uploaded!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@CameraActivity, "Try Again", Toast.LENGTH_LONG).show()
            }

        }

        override fun onPreExecute() {}

        override fun onProgressUpdate(vararg values: Void) {}
    }
}





