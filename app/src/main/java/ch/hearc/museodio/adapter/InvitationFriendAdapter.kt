package ch.hearc.museodio.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.museodio.R
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Friends
import kotlinx.android.synthetic.main.activity_list_invitation_friend.view.*


class InvitationFriendAdapter( val items : ArrayList<Friends.Friend>, val context: Context) : RecyclerView.Adapter<ViewHolderInvitation>() {

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
                ServiceAPI.deleteFriend(bearerToken,v?.id!!)
            }
        })
        holder.btnAccept?.id = items.get(position).id
        holder.btnAccept.setOnClickListener (object: View.OnClickListener {
            override fun onClick(v: View?) {
                val bearerToken = ServiceAPI.loadApiKey(this@InvitationFriendAdapter.context)
                ServiceAPI.acceptFriend(bearerToken,v?.id!!)
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
    val tvLastName =view.tvLastName
    val btnAccept= view.btnAccept
    val btnDelete= view.btnDelete
}