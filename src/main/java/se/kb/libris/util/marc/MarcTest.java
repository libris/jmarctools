/*
 * MarcTest.java
 *
 * Created on den 18 augusti 2003, 22:48
 */

package se.kb.libris.util.marc;

import java.util.*;
/**
 *
 * @author  Martin Malmsten
 */
public class MarcTest {
    
    /** Creates a new instance of MarcTest */
    public MarcTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MarcRecordBuilder mb = new se.kb.libris.util.marc.impl.MarcRecordBuilderImpl();
        Comparator c = MarcFieldComparator.strictSorted;
        MarcRecord mr = mb.createMarcRecord();
        
        mr.setLeader("012345678901234567890123");
        mr.addField(mr.createControlfield("001", "ABV"));
        mr.addField(mr.createDatafield("100").setIndicator(0, '1').addSubfield('a', "A B [U+0043] D"));
        mr.addField(mr.createDatafield("050").setIndicator(0, '1').addSubfield('a', "A B [U+0043] D"));
        
        java.util.Collections.sort(mr.getFields(), MarcFieldComparator.strictSorted);
        
        System.out.println(mr);
        System.out.println(new String(se.kb.libris.util.marc.io.Iso2709Serializer.serialize(mr)));
    }
    
}
