package ch.hearc.museodio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.museodio.api.model.Users


/**
 * Adapter used to show a simple grid of products.
 */
class FriendsCardRecyclerViewAdapter(private val usersList: List<Users.Success>) : RecyclerView.Adapter<FriendsCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsCardViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.friends_card, parent, false)
        return FriendsCardViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: FriendsCardViewHolder, position: Int) {
        if (position < usersList.size) {
            val user = usersList[position]
            holder.firstName.text = user.firstname
            holder.lastName.text = user.lastname
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}