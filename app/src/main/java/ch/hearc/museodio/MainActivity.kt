package ch.hearc.museodio

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
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
import android.net.Uri
import android.os.PowerManager
import java.io.IOException
import android.widget.LinearLayout
import android.view.View.OnClickListener
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.birjuvachhani.locus.Locus
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.io.FileInputStream
import java.net.URI

/* Variables globales */
private const val LOG_TAG_RECORD = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {

    internal var map: MapView? = null
    private lateinit var serviceAPI: ServiceAPI

    /* Initialisation variables record */
    private var fileName: String = ""
    private var recordButton: RecordButton? = null
    private var recorder: MediaRecorder? = null
    private var playButton: PlayButton? = null
    private var saveButton: SaveButton? = null
    private var player: MediaPlayer = MediaPlayer()
    private var permissionToRecordAccepted = false
    private var isMapCentered: Boolean = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_COARSE_LOCATION)

    /* Fonction onCreate() : initialise les éléments de la page et gère les permissions*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        setContentView(R.layout.activity_main)
        map = findViewById<MapView>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)

        /* Permissions */
        val permissionRecord =  ActivityCompat.checkSelfPermission(this, permissions[1])
        if ( permissionRecord!= PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        }

        /* Initialisation record élément */
        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"
        recordButton = RecordButton(this)
        playButton = PlayButton(this)
        saveButton = SaveButton(this)
        val l1 = LinearLayout(this).apply {
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
        linearLayout.addView(l1)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        /* permission record */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()

    }
    private  fun stopRequestingLocation() {
        Locus.stopLocationUpdates()
    }

    private fun addAudioNoteToMap(audioNote: AudioNote){
        val startPoint = GeoPoint(audioNote.latitude, audioNote.longitude)
        val startMarker = Marker(map!!)
        startMarker.setPosition(startPoint)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.subDescription = "${audioNote.firstName}, ${audioNote.lastName}"

        startMarker.setOnMarkerClickListener { marker, mapView ->
            val bearerToken = ServiceAPI.loadApiKey(this.applicationContext)
            this@MainActivity.playFile(audioNote.file_name, bearerToken)
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
        //myLocationOverlay.enableFollowLocation()
        //myLocationOverlay.enableMyLocation()
        map!!.getOverlays().add(myLocationOverlay)
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
            //lancer a l'API
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
        Log.i("Bonjour2", filename)
        player.apply {
            try {
                setDataSource(
                    this@MainActivity.applicationContext,
                    Uri.parse("http://10.0.2.2:8000/api/audio-notes/download/$filename"),
                    headers
                )
                prepare()
                setWakeMode(this@MainActivity, PowerManager.PARTIAL_WAKE_LOCK)
                setOnPreparedListener(MediaPlayer.OnPreparedListener {
                    start()
                })
            } catch (e: Exception){
                Log.e(LOG_TAG_RECORD, "Error : $e")
            } catch (e: IOException) {
                Log.e(LOG_TAG_RECORD, "prepare() failed $e")
            }
        }
    }

    private fun startPlaying() {
        player.apply {
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
        player.release()
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
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

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player.release()
    }
}


