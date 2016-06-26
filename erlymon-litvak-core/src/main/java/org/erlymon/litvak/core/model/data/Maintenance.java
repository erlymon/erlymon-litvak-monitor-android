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

package org.erlymon.litvak.core.model.data;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sergey on 1/24/16.
 */
public class Maintenance extends RealmObject implements Parcelable {
    @PrimaryKey
    private Long id;
    private Integer indexNo;
    private String name;

    public Maintenance() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // how often to perform service - value in kilometers
    private Double serviceInterval;

    // odometer value when service was last performed
    private Double lastService;

    public Integer getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(Integer indexNo) {
        this.indexNo = indexNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getServiceInterval() {
        return serviceInterval;
    }

    public void setServiceInterval(Double serviceInterval) {
        this.serviceInterval = serviceInterval;
    }

    public Double getLastService() {
        return lastService;
    }

    public void setLastService(Double lastService) {
        this.lastService = lastService;
    }

    protected Maintenance(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readLong();
        indexNo = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
        serviceInterval = in.readByte() == 0x00 ? null : in.readDouble();
        lastService = in.readByte() == 0x00 ? null : in.readDouble();
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
        if (indexNo == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(indexNo);
        }
        dest.writeString(name);
        if (serviceInterval == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(serviceInterval);
        }
        if (lastService == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(lastService);
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<Maintenance> CREATOR = new Creator<Maintenance>() {
        @Override
        public Maintenance createFromParcel(Parcel in) {
            return new Maintenance(in);
        }

        @Override
        public Maintenance[] newArray(int size) {
            return new Maintenance[size];
        }
    };
}