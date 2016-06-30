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

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*

import org.slf4j.LoggerFactory

import kotlinx.android.synthetic.main.activity_positions.*
import kotlinx.android.synthetic.main.content_positions.*
import org.erlymon.litvak.core.model.data.Device
import org.erlymon.litvak.core.model.data.Position
import org.erlymon.litvak.core.presenter.PositionsPresenter
import org.erlymon.litvak.core.presenter.PositionsPresenterImpl
import org.erlymon.litvak.core.view.PositionsView
import org.erlymon.litvak.monitor.R
import org.erlymon.litvak.monitor.view.adapter.PositionsExpandableListAdapter
import org.erlymon.litvak.monitor.view.fragment.DatePickerDialogFragment
import org.erlymon.litvak.monitor.view.fragment.TimePickerDialogFragment
import org.erlymon.litvak.monitor.view.map.layers.*
import org.erlymon.litvak.monitor.view.widget.DrawableClickListener

import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBoxE6
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import java.util.*

class PositionsActivity : BaseActivity<PositionsPresenter>(), PositionsView, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private var tag: String = ""
    private var pathOverlay: Polyline? = null
    private var hybridTilesOverlay: TilesOverlay? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when(id) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positions)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val device = intent.getParcelableExtra<Device>("device")
        supportActionBar?.setTitle(device?.name)

        presenter = PositionsPresenterImpl(this, this)

        tabHost.setup()

        var spec: TabHost.TabSpec = tabHost.newTabSpec("map_position")
        spec.setContent(R.id.ll_map_positions)
        spec.setIndicator(getString(R.string.mapTitle))
        tabHost.addTab(spec)

        spec = tabHost.newTabSpec("list_positions")
        spec.setContent(R.id.ll_list_positions)
        spec.setIndicator(getString(R.string.listTitle))
        tabHost.addTab(spec)

        dtp_time_from.setTime(0, 0, 0)
        dtp_time_from.setOnClickDate(object : DrawableClickListener {
            override fun onClick(target: DrawableClickListener.DrawablePosition) {
                tag = "date_picker_dialog_from"
                val newFragment = DatePickerDialogFragment.newInstance(dtp_time_from.getYear(), dtp_time_from.getMonth(), dtp_time_from.getDay())
                newFragment.show(supportFragmentManager, tag)
            }
        })

        dtp_time_from.setOnClickTime(object : DrawableClickListener{
            override fun onClick(target: DrawableClickListener.DrawablePosition) {
                tag = "time_picker_dialog_from"
                val newFragment = TimePickerDialogFragment.newInstance(dtp_time_from.getHours(), dtp_time_from.getMinutes())
                newFragment.show(supportFragmentManager, tag)
            }
        })

        dtp_time_to.setTime(23, 59, 59)
        dtp_time_to.setOnClickDate(object : DrawableClickListener{
            override fun onClick(target: DrawableClickListener.DrawablePosition) {
                tag = "date_picker_dialog_to"
                val newFragment = DatePickerDialogFragment.newInstance(dtp_time_to.getYear(), dtp_time_to.getMonth(), dtp_time_to.getDay())
                newFragment.show(supportFragmentManager, tag)
            }
        })

        dtp_time_to.setOnClickTime(object : DrawableClickListener{
            override fun onClick(target: DrawableClickListener.DrawablePosition) {
                tag = "time_picker_dialog_to"
                val newFragment = TimePickerDialogFragment.newInstance(dtp_time_to.getHours(), dtp_time_to.getMinutes())
                newFragment.show(supportFragmentManager, tag)
            }
        })

        mapview.isTilesScaledToDpi = true
        mapview.setMultiTouchControls(true)
        mapview.setLayerType(MapView.LAYER_TYPE_SOFTWARE, null)

        // Add tiles layer with custom tile source
        val tileSource = GoogleMapProvider("GoogleHybridProvider", GoogleMapProvider.HYBRID)
        val tileProvider = MapTileProviderBasic(this@PositionsActivity)
        tileProvider.tileSource = tileSource
        hybridTilesOverlay = TilesOverlay(tileProvider, this@PositionsActivity)
        hybridTilesOverlay?.setLoadingBackgroundColor(Color.TRANSPARENT)
        hybridTilesOverlay?.isEnabled = false
        mapview.getOverlays().add(hybridTilesOverlay)

        mapLayers.setOnClickListener { view ->
            val popupMenu = PopupMenu(this@PositionsActivity, view)
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

        fab_load_positions.setOnClickListener{
            presenter?.onLoadPositionsButtonClick()
        }
    }

    public override fun onResume() {
        super.onResume()
        pathOverlay = Polyline(this)
        pathOverlay!!.color = resources.getColor(R.color.colorPrimaryDark)
        pathOverlay!!.paint.strokeWidth = 10f
        mapview.overlays.add(pathOverlay)
    }

    public override fun onPause() {
        mapview.overlays.remove(pathOverlay)
        super.onPause()
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        logger.debug("ON DATE SET" + view.tag + " " + view.id)
        if (tag == "date_picker_dialog_from") {
            dtp_time_from.setDate(year, monthOfYear, dayOfMonth)
        }
        if (tag == "date_picker_dialog_to") {
            dtp_time_to.setDate(year, monthOfYear, dayOfMonth)
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        if (tag == "time_picker_dialog_from") {
            dtp_time_from.setTime(hourOfDay, minute, 0)
        }
        if (tag == "time_picker_dialog_to") {
            dtp_time_to.setTime(hourOfDay, minute, 59)
        }
    }

    override fun showData(data: Array<out Position>) {
        createTrack(data)
        createList(data)
    }

    override fun getDevice(): Device {
        val device = intent.getParcelableExtra<Device>("device")
        val tmp = Device()
        tmp.id = device.id
        tmp.uniqueId = device.uniqueId
        tmp.name = device.name
        return tmp;
    }

    override fun getTimeFrom(): Date? {
        return dtp_time_from.getDate();
    }

    override fun getTimeTo(): Date? {
        return dtp_time_to.getDate();
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    fun createTrack(positions: Array<out Position>) {
        val points = ArrayList<GeoPoint>()
        for (position in positions) {
            points.add(GeoPoint(position.latitude!!, position.longitude!!))
        }
        pathOverlay!!.points = points
        mapview.zoomToBoundingBox(BoundingBoxE6.fromGeoPoints(points))
        mapview.postInvalidate()
    }

    fun createList(positions: Array<out Position>) {
        lv_positions.setAdapter(PositionsExpandableListAdapter(this, 0, Arrays.asList(*positions)))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PositionsActivity::class.java)
    }
}
