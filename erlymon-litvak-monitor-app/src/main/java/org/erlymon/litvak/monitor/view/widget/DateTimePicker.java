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
package org.erlymon.litvak.monitor.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.erlymon.litvak.monitor.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 28.05.15.
 */
public class DateTimePicker extends LinearLayout {

    private ButtonTextView mDate;
    private ButtonTextView mTime;



    SimpleDateFormat dateFormat;
    SimpleDateFormat gmtDateFormat;
    SimpleDateFormat iso8601DateFormat;
    Calendar c;

    DrawableClickListener onDateClickListener;
    DrawableClickListener onTimeClickListener;

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        iso8601DateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DateTimePickerOption);

        dateFormat = new SimpleDateFormat(a.getString(R.styleable.DateTimePickerOption_format));

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_date_time_picker, this, true);
        LinearLayout layout = (LinearLayout) getChildAt(0);

        mDate = (ButtonTextView) layout.getChildAt(0);
        mTime = (ButtonTextView) layout.getChildAt(1);


        c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);


        String[] dateTimeArr = dateFormat.format(c.getTime()).split(" ");

        mDate.setText(dateTimeArr[0]);
        mTime.setText(dateTimeArr[1]);

        mDate.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (mDate.isEnabled() && onDateClickListener != null) {
                    onDateClickListener.onClick(target);
                }
            }
        });

        mTime.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (mTime.isEnabled() && onTimeClickListener != null) {
                    onTimeClickListener.onClick(target);
                }
            }
        });
    }

    public long getUtcTimestamp() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(getYear(), getMonth(),getDay(), getHours(), getMinutes(), getSeconds());
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public Date getDate() {
        return c.getTime();
    }

    public String getTimeISO8601() {
        return iso8601DateFormat.format(c.getTime());
    }

    public String getTimeGmt() {
        return gmtDateFormat.format(c.getTime()) + " GMT";
    }

    public long getTimeInMillis() {
        return c.getTimeInMillis();
    }

    public void setTimeInMillis(long time) {
        c.setTimeInMillis(time);
        redraw();
    }

    public int getYear() {
        return c.get(Calendar.YEAR);
    }

    public int getMonth() {
        return c.get(Calendar.MONTH);
    }

    public int getDay() {
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public int getHours() {
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        return c.get(Calendar.MINUTE);
    }

    public int getSeconds() {
        return c.get(Calendar.SECOND);
    }

    public void setDate(int year, int month, int day) {
        c.set(year, month, day);
        redraw();
    }

    public void setTime(int hours, int minutes, int seconds) {
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minutes);
        c.set(Calendar.SECOND, seconds);
        c.set(Calendar.MILLISECOND, 0);
        redraw();
    }

    public void set(int year, int month, int day, int hours, int minutes, int seconds) {
        c.set(year, month, day, hours, minutes, seconds);
        c.set(Calendar.MILLISECOND, 0);
        redraw();
    }

    public void setOnClickDate(DrawableClickListener onDateClickListener) {
        this.onDateClickListener = onDateClickListener;
    }

    public void setOnClickTime(DrawableClickListener onTimeClickListener) {
        this.onTimeClickListener = onTimeClickListener;
    }

    private void redraw() {
        String[] dateTimeArr = dateFormat.format(c.getTime()).split(" ");
        mDate.setText(dateTimeArr[0]);
        mTime.setText(dateTimeArr[1]);
    }

    public String toString() {
        return c.toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        mDate.setEnabled(enabled);
        mTime.setEnabled(enabled);
    }
}
