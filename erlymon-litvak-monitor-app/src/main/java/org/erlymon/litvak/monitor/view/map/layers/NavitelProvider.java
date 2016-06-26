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

import java.util.Locale;

public class NavitelProvider extends XYTileSource {
    public NavitelProvider() {
        //http://m01.navitel.su/navitms.fcgi?j=00000154,00000178,08,5618653998244554&hl=ru
        super("NavitelProvider", 3, 18, 256, null, new String[]{
                "http://m01.navitel.su/navitms.fcgi?j={x},{y},{z}&hl={hl}",
                "http://m02.navitel.su/navitms.fcgi?j={x},{y},{z}&hl={hl}",
                "http://m03.navitel.su/navitms.fcgi?j={x},{y},{z}&hl={hl}"
        });
    }

    @Override
    public String getTileURLString(final MapTile aTile) {
        int y = (int) Math.pow(2, aTile.getZoomLevel()) - aTile.getY() - 1;
        String url = getBaseUrl()
                .replace("{z}", String.format("%02d", aTile.getZoomLevel()))
                .replace("{x}", String.format("%08d", aTile.getX()))
                .replace("{y}", String.format("%08d", y))
                .replace("{hl}", Locale.getDefault().getLanguage());
        System.out.println("NAVITEL URL: " + url);
        return url;
    }
}
