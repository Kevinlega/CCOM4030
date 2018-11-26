package com.example.spider.grafia

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_project.*
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

class CreateProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)

        supportActionBar!!.setTitle("Create Project")

        val userId = intent.getIntExtra("userId",-1)


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

                val downloadData = Download(this,userId,name)

                try
                {
                    var reqParam = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                    reqParam += "&location=" + URLEncoder.encode(location, "UTF-8")
                    reqParam += "&description=" + URLEncoder.encode(description, "UTF-8")
                    reqParam += "&user_id=" + URLEncoder.encode(userId.toString(), "UTF-8")
                    val url = "http://54.81.239.120/insertAPI.php?queryType=2&$reqParam"
                    Log.i("CreateProjectActivity", "URL: $url")

                    println(url)
                    downloadData.execute(url)

                }catch (e: Exception){println(e.message)}
            }
        }
    }
    companion object {
        class Download(private val mContext: Context, private val userId: Int, private val Name: String) : AsyncTask<String, Void, String>(){
            var projectId = -1

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
                        projectId = (jSONObject.getString("project_id")).toInt()
                        Toast.makeText(mContext, "Project Created.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(mContext, ProjectActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("pid", projectId)
                        intent.putExtra("projectName",Name)
                        mContext.startActivity(intent)

                    } else {
                        Toast.makeText(mContext, "Project Not Created.", Toast.LENGTH_SHORT).show()
                    }

                }catch (e: Exception){
                    println(e.message)
                }

                super.onPostExecute(result)
            }
        }
    }
}