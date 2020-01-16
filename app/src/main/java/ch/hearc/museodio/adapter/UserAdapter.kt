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
import ch.hearc.museodio.api.model.Users
import ch.hearc.museodio.api.ServiceAPI
import kotlinx.android.synthetic.main.activity_list_search.view.*


class UserAdapter(val items : ArrayList<Users.Success>,
                  val context: Context,
                  val callbackFn: (message: String) -> Unit) : RecyclerView.Adapter<ViewHolderUser>() {

    override fun getItemCount(): Int {
       return  items.size
    }

    override fun onBindViewHolder(holder: ViewHolderUser, position: Int) {
        holder.tvFirstName?.text = items.get(position).firstname
        holder.tvLastName?.text = items.get(position).lastname
        holder.btn?.id = items.get(position).id
        holder.btn.setOnClickListener (object: View.OnClickListener {
            override fun onClick(v: View?) {
                val bearerToken = ServiceAPI.loadApiKey(this@UserAdapter.context)
                if(v?.id !=null) {
                    ServiceAPI.addFriend(bearerToken, v?.id, callbackFn)
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderUser {
        return ViewHolderUser(
            LayoutInflater.from(context).inflate(
                R.layout.activity_list_search,
                parent,
                false
            )
        )
    }
}

class ViewHolderUser (view: View) : RecyclerView.ViewHolder(view) {
    val tvFirstName = view.tvFirstName
    val tvLastName =view.tvLastName
    val btn= view.btnAccept
}