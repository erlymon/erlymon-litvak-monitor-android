package org.erlymon.litvak.monitor.view.map.layers;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

import android.util.Log;

/**
 * This class will allow you to overlay tiles from a WMS server.  Your WMS
 * server needs to support .  An example of how your base url should look:
 *
 * https://xxx.xxx.xx.xx/geoserver/gwc/service/wms?LAYERS=base_map&FORMAT=image/jpeg
 * &SERVICE=WMS&VERSION=1.1.1REQUEST=GetMap&STYLES=&SRS=EPSG:900913&WIDTH=256&HEIGHT=256&BBOX=
 *
 * Notice three things:
 * 1. I am pulling jpeg instead of png files.  For some reason our server
 * makes much smaller jpg files and this gives us a faster load time on
 * mobile networks.
 * 2. The bounding box is at the end of the base url. This is because the
 * getTileURLString method adds the bounding box values onto the end of
 * the base url.
 * 3. We are pulling the SRS=EPSG:900913 and not the SRS=EPSG:4326.
 * This all has to do drawing rounded maps onto flat displays.
 *
 * @author Steve Potell -- spotell@t-sciences.com
 *
 */
public class WMSTileSource extends OnlineTileSourceBase {

    public WMSTileSource(final String aName, final int aZoomMinLevel,
                         final int aZoomMaxLevel, final int aTileSizePixels, final String aImageFilenameEnding,
                         final String... aBaseUrl) {
        super(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
                aImageFilenameEnding, aBaseUrl);
    }

    @Override
    public String getTileURLString(MapTile aTile) {

        StringBuffer tileURLString = new StringBuffer();
        tileURLString.append(getBaseUrl());
        tileURLString.append(wmsTileCoordinates(aTile));

        return tileURLString.toString();
    }

    private final static double ORIGIN_SHIFT = Math.PI * 6378137;

    /**
     * WMS requires the bounding box to be defined as the point (west, south)
     * to the point (east, north).
     *
     * @return The WMS string defining the bounding box values.
     */
    public String wmsTileCoordinates(MapTile value) {

        BoundingBox newTile = tile2boundingBox(value.getX(), value.getY(), value.getZoomLevel());

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(newTile.west);
        stringBuffer.append(",");
        stringBuffer.append(newTile.south);
        stringBuffer.append(",");
        stringBuffer.append(newTile.east);
        stringBuffer.append(",");
        stringBuffer.append(newTile.north);

        return stringBuffer.toString();

    }

    /**
     * A simple class for holding the NSEW lat and lon values.
     */
    class BoundingBox {
        double north;
        double south;
        double east;
        double west;
    }

    /**
     * This method converts tile xyz values to a WMS bounding box.
     *
     * @param x    The x tile coordinate.
     * @param y    The y tile coordinate.
     * @param zoom The zoom level.
     *
     * @return The completed bounding box.
     */
    BoundingBox tile2boundingBox(final int x, final int y, final int zoom) {

        Log.d("MapTile", "--------------- x = " + x);
        Log.d("MapTile", "--------------- y = " + y);
        Log.d("MapTile", "--------------- zoom = " + zoom);

        BoundingBox bb = new BoundingBox();

        bb.north = yToWgs84toEPSGLat(y, zoom);
        bb.south = yToWgs84toEPSGLat(y + 1, zoom);
        bb.west = xToWgs84toEPSGLon(x, zoom);
        bb.east = xToWgs84toEPSGLon(x + 1, zoom);

        return bb;
    }

    /**
     * Converts X tile number to EPSG value.
     *
     * @param tileX the x tile being requested.
     * @param zoom  The current zoom level.
     *
     * @return EPSG longitude value.
     */
    static double xToWgs84toEPSGLon(int tileX, int zoom) {

        // convert x tile position and zoom to wgs84 longitude
        double value = tileX / Math.pow(2.0, zoom) * 360.0 - 180;

        // apply the shift to get the EPSG longitude
        return value * ORIGIN_SHIFT / 180.0;

    }

    /**
     * Converts Y tile number to EPSG value.
     *
     * @param tileY the y tile being requested.
     * @param zoom  The current zoom level.
     *
     * @return EPSG latitude value.
     */
    static double yToWgs84toEPSGLat(int tileY, int zoom) {

        // convert x tile position and zoom to wgs84 latitude
        double value = Math.PI - (2.0 * Math.PI * tileY) / Math.pow(2.0, zoom);
        value = Math.toDegrees(Math.atan(Math.sinh(value)));

        value = Math.log(Math.tan((90 + value) * Math.PI / 360.0)) / (Math.PI / 180.0);

        // apply the shift to get the EPSG latitude
        return value * ORIGIN_SHIFT / 180.0;

    }

}