package se.kb.libris.util.marc.io;

import org.junit.Test;
import se.kb.libris.util.marc.Datafield;
import se.kb.libris.util.marc.MarcRecord;
import se.kb.libris.util.marc.Subfield;
import se.kb.libris.util.marc.impl.DatafieldImpl;
import se.kb.libris.util.marc.impl.MarcRecordImpl;
import static org.junit.Assert.*;

import java.util.List;

public class Iso2709SerializerTest {
    @Test
    public void testTooLongField() {
        MarcRecord r1 = new MarcRecordImpl();
        for (int i = 0 ; i < 5 ; i++) {
            r1.addField(new DatafieldImpl("502").addSubfield('a', bigString(22222)));    
        }
        
        MarcRecord r2 = Iso2709Deserializer.deserialize(Iso2709Serializer.serialize(r1));
        List<Datafield> fields = r2.getDatafields();
        assertEquals(5, fields.size());
        
        for (Datafield f : fields) {
            List<Subfield> subFields = f.getSubfields();
            assertEquals(1, subFields.size());
            int expectedDataBytes = 9999 - 5; // i1, i2, delimiter, subfield code, field terminator are each one byte
            assertEquals(bigString(expectedDataBytes), subFields.get(0).getData());
        }
    }

    @Test
    public void testTooLongField2() {
        int NUM_FIELDS_SUB_FIELDS = 5;
        
        MarcRecord r1 = new MarcRecordImpl();
        for (int i = 0 ; i < NUM_FIELDS_SUB_FIELDS ; i++) {
            DatafieldImpl df = new DatafieldImpl("502");
            for (int j = 0 ; j < NUM_FIELDS_SUB_FIELDS ; j++) {
                df.addSubfield((char) ('a' + i), bigString(22222));    
            }
            r1.addField(df);
        }

        MarcRecord r2 = Iso2709Deserializer.deserialize(Iso2709Serializer.serialize(r1));
        List<Datafield> fields = r2.getDatafields();
        assertEquals(NUM_FIELDS_SUB_FIELDS, fields.size());

        for (Datafield f : fields) {
            List<Subfield> subFields = f.getSubfields();
            assertEquals(NUM_FIELDS_SUB_FIELDS, subFields.size());
            int actualDataBytes = 0;
            for (Subfield s : subFields) {
                actualDataBytes += s.getData().length();
            }
            int expectedDataBytes = 9999 - 2 - NUM_FIELDS_SUB_FIELDS * 2 - 1; // i1, i2, delimiter, subfield code, field terminator are each one byte
            assertEquals(expectedDataBytes, actualDataBytes);
        }
    }

    @Test
    public void testTooBigRecord() {
        MarcRecord r1 = new MarcRecordImpl();
        for (int i = 0 ; i < 25 ; i++) {
            r1.addField(new DatafieldImpl("111").addSubfield('1', bigString(10000)));
        }

        MarcRecord r2 = Iso2709Deserializer.deserialize(Iso2709Serializer.serialize(r1));
        List<Datafield> fields = r2.getDatafields();

        assertEquals(9, fields.size());
    }
    
    private static String bigString(int numChars) {
       StringBuilder s = new StringBuilder();
       for (int i = 0 ; i < numChars ; i++) {
           s.append((char) ('a' + (i % 20)));
       }
       return s.toString();
    } 
}
