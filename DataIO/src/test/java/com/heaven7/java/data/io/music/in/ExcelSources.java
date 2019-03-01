package com.heaven7.java.data.io.music.in;

/**
 * @author heaven7
 */
public class ExcelSources {
    //数据源：旧标准， 标准，切点，速度特效，转场,,滤镜
    private ExcelSource oldStandSource;
    private ExcelSource standSource;
    private ExcelSource speedEffectSource;
    private ExcelSource transitionSource;
    private ExcelSource filterSource;
    private ExcelSource transCutSource;

    protected ExcelSources(ExcelSources.Builder builder) {
        this.oldStandSource = builder.oldStandSource;
        this.standSource = builder.standSource;
        this.speedEffectSource = builder.speedEffectSource;
        this.transitionSource = builder.transitionSource;
        this.filterSource = builder.filterSource;
        this.transCutSource = builder.transCutSource;
    }

    public void setTransCutSource(ExcelSource transitionSource) {
        this.transCutSource = transitionSource;
    }
    public void setStandSource(ExcelSource standSource) {
        this.standSource = standSource;
    }

    public void setSpeedEffectSource(ExcelSource speedEffectSource) {
        this.speedEffectSource = speedEffectSource;
    }

    public void setFilterSource(ExcelSource filterSource) {
        this.filterSource = filterSource;
    }

    public void setOldStandSource(ExcelSource oldStandSource) {
        this.oldStandSource = oldStandSource;
    }

    public ExcelSource getOldStandSource() {
        return this.oldStandSource;
    }

    public ExcelSource getStandSource() {
        return this.standSource;
    }

    public ExcelSource getSpeedEffectSource() {
        return this.speedEffectSource;
    }

    public ExcelSource getTransitionSource() {
        return this.transitionSource;
    }

    public ExcelSource getFilterSource() {
        return this.filterSource;
    }

    public ExcelSource getTransCutSource() {
        return transCutSource;
    }

    public static class Builder {
        //数据源：旧标准， 标准，切点，速度特效，其他（短叠黑）,滤镜
        private ExcelSource oldStandSource;
        private ExcelSource standSource;
        private ExcelSource speedEffectSource;
        private ExcelSource transitionSource;
        private ExcelSource filterSource;
        private ExcelSource transCutSource;

        public Builder setOldStandSource(ExcelSource oldStandSource) {
            this.oldStandSource = oldStandSource;
            return this;
        }

        public Builder setStandSource(ExcelSource standSource) {
            this.standSource = standSource;
            return this;
        }

        public Builder setSpeedEffectSource(ExcelSource speedEffectSource) {
            this.speedEffectSource = speedEffectSource;
            return this;
        }

        public Builder setTransitionSource(ExcelSource transitionSource) {
            this.transitionSource = transitionSource;
            return this;
        }

        public Builder setFilterSource(ExcelSource filterSource) {
            this.filterSource = filterSource;
            return this;
        }

        public Builder setTransCutSource(ExcelSource transCutSource) {
            this.transCutSource = transCutSource;
            return this;
        }

        public ExcelSources build() {
            return new ExcelSources(this);
        }
    }
}
