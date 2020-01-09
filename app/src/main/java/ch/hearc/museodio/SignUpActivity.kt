/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import ch.hearc.museodio.api.ServiceAPI
import kotlinx.android.synthetic.main.activity_sign_up.*

/**
 * SignUpActivity : Activity for the user to sign up
 */
class SignUpActivity: DrawerWrapper() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        buttonSignUp.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                Log.i("Bonjour", "on click")
                val firstName = editTextFirstName.text.toString()
                val lastName = editTextLastName.text.toString()
                val email = editTextSignupEmail.text.toString()
                val password = editTextPasswordSignUp.text.toString()
                val passwordConfirmation = editTextPasswordConfirmation.text.toString()

                if(firstName.trim().isEmpty() || lastName.trim().isEmpty() ||
                    email.trim().isEmpty() || password.trim().isEmpty() ||
                    passwordConfirmation.trim().isEmpty()) {
                    Toast.makeText(this@SignUpActivity, "Empty email or password", Toast.LENGTH_LONG).show()
                }
                else {
                    ServiceAPI.signUp(firstName, lastName, email,
                                      password, passwordConfirmation, this@SignUpActivity.applicationContext, ::signUpCallback)
                }
            }
        })

        buttonLoginIntent.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                loadLoginActivity()
            }
        })
    }

    private fun signUpCallback(isSignedUp: Boolean) {
        if(isSignedUp){
            runOnUiThread() {Toast.makeText(this, "Successfully signed up", Toast.LENGTH_SHORT).show()}
            loadLoginActivity()
        } else {
            runOnUiThread() {Toast.makeText(this, "Failed to sign up, make sure all fields are correct", Toast.LENGTH_LONG).show()}
        }
    }

    private  fun loadLoginActivity() {
        val loginActivityIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginActivityIntent)
    }
}