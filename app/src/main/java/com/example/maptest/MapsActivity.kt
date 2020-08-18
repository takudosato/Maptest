package com.example.maptest

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private inner class WeatherInfoReciever(): AsyncTask<String, String, String>(){
        override fun doInBackground(vararg params: String?): String {

            //都市ID
            val id = params[0]

            val urlStr = "https://samples.openweathermap.org/data/2.5/weather?q=Tokyo&appid=439d4b804bc8187953eb36d2a8c26a02"

            val url = URL(urlStr)
            val con =url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"

            con.connect()

            val stream = con.inputStream

            val result = is2String(stream)

            val rootJSON = JSONObject(result)
            val weatherData = rootJSON.getString("weather")
            Log.i("天気情報", weatherData)

            val arrayJson = JSONArray(weatherData)

            var main: String = ""
            var description: String = ""

            val num = arrayJson.length()

           // for(i in 0..num)
            //{
                val weatherPart = arrayJson.getJSONObject(0)
                main = weatherPart.getString("main")
                description = weatherPart.getString("description")
            //}

            Log.i("天気", main)
            Log.i("天気2", description)





            //ここでJSONを返す
            return result
        }

        private fun is2String(stream: InputStream): String{
            val sb = StringBuilder()

            val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var line = reader.readLine()
            while(line != null) {
                sb.append(line)
                line = reader.readLine()
            }

            reader.close()

            return sb.toString()

        }

        override fun onPostExecute(result: String) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //地図フラグメントのハンドルを取得する
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        //地図コールバックの登録
        mapFragment.getMapAsync(this)


        //天気取得
        val receiver = WeatherInfoReciever()
        receiver.execute("東京")




    }

    private val SYDNEY = LatLng(-33.87365, 151.20689)

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(38.55, 139.50)

        //マーカー表示
        // mMap.addMarker(MarkerOptions().position(sydney).title("酒田だよ"))



        val dispicon = BitmapDescriptorFactory.fromResource(R.drawable.kumori)
        val Sydney = mMap.addMarker(
            MarkerOptions()
                .position(SYDNEY)
                .title("シドニー")
                .icon(dispicon)
        )
        Sydney.setTag(0)

        /*
        val cu = CameraUpdateFactory.newLatLng(
            LatLng(43.0675, 141.350784)
        )
        */

        val cu = CameraUpdateFactory.newLatLngZoom(
            LatLng(43.0675, 141.350784), 15f
        )
        mMap.moveCamera(cu)

        // Set a listener for marker click.
       // mMap.setOnMapClickListener(onMarkerClick(Sydney))

        //MapStyleOptions.loadRawResourceStyle(this, R.row.stylemap)
    }

    /** Called when the user clicks a marker.  */
    fun onMarkerClick(marker: Marker): Boolean {

        // Retrieve the data from the marker.
        var clickCount = marker.tag as Int?

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1
            marker.tag = clickCount
            Toast.makeText(
                this,
                marker.title +
                        " has been clicked " + clickCount + " times.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }
}