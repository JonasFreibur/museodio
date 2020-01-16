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
import kotlinx.android.synthetic.main.activity_list_invitation_friend.view.*


class InvitationFriendAdapter( val items : ArrayList<Friends.Friend>,
                               val context: Context,
                               val callbackFn: (message: String) -> Unit) : RecyclerView.Adapter<ViewHolderInvitation>() {

    override fun getItemCount(): Int {
        return  items.size
    }

    override fun onBindViewHolder(holder: ViewHolderInvitation, position: Int) {
        holder.tvFirstName?.text = items.get(position).firstname
        holder.tvLastName?.text = items.get(position).lastname
        holder.btnDelete?.id = items.get(position).id
        holder.btnDelete.setOnClickListener (object: View.OnClickListener {
            override fun onClick(v: View?) {
                val bearerToken = ServiceAPI.loadApiKey(this@InvitationFriendAdapter.context)
                if(v?.id != null) {
                    ServiceAPI.deleteFriend(bearerToken, v?.id, callbackFn)
                }
            }
        })
        holder.btnAccept?.id = items.get(position).id
        holder.btnAccept.setOnClickListener (object: View.OnClickListener {
            override fun onClick(v: View?) {
                val bearerToken = ServiceAPI.loadApiKey(this@InvitationFriendAdapter.context)
                if(v?.id != null) {
                    ServiceAPI.acceptFriend(bearerToken, v?.id, callbackFn)
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderInvitation {
        return ViewHolderInvitation(
            LayoutInflater.from(context).inflate(
                R.layout.activity_list_invitation_friend,
                parent,
                false
            )
        )
    }
}

class ViewHolderInvitation (view: View) : RecyclerView.ViewHolder(view) {
    val tvFirstName = view.tvFirstName
    val tvLastName = view.tvLastName
    val btnAccept= view.btnAccept
    val btnDelete= view.btnDelete
}