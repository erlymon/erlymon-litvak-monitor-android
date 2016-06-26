package org.erlymon.litvak.core.model.data;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserSettings extends RealmObject implements Parcelable {
    @PrimaryKey
    private Long id;

    private String speedUnit;
    private Integer timePrintInterval;
    private Integer zoomLevel;
    private Double centerLongitude;
    private Double centerLatitude;
    private String mapType;
    private Boolean maximizeOverviewMap;

    public UserSettings() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
     * @return The timePrintInterval
     */
    public Integer getTimePrintInterval() {
        return timePrintInterval;
    }

    /**
     * @param timePrintInterval The timePrintInterval
     */
    public void setTimePrintInterval(Integer timePrintInterval) {
        this.timePrintInterval = timePrintInterval;
    }

    /**
     * @return The zoomLevel
     */
    public Integer getZoomLevel() {
        return zoomLevel;
    }

    /**
     * @param zoomLevel The zoomLevel
     */
    public void setZoomLevel(Integer zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    /**
     * @return The centerLongitude
     */
    public Double getCenterLongitude() {
        return centerLongitude;
    }

    /**
     * @param centerLongitude The centerLongitude
     */
    public void setCenterLongitude(Double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    /**
     * @return The centerLatitude
     */
    public Double getCenterLatitude() {
        return centerLatitude;
    }

    /**
     * @param centerLatitude The centerLatitude
     */
    public void setCenterLatitude(Double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    /**
     * @return The mapType
     */
    public String getMapType() {
        return mapType;
    }

    /**
     * @param mapType The mapType
     */
    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    /**
     * @return The maximizeOverviewMap
     */
    public Boolean getMaximizeOverviewMap() {
        return maximizeOverviewMap;
    }

    /**
     * @param maximizeOverviewMap The maximizeOverviewMap
     */
    public void setMaximizeOverviewMap(Boolean maximizeOverviewMap) {
        this.maximizeOverviewMap = maximizeOverviewMap;
    }

    protected UserSettings(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readLong();
        speedUnit = in.readString();
        timePrintInterval = in.readByte() == 0x00 ? null : in.readInt();
        zoomLevel = in.readByte() == 0x00 ? null : in.readInt();
        centerLongitude = in.readByte() == 0x00 ? null : in.readDouble();
        centerLatitude = in.readByte() == 0x00 ? null : in.readDouble();
        mapType = in.readString();
        byte maximizeOverviewMapVal = in.readByte();
        maximizeOverviewMap = maximizeOverviewMapVal == 0x02 ? null : maximizeOverviewMapVal != 0x00;
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
        dest.writeString(speedUnit);
        if (timePrintInterval == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(timePrintInterval);
        }
        if (zoomLevel == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(zoomLevel);
        }
        if (centerLongitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(centerLongitude);
        }
        if (centerLatitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(centerLatitude);
        }
        dest.writeString(mapType);
        if (maximizeOverviewMap == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (maximizeOverviewMap ? 0x01 : 0x00));
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<UserSettings> CREATOR = new Creator<UserSettings>() {
        @Override
        public UserSettings createFromParcel(Parcel in) {
            return new UserSettings(in);
        }

        @Override
        public UserSettings[] newArray(int size) {
            return new UserSettings[size];
        }
    };
}