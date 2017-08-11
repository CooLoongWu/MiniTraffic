package com.cooloongwu.minitraffic.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by CooLoongWu on 2017-8-10 16:46.
 */

public class ColorUtil {

    private static int colors[] = {
            Color.argb(255, 1, 161, 255),
            Color.argb(255, 255, 171, 0),
            Color.argb(255, 255, 227, 1),
            Color.argb(255, 55, 233, 1),
    };

    public static int getRandomColor() {
        Random random = new Random();
        int index = random.nextInt(colors.length);
        return colors[index];
    }
}
