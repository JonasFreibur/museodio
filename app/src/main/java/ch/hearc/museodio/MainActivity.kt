package ch.hearc.museodio


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import com.birjuvachhani.locus.Locus


class MainActivity : AppCompatActivity() {

    internal var map: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        setContentView(R.layout.activity_main)
        map = findViewById<MapView>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)


        startRequestingLocation()
    }

    public override fun onResume() {
        super.onResume()

        map!!.onResume()

        startRequestingLocation()
    }

    public override fun onPause() {
        super.onPause()

        map!!.onPause()

        stopRequestingLocation()
    }

    private fun startRequestingLocation() {
        Locus.startLocationUpdates(this) { result ->
            result.location?.let { addLocationToMap(it.latitude, it.longitude) }
        }
    }

    private  fun stopRequestingLocation() {
        Locus.stopLocationUpdates()
    }

    private fun addLocationToMap(latitude: Double, longitude: Double){
        val mapController = map!!.getController()
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(latitude, longitude)
        mapController.setCenter(startPoint)


        val myLocationOverlay = MyLocationNewOverlay(map!!)
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.enableMyLocation()
        map!!.getOverlays().add(myLocationOverlay)
    }

}


