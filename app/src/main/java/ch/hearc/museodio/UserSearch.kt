package ch.hearc.museodio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Users
import kotlinx.android.synthetic.main.activity_friends.*
import org.osmdroid.config.Configuration

class UserSearch : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))


        btnTest.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                val bearerToken = ServiceAPI.loadApiKey(applicationContext)
                ServiceAPI.fetchSearchUsers(bearerToken,::printUsers)
            }
        })
    }


    private fun printUsers(user: Users){
        //display dans une liste avec une bouton
    }
}
