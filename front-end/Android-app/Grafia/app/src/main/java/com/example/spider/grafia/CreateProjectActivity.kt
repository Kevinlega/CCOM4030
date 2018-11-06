package com.example.spider.grafia

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_project.*
//import okhttp3.*
import org.json.JSONObject
//import java.io.IOException
import java.net.URL
import java.net.URLEncoder

class CreateProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)

        //var user_id=intent.getStringExtra("user_id")
        val userId = intent.getStringExtra("userId")


        DontCreateProject.setOnClickListener {
            val intent = Intent(this@CreateProjectActivity, DashboardActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
        }

        CreateProject.setOnClickListener {

            Log.i("CreateProjectActivity", "Button Pressed.")
            var name = Name.text.toString()
            var location = Location.text.toString()
            var description = Description.text.toString()

            if(name == "" || location == "" || description == "") {
                Log.i("CreateProjectActivity", "Empty input")
                Toast.makeText(this, "All fields are requiered.", Toast.LENGTH_SHORT).show()
            } else {

                val downloadData = Download()

                try
                {
                    var reqParam = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                    reqParam += "&location=" + URLEncoder.encode(location, "UTF-8")
                    reqParam += "&description=" + URLEncoder.encode(description, "UTF-8")
                    reqParam += "&user_id=" + URLEncoder.encode(userId, "UTF-8")
                    val url = "http://54.81.239.120/insertAPI.php?queryType=2&$reqParam"
                    Log.i("CreateProjectActivity", "URL: $url")

                    println(url)
                    downloadData.execute(url)
                    Toast.makeText(this, "Project Created.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CreateProjectActivity, ProjectActivity::class.java)
                    startActivity(intent)

                }catch (e: Exception){println(e.message)}
            }
        }
    }
    companion object {
        class Download : AsyncTask<String, Void, String>(){

            override fun doInBackground(vararg p0: String?): String {

                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String
            {
                return URL(url).readText()
            }

            override fun onPostExecute(result: String?) {

                try
                {
                    val jSONObject = JSONObject(result)

                    val created = jSONObject.getBoolean("created")

                    if(created){
                        val project_id = jSONObject.getString("project_id")
                        println(project_id)
                    } else {
                        println("Not created.")
                    }

                }catch (e: Exception){
                    println(e.message)
                }

                super.onPostExecute(result)
            }
        }
    }
}