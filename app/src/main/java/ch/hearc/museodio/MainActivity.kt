package ch.hearc.museodio


import androidx.appcompat.app.AppCompatActivity
import android.preference.PreferenceManager
import android.content.Context
import org.osmdroid.config.Configuration
import android.os.Bundle

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView


class MainActivity : AppCompatActivity() {

    internal var map: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))


        setContentView(R.layout.activity_main)
        map = findViewById<MapView>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)
    }

    public override fun onResume() {
        super.onResume()

        map!!.onResume()
    }

    public override fun onPause() {
        super.onPause()

        map!!.onPause()
    }
}

