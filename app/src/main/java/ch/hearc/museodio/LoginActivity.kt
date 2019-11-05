package ch.hearc.museodio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                if(email.trim().isEmpty() || password.trim().isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Empty email or password", Toast.LENGTH_LONG).show()
                }
                else {
                    ServiceAPI.login(email, password, this@LoginActivity.applicationContext, ::loginCallback)
                }

            }
        })

        buttonSignUpIntent.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                loadSignUpActivity()
            }
        })
    }

    private fun loginCallback(isLoggedIn: Boolean){
        if(isLoggedIn){
            runOnUiThread() {Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()}
            loadMainActivity()
        } else {
            runOnUiThread() {Toast.makeText(this, "Incorrect password or email", Toast.LENGTH_LONG).show()}
        }
    }

    private fun loadMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
    }

    private fun loadSignUpActivity() {
        val signUpActivityIntent = Intent(this, SignUpActivity::class.java)
        startActivity(signUpActivityIntent)
    }


}
