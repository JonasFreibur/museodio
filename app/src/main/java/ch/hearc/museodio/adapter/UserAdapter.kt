package ch.hearc.museodio.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.museodio.R
import ch.hearc.museodio.api.model.Users
import ch.hearc.museodio.api.ServiceAPI
import kotlinx.android.synthetic.main.activity_list_search.view.*


class UserAdapter(val items : ArrayList<Users.Success>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
       return  items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvFirstName?.text = items.get(position).firstname
        holder.tvLastName?.text = items.get(position).lastname
        holder.btn?.id = items.get(position).id
        holder.btn.setOnClickListener (object: View.OnClickListener {
            override fun onClick(v: View?) {
                Log.i("USER ON CLICK",v?.id.toString())
                val bearerToken = ServiceAPI.loadApiKey(this@UserAdapter.context)
                ServiceAPI.addFriend(bearerToken,v?.id!!)

            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.activity_list_search,
                parent,
                false
            )
        )
    }


}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvFirstName = view.tvFirstName
    val tvLastName =view.tvLastName
    val btn= view.btn
}