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
import com.google.android.gms.location.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    }

    internal var map: MapView? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        setContentView(R.layout.activity_main)
        map = findViewById<MapView>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)

        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSIONS_REQUEST_CODE)
        }
        else {
            if(isLocationEnabled()){
                getLocationUpdates()
                fusedLocationClient!!.lastLocation.addOnCompleteListener{location ->
                    if (location.result != null) {
                        addLocationToMap(location.result.latitude, location.result.longitude)
                    }
                }
            }
            else {
                Toast.makeText(this, "The application requires the location to work correctly", Toast.LENGTH_LONG)
            }
        }
    }

    public override fun onResume() {
        super.onResume()

        map!!.onResume()

        startLocationUpdates()
    }

    public override fun onPause() {
        super.onPause()

        map!!.onPause()

        stopLocationUpdates()
    }

    @SuppressLint("RestrictedApi")
    private fun getLocationUpdates()
    {
        locationRequest = LocationRequest()
            .setInterval(1000)
            .setFastestInterval(1000)
            .setSmallestDisplacement(100f)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    addLocationToMap(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startLocationUpdates()
            }
        }
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


