package org.erlymon.litvak.monitor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.erlymon.litvak.monitor.R;

/**
 * Created by sergey on 6/25/16.
 */
public class Utils {
    public static Bitmap createDrawableText(Context context, String label, int color) {

        TextView text = new TextView(context);
        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        text.setPadding(5, 5, 5, 5);
        text.setBackgroundResource(R.drawable.bg_label);
        text.setText(label);

        text.setTextColor(color);
/*
        if (time == 0) {
            text.setTextColor(context.getResources().getColor(R.color.accent));
        } else {
            long diff = System.currentTimeMillis() - time;
            if (diff <= 15 * 60 * 1000) {
                text.setTextColor(context.getResources().getColor(R.color.green));
            } else if (diff > 15 * 60 * 1000 && diff <= 60 * 60 * 1000) {
                text.setTextColor(context.getResources().getColor(R.color.yelow));
            } else if (diff > 60 * 60 * 1000) {
                text.setTextColor(context.getResources().getColor(R.color.red));
            }
        }
*/
        FrameLayout layout = new FrameLayout(context);
        layout.setPadding(5,5,5,5);
        layout.addView(text);

        View view = layout;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}
