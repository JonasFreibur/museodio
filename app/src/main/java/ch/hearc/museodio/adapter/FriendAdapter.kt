/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.museodio.R
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Friends
import kotlinx.android.synthetic.main.activity_list_friend.view.*

/**
 * Class to adapt a list of Friend in items of RecyclerView
 *
 * @property ArrayList<Friends.Friend> items: list of Friends
 * @property Context context: context of the application
 */
class FriendAdapter( val items : ArrayList<Friends.Friend>, val context: Context) : RecyclerView.Adapter<ViewHolderFriend>() {

    /**
     * Function to return to number of items
     */
    override fun getItemCount(): Int {
        return  items.size
    }

    /**
     * Function to bind all the list items to a view
     */
    override fun onBindViewHolder(holder: ViewHolderFriend, position: Int) {
        holder.tvFirstName?.text = items.get(position).firstname
        holder.tvLastName?.text = items.get(position).lastname
        holder.btnDelete?.id = items.get(position).id
        holder.btnDelete.setOnClickListener (object: View.OnClickListener {
            override fun onClick(v: View?) {
                val bearerToken = ServiceAPI.loadApiKey(this@FriendAdapter.context)
                if(v?.id != null) {
                    ServiceAPI.deleteFriend(bearerToken, v?.id)
                }
            }
        })
    }

    /**
     * Function to inflate the view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFriend {
        return ViewHolderFriend(
            LayoutInflater.from(context).inflate(
                R.layout.activity_list_friend,
                parent,
                false
            )
        )
    }
}

/**
 * Class to make the conversion between View and the list
 */
class ViewHolderFriend (view: View) : RecyclerView.ViewHolder(view) {
    val tvFirstName = view.tvFirstName
    val tvLastName =view.tvLastName
    val btnDelete= view.btnDelete
}