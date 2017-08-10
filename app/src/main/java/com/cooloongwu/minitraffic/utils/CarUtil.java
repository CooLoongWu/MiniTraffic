package com.cooloongwu.minitraffic.utils;

import com.cooloongwu.minitraffic.R;

import java.util.Random;

/**
 * Created by CooLoongWu on 2017-8-10 17:20.
 */

public class CarUtil {

    private static int cars[] = {
            R.drawable.icon_traffic_bus,
            R.drawable.icon_traffic_car
    };

    public static int getRandomCar() {
        Random random = new Random();
        int index = random.nextInt(cars.length);
        return cars[index];
    }
}
