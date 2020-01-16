package ch.hearc.museodio

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import ch.hearc.museodio.adapter.FriendAdapter
import ch.hearc.museodio.adapter.InvitationFriendAdapter
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Friends
import kotlinx.android.synthetic.main.activity_friend.*


class FriendActivity : AppCompatActivity() {

    private val listFriends=ArrayList<Friends.Friend>()
    private val listInvitationToAnswer=ArrayList<Friends.Friend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)
        val bearerToken = ServiceAPI.loadApiKey(applicationContext)
        listFriends.clear()
        listInvitationToAnswer.clear()
        ServiceAPI.fetchFriends(bearerToken, ::addFriend)

        var adapterFriend = FriendAdapter(listFriends, this)
        rv_friend.layoutManager = GridLayoutManager(this, 1)
        rv_friend.adapter = adapterFriend

        var adapterInvitation = InvitationFriendAdapter(listInvitationToAnswer, this)
        rv_invitation_friend.layoutManager = GridLayoutManager(this, 1)
        rv_invitation_friend.adapter = adapterInvitation
    }

    fun addFriend(friends: Array<Friends.Friend>, invitationToAnswer:Array<Friends.Friend>, invitationWaitingForAnswer:Array<Friends.Friend>)
    {
        listFriends.addAll(friends)
        listInvitationToAnswer.addAll(invitationToAnswer)
        listFriends.addAll(invitationWaitingForAnswer)
        friends.forEach { f->
            Log.i("FRIEND ADD",f.toString())

        }
        invitationToAnswer.forEach { f->
            Log.i("INV FRIEND ADD",f.toString())

        }
        rv_invitation_friend.adapter?.notifyDataSetChanged()
        rv_friend.adapter?.notifyDataSetChanged()

    }

}