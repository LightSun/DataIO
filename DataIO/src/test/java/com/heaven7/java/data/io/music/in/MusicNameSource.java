package com.heaven7.java.data.io.music.in;

import java.util.List;
import java.util.Map;

/**
 * @author heaven7
 */
public interface MusicNameSource {

    List<String> getMusicNames();

    Map<String, Integer> getSortMap();

}
