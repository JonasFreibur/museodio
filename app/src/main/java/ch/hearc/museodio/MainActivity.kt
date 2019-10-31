package ch.hearc.museodio


import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import ch.hearc.museodio.api.ServiceAPI
import ch.hearc.museodio.api.model.AudioNote
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import com.birjuvachhani.locus.Locus
import org.osmdroid.views.overlay.Marker


class MainActivity : AppCompatActivity() {

    internal var map: MapView? = null
    private lateinit var serviceAPI: ServiceAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        setContentView(R.layout.activity_main)
        map = findViewById<MapView>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)


        startRequestingLocation()

        ServiceAPI.fetchAllAudioNotes(::addAudioNoteToMap)
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

    private fun addAudioNoteToMap(audioNote: AudioNote){
        val startPoint = GeoPoint(audioNote.latitude, audioNote.longitude)
        val startMarker = Marker(map!!)
        startMarker.setPosition(startPoint)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map!!.getOverlays().add(startMarker)
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


