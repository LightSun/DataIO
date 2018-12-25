package com.heaven7.java.data.io.music.adapter;

/**
 * @author heaven7
 */
public class IndexDelegateV3 extends IndexDelegateV2{

    public int getNameIndex(){
        return 0;
    }
    public int getDomainIndex(){
        return 2;
    }
    public int getMoodIndex(){
        return 3;
    }
    public int getRhythmIndex(){
        return 4;
    }
    public int getSlowSpeedIndex(){
        return 6;
    }
    public int getMiddleSpeedIndex(){
        return 7;
    }
    public int getHighSpeedIndex(){
        return 8;
    }
    //----------------------------------------
    public int getDurationIndex(){
        return 1;
    }

    public int getCategoryIndex(){
        return 5;
    }

}
