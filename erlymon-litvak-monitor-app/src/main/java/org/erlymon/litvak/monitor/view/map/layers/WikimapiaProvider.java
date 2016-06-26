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

public class WikimapiaProvider extends XYTileSource {
    public WikimapiaProvider() {
        super("WikimapiaProvider", 0, 18, 256, null, new String[]{"http://i{num}.wikimapia.org/?lng=1&x={x}&y={y}&zoom={z}"});
    }

    @Override
    public String getTileURLString(final MapTile aTile) {
        int num = aTile.getX() % 4 + (aTile.getY() % 4) * 4;
        return getBaseUrl().replace("{z}", "" + aTile.getZoomLevel()).replace("{x}", "" + aTile.getX()).replace("{y}", "" + aTile.getY()).replace("{num}", "" + num);
    }
}
