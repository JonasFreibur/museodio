/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

import android.os.Bundle
import android.preference.PreferenceManager
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
import android.view.LayoutInflater
import java.io.IOException
import android.widget.LinearLayout
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import ch.hearc.museodio.api.model.AudioNoteStatus
import com.birjuvachhani.locus.Locus
import com.google.android.gms.location.LocationRequest
import kotlinx.android.synthetic.main.activity_main.linearLayout
import kotlinx.android.synthetic.main.drawer_wrapper.*
import org.osmdroid.views.overlay.Marker

/* Global variables*/
private const val LOG_TAG_RECORD = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private var fileName: String = ""

/**
 * MainActivity : Activity with all the applications's business
 */
class MainActivity : DrawerWrapper()  {

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
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION)


    /* Fonction onCreate() : initialise les éléments de la page et gère les permissions*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        val layoutInflater:LayoutInflater = LayoutInflater.from(applicationContext);


        layoutInflater.inflate(
            R.layout.activity_main, // Custom view/ layout
            content_layout, // Root layout to attach the view
            true // Attach with root layout or not
        )


        /* Config Map */
        map = findViewById<MapView>(R.id.map) as MapView
        map?.setTileSource(TileSourceFactory.MAPNIK)

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

        //val linearLayout = mainView.findViewById<LinearLayout>(R.id.linearLayout) as LinearLayout;

        linearLayout.addView(linearLayoutRecord);


        startRequestingLocation()

        ServiceAPI.fetchAllAudioNotes(::addAudioNoteToMap)

    }

    public override fun onResume() {
        super.onResume()
        map?.onResume()
        startRequestingLocation()
    }

    public override fun onPause() {
        super.onPause()
        map?.onPause()
        stopRequestingLocation()
    }

    public override fun onBackPressed() {
        // Nothing
    }

    private fun startRequestingLocation() {
        Locus.configure {
            request {
                fastestInterval = 600000 // 10 minutes
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 600000
            }
        }

        Locus.startLocationUpdates(this) { result ->
            result.location?.let {
                addLocationToMap(it.latitude, it.longitude)
            }
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
        val bearerToken = ServiceAPI.loadApiKey(this.applicationContext)

        ServiceAPI.fetchAudioNoteStatus(bearerToken, audioNote.file_name, fun (status: AudioNoteStatus?) {
            val startMarker = Marker(map)
            val startPoint = GeoPoint(audioNote.latitude, audioNote.longitude)
            startMarker.setPosition(startPoint)
            val drawable: Drawable = resources.getDrawable(R.drawable.ic_guitar_pick_outline, null);
            startMarker.icon = drawable
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            startMarker.title = "${audioNote.firstName}, ${audioNote.lastName}"

            if(status?.error != null){
                startMarker.subDescription = status.error?.error
                startMarker.setOnMarkerClickListener { marker, mapView ->
                    marker.showInfoWindow()
                    false
                }
            }
            else {
                startMarker.subDescription = "${audioNote.latitude}, ${audioNote.longitude}"
                startMarker.setOnMarkerClickListener { marker, mapView ->
                    this@MainActivity.playFile(audioNote.file_name, bearerToken)
                    marker.showInfoWindow()
                    false
                }
            }

            map?.getOverlays()?.add(startMarker)
        })
    }

    private fun addLocationToMap(latitude: Double, longitude: Double){
        if(map != null){
            val mapController = map?.getController()
            if(!isMapCentered) {
                mapController?.setZoom(9.5)
                val startPoint = GeoPoint(latitude, longitude)
                mapController?.setCenter(startPoint)
                isMapCentered = true
            }



            val myLocationOverlay = MyLocationNewOverlay(map)
            myLocationOverlay.enableFollowLocation()
            myLocationOverlay.enableMyLocation()
            map?.getOverlays()?.add(myLocationOverlay)
        }
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
            - https://android.jlelse.eu/using-recyclerview-in-android-kotlin-722991e86bf3
*/