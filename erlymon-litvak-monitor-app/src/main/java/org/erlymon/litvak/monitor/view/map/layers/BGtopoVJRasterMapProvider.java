/*
 * Copyright (c) 2016, Sergey Penkovsky <sergey.penkovsky@gmail.com>
 *
 * This file is part of TraccarLitvakM (fork Erlymon Monitor)
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
package org.erlymon.litvak.monitor.view.map.layers;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

public class BGtopoVJRasterMapProvider extends WMSTileSource {
    /*
    http://www.kade.si/cgi-bin/mapserv?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=true&LAYERS=BGtopoVJ-raster-v3.00&TILED=true&format=image%2Fpng&WIDTH=256&HEIGHT=256&CRS=EPSG%3A3857&STYLES=&BBOX=2661231.576776698%2C5244191.636589371%2C2700367.3352587083%2C5283327.395071381
     */
    public BGtopoVJRasterMapProvider() {
        super("BGTopoMapProvider", 0, 18, 256, null, new String[]{
                "http://www.kade.si/cgi-bin/mapserv?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=true&LAYERS=BGtopoVJ-raster-v3.00&TILED=true&format=image%2Fpng&WIDTH=256&HEIGHT=256&CRS=EPSG%3A3857&STYLES=&BBOX="
        });
    }
}
