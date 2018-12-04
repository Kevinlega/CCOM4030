package com.example.spider.grafia

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_project.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class ProjectActivity : AppCompatActivity() {

    private var projectPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        val title = intent.getStringExtra("projectName")
        supportActionBar!!.title = title

        val userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)


        val connectToAPI = Connect()
        try{
            val url = "http://54.81.239.120/selectAPI.php?queryType=10&pid=$projectId"
            println(url)
            connectToAPI.execute(url)
        }
        catch (error: Exception){}


        BackToDashboard.setOnClickListener {
            val intent = Intent(this@ProjectActivity, DashboardActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            // start your next activity
            startActivity(intent)
        }

        AddUsers.setOnClickListener {
            val intent = Intent(this@ProjectActivity, AddParticipantsActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectName",title)
//             start your next activity
            startActivity(intent)
        }

        Camera.setOnClickListener {
            val intent = Intent(this@ProjectActivity, CameraActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectPath",projectPath)
            intent.putExtra("projectName",title)
//             start your next activity
            startActivity(intent)
        }

        Voice.setOnClickListener {
            val intent = Intent(this@ProjectActivity, VoiceActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectPath",projectPath)
            intent.putExtra("projectName",title)
//             start your next activity
            startActivity(intent)
        }

        Video.setOnClickListener {
            val intent = Intent(this@ProjectActivity, VideoActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectPath",projectPath)
            intent.putExtra("projectName",title)
//             start your next activity
            startActivity(intent)
        }

        Notes.setOnClickListener {
            val intent = Intent(this@ProjectActivity, NotesActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectPath",projectPath)
            intent.putExtra("projectName",title)
//             start your next activity
            startActivity(intent)
        }
    }

    private inner class Connect : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg p0: String?): String {
            return downloadJSON(p0[0])
        }

        private fun downloadJSON(url: String?): String {
            return URL(url).readText()
        }

        override fun onPostExecute(result: String) {
            try {
                val jSONObject = JSONObject(result)

                val empty = jSONObject.getBoolean("empty")

                if (!empty) {
                    projectPath = jSONObject.getString("path")
                }

            } catch (error: Exception) {
            }
            super.onPostExecute(result)
        }
    }
}
