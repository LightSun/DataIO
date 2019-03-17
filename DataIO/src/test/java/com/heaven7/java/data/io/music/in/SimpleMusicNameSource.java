package com.heaven7.java.data.io.music.in;

import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.data.io.music.UniformNameHelper;

import java.util.List;
import java.util.Map;

/**
 * @author heaven7
 */
public class SimpleMusicNameSource implements MusicNameSource {

    private List<String> musicNames;

    public SimpleMusicNameSource(String file) {
        musicNames = new TextReadHelper<String>(new TextReadHelper.Callback<String>() {
            @Override
            public String parse(String line) {
                return UniformNameHelper.uniformSimpleMusicName(line);
            }
        }).read(null, file);
    }

    @Override
    public List<String> getMusicNames() {
        return musicNames;
    }

    @Override
    public Map<String, Integer> getSortMap() {
        return null;
    }

    public static void main(String[] args) {
        SimpleMusicNameSource source = new SimpleMusicNameSource("E:\\tmp\\bugfinds\\filenames.txt");
        List<String> musicNames = source.getMusicNames();
        System.out.println();
    }
}
