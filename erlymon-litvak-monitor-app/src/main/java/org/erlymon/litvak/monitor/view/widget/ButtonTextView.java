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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 05.10.15.
 */
public class ButtonTextView extends TextView {

    private int actionX, actionY;

    private DrawableClickListener clickListener;

    public ButtonTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ButtonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonTextView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //System.out.println("onTouchEvent: " + event.getAction());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            actionX = (int) event.getXPrecision();
            actionY = (int) event.getYPrecision();


            Drawable[] drawables = getCompoundDrawables();

            Drawable drawableRight = drawables[2];
            Drawable drawableLeft = drawables[0];
            Drawable drawableTop = drawables[1];
            Drawable drawableBottom = drawables[3];

            if (drawableBottom != null && drawableBottom.getBounds().contains(actionX, actionY)) {
                //System.out.println("drawableBottom onTouchEvent: " + drawableBottom.getBounds().contains(actionX, actionY));
                clickListener.onClick(DrawableClickListener.DrawablePosition.BOTTOM);
                return super.onTouchEvent(event);
            }

            if (drawableTop != null && drawableTop.getBounds().contains(actionX, actionY)) {
                //System.out.println("drawableTop onTouchEvent: " + drawableTop.getBounds().contains(actionX, actionY));
                clickListener.onClick(DrawableClickListener.DrawablePosition.TOP);
                return super.onTouchEvent(event);
            }

            if (drawableLeft != null && drawableLeft.getBounds().contains(actionX, actionY)) {
                //System.out.println("drawableLeft onTouchEvent: " + drawableLeft.getBounds().contains(actionX, actionY));
                clickListener.onClick(DrawableClickListener.DrawablePosition.LEFT);
                return super.onTouchEvent(event);
            }

            //System.out.println("!!!! drawableRight onTouchEvent: " + drawables[2] + " and " + drawables[2].getBounds().contains(actionX, actionY));
            //System.out.println(String.format("!!! drawableRight onTouchEvent: actionX = %d, actionY= %d", actionX, actionY) + " event.getXPrecision()" + event.getXPrecision());
            //System.out.println("!!!! drawableRight onTouchEvent: getBounds: " + drawables[2].getBounds());
            if (drawables[2] != null && drawables[2].getBounds().contains(actionX, actionY)) {
                //System.out.println("drawableRight onTouchEvent: " + drawables[2].getBounds().contains(actionX, actionY));
                clickListener.onClick(DrawableClickListener.DrawablePosition.RIGHT);
                return super.onTouchEvent(event);
            }
        }


        return super.onTouchEvent(event);
    }

    public void setDrawableClickListener(DrawableClickListener listener) {
        this.clickListener = listener;
    }
}