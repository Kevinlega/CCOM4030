package com.example.spider.grafia

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.AsyncTask
import android.os.CancellationSignal
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class FingerprintHandler(private val appContext: Context) : FingerprintManager.
AuthenticationCallback() {

    private var cancellationSignal: CancellationSignal? = null
    private var mContext : Context

    init {
        mContext = appContext
    }

    fun startAuth(manager: FingerprintManager,
                  cryptoObject: FingerprintManager.CryptoObject) {
        cancellationSignal = CancellationSignal()
        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
            PackageManager.PERMISSION_GRANTED) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationHelp(helpMsgId: Int,
                                      helpString: CharSequence) {
        Toast.makeText(appContext,
            "Authentication help\n" + helpString,
            Toast.LENGTH_LONG).show()
    }
    override fun onAuthenticationFailed() {
        Toast.makeText(appContext,
            "Authentication failed.",
            Toast.LENGTH_LONG).show()
    }
    override fun onAuthenticationSucceeded(
        result: FingerprintManager.AuthenticationResult) {
        Toast.makeText(appContext,
            "Authentication succeeded.",
            Toast.LENGTH_LONG).show()

        val sharedPreferences = mContext.getSharedPreferences("Grafia_Login", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("Email", "")
        val password = sharedPreferences.getString("Password", "")

        val query = 4
        val connectToAPI = Connect(mContext, 2,email as String,password as String)
        try{
            val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"
            println(url)
            connectToAPI.execute(url)
        }
        catch (error: Exception){}

    }

    // Verifies Login data
    companion object {
        class Connect(private val mContext: Context,private val flag: Int, private val email: String, private val password: String) :
            AsyncTask<String, Void, String>() {

            // Send uid retrieve request
            private fun getUID(email: String) {
                val query = 2
                val connectToAPI = Connect(mContext, 1, email, password)
                try {
                    val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"
                    println(url)
                    connectToAPI.execute(url)
                } catch (error: Exception) {
                }
            }

            override fun doInBackground(vararg p0: String?): String {
                return downloadJSON(p0[0])
            }

            private fun downloadJSON(url: String?): String {
                return URL(url).readText()
            }

            // Gets data and if its correct, retrieves the user id
            override fun onPostExecute(result: String) {
                try {
                    val jSONObject = JSONObject(result)

                    if (flag == 2) {

                        val registered = jSONObject.getBoolean("empty")

                        if (!registered) {

                            val dbPassword = jSONObject.getString("hashed_password")

                            if (password == dbPassword) {
                                getUID(email)
                            } else {
                                Toast.makeText(mContext, "Incorrect login info, try again.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(mContext, "Incorrect login info, try again.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val uid = jSONObject.getInt("uid")
                        val verified = jSONObject.getInt("verified")

                        if (verified == 1) {
                            val intent = Intent(mContext, DashboardActivity::class.java)
                            intent.putExtra("userId", uid)
                            mContext.startActivity(intent)
                        } else {
                            val intent = Intent(mContext, NotVerifiedActivity::class.java)
                            mContext.startActivity(intent)
                        }
                    }

                } catch (error: Exception) {}
                super.onPostExecute(result)
            }
        }
    }
}
