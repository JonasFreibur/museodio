package ch.hearc.museodio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.museodio.api.model.Users
import kotlinx.android.synthetic.main.test_card_list.view.*

class UserAdapter(val items : ArrayList<Users.Success>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
       return  items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvFirstName?.text = items.get(position).firstname
        holder.tvLastName?.text = items.get(position).lastname
        holder.btn?.id = items.get(position).id
        holder.btn?.text = "ADD"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.test_card_list, parent, false))
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvFirstName = view.tvFirstName
    val tvLastName =view.tvLastName
    val btn= view.btnAddFriend
}