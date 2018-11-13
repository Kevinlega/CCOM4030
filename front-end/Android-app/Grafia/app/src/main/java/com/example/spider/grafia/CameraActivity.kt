package com.example.spider.grafia

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.lang.Exception
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast


class CameraActivity : AppCompatActivity() {

    private var mCurrentPhotoPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        backToProject1.setOnClickListener {
            val intent = Intent(this@CameraActivity, ProjectActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
        }

        openCamera.setOnClickListener {
            dispatchTakePictureIntent()
        }

        saveImage.setOnClickListener {
            if (mCurrentPhotoPath != ""){
                galleryAddPic()
                Toast.makeText(this, "Saved to Gallery.", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Nothing to Save.", Toast.LENGTH_SHORT).show()
            }

        }

    }
    private fun dispatchTakePictureIntent() {
        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? =  try {
                    createImageFile()
                } catch (t: Exception){null}
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: android.net.Uri = android.support.v4.content.FileProvider.getUriForFile(
                        this@CameraActivity,
                        "com.example.spider.grafia", it )
                    takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI)

                    startActivityForResult(takePictureIntent, 1)
                    }
                }
           }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                setPic()
            }
        }

//    @Throws(IOException::class)
    private fun createImageFile(): File {
            // Create an image file name
            val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
            val storageDir: File = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                mCurrentPhotoPath = absolutePath
            }
        }


    private fun setPic() {
        // Get the dimensions of the View
        val targetW = imageView.width
        val targetH= imageView.height


        BitmapFactory.decodeFile(mCurrentPhotoPath)?.also { bitmap ->


            val matrix = android.graphics.Matrix()

            matrix.postRotate(90f)

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)

            val rotatedBitmap =
                Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)

            imageView.setImageBitmap(rotatedBitmap)
        }
    }

    private fun galleryAddPic() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(mCurrentPhotoPath)
        val contentUri = android.net.Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        this.sendBroadcast(mediaScanIntent)
    }



}