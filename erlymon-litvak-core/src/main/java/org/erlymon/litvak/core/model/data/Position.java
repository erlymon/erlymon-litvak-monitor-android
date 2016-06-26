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

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class Position extends RealmObject implements Parcelable {
    @PrimaryKey
    private Long id;
    private String protocol;

    private Date serverTime;
    @SerializedName("time")
    private Date fixTime;

    @SerializedName("valid")
    private Boolean real;

    private Double latitude;
    private Double longitude;
    private Double altitude;

    private Float speed;
    private Float course;

    private String address;

    private String other;

    private Device device;

    public  Position() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }

    public Date getFixTime() {
        return fixTime;
    }

    public void setFixTime(Date fixTime) {
        this.fixTime = fixTime;
    }

    public Boolean getReal() {
        return real;
    }

    public void setReal(Boolean real) {
        this.real = real;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getCourse() {
        return course;
    }

    public void setCourse(Float course) {
        this.course = course;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public static List<String>  createList(Position position) {
        List<String> array = new ArrayList<>();
        array.add("" + position.getReal());
        array.add("" + position.getFixTime());
        array.add("" + position.getLatitude());
        array.add("" + position.getLongitude());
        array.add("" + position.getAltitude());
        array.add("" + position.getSpeed());
        array.add("" + position.getCourse());
        array.add("" + position.getAddress());
        array.add("" + position.getOther());
        return array;
    }

    protected Position(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readLong();
        protocol = in.readString();
        long tmpServerTime = in.readLong();
        serverTime = tmpServerTime != -1 ? new Date(tmpServerTime) : null;
        long tmpFixTime = in.readLong();
        fixTime = tmpFixTime != -1 ? new Date(tmpFixTime) : null;
        byte realVal = in.readByte();
        real = realVal == 0x02 ? null : realVal != 0x00;
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        altitude = in.readByte() == 0x00 ? null : in.readDouble();
        speed = in.readByte() == 0x00 ? null : in.readFloat();
        course = in.readByte() == 0x00 ? null : in.readFloat();
        address = in.readString();
        other = in.readString();
        device = (Device) in.readValue(Device.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(id);
        }
        dest.writeString(protocol);
        dest.writeLong(serverTime != null ? serverTime.getTime() : -1L);
        dest.writeLong(fixTime != null ? fixTime.getTime() : -1L);
        if (real == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (real ? 0x01 : 0x00));
        }
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
        if (altitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(altitude);
        }
        if (speed == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(speed);
        }
        if (course == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(course);
        }
        dest.writeString(address);
        dest.writeString(other);
        dest.writeValue(device);
    }

    @SuppressWarnings("unused")
    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };
}