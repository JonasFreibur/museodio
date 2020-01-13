package ch.hearc.museodio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.hearc.museodio.api.ServiceAPI


class FriendActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)
        val bearerToken = ServiceAPI.loadApiKey(applicationContext)
        //ServiceAPI.fetchFriends(bearerToken,::addFriend)


    }

    fun addFriend()
    {

    }

}