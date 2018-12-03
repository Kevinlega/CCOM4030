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
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import android.os.AsyncTask




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

        uploadImage.setOnClickListener {
            val notused = uploadFile(mCurrentPhotoPath)
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


    fun uploadFile(selectedFilePath:String):Int {

        var serverResponseCode = 0

        val connection:HttpURLConnection
        val dataOutputStream:DataOutputStream
        val lineEnd = "\r\n"
        val twoHyphens = "--"
        val boundary = "*****"


        var bytesRead:Int
        var bytesAvailable:Int
        var bufferSize:Int
        var buffer:ByteArray
        var maxBufferSize = 1 * 1024 * 1024
        var selectedFile = File(selectedFilePath)


        val parts = selectedFilePath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val fileName = parts[parts.size - 1]

        try
        {
        val fileInputStream = FileInputStream(selectedFile)
        val url = URL("")
        connection = url.openConnection() as HttpURLConnection
        connection.setDoInput(true)//Allow Inputs
        connection.setDoOutput(true)//Allow Outputs
        connection.setUseCaches(false)//Don't use a cached Copy
        connection.setRequestMethod("POST")
        connection.setRequestProperty("Connection", "Keep-Alive")
        connection.setRequestProperty("ENCTYPE", "multipart/form-data")
        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
        connection.setRequestProperty("uploaded_file", selectedFilePath)

         //creating new data output stream
                        dataOutputStream = DataOutputStream(connection.getOutputStream())

         //writing bytes to data output stream
                        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes(
            "Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
            + selectedFilePath + "\"" + lineEnd
        )

        dataOutputStream.writeBytes(lineEnd)

         //returns no. of bytes present in fileInputStream
                        bytesAvailable = fileInputStream.available()
         //selecting the buffer size as minimum of available bytes or 1 MB
                        bufferSize = Math.min(bytesAvailable, maxBufferSize)
         //setting the buffer as byte array of size of bufferSize
                        buffer = ByteArray(bufferSize)

         //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize)

         //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                        while (bytesRead > 0)
        {
         //write the bytes read from inputstream
                            dataOutputStream.write(buffer, 0, bufferSize)
        bytesAvailable = fileInputStream.available()
        bufferSize = Math.min(bytesAvailable, maxBufferSize)
        bytesRead = fileInputStream.read(buffer, 0, bufferSize)
        }

        dataOutputStream.writeBytes(lineEnd)
        dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

        serverResponseCode = connection.getResponseCode()
        val serverResponseMessage = connection.getResponseMessage()


         //response code of 200 indicates the server status OK
                        if (serverResponseCode == 200)
        {
        }

         //closing the input and output streams




        fileInputStream.close()
        dataOutputStream.flush()
        dataOutputStream.close()



        }
        catch (e: FileNotFoundException) {
        e.printStackTrace()
        runOnUiThread { Toast.makeText(this@CameraActivity, "File Not Found", Toast.LENGTH_SHORT).show() }
        }
        catch (e:MalformedURLException) {
        e.printStackTrace()
        Toast.makeText(this@CameraActivity, "URL error!", Toast.LENGTH_SHORT).show()

        }
        catch (e:IOException) {
        e.printStackTrace()
        Toast.makeText(this@CameraActivity, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show()
        }

        return serverResponseCode
    }



      private inner class UploadFileAsync:AsyncTask<String, Void, String>() {

            override fun doInBackground(vararg params:String):String {

            try
            {
            val sourceFileUri = "/mnt/sdcard/abc.png"

            var conn:HttpURLConnection? = null
            var dos:DataOutputStream? = null
            val lineEnd = "\r\n"
            val twoHyphens = "--"
            val boundary = "*****"
            var bytesRead:Int
            var bytesAvailable:Int
            var bufferSize:Int
            var buffer:ByteArray
            val maxBufferSize = 1 * 1024 * 1024
            val sourceFile = File(sourceFileUri)

            if (sourceFile.isFile)
            {

            try
            {
            val upLoadServerUri = "http://website.com/abc.php?"

             // open a URL connection to the Servlet
                                val fileInputStream = FileInputStream(
            sourceFile)
            val url = URL(upLoadServerUri)

             // Open a HTTP connection to the URL
                                conn = url.openConnection() as HttpURLConnection
                conn!!.doInput = true // Allow Inputs
                conn!!.doOutput = true // Allow Outputs
                conn!!.useCaches = false // Don't use a Cached Copy
                conn!!.requestMethod = "POST"
            conn!!.setRequestProperty("Connection", "Keep-Alive")
            conn!!.setRequestProperty("ENCTYPE",
            "multipart/form-data")
            conn!!.setRequestProperty("Content-Type",
                "multipart/form-data;boundary=$boundary"
            )
            conn!!.setRequestProperty("bill", sourceFileUri)

            dos = DataOutputStream(conn!!.outputStream)

            dos!!.writeBytes(twoHyphens + boundary + lineEnd)
            dos!!.writeBytes(
                "Content-Disposition: form-data; name=\"bill\";filename=\""
                + sourceFileUri + "\"" + lineEnd
            )

            dos!!.writeBytes(lineEnd)

             // create a buffer of maximum size
                                bytesAvailable = fileInputStream.available()

            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            buffer = ByteArray(bufferSize)

             // read file and write it into form...
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize)

            while (bytesRead > 0)
            {

            dos!!.write(buffer, 0, bufferSize)
            bytesAvailable = fileInputStream.available()
            bufferSize = Math
            .min(bytesAvailable, maxBufferSize)
            bytesRead = fileInputStream.read(buffer, 0,
            bufferSize)

            }

             // send multipart form data necesssary after file
                                // data...
                                dos!!.writeBytes(lineEnd)
            dos!!.writeBytes((twoHyphens + boundary + twoHyphens
            + lineEnd))

             // Responses from the server (code and message)

            val serverResponseCode = conn!!.responseCode
            val serverResponseMessage = conn!!
            .responseMessage

            if (serverResponseCode === 200)
            {

             // messageText.setText(msg);
                                    //Toast.makeText(ctx, "File Upload Complete.",
                                    //      Toast.LENGTH_SHORT).show();

                                    // recursiveDelete(mDirectory1);

                                }

             // close the streams //
                                fileInputStream.close()
            dos!!.flush()
            dos!!.close()

            }
            catch (e:Exception) {

             // dialog.dismiss();
                                e.printStackTrace()

            }

             // dialog.dismiss();

                        } // End else block


            }
            catch (ex:Exception) {
             // dialog.dismiss();

                        ex.printStackTrace()
            }

            return "Executed"
            }

            override fun onPostExecute(result:String) {}

            override fun onPreExecute() {}

            override fun onProgressUpdate(vararg values:Void) {}
      }
}