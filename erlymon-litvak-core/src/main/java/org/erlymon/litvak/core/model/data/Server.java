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
package org.erlymon.litvak.core.model.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class Server extends RealmObject implements Parcelable {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("registration")
    @Expose
    private Boolean registration;

    @SerializedName("readonly")
    @Expose
    private Boolean readonly;

    @SerializedName("map")
    @Expose
    private String map;

    @SerializedName("bingKey")
    @Expose
    private String bingKey;
    @SerializedName("mapUrl")
    @Expose
    private String mapUrl;

    @SerializedName("distanceUnit")
    @Expose
    private String distanceUnit;

    @SerializedName("speedUnit")
    @Expose
    private String speedUnit;

    @SerializedName("latitude")
    @Expose
    private Double latitude;

    @SerializedName("longitude")
    @Expose
    private Double longitude;

    @SerializedName("zoom")
    @Expose
    private Integer zoom;

    @SerializedName("twelveHourFormat")
    @Expose
    private Boolean twelveHourFormat;

    public Server() {}
    /**
     * @return The id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return The registration
     */
    public Boolean getRegistration() {
        return registration;
    }

    /**
     * @param registration The registration
     */
    public void setRegistration(Boolean registration) {
        this.registration = registration;
    }

    /**
     * @return The readonly
     */
    public Boolean getReadonly() {
        return readonly;
    }

    /**
     * @param readonly The readonly
     */
    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * @return The map
     */
    public String getMap() {
        return map;
    }

    /**
     * @param map The map
     */
    public void setMap(String map) {
        this.map = map;
    }

    /**
     * @return The bingKey
     */
    public String getBingKey() {
        return bingKey;
    }

    /**
     * @param bingKey The bingKey
     */
    public void setBingKey(String bingKey) {
        this.bingKey = bingKey;
    }

    /**
     * @return The mapUrl
     */
    public String getMapUrl() {
        return mapUrl;
    }

    /**
     * @param mapUrl The mapUrl
     */
    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    /**
     * @return The distanceUnit
     */
    public String getDistanceUnit() {
        return distanceUnit;
    }

    /**
     * @param distanceUnit The distanceUnit
     */
    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    /**
     * @return The speedUnit
     */
    public String getSpeedUnit() {
        return speedUnit;
    }

    /**
     * @param speedUnit The speedUnit
     */
    public void setSpeedUnit(String speedUnit) {
        this.speedUnit = speedUnit;
    }

    /**
     * @return The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return The zoom
     */
    public Integer getZoom() {
        return zoom;
    }

    /**
     * @param zoom The zoom
     */
    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    /**
     * @return The twelveHourFormat
     */
    public Boolean getTwelveHourFormat() {
        return twelveHourFormat;
    }

    /**
     * @param twelveHourFormat The twelveHourFormat
     */
    public void setTwelveHourFormat(Boolean twelveHourFormat) {
        this.twelveHourFormat = twelveHourFormat;
    }

    protected Server(Parcel in) {
        id = in.readLong();
        byte registrationVal = in.readByte();
        registration = registrationVal == 0x02 ? null : registrationVal != 0x00;
        byte readonlyVal = in.readByte();
        readonly = readonlyVal == 0x02 ? null : readonlyVal != 0x00;
        map = in.readString();
        bingKey = in.readString();
        mapUrl = in.readString();
        distanceUnit = in.readString();
        speedUnit = in.readString();
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        zoom = in.readByte() == 0x00 ? null : in.readInt();
        byte twelveHourFormatVal = in.readByte();
        twelveHourFormat = twelveHourFormatVal == 0x02 ? null : twelveHourFormatVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        if (registration == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (registration ? 0x01 : 0x00));
        }
        if (readonly == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (readonly ? 0x01 : 0x00));
        }
        dest.writeString(map);
        dest.writeString(bingKey);
        dest.writeString(mapUrl);
        dest.writeString(distanceUnit);
        dest.writeString(speedUnit);
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitude);
        }
        if (zoom == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(zoom);
        }
        if (twelveHourFormat == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (twelveHourFormat ? 0x01 : 0x00));
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<Server> CREATOR = new Creator<Server>() {
        @Override
        public Server createFromParcel(Parcel in) {
            return new Server(in);
        }

        @Override
        public Server[] newArray(int size) {
            return new Server[size];
        }
    };
}