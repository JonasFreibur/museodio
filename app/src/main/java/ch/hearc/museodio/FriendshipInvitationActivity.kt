/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ch.hearc.museodio.adapter.InvitationFriendAdapter
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Friends
import kotlinx.android.synthetic.main.activity_friend.*
import kotlinx.android.synthetic.main.drawer_wrapper.*

/**
 * FriendshipInvitationActivity : Activity for the user to display its friendship invitations
 */
class FriendshipInvitationActivity : DrawerWrapper() {

    private val listInvitationToAnswer = ArrayList<Friends.Friend>()

    /**
     * onCreate function
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutInflater: LayoutInflater = LayoutInflater.from(applicationContext);

        layoutInflater.inflate(
            R.layout.activity_friendship_invitation,
            content_layout,
            true
        )

        loadRelationships()

        var adapterInvitation = InvitationFriendAdapter(listInvitationToAnswer, this, ::displayStatus)
        rv_invitation_friend.layoutManager = LinearLayoutManager(this)
        rv_invitation_friend.adapter = adapterInvitation
    }

    private fun loadRelationships() {
        val bearerToken = ServiceAPI.loadApiKey(applicationContext)
        listInvitationToAnswer.clear()
        ServiceAPI.fetchFriends(bearerToken, ::addFriend)
    }

    /**
     * Displays a status feedback after operation
     */
    fun displayStatus(message: String){
        runOnUiThread{ Toast.makeText(this, message, Toast.LENGTH_LONG).show() }
        loadRelationships()
    }

    /**
     * Callback function called by the API request to display the list
     */
    fun addFriend(friends: Array<Friends.Friend>, invitationToAnswer:Array<Friends.Friend>,
                  invitationWaitingForAnswer:Array<Friends.Friend>)
    {
        listInvitationToAnswer.addAll(invitationToAnswer)

        runOnUiThread {
            rv_invitation_friend.adapter?.notifyDataSetChanged()
            rv_invitation_friend.adapter?.notifyDataSetChanged()
        }
    }
}
