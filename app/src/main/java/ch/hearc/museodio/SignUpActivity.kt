package ch.hearc.museodio

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ch.hearc.museodio.api.ServiceAPI
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        buttonSignUp.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                // Call sign up api
            }
        })
    }
}