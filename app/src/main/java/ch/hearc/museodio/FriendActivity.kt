package ch.hearc.museodio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ch.hearc.museodio.adapter.FriendAdapter
import ch.hearc.museodio.adapter.InvitationFriendAdapter
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Friends
import kotlinx.android.synthetic.main.activity_friend.*
import kotlinx.android.synthetic.main.drawer_wrapper.*


class FriendActivity : DrawerWrapper() {

    private val listFriends=ArrayList<Friends.Friend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutInflater: LayoutInflater = LayoutInflater.from(applicationContext);

        layoutInflater.inflate(
            R.layout.activity_friend,
            content_layout,
            true
        )

        val bearerToken = ServiceAPI.loadApiKey(applicationContext)
        listFriends.clear()
        ServiceAPI.fetchFriends(bearerToken, ::addFriend)

        var adapterFriend = FriendAdapter(listFriends, this)
        rv_friend.layoutManager = LinearLayoutManager(this)
        rv_friend.adapter = adapterFriend
    }

    fun addFriend(friends: Array<Friends.Friend>, invitationToAnswer:Array<Friends.Friend>, invitationWaitingForAnswer:Array<Friends.Friend>)
    {
        listFriends.addAll(friends)
        listFriends.addAll(invitationWaitingForAnswer)

        rv_friend.adapter?.notifyDataSetChanged()

    }

}