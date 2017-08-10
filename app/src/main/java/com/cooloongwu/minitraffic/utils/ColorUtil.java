package com.cooloongwu.minitraffic.utils;

import com.cooloongwu.minitraffic.R;

import java.util.Random;

/**
 * Created by CooLoongWu on 2017-8-10 16:46.
 */

public class ColorUtil {

    private static int colors[] = {
            R.color.line_blue,
            R.color.line_gray,
            R.color.line_green,
            R.color.line_red,
            R.color.line_yellow
    };

    public static int getRandomColor() {
        Random random = new Random();
        int index = random.nextInt(colors.length);
        return colors[index];
    }
}
