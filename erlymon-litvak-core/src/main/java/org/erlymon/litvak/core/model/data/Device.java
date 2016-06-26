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

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class Device extends RealmObject implements Parcelable {
    @PrimaryKey
    private Long id;
    private String name;
    private String uniqueId;
    private Long timeout;
    private Float idleSpeedThreshold;
    private Integer minIdleTime;
    private String status;
    private Date lastUpdate;

    private RealmList<Maintenance> maintenances;
    private RealmList<Sensor> sensors;

    public Device() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Float getIdleSpeedThreshold() {
        return idleSpeedThreshold;
    }

    public void setIdleSpeedThreshold(Float idleSpeedThreshold) {
        this.idleSpeedThreshold = idleSpeedThreshold;
    }

    public Integer getMinIdleTime() {
        return minIdleTime;
    }

    public void setMinIdleTime(Integer minIdleTime) {
        this.minIdleTime = minIdleTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public RealmList<Maintenance> getMaintenances() {
        return maintenances;
    }

    public void setMaintenances(RealmList<Maintenance> maintenances) {
        this.maintenances = maintenances;
    }

    public RealmList<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(RealmList<Sensor> sensors) {
        this.sensors = sensors;
    }

    protected Device(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readLong();
        name = in.readString();
        uniqueId = in.readString();
        timeout = in.readByte() == 0x00 ? null : in.readLong();
        idleSpeedThreshold = in.readByte() == 0x00 ? null : in.readFloat();
        minIdleTime = in.readByte() == 0x00 ? null : in.readInt();

        status = in.readString();
        long tmpLastUpdate = in.readLong();
        lastUpdate = tmpLastUpdate != -1 ? new Date(tmpLastUpdate) : null;

        maintenances = new RealmList<>();
        in.readList(maintenances, RealmList.class.getClassLoader());

        sensors = new RealmList<>();
        in.readList(sensors, RealmList.class.getClassLoader());
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
        dest.writeString(name);
        dest.writeString(uniqueId);
        if (timeout == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(timeout);
        }
        if (idleSpeedThreshold == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(idleSpeedThreshold);
        }
        if (minIdleTime == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(minIdleTime);
        }

        dest.writeString(status);
        dest.writeLong(lastUpdate != null ? lastUpdate.getTime() : -1L);

        dest.writeList(maintenances);
        dest.writeList(sensors);
    }

    @SuppressWarnings("unused")
    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
}