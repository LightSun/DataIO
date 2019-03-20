package com.heaven7.java.data.io.os;

/**
 * @author heaven7
 */
public class WrappedSourceContext implements SourceContext {

    private final SourceContext base;

    public WrappedSourceContext(SourceContext base) {
        this.base = base;
    }
    public SourceContext getBase() {
        return base;
    }
}
