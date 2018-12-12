package com.example.spider.grafia

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.CancellationSignal
import android.support.v4.app.ActivityCompat
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class FingerprintHandler(private val appContext: Context) : FingerprintManager.AuthenticationCallback() {

    private var cancellationSignal: CancellationSignal? = null
    private var mContext : Context

    init {
        mContext = appContext
    }

    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
        cancellationSignal = CancellationSignal()
        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
            PackageManager.PERMISSION_GRANTED) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    fun cancel(){

        cancellationSignal?.also {
            it.cancel()
        }
        cancellationSignal = null

    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
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

        Toast.makeText(appContext,"Authentication succeeded.",Toast.LENGTH_LONG).show()

        val sharedPreferences = mContext.getSharedPreferences("Grafia_Login", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("Email", "")
        val password = sharedPreferences.getString("Password", "")

        val query = 4
        val connectToAPI = Connect(mContext, 2, email as String, password as String)
        if(isNetworkAvailable(mContext)) {
            try {
                val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"
                connectToAPI.execute(url)
            } catch (error: Exception) {
            }
        } else {
            showInternetNotification(mContext)
        }
    }


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

                    val Logout = Intent(mContext, LoginActivity::class.java)
                    Logout.putExtra("Failed",true)
                    mContext.startActivity(Logout)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    val Logout = Intent(mContext, FingerprintActivity::class.java)
                    mContext.startActivity(Logout)
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

    private fun isNetworkAvailable(mContext: Context): Boolean {
        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    // Verifies Login data
    companion object {
        class Connect(private val mContext: Context,private val flag: Int, private val email: String, private val password: String) :
            AsyncTask<String, Void, String>() {


            // Method to show an alert dialog with yes, no and cancel button
            private fun showInternetNotification(mContext: Context){
                // Late initialize an alert dialog object
                lateinit var dialog: AlertDialog


                // Initialize a new instance of alert dialog builder object
                val builder = AlertDialog.Builder(mContext)
                // Set a title for alert dialog
                builder.setTitle("Lost Internet Connection.")

                // Set a message for alert dialog
                builder.setMessage("Do you want to return home or retry?")

                // On click listener for dialog buttons
                val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
                    when(which){
                        DialogInterface.BUTTON_POSITIVE -> {

                            val Logout = Intent(mContext, LoginActivity::class.java)
                            Logout.putExtra("Failed",true)
                            mContext.startActivity(Logout)
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                            val Logout = Intent(mContext, FingerprintActivity::class.java)
                            mContext.startActivity(Logout)
                        }
                    }
                }

                // Set the alert dialog positive/yes button
                builder.setPositiveButton("Home",dialogClickListener)
                // Set the alert dialog negative/no button
                builder.setNegativeButton("Retry",dialogClickListener)
                // Initialize the AlertDialog using builder object
                dialog = builder.create()
                // Finally, display the alert dialog
                dialog.show()
            }

            private fun isNetworkAvailable(mContext: Context): Boolean {
                val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            }

            // Send uid retrieve request
            private fun getUID(email: String) {
                if (isNetworkAvailable(mContext)) {
                    val query = 2
                    val connectToAPI = Connect(mContext, 1, email, password)
                    try {
                        val url = "http://54.81.239.120/selectAPI.php?queryType=$query&email=$email"
                        connectToAPI.execute(url)
                    } catch (error: Exception) {
                    }
                } else {
                    showInternetNotification(mContext)
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