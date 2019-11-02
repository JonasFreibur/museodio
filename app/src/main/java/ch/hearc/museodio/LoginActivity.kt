package ch.hearc.museodio

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import ch.hearc.museodio.api.ServiceAPI
import kotlinx.android.synthetic.main.activity_login.*
import android.content.Intent
import android.widget.Toast


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonLogin.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                var email = editTextEmail.text.toString()
                var password = editTextPassword.text.toString()

                if(email.trim().isEmpty() || password.trim().isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Empty email or password", Toast.LENGTH_LONG).show()
                }
                else {
                    ServiceAPI.login(email, password, this@LoginActivity.applicationContext, ::loginCallback)
                }

            }
        })
    }

    private fun loginCallback(isLoggedIn: Boolean){
        if(isLoggedIn){
            loadMainActivity()
        } else {
            runOnUiThread() {Toast.makeText(this, "Incorrect password or email", Toast.LENGTH_LONG).show()}
        }
    }

    private fun loadMainActivity() {
        val myIntent = Intent(this, MainActivity::class.java)
        startActivity(myIntent)
    }



}
