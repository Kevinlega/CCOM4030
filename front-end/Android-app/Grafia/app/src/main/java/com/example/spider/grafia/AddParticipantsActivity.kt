package com.example.spider.grafia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_participants.*

class AddParticipantsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_participants)

            BackToProject.setOnClickListener {
                val intent = Intent(this@AddParticipantsActivity, ProjectActivity::class.java)
                // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
                startActivity(intent)
            }

        AddParticipants.setOnClickListener {
            val intent = Intent(this@AddParticipantsActivity, ProjectActivity::class.java)
            // To pass any data to next activity
//            intent.putExtra("keyIdentifier", value)
//             start your next activity
            startActivity(intent)
        }


    }
}
