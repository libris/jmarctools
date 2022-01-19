package se.kb.libris.util.marc.io;

import org.junit.Test;
import se.kb.libris.util.marc.Datafield;
import se.kb.libris.util.marc.MarcRecord;
import se.kb.libris.util.marc.Subfield;
import se.kb.libris.util.marc.impl.DatafieldImpl;
import se.kb.libris.util.marc.impl.MarcRecordImpl;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
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
            int expectedDataBytes = maxDataLenBytes(1);
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
            assertEquals(maxDataLenBytes(NUM_FIELDS_SUB_FIELDS), actualDataBytes);
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

    @Test
    public void testCutFieldOnMultibyteCharacter() throws UnsupportedEncodingException {
        for (String multiByteChar : new String[]{"木", "🐒", "௵", "🎃"}) {
            for (int i = 1 ; i < multiByteChar.getBytes("UTF-8").length - 1; i++) {
                MarcRecord r1 = new MarcRecordImpl();
                int maxLen = maxDataLenBytes(1);
                String s = bigString(maxLen - i) + multiByteChar;
    
                r1.addField(new DatafieldImpl("502").addSubfield('a', s));
    
                MarcRecord r2 = Iso2709Deserializer.deserialize(Iso2709Serializer.serialize(r1, "UTF-8"), "UTF-8");
                for(Datafield f : r2.getDatafields("502")) {
                    for (Subfield sf : f.getSubfields("a")) {
                        assertEquals(bigString(maxLen - i), sf.getData());
                    }
                }
            }
        }
    }
    
    private static String bigString(int numChars) {
       StringBuilder s = new StringBuilder();
       for (int i = 0 ; i < numChars ; i++) {
           s.append((char) ('a' + (i % 20)));
       }
       return s.toString();
    } 
    
    private static int maxDataLenBytes(int numSubFields) {
        // Max field size is 9999 bytes
        // i1, i2, delimiter, subfield code, field terminator are each one byte
        int overhead = 2 + 2 * numSubFields + 1;
        return 9999 - overhead;
    }
}
