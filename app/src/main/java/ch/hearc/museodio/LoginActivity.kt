package ch.hearc.museodio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import ch.hearc.museodio.api.ServiceAPI
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonLogin.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                var email = editTextEmail.text.toString()
                var password = editTextPassword.text.toString()

                ServiceAPI.login(email, password)
            }
        })
    }
}
