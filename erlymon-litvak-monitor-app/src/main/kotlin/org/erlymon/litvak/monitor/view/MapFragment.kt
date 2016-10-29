/*
 * Copyright (c) 2016, Sergey Penkovsky <sergey.penkovsky@gmail.com>
 *
 * This file is part of TraccarLitvakM (fork Erlymon Monitor).
 *
 * TraccarLitvakM (fork Erlymon Monitor) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TraccarLitvakM (fork Erlymon Monitor) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TraccarLitvakM (fork Erlymon Monitor).  If not, see <http://www.gnu.org/licenses/>.
 */
package org.erlymon.litvak.monitor.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import io.realm.RealmChangeListener

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.util.GeoPoint
import org.slf4j.LoggerFactory
import java.util.*

import kotlinx.android.synthetic.main.fragment_map.*
import org.erlymon.litvak.core.model.data.Device
import org.erlymon.litvak.monitor.R

import org.erlymon.litvak.monitor.view.map.layers.*
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.erlymon.litvak.monitor.view.map.layers.TwoGisProvider
import org.erlymon.litvak.monitor.view.map.layers.VisicomProvider
import org.erlymon.litvak.monitor.view.map.layers.WikimapiaProvider
import org.erlymon.litvak.monitor.view.map.marker.MarkerWithLabel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBoxE6
import org.osmdroid.views.overlay.TilesOverlay

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import com.jakewharton.rxbinding.view.RxView
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.Realm
import org.erlymon.litvak.core.model.data.Position
import org.erlymon.litvak.core.presenter.MapPresenter
import org.erlymon.litvak.core.presenter.MapPresenterImpl
import org.erlymon.litvak.core.view.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 4/7/16.
 */
class MapFragment : BaseFragment<MapPresenter>(), MapView {

    private inner class DevicesMarkerClusterer(ctx: Context) : RadiusMarkerClusterer(ctx) {

        fun remove(marker: Marker) {
            mItems.remove(marker)
        }
    }

    private var mRadiusMarkerClusterer: DevicesMarkerClusterer? = null
    private var markers: MutableMap<Long, MarkerWithLabel> = HashMap()
    private var mLocationOverlay: MyLocationNewOverlay? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_map, container, false)
    }

    private var arrowDrawable: Drawable? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = MapPresenterImpl(context, this)

        arrowDrawable = resources.getDrawable(R.drawable.ic_arrow)

        mapview.isTilesScaledToDpi = true
        mapview.setMultiTouchControls(true)

        RxView.clicks(myPlace)
                .compose(RxPermissions.getInstance(context).ensure(Manifest.permission.ACCESS_COARSE_LOCATION))
                .subscribe({ granted ->
                    if (granted) {
                        if (myPlace.isChecked) {
                            mLocationOverlay?.enableFollowLocation()
                            mLocationOverlay?.enableMyLocation()
                            mLocationOverlay?.runOnFirstFix {
                                mapview.post {
                                    try {
                                        mapview.controller.setZoom(15)
                                        mapview.controller.animateTo(GeoPoint(
                                                mLocationOverlay!!.lastFix.latitude,
                                                mLocationOverlay!!.lastFix.longitude
                                        ))
                                        mapview.postInvalidate()
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                        } else {
                            mLocationOverlay?.disableFollowLocation()
                            mLocationOverlay?.disableMyLocation()
                        }
                    } else {
                        myPlace.isChecked = false
                        makeToast(myPlace, getString(R.string.errorPermissionCoarseLocation))
                    }
                })
    }

    override fun onResume() {
        super.onResume()

        mRadiusMarkerClusterer = DevicesMarkerClusterer(context)
        mRadiusMarkerClusterer?.setIcon(BitmapFactory.decodeResource(resources, R.drawable.marker_cluster))
        mapview.overlays.add(mRadiusMarkerClusterer)

        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(activity), mapview)
        mLocationOverlay?.disableFollowLocation()
        mLocationOverlay?.disableMyLocation()
        mapview.getOverlays().add(mLocationOverlay)

        presenter?.onStart()
    }

    override fun onPause() {
        mLocationOverlay?.disableFollowLocation()
        mLocationOverlay?.disableMyLocation()

        mapview.overlays.remove(mLocationOverlay)
        mapview.overlays.remove(mRadiusMarkerClusterer)
        markers.clear()
        super.onPause()
    }

    fun animateTo(geoPoint: GeoPoint, zoom: Int) {
        mapview.controller.setZoom(zoom)
        mapview.controller.animateTo(geoPoint)
        mapview.postInvalidate()
    }

    fun updateUnitMarker(device: Device, position: Position) {
        try {
            logger.debug("UPDATE MARKER: " + device)
            var marker: MarkerWithLabel? = markers[device.id]
            if (marker == null) {
                marker = MarkerWithLabel(mapview, device.name)
                mRadiusMarkerClusterer?.add(marker)
                markers.put(device.id, marker)

            }
            marker.title = device.name
            marker.snippet = position.fixTime.toString()

            marker.setIcon(arrowDrawable)
            if (position.course != null) {
                marker.rotation = position.course
            }
            marker.position = GeoPoint(position.latitude, position.longitude)
        } catch (e: Exception) {
            logger.warn(Log.getStackTraceString(e))
        }
    }

    override fun showLastPositions(positions: Array<out Position>) {
        positions.forEach { position ->
            if (position.device != null) {
                updateUnitMarker(position.device, position)
            }
        }
        mRadiusMarkerClusterer?.invalidate()
        mapview?.postInvalidate()
    }

    override fun showError(error: String) {
        makeToast(mapview, error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MapFragment::class.java)
    }
}
/*
class MapFragment : BaseFragment<MapPresenter>(), MapView {

    private inner class DevicesMarkerClusterer(ctx: Context) : RadiusMarkerClusterer(ctx) {

        fun remove(marker: Marker) {
            mItems.remove(marker)
        }
    }

    private var listener: RealmChangeListener<Realm> = RealmChangeListener { realm ->
        realm.where(Device::class.java).findAll().forEach { device ->
            updateUnitMarker(device)
        }
        mRadiusMarkerClusterer?.invalidate()
        mapview?.postInvalidate()
    }

    private val REQUEST_ACCESS_COARSE_LOCATION = 1

    private var mRadiusMarkerClusterer: DevicesMarkerClusterer? = null
    private var hybridTilesOverlay: TilesOverlay? = null
    private var markers: MutableMap<Long, MarkerWithLabel> = HashMap()
    private var mLocationOverlay: MyLocationNewOverlay? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_map, container, false)
    }

    private var arrowDrawable: Drawable? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrowDrawable = resources.getDrawable(R.drawable.ic_arrow)

        mapview.isTilesScaledToDpi = true
        mapview.setMultiTouchControls(true)

        mapview.postDelayed({
            try {
                val settings = storage.firstUser?.userSettings

                if (settings?.centerLatitude === 0.0 && settings?.centerLongitude === 0.0 && settings?.zoomLevel === 0) {
                    val server = storage.firstServer
                    if (server.latitude === 0.0 && server.longitude === 0.0 && server.zoom === 0) {
                        animateTo(GeoPoint(server.latitude!!, server.longitude!!), server.zoom!!)
                    }
                } else {
                    animateTo(GeoPoint(settings?.centerLatitude as Double, settings?.centerLongitude as Double), settings?.zoomLevel as Int)
                }
            } catch (ignore: Exception) {
                //logger.warn(Log.getStackTraceString(e))
            }
            mapview.postInvalidate()
        }, 1000)

        mapLayers.setOnClickListener { view ->
            val popupMenu = PopupMenu(activity, view)
            popupMenu.inflate(R.menu.popupmenu_map_layers) // Для Android 4.0
            popupMenu.setOnMenuItemClickListener { item ->
                hybridTilesOverlay?.isEnabled = false
                when (item.itemId) {
                    R.id.action_layer_two_gis -> mapview.getTileProvider().setTileSource(TwoGisProvider())
                    R.id.action_layer_osm -> mapview.getTileProvider().setTileSource(TileSourceFactory.MAPNIK)
                    R.id.action_layer_visicom -> mapview.getTileProvider().setTileSource(VisicomProvider())
                    R.id.action_layer_wikimapia -> mapview.getTileProvider().setTileSource(WikimapiaProvider())
                    R.id.action_layer_satellite -> mapview.getTileProvider().setTileSource(TileSourceFactory.MAPQUESTAERIAL)
                    R.id.action_layer_open_cycle_map -> mapview.getTileProvider().setTileSource(OpenCycleMapProvider())
                    R.id.action_layer_bgmountains_map -> mapview.getTileProvider().setTileSource(BGMountainsMapProvider())
                    R.id.action_layer_bgtopo_map -> mapview.getTileProvider().setTileSource(BGtopoVJRasterMapProvider())
                    R.id.action_layer_google_standart -> mapview.getTileProvider().setTileSource(GoogleMapProvider("GoogleMapStandart", GoogleMapProvider.STANDARD))
                    R.id.action_layer_google_satellite -> mapview.getTileProvider().setTileSource(GoogleMapProvider("GoogleMapSatellite", GoogleMapProvider.SATELLITE))
                    R.id.action_layer_google_hybrid -> {
                        mapview.getTileProvider().setTileSource(GoogleMapProvider("GoogleMapSatellite", GoogleMapProvider.SATELLITE))
                        hybridTilesOverlay?.isEnabled = true
                    }
                    else -> mapview.getTileProvider().setTileSource(TileSourceFactory.MAPNIK)
                }

                mapview.postInvalidate()
                true
            }
            popupMenu.show()
        }

        myPlace.setOnClickListener {
            if (mayRequestAccessCoarseLocation()) {
                if (myPlace.isChecked) {
                    mLocationOverlay?.enableFollowLocation()
                    mLocationOverlay?.enableMyLocation()
                } else {
                    mLocationOverlay?.disableFollowLocation()
                    mLocationOverlay?.disableMyLocation()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        mapview.postDelayed({
            try {
                val settings = storage.firstUser?.userSettings

                if (settings?.centerLatitude === 0.0 && settings?.centerLongitude === 0.0 && settings?.zoomLevel === 0) {
                    val server = storage.firstServer
                    if (server.latitude === 0.0 && server.longitude === 0.0 && server.zoom === 0) {
                        animateTo(GeoPoint(server.latitude!!, server.longitude!!), server.zoom!!)
                    }
                } else {
                    animateTo(GeoPoint(settings?.centerLatitude as Double, settings?.centerLongitude as Double), settings?.zoomLevel as Int)
                }
            } catch (ignore: Exception) {
                //logger.warn(Log.getStackTraceString(e))
            }
            mapview.postInvalidate()
        }, 1000)

        // Add tiles layer with custom tile source
        val tileSource = GoogleMapProvider("GoogleHybridProvider", GoogleMapProvider.HYBRID)
        val tileProvider = MapTileProviderBasic(activity)
        tileProvider.tileSource = tileSource
        hybridTilesOverlay = TilesOverlay(tileProvider, activity)
        hybridTilesOverlay?.setLoadingBackgroundColor(Color.TRANSPARENT)
        hybridTilesOverlay?.isEnabled = false
        mapview.getOverlays().add(hybridTilesOverlay)

        mRadiusMarkerClusterer = DevicesMarkerClusterer(context)
        mRadiusMarkerClusterer?.setIcon(BitmapFactory.decodeResource(resources, R.drawable.marker_cluster))
        mapview.overlays.add(mRadiusMarkerClusterer)

        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(activity), mapview)
        mLocationOverlay?.disableFollowLocation()
        mLocationOverlay?.disableMyLocation()
        mapview.getOverlays().add(mLocationOverlay)

        storage.realm?.addChangeListener(listener)
    }

    override fun onPause() {
        storage.realm?.removeChangeListener(listener)
        mapview.overlays.remove(mRadiusMarkerClusterer)
        markers.clear()
        super.onPause()
    }

    fun animateTo(geoPoint: GeoPoint, zoom: Int) {
        mapview.controller.setZoom(zoom)
        mapview.controller.animateTo(geoPoint)
        mapview.postInvalidate()
    }

    fun zoomToBoundingBox(boundingBoxE6: BoundingBoxE6) {
        mapview.zoomToBoundingBox(boundingBoxE6)
        mapview.postInvalidate()
    }

    private fun removeUnitMarker(device: Device) {
        try {
            logger.debug("REMOVE MARKER: " + device)
            val marker = markers[device.id]
            logger.debug("REMOVE MARKER: " + marker)
            mRadiusMarkerClusterer?.remove(marker as Marker)
            markers.remove(device.id)
            marker?.remove(mapview)
        } catch (e: NullPointerException) {
            logger.warn(Log.getStackTraceString(e))
        }

    }

    private fun updateUnitMarker(device: Device) {

        try {
            logger.debug("UPDATE MARKER: " + device)
            val position = storage.getPositionByDeviceId(device.id)
            if (position != null) {
                var marker = markers[device.id]
                if (marker == null) {
                    marker = MarkerWithLabel(mapview, device.getName())
                    marker?.setAnchor(0.33f, 0.6f)
                    mRadiusMarkerClusterer?.add(marker)
                    markers.put(device.id, marker)

                }
                marker.title = device.name
                marker.snippet = position.fixTime.toString()

                marker.setIcon(arrowDrawable)
                if (position.course != null) {
                    marker.rotation = position.course
                }
                marker.position = GeoPoint(position.latitude, position.longitude)
            }
        } catch (e: Exception) {
            logger.warn(Log.getStackTraceString(e))
        }

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                   grantResults: IntArray) {
        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
                logger.debug("onRequestPermissionsResult => REQUEST_ACCESS_COARSE_LOCATION")
            }
        }
    }

    override fun showError(error: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLastPositions(positions: Array<out Position>) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MapFragment::class.java)
    }
}
*/