package ch.hearc.museodio

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.Users
import kotlinx.android.synthetic.main.test_card_frag.*


class TestActivity : AppCompatActivity() {

    var listUser = ArrayList<Users.Success>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_card_frag)

        btnRecherche.setOnClickListener (object: View.OnClickListener {
            override fun onClick(v: View?) {

                val bearerToken = ServiceAPI.loadApiKey(applicationContext)
                val stringText = "a"

                listUser.clear()
                //Log.i("USER bea",bearerToken)

                ServiceAPI.fetchSearchUsers(bearerToken, stringText, ::addUser)
               // addUser(Users.Success(0," w","sdfjh"))


            }

        })



        var adapter = UserAdapter(listUser,this)

        // Creates a vertical Layout Manager
        //recyclerView.layoutManager = LinearLayoutManager(this)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        recyclerView.adapter = adapter

        Log.i("USER LIST",listUser.toString())
        //re?.adapter=adapter
    }

    fun addUser(user:Users.Success)
    {
        listUser.add(user)
        Log.i("USER LIST A",listUser.toString())

        //adapter?.onAddItem("coucou")
        recyclerView.adapter?.notifyDataSetChanged()
    }
}