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
package org.erlymon.litvak.monitor.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.erlymon.litvak.core.model.data.Position;
import org.erlymon.litvak.monitor.R;

import java.util.List;


/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 1/9/16.
 */
public class PositionsExpandableListAdapter extends BaseExpandableListAdapter {

    Context context;
    int resId;
    List<Position> groups;

    public PositionsExpandableListAdapter(Context context, int resId, List<Position> groups) {
        this.context = context;
        this.resId = resId;
        this.groups = groups;
    }

    private static class ViewGroupHolder {
        TextView time;
    }

    private static class ViewChildHolder {
        TextView key;
        TextView value;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Position.createList(getGroup(groupPosition)).size();
    }

    @Override
    public Position getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return Position.createList(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groups.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewGroupHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            viewHolder = new ViewGroupHolder();
            viewHolder.time = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewGroupHolder) convertView.getTag();
        }

        Position item = getGroup(groupPosition);
        viewHolder.time.setText(item.getFixTime().toString());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewChildHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.child_item_position, parent, false);
            viewHolder = new ViewChildHolder();
            viewHolder.key = (TextView) convertView.findViewById(R.id.key);
            viewHolder.value = (TextView) convertView.findViewById(R.id.value);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewChildHolder) convertView.getTag();
        }

        String item = getChild(groupPosition, childPosition);
        viewHolder.key.setText(getKeyResId(childPosition));
        viewHolder.value.setText(item);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private int getKeyResId(int position) {
        switch (position) {
            case 0:
                return R.string.positionValid;
            case 1:
                return R.string.positionFixTime;
            case 2:
                return R.string.positionLatitude;
            case 3:
                return R.string.positionLongitude;
            case 4:
                return R.string.positionAltitude;
            case 5:
                return R.string.positionSpeed;
            case 6:
                return R.string.positionCourse;
            case 7:
                return R.string.positionAddress;
            case 8:
                return R.string.positionOther;
            default:
                return R.string.positionUnknown;
        }
    }
}
