/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable

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
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import android.net.Uri
import android.os.PowerManager
import android.view.Gravity
import java.io.IOException
import android.widget.LinearLayout
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import ch.hearc.museodio.util.Util
import com.birjuvachhani.locus.Locus
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider

/* Global variables*/
private const val LOG_TAG_RECORD = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private var fileName: String = ""

/**
 * MainActivity : Activity with all the applications's business
 */
class MainActivity : AppCompatActivity() {

    /* Initialisation variables */
    internal var map: MapView? = null
    private lateinit var serviceAPI: ServiceAPI
    private var recordButton: RecordButton? = null
    private var recorder: MediaRecorder? = null
    private var playButton: PlayButton? = null
    private var saveButton: SaveButton? = null
    private var player: MediaPlayer ?= null
    private var permissionToRecordAccepted = false
    private var isMapCentered: Boolean = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_COARSE_LOCATION)


    /* Fonction onCreate() : initialise les éléments de la page et gère les permissions*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_main)

        /* Config Map */
        map = findViewById<MapView>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)

        /* Permissions */
        val permissionRecord =  ActivityCompat.checkSelfPermission(this, permissions[0])
        if ( permissionRecord!= PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        }

        /* Initialisation record elements */
        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.mp3"
        recordButton = RecordButton(this)
        playButton = PlayButton(this)
        saveButton = SaveButton(this)

        val linearLayoutRecord = LinearLayout(this).apply {
            addView(recordButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
            addView(playButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
            addView(saveButton,LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0f))
        }

        linearLayout.addView(linearLayoutRecord)

        setUpToolBar()

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

    private fun setUpToolBar(){
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

            when (id){
                R.id.nav_search_user -> {
                    val searchUserActivityIntent = Intent(this, UserSearch::class.java)
                    startActivity(searchUserActivityIntent)
                    true
                }
                R.id.nav_logout -> {
                    ServiceAPI.logout(::logoutCallback)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun logoutCallback(isLoggedOut: Boolean){
        if(isLoggedOut){
            runOnUiThread() {Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show()}
            loadLoginActivity()
        } else {
            runOnUiThread() {Toast.makeText(this, "Error while logging out", Toast.LENGTH_LONG).show()}
        }
    }

    private fun loadLoginActivity() {
        val logInActivityIntent = Intent(this, LoginActivity::class.java)
        startActivity(logInActivityIntent)
    }

    private fun startRequestingLocation() {
        Locus.startLocationUpdates(this) { result ->
            result.location?.let { addLocationToMap(it.latitude, it.longitude) }
        }
    }

    private  fun stopRequestingLocation() {
        Locus.stopLocationUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun addAudioNoteToMap(audioNote: AudioNote){
        val startPoint = GeoPoint(audioNote.latitude, audioNote.longitude)

        val drawable: Drawable = resources.getDrawable(R.drawable.ic_guitar_pick_outline, null);

        val startMarker = Marker(map!!)
        startMarker.setPosition(startPoint)
        startMarker.icon = drawable
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.title = "${audioNote.firstName}, ${audioNote.lastName}"
        startMarker.subDescription = "${audioNote.latitude}, ${audioNote.longitude}"
        startMarker.setOnMarkerClickListener { marker, mapView ->
            val bearerToken = ServiceAPI.loadApiKey(this.applicationContext)
            this@MainActivity.playFile(audioNote.file_name, bearerToken)
            marker.showInfoWindow()
            false
        }
        map!!.getOverlays().add(startMarker)
    }

    private fun addLocationToMap(latitude: Double, longitude: Double){
        val mapController = map!!.getController()
        if(!isMapCentered) {
            mapController.setZoom(9.5)
            val startPoint = GeoPoint(latitude, longitude)
            mapController.setCenter(startPoint)
            isMapCentered = true
        }


        val myLocationOverlay = MyLocationNewOverlay(map!!)
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.enableMyLocation()
        map!!.getOverlays().add(myLocationOverlay)


        /*
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.user_location);
        locationOverlay.setPersonIcon(icon);
        locationOverlay.setPersonHotspot(icon.getWidth() / 2, icon.getHeight());
        map.getOverlays().add(locationOverlay);
        */
    }

    /* functions and classes to record part */
    internal inner class RecordButton(ctx: Context) : AppCompatButton(ctx) {
        var mStartRecording = true
        var clicker: OnClickListener = OnClickListener {
            onRecord(mStartRecording)
            text = when (mStartRecording) {
                true -> "Stop recording"
                false -> "Start recording"
            }
            mStartRecording = !mStartRecording
        }
        init {
            text = "Start recording"
            setOnClickListener(clicker)
        }
    }

    internal inner class SaveButton(ctx: Context) : AppCompatButton(ctx) {
        var clicker: OnClickListener = OnClickListener {

            val alertDialog = AlertDialog.Builder(this@MainActivity)
            alertDialog.setTitle("Save Record")
            alertDialog.setMessage("Are you sure to want to save your record")
            alertDialog.setPositiveButton("YES"){dialog, which ->
                Locus.getCurrentLocation(ctx){result->
                    var lat=result.location?.latitude
                    var lon=result.location?.longitude
                    if(lat !=null && lon!=null) {
                        ServiceAPI.uploadAudioNote(ServiceAPI.loadApiKey(ctx) ,lat,lon,fileName)
                    }
                }
                Toast.makeText(applicationContext,"Saved success",Toast.LENGTH_SHORT).show()

            }
            alertDialog.setNegativeButton("No"){dialog,which ->
                Toast.makeText(applicationContext,"Cancel saved",Toast.LENGTH_SHORT).show()
            }
            val dialog: AlertDialog = alertDialog.create()
            dialog.show()
        }
        init {
            text = "Save recording"
            setOnClickListener(clicker)
        }
    }

    internal inner class PlayButton(ctx: Context) : AppCompatButton(ctx) {
        var mStartPlaying = true
        var clicker: OnClickListener = OnClickListener {
            onPlay(mStartPlaying)
            text = when (mStartPlaying) {
                true -> "Stop playing"
                false -> "Start playing"
            }
            mStartPlaying = !mStartPlaying
        }
        init {
            text = "Start playing"
            setOnClickListener(clicker)
        }
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun playFile(filename: String, token: String){
        val headers: Map<String, String>? = mapOf("Authorization" to "Bearer $token")
        headers.toString().replace("=", ":")
        stopPlaying()
        stopRecording()
        player=MediaPlayer().apply {
            try{
                setDataSource(
                    this@MainActivity.applicationContext,
                    //Uri.parse("http://10.0.2.2:81/museodio/public/api/audio-notes/download/$filename"),
                    //Uri.parse("http://10.0.2.2:8000/api/audio-notes/download/$filename"),
                    Uri.parse("https://museodio.srvz-webapp.he-arc.ch/api/audio-notes/download/$filename"),
                    headers
                )
                prepare()
                setWakeMode(this@MainActivity, PowerManager.PARTIAL_WAKE_LOCK)
                setOnPreparedListener(MediaPlayer.OnPreparedListener {
                    start()
                })
                setOnCompletionListener(MediaPlayer.OnCompletionListener() {
                    stopPlaying()
                })
                setOnErrorListener(MediaPlayer.OnErrorListener() { mp : MediaPlayer, what: Int, extras: Int ->
                    Log.e(LOG_TAG_RECORD, "Failed to play song")
                    false
                })
            } catch (e: IOException) {
                Log.e(LOG_TAG_RECORD, "prepare() failed $e")
            } catch (e: Exception){
                Log.e(LOG_TAG_RECORD, "Error : $e")
            }
        }
    }

    private fun startPlaying() {
        stopPlaying()
        stopRecording()
        player = MediaPlayer().apply {
            try {
                reset()
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG_RECORD, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.stop()
        player?.release()
        player = null
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun startRecording() {
        stopPlaying()
        stopRecording()
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG_RECORD, "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

}

/*
source : - https://developer.android.com/guide/topics/media/mediarecorder
            - https://android--code.blogspot.com/2018/02/android-kotlin-alertdialog-example.html

*/