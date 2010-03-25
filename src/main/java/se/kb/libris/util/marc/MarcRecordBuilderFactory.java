/*
 * MarcRecordFactory.java
 *
 * Created on den 31 maj 2003, 20:46
 */

package se.kb.libris.util.marc;

/**
 *
 * @author  Martin Malmsten
 */
public class MarcRecordBuilderFactory {
    protected static MarcRecordBuilder instance = null;

    public static MarcRecordBuilder newBuilder() {
        /** @todo use SPI to automatically initialize
         *  the right builder */
        if (instance == null) {
            instance = new se.kb.libris.util.marc.impl.MarcRecordBuilderImpl();
        }
        
        return instance;
    }
    
    public static void registerBuilder(MarcRecordBuilder f) {
        instance = f;
    }
}
