package com.harmoni.pos.http.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utility class for object array operations.
 */
public final class PosObjectUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private PosObjectUtils() {
    }

    /**
     * Appends a string value to the given object array.
     *
     * @param obj the original object array
     * @param value the string value to append
     * @return a new object array with the value appended
     */
    public static Object[] appendValue(Object[] obj, String value) {
        ArrayList<Object> temp = new ArrayList<>(Arrays.asList(obj));
        temp.add(value);
        return temp.toArray();
    }
}
