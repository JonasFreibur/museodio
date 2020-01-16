package ch.hearc.museodio

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import ch.hearc.museodio.adapter.UserAdapter
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Users
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity() {

    private var listUser = ArrayList<Users.Success>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        btnRecherche.setOnClickListener (object: View.OnClickListener {
            override fun onClick(v: View?) {
                val bearerToken = ServiceAPI.loadApiKey(applicationContext)

                if(!etRecherche.text.toString().isEmpty())
                {
                    val stringText=etRecherche.text.toString()
                    listUser.clear()
                    ServiceAPI.fetchSearchUsers(bearerToken, stringText, ::addUser)
                }
            }
        })

        var adapter = UserAdapter(listUser, this)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        recyclerView.adapter = adapter
    }

    fun addUser(users: Array<Users.Success>)
    {
        users.forEach { user ->
            listUser.add(user)
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

}