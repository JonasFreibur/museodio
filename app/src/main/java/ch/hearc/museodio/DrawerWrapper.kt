package ch.hearc.museodio

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import ch.hearc.museodio.api.ServiceAPI
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

open class DrawerWrapper : AppCompatActivity() {

    companion object {
        var lastStartedActivity: Int = R.id.nav_home;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_wrapper);

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar) as MaterialToolbar
        setSupportActionBar(toolbar)
        toolbar.showOverflowMenu()
        toolbar.setNavigationIcon(R.drawable.ic_menu)
        toolbar.setNavigationOnClickListener {
            val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout) as DrawerLayout

            if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                drawerLayout.closeDrawer(Gravity.LEFT)
            }else {
                drawerLayout.openDrawer(Gravity.LEFT)
            }
        }

        val navigationView = findViewById<NavigationView>(R.id.navigation_view) as NavigationView
        navigationView.setNavigationItemSelectedListener {
            val id = it.getItemId();

            if(lastStartedActivity != id){
                when (id) {
                    R.id.nav_home -> {
                        val homeActivityIntent = Intent(this, MainActivity::class.java)
                        startActivity(homeActivityIntent)
                        lastStartedActivity = id
                        true
                    }
                    R.id.nav_search_user -> {
                        val searchUserActivityIntent = Intent(this, SearchActivity::class.java)
                        startActivity(searchUserActivityIntent)
                        lastStartedActivity = id
                        true
                    }
                    R.id.nav_friendship -> {
                        val friendActivity = Intent(this, FriendActivity::class.java)
                        startActivity(friendActivity)
                        lastStartedActivity = id
                        true
                    }
                    R.id.nav_friendship_invitation -> {
                        val friendshipInvitationActivity = Intent(this, FriendshipInvitationActivity::class.java)
                        startActivity(friendshipInvitationActivity)
                        lastStartedActivity = id
                        true
                    }
                    R.id.nav_logout -> {
                        ServiceAPI.logout(::logoutCallback)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        finish()
                        true
                    }
                    else -> false
                }
            }
            else{
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout) as DrawerLayout
                drawerLayout.closeDrawer(Gravity.LEFT)
                true
            }
        }
    }

    private fun logoutCallback(isLoggedOut: Boolean){
        if(isLoggedOut){
            runOnUiThread() { Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show()}
            loadLoginActivity()
        } else {
            runOnUiThread() { Toast.makeText(this, "Error while logging out", Toast.LENGTH_LONG).show()}
        }
    }

    private fun loadLoginActivity() {
        val logInActivityIntent = Intent(this, LoginActivity::class.java)
        startActivity(logInActivityIntent)
    }

}