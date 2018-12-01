package com.example.spider.grafia

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
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

class CameraActivity : AppCompatActivity() {

    private var mCurrentPhotoPath = ""
    private var saved = false
    private var userId = -1
    private var projectId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        userId = intent.getIntExtra("userId",-1)
        projectId = intent.getIntExtra("pId",-1)

        backToProject1.setOnClickListener {
            finish()
            if((mCurrentPhotoPath != "") and !saved){
                val myFile = File(mCurrentPhotoPath)
                myFile.delete()
                mCurrentPhotoPath = ""
            }

            val intent = Intent(this@CameraActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pid", projectId)
            // start your next activity
            startActivity(intent)
        }

        openCamera.setOnClickListener {

            if((mCurrentPhotoPath != "") and !saved){
                val myFile = File(mCurrentPhotoPath)
                myFile.delete()
                mCurrentPhotoPath = ""
                saved = false
            }
            dispatchTakePictureIntent()
        }

        openGallery.setOnClickListener {

            dispatchPicPictureIntent()
        }

        saveImage.setOnClickListener {
            if (mCurrentPhotoPath != "" && !saved){
                galleryAddPic()
                Toast.makeText(this, "Saved to Gallery.", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Nothing to Save.", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? =  try {
                    createTempImageFile()
                } catch (t: Exception){null}
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@CameraActivity,
                        "com.example.spider.grafia", it )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    startActivityForResult(takePictureIntent, 1)
                    }
                }
           }
        }

    private fun dispatchPicPictureIntent(){
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
        } else if (requestCode == 2 && resultCode == RESULT_OK){
            if(mCurrentPhotoPath != ""){
                val myFile = File(mCurrentPhotoPath)
                myFile.delete()
                mCurrentPhotoPath = ""
            }
            val photoUri = data?.data
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
                mCurrentPhotoPath = absolutePath
            }
        }

    private fun rotatePic(bitmap: Bitmap) : Bitmap {
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

                    MediaStore.Images.Media.insertImage(contentResolver,rotatePic(bitmap),"test","test")

                }

            saved = true

            // save via file path
            // val file = File(mCurrentPhotoPath)
            // MediaStore.Images.Media.insertImage(contentResolver,file.absolutePath,"test","test")
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

}