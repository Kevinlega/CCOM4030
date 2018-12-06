package com.example.spider.grafia

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_project.*
import java.lang.Exception

import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL



class ProjectActivity : AppCompatActivity() {

    private var projectPath = ""
    private var userId = -1
    private var projectId = -1
    private var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        title = intent.getStringExtra("projectName")
        supportActionBar!!.title = title

        userId = intent.getIntExtra("userId",-1)
        projectId = intent.getIntExtra("pId",-1)


        var connectToAPI = Connect(this,1)
        try{
            val url = "http://54.81.239.120/selectAPI.php?queryType=8&pid=$projectId"
            connectToAPI.execute(url)
        }
        catch (error: Exception){}

        connectToAPI = Connect(this,0)
        try{
            val url = "http://54.81.239.120/selectAPI.php?queryType=10&pid=$projectId"
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


    private fun fetchJson(mContext: Context) {
        println("Fetching Json.")
        val url = "http://54.81.239.120/listdir.php?path=$projectPath"

        println(url)

        var request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                val myfiles: myFiles?
                myfiles = gson.fromJson(body, myFiles::class.java)
                val listview = findViewById<ListView>(R.id.project_list_files)

                runOnUiThread {
                    listview.adapter = MyCustomAdapter(this@ProjectActivity, myfiles)

                    listview.setOnItemClickListener { parent, view, position, id ->

                        val file = listview.getItemAtPosition(position) as Array<String>



                        var intent = Intent(this@ProjectActivity, ProjectActivity::class.java)

                        when(file[1]) {
                            "voice" ->  intent = Intent(this@ProjectActivity, DownloadAudioActivity::class.java)
                            "images" -> intent = Intent(this@ProjectActivity, DownloadImageActivity::class.java)
                            "videos" -> intent = Intent(this@ProjectActivity, DownloadVideoActivity::class.java)
                            "docs" ->  intent = Intent(this@ProjectActivity, DownloadNotesActivity::class.java)
                        }


                        var path = projectPath.substringAfter("/var/www/html/")

                        val location = "http://54.81.239.120/" + path + "/" + file[1] + "/" + file[0]

                        // To pass any data to next activity
                        intent.putExtra("userId", userId)
                        intent.putExtra("pId", projectId)
                        intent.putExtra("projectPath",location)
                        intent.putExtra("projectName",title)
            //             start your next activity
                        startActivity(intent)


                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("NOPE")
            }
        })


    }
    private inner class MyCustomAdapter(context: Context, myfiles : myFiles) : BaseAdapter() {
        private val mContext: Context
        private val myfiles : myFiles

        init {
            this.mContext = context
            this.myfiles = myfiles
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutinflator = LayoutInflater.from(mContext)
            val row_main = layoutinflator.inflate(R.layout.file_row, parent, false)

            val name_text_view = row_main.findViewById<TextView>(R.id.filename)
            name_text_view.text = myfiles.files[position].filename
            val filetype_texview = row_main.findViewById<TextView>(R.id.filetype)
            filetype_texview.text = myfiles.files[position].type

            return row_main
        }

        override fun getItem(position: Int): Array<String> {
            val filename =  myfiles.files.get(position).filename

            val type = myfiles.files.get(position).type

            return arrayOf(filename,type)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return myfiles.files.size
        }
    }

    private inner class Connect(val mContext: Context,val flag:Int) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg p0: String?): String {
            return downloadJSON(p0[0])
        }

        private fun downloadJSON(url: String?): String {
            return URL(url).readText()
        }

        override fun onPostExecute(result: String) {
            try {
                val jSONObject = JSONObject(result)
                if (flag == 0) {


                    val empty = jSONObject.getBoolean("empty")

                    if (!empty) {
                        projectPath = jSONObject.getString("path")
                        fetchJson(mContext)

                    }
                } else if (flag == 1) {
                    if (jSONObject.getInt("admin") != userId) {
                        AddUsers.visibility = View.INVISIBLE
                        }
                    }
                } catch (error: Exception) {}
            super.onPostExecute(result)
        }
    }
}


    class myFiles(val files : List<mFile>) {

    }

    class mFile(val filename : String, val type : String) {

    }
