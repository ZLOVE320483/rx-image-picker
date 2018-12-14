package com.zlove.image.picker.support;

import java.util.LinkedHashSet;
import java.util.Set;

public class MimeTypeUtils {

    public static Set<String> setOf(String... args) {
        Set<String> set = new LinkedHashSet<>();
        for (String element : args) {
            set.add(element);
        }
        return set;
    }
}
