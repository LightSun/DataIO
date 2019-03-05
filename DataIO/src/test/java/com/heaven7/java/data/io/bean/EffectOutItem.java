package com.heaven7.java.data.io.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 特效 转场，都是这个
 * @author heaven7
 */
public class EffectOutItem {

    SpeedArea slow_speed_area = new SpeedArea();

    SpeedArea middle_speed_area = new SpeedArea();

    SpeedArea high_speed_area = new SpeedArea();

    public SpeedArea getSlow_speed_area() {
        return slow_speed_area;
    }

    public void setSlow_speed_area(SpeedArea slow_speed_area) {
        this.slow_speed_area = slow_speed_area;
    }

    public SpeedArea getMiddle_speed_area() {
        return middle_speed_area;
    }

    public void setMiddle_speed_area(SpeedArea middle_speed_area) {
        this.middle_speed_area = middle_speed_area;
    }

    public SpeedArea getHigh_speed_area() {
        return high_speed_area;
    }

    public void setHigh_speed_area(SpeedArea high_speed_area) {
        this.high_speed_area = high_speed_area;
    }

    public static class SpeedArea{
        ScoreArea low_score_area = new ScoreArea();
        ScoreArea middle_score_area  = new ScoreArea();
        ScoreArea high_score_area  = new ScoreArea();
        ScoreArea no_score_area;

        //for no-score-area. just set low/middle/high score_area null.
        public ScoreArea ensureNoScoreAreaNotNull(){
            if(this.no_score_area == null){
                this.no_score_area = new ScoreArea();
                low_score_area = null;
                middle_score_area = null;
                high_score_area = null;
            }
            return no_score_area;
        }

        public ScoreArea getLow_score_area() {
            return low_score_area;
        }

        public void setLow_score_area(ScoreArea low_score_area) {
            this.low_score_area = low_score_area;
        }

        public ScoreArea getMiddle_score_area() {
            return middle_score_area;
        }

        public void setMiddle_score_area(ScoreArea middle_score_area) {
            this.middle_score_area = middle_score_area;
        }

        public ScoreArea getHigh_score_area() {
            return high_score_area;
        }

        public void setHigh_score_area(ScoreArea high_score_area) {
            this.high_score_area = high_score_area;
        }
    }
    public static class ScoreArea{
        Scope imageScope = Scope.DEFAULT_IMAGE;
        Scope videoScope = new Scope();

        public Scope getImageScope() {
            return imageScope;
        }

        public void setImageScope(Scope imageScope) {
            this.imageScope = imageScope;
        }

        public Scope getVideoScope() {
            return videoScope;
        }

        public void setVideoScope(Scope videoScope) {
            this.videoScope = videoScope;
        }
    }

    public static class Scope{
        public static final Scope DEFAULT_IMAGE = ofDefaultImageScope();
        List<String> effects;

        public List<String> getEffects() {
            return effects;
        }

      /*  public void setEffects(List<String> effects) {
            this.effects = effects;
        }*/

        public void addEffects(List<String> effects) {
            if(this.effects == null){
                this.effects = new ArrayList<>();
            }
            this.effects.addAll(effects);
        }
    }

    public static Scope ofDefaultImageScope(){
        Scope scope = new Scope();
        scope.effects = new ArrayList<>(Arrays.asList("left_translation",
                "right_translation", "zoom_in", "zoom_out"));
        return scope;
    }
}
