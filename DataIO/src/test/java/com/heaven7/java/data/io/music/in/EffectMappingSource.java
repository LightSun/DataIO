package com.heaven7.java.data.io.music.in;

import java.util.List;

/**
 * @author heaven7
 */
public interface EffectMappingSource {

    List<String> getSpecialEffects();
    List<String> getTransitions();
    List<String> getFilters();
}
