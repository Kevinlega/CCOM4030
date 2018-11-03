package com.example.spider.grafia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_participants.*


import android.os.AsyncTask
import org.json.JSONObject
import java.net.URL

class AddParticipantsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_participants)

        // retrieve data from another view
        // val _ = intent.getStringExtra("_")


        val downloadData = Download()

        try
        {
            val url = "http://54.81.239.120/selectAPI.php?queryType=1&pid=1&uid=4"
            downloadData.execute(url)

        }catch (e: Exception)
        {
            println(e.message)
        }

        // Segue trigger
        BackToProject.setOnClickListener {
                val intent = Intent(this@AddParticipantsActivity, ProjectActivity::class.java)
                // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
                // start your next activity
                startActivity(intent)
            }

        AddParticipants.setOnClickListener {
            val intent = Intent(this@AddParticipantsActivity, ProjectActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
            // start your next activity
            startActivity(intent)
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

                    var empty = jSONObject.getBoolean("empty")

                    if(!empty){
                        var names = jSONObject.getJSONArray("names")
                        var emails = jSONObject.getJSONArray("emails")
                        println(names)
                        println(emails)
                    }

                }catch (e: Exception){
                    println(e.message)
                }

                super.onPostExecute(result)
            }
        }
    }
}
