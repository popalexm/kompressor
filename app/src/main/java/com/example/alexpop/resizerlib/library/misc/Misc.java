package com.example.alexpop.resizerlib.library.misc;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Misc {

    public static String formatTimeHHmmSS(Long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:SS", Locale.US);
        return formatter.format(timestamp * 1000 );
    }
}
