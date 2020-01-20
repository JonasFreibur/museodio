/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ch.hearc.museodio.adapter.FriendAdapter
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Friends
import kotlinx.android.synthetic.main.activity_friend.*
import kotlinx.android.synthetic.main.drawer_wrapper.*

/**
 * FriendActivity : Activity for the user to display its friends
 */
class FriendActivity : DrawerWrapper() {

    private val listFriends = ArrayList<Friends.Friend>()

    /**
     * onCreate function
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutInflater: LayoutInflater = LayoutInflater.from(applicationContext);

        layoutInflater.inflate(
            R.layout.activity_friend,
            content_layout,
            true
        )

        var adapterFriend = FriendAdapter(listFriends, this, ::displayStatus)
        rv_friend.layoutManager = LinearLayoutManager(this)
        rv_friend.adapter = adapterFriend

        loadFriendList()
    }

    /**
     * Loads the list of the friends
     */
    private fun loadFriendList() {
        val bearerToken = ServiceAPI.loadApiKey(applicationContext)
        listFriends.clear()
        ServiceAPI.fetchFriends(bearerToken, ::addFriend)
    }

    /**
     * Displays a status feedback after delete operation
     */
    fun displayStatus(message: String){
        runOnUiThread{ Toast.makeText(this, message, Toast.LENGTH_LONG).show() }
        loadFriendList()
    }

    /**
     * Callback function called by the API request
     */
    fun addFriend(friends: Array<Friends.Friend>, invitationToAnswer:Array<Friends.Friend>,
                  invitationWaitingForAnswer:Array<Friends.Friend>)
    {
        listFriends.addAll(friends)
        runOnUiThread{ rv_friend.adapter?.notifyDataSetChanged() }
    }
}