package ch.hearc.museodio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Users
import kotlinx.android.synthetic.main.activity_friends.*
import org.osmdroid.config.Configuration


private val data  = ArrayList<String>()

class FriendsActivity : AppCompatActivity() {

    val friendsGridFragment=FriendsGridFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        data.clear()

        btnTest.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                val bearerToken = ServiceAPI.loadApiKey(applicationContext)
                val stringText = "a"

                data.clear()

                ServiceAPI.fetchSearchUsers(bearerToken, stringText, ::printUsers)



            }

        })

        //val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        //listView.adapter = adapter

        //val intent = Intent(this, FriendsGridFragment::class.java)
        //this.navigateUpTo(intent)


    }

    private fun printUsers(user: Users.Success){
        data.add(user.firstname.toString())
        data.add(user.lastname.toString())
        //listView.adapter=adapter
    }


}

