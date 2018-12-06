package com.example.spider.grafia

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_download_notes.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadNotesActivity : AppCompatActivity() {

    var mCurrentPath = ""
    var userId = -1
    private var location = ""

    private fun createTempFile(): File {
        // Create an image file name
        val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        return File.createTempFile(
            "NOTES_${userId}_${timeStamp}_", /* prefix */
            ".txt", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPath = this.absolutePath
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS))

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_notes)

        supportActionBar!!.title = "Notes"

        userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)
        location = intent.getStringExtra("projectPath")
        val name = intent.getStringExtra("projectName")

        DownloadFileAsync().execute("")

        BackToProject5.setOnClickListener {
            finish()
            val intent = Intent(this@DownloadNotesActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectName",name)
            // start your next activity
            startActivity(intent)
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
                val doc = File(mCurrentPath).readText()

                note2.setText(doc)

                Toast.makeText(this@DownloadNotesActivity, "Downloaded!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@DownloadNotesActivity, "Try Again", Toast.LENGTH_LONG).show()
            }
        }

        override fun onPreExecute() {}

        override fun onProgressUpdate(vararg values: Void) {}
    }
}
