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

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject implements Parcelable {

    @PrimaryKey
    private Integer id;
    private String login;
    private String password;

    private Boolean admin;
    private Boolean manager;
    private UserSettings userSettings;
    private Boolean readOnly;
    private Boolean archive;
    private Boolean expired;
    private String passwordHashMethod;

    public User() {}
    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The login
     */
    public String getLogin() {
        return login;
    }

    /**
     *
     * @param login
     * The login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     * The admin
     */
    public Boolean getAdmin() {
        return admin;
    }

    /**
     *
     * @param admin
     * The admin
     */
    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    /**
     *
     * @return
     * The manager
     */
    public Boolean getManager() {
        return manager;
    }

    /**
     *
     * @param manager
     * The manager
     */
    public void setManager(Boolean manager) {
        this.manager = manager;
    }

    /**
     *
     * @return
     * The userSettings
     */
    public UserSettings getUserSettings() {
        return userSettings;
    }

    /**
     *
     * @param userSettings
     * The userSettings
     */
    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    /**
     *
     * @return
     * The readOnly
     */
    public Boolean getReadOnly() {
        return readOnly;
    }

    /**
     *
     * @param readOnly
     * The readOnly
     */
    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     *
     * @return
     * The archive
     */
    public Boolean getArchive() {
        return archive;
    }

    /**
     *
     * @param archive
     * The archive
     */
    public void setArchive(Boolean archive) {
        this.archive = archive;
    }

    /**
     *
     * @return
     * The expired
     */
    public Boolean getExpired() {
        return expired;
    }

    /**
     *
     * @param expired
     * The expired
     */
    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    /**
     *
     * @return
     * The passwordHashMethod
     */
    public String getPasswordHashMethod() {
        return passwordHashMethod;
    }

    /**
     *
     * @param passwordHashMethod
     * The passwordHashMethod
     */
    public void setPasswordHashMethod(String passwordHashMethod) {
        this.passwordHashMethod = passwordHashMethod;
    }

    protected User(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        login = in.readString();
        password = in.readString();
        byte adminVal = in.readByte();
        admin = adminVal == 0x02 ? null : adminVal != 0x00;
        byte managerVal = in.readByte();
        manager = managerVal == 0x02 ? null : managerVal != 0x00;
        userSettings = (UserSettings) in.readValue(UserSettings.class.getClassLoader());
        byte readOnlyVal = in.readByte();
        readOnly = readOnlyVal == 0x02 ? null : readOnlyVal != 0x00;
        byte archiveVal = in.readByte();
        archive = archiveVal == 0x02 ? null : archiveVal != 0x00;
        byte expiredVal = in.readByte();
        expired = expiredVal == 0x02 ? null : expiredVal != 0x00;
        passwordHashMethod = in.readString();
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
            dest.writeInt(id);
        }
        dest.writeString(login);
        dest.writeString(password);
        if (admin == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (admin ? 0x01 : 0x00));
        }
        if (manager == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (manager ? 0x01 : 0x00));
        }
        dest.writeValue(userSettings);
        if (readOnly == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (readOnly ? 0x01 : 0x00));
        }
        if (archive == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (archive ? 0x01 : 0x00));
        }
        if (expired == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (expired ? 0x01 : 0x00));
        }
        dest.writeString(passwordHashMethod);
    }

    @SuppressWarnings("unused")
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}