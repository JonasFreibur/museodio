package ch.hearc.museodio


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FriendsCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var firstName: TextView = itemView.findViewById(R.id.tvFirstName)
    var lastName: TextView = itemView.findViewById(R.id.tvLastName)
}