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
import org.osmdroid.tileprovider.tilesource.XYTileSource;

public class TwoGisProvider extends XYTileSource {
    public TwoGisProvider() {
        super("TwoGisProvider", 0, 18, 256, null, new String[]{
                "http://tile0.maps.2gis.ru/tiles?x={x}&y={y}&z={z}&v=1",
                "http://tile1.maps.2gis.ru/tiles?x={x}&y={y}&z={z}&v=1",
                "http://tile2.maps.2gis.ru/tiles?x={x}&y={y}&z={z}&v=1",
                "http://tile3.maps.2gis.ru/tiles?x={x}&y={y}&z={z}&v=1",
                "http://tile4.maps.2gis.ru/tiles?x={x}&y={y}&z={z}&v=1",
                "http://tile5.maps.2gis.ru/tiles?x={x}&y={y}&z={z}&v=1",
                "http://tile6.maps.2gis.ru/tiles?x={x}&y={y}&z={z}&v=1",
                "http://tile7.maps.2gis.ru/tiles?x={x}&y={y}&z={z}&v=1",
                "http://tile8.maps.2gis.ru/tiles?x={x}&y={y}&z={z}&v=1",
                "http://tile9.maps.2gis.ru/tiles?x={x}&y={y}&z={z}&v=1"});
    }

    @Override
    public String getTileURLString(final MapTile aTile) {
        return getBaseUrl().replace("{z}", "" + aTile.getZoomLevel()).replace("{x}", "" + aTile.getX()).replace("{y}", "" + aTile.getY());
    }
}
