// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        : NotesActivity.kt
// Description : Creates a note and saves it to server
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

package com.example.spider.grafia

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_notes.*
import org.json.JSONObject
import java.io.File
import java.net.URL

class NotesActivity : AppCompatActivity() {

    // global varibales
    private var projectPath = ""
    private var projectName = ""
    private var userId = -1
    private var projectId = -1


    // Method to show an alert dialog with yes, no and cancel button
    private fun showInternetNotification(mContext: Context){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog


        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(mContext)

        // Set a title for alert dialog
        builder.setTitle("Lost Internet Connection.")

        // Set a message for alert dialog
        builder.setMessage("Do you want to log out or retry?")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {

                    val intent = Intent(mContext, LoginActivity::class.java)
                    intent.putExtra("Failed",true)
                    mContext.startActivity(intent)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    finish()
                    startActivity(intent)
                }
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Log Out",dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("Retry",dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        supportActionBar!!.title = "Notes"
        // get users data
        userId = intent.getIntExtra("userId",-1)
        projectId = intent.getIntExtra("pId",-1)
        projectPath = intent.getStringExtra("projectPath")
        projectName = intent.getStringExtra("projectName")

        // Segue
        backToProject4.setOnClickListener {
            val intent = Intent(this@NotesActivity, ProjectActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("pId", projectId)
            intent.putExtra("projectName",projectName)
            // start your next activity
            startActivity(intent)
        }

        // Text written
        var Note = findViewById(R.id.Note) as EditText
        Note.afterTextChanged{}

        // save to server
        save_button.setOnClickListener{
            val Note = findViewById<EditText>(R.id.Note)
            val Name = findViewById<EditText>(R.id.Name)
            var name = Name.text.toString()
            val note = Note.text.toString()

            if(note.isNullOrBlank()){
                Toast.makeText(this, "Cannot be empty", Toast.LENGTH_LONG).show()
            }
            else{

                val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())

                if(name.isNullOrBlank()){
                    name = "NOTES_$userId" + "_$timeStamp" + "_.txt"
                } else{
                    name += "_NOTES_$userId" + "_$timeStamp" + "_.txt"
                }
                save(note,name)
            }
        }

        // clear Text View
        clear_button.setOnClickListener {
            var Note = findViewById<EditText>(R.id.Note)
            Note.setText("")
            var Label = findViewById<TextView>(R.id.character_count)
            Label.setText("Characters: 0")
            Toast.makeText(this, "Text cleared.", Toast.LENGTH_LONG).show()
        }


    }

    // Post to API to upload file
    private fun save(text:String,name:String){

        if(isNetworkAvailable()) {
            val fileType = 0
            val path = projectPath + "/docs/" + name
            val connectToAPI = Connect(this,projectName,userId,projectId)//,fileType,path,text)
            try {
                val url = "http://54.81.239.120/fUploadAPI.php?fileType=$fileType&path=$path&pid=$projectId&uid=$userId&text=$text"
                connectToAPI.execute(url)
            } catch (error: Exception) {
            }
        } else {
            showInternetNotification(this@NotesActivity)
        }
    }

    // Listener for text change
    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){

                character_count.text = ("Characters: " + (Note.text.length).toString())

            }
        })
    }

    // Connect class that uploads string
    companion object {
        class Connect(private val mContext: Context,private val name: String, private val userId: Int, private val projectId: Int) :
            AsyncTask<String, Void, String>() {//, private val type : Int, private val path: String, private val fileType: String,private val text: String): AsyncTask<String, Void, String>(){

            override fun doInBackground(vararg p0: String?): String {
                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String {
                return URL(url).readText()
            }

            override fun onPostExecute(result: String?) {
                try {
                    val jSONObject = JSONObject(result)
                    println(jSONObject)
                    val uploaded = jSONObject.getBoolean("file_created")

                    if (uploaded) {
                        val intent = Intent(mContext, ProjectActivity::class.java)
                        Toast.makeText(mContext, "File created.", Toast.LENGTH_SHORT).show()
                        intent.putExtra("projectName",name)
                        intent.putExtra("userId", userId)
                        intent.putExtra("pId", projectId)
                        mContext.startActivity(intent)

                    } else {
                        Toast.makeText(mContext, "File not created.", Toast.LENGTH_SHORT).show()
                    }
                } catch (error: Exception) {
                }
                super.onPostExecute(result)
            }
        }
    }
}
