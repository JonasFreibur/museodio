/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Users
import kotlinx.android.synthetic.main.activity_friends.*


/**
 * LoginActivity : Application's entry point
 */
class FriendsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        btnTest.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                ServiceAPI.fetchSearchUsers(::printUsers)
            }
        })
    }

    private fun printUsers(user:Users){
    }
}
