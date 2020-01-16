/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import ch.hearc.museodio.adapter.UserAdapter
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Users
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.drawer_wrapper.*



class SearchActivity : DrawerWrapper() {

    private var listUser = ArrayList<Users.Success>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutInflater: LayoutInflater = LayoutInflater.from(applicationContext);

        layoutInflater.inflate(
            R.layout.activity_search,
            content_layout,
            true
        )

        btnResearch.setOnClickListener (object: View.OnClickListener {
            override fun onClick(v: View?) {
                val bearerToken = ServiceAPI.loadApiKey(applicationContext)

                if(!researchBar.text.toString().isEmpty())
                {
                    val stringText = researchBar.text.toString()
                    listUser.clear()
                    ServiceAPI.fetchSearchUsers(bearerToken, stringText, ::addUser)
                }
            }
        })

        var adapter = UserAdapter(listUser, this, ::displayStatus)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        recyclerView.adapter = adapter
    }

    fun displayStatus(message: String){
        runOnUiThread{ Toast.makeText(this, message, Toast.LENGTH_LONG).show() }
    }

    fun addUser(users: Array<Users.Success>)
    {
        users.forEach { user ->
            listUser.add(user)
        }

        recyclerView.adapter?.notifyDataSetChanged()
    }
}