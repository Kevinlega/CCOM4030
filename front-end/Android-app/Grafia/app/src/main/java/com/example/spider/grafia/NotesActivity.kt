package com.example.spider.grafia

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_notes.*
import org.json.JSONObject
import java.net.URL

class NotesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        supportActionBar!!.title = "Notes"

        val userId = intent.getIntExtra("userId",-1)
        val projectId = intent.getIntExtra("pId",-1)

        backToProject4.setOnClickListener {
            val intent = Intent(this@NotesActivity, DashboardActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("userId", userId)
            intent.putExtra("projectId", projectId)
            // start your next activity
            startActivity(intent)
        }

        var Note = findViewById(R.id.Note) as EditText
        Note.afterTextChanged{}

        save_button.setOnClickListener{
            val Note = findViewById<EditText>(R.id.Note)
            val Name = findViewById<EditText>(R.id.Name)
            var name = Name.text.toString()
            val note = Note.text.toString()

            if(note.isNullOrBlank() || note.length > 500){
                Toast.makeText(this, "Character length must be in range (1,500)", Toast.LENGTH_LONG).show()
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

        clear_button.setOnClickListener {
            var Note = findViewById<EditText>(R.id.Note)
            Note.setText("")
            character_count.text = "Characters: 0"
            Toast.makeText(this, "Text cleared.", Toast.LENGTH_LONG).show()
        }


    }

    // Post al API para subir el archivo
    private fun save(text:String,name:String){
        val fileType = 0
        val path = "/var/www/projects/1/fb633b48-9850-40ca-ba37-26beb9558892" + "/docs/" + name
        val connectToAPI = Connect(this)//,fileType,path,text)
        try{
            val url = "http://54.81.239.120/fUploadAPI.php?fileType=$fileType&path=$path&text=$text"
            connectToAPI.execute(url)
        }
        catch (error: Exception){}
    }

    // Listener si cambio el textview
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
        class Connect(private val mContext: Context) :
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
