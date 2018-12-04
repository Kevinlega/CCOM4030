package com.example.spider.grafia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dashboard.*
import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.GridLayout.VERTICAL
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL



class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar!!.setTitle("Dashboard")

        val userId = intent.getIntExtra("userId",-1)

        val rview= findViewById<View>(R.id.rview) as RecyclerView

        val lManager = GridLayoutManager(this, 2, VERTICAL, false)
        rview.layoutManager = lManager

        val downloadData = Connect(this@DashboardActivity, rview, userId)

        try {
            val url = "http://54.81.239.120/selectAPI.php?queryType=3&uid=$userId"
            downloadData.execute(url)

        }catch (e: Exception)
        {
            println(e.message)
        }

        logout.setOnClickListener {
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            // To pass any data to next activity
            // start your next activity
            finish()
            startActivity(intent)
        }

        CreateProject.setOnClickListener {
            val intent = Intent(this@DashboardActivity, CreateProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            // start your next activity
            startActivity(intent)
        }

        Friends.setOnClickListener {
            val intent = Intent(this@DashboardActivity, FriendsActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
//             start your next activity
            startActivity(intent)
        }
    }

    companion object {
        class Connect(context: Context, recyclerView: RecyclerView, private val userId : Int): AsyncTask<String, Void, String>(){
            var names = JSONArray()
            var projects = JSONArray()
            val mContext = context
            val rview = recyclerView

            override fun doInBackground(vararg p0: String?): String{
                return downloadJSON(p0[0])

            }
            private fun downloadJSON(url: String?): String{
                return URL(url).readText()
            }
            override fun onPostExecute(result: String?){
                try{
                    val jSONObject = JSONObject(result)

                    var empty = jSONObject.getBoolean("empty")

                    if (!empty) {

                        names = jSONObject.getJSONArray("project_name")
                        projects = jSONObject.getJSONArray("project_id")

                        val project : MutableList<Int> = ArrayList()

                        for(i in 0..projects.length()-1){
                            project.add(i,R.drawable.folder_icon)
                        }

                        rview.adapter = DashboardAdapter(project, names,projects,mContext,userId)
                    }
                }
                catch (error: Exception){}
                super.onPostExecute(result)
            }
        }
    }
}
