package com.github.u3games.eventengine.api.adapter;

import com.l2jserver.util.Rnd;

public class ERandom {

    public static int get(int value) {
        return Rnd.get(value);
    }

    public static int get(int min, int max) {
        return Rnd.get(min, max);
    }
}
