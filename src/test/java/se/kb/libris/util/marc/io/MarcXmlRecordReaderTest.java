package se.kb.libris.util.marc.io;

import java.io.ByteArrayInputStream;
import org.junit.Test;
import static org.junit.Assert.*;
import se.kb.libris.util.marc.MarcRecord;

public class MarcXmlRecordReaderTest {  
    static String record = "<record>\n" +
                           "  <leader>00000cam a2200000 ar4500</leader>\n" +
                           "  <controlfield tag=\"001\">123</controlfield>\n" +
                           "  <datafield tag=\"100\" ind1=\" \" ind2=\" \">\n" +
                           "    <subfield code=\"a\">Strindberg, August,</subfield>\n" +
                           "    <subfield code=\"d\">1849-1912</subfield>\n" +
                           "  </datafield>\n" +
                           "  <datafield tag=\"245\" ind1=\" \" ind2=\" \">\n" +
                           "    <subfield code=\"a\">Röda rummet</subfield>\n" +
                           "  </datafield>\n" +
                           "</record>\n";

    @Test
    public void parseTest() {
        String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
                      "<collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
                      record +
                      "</collection>\n";

        try {
            MarcXmlRecordReader reader = new MarcXmlRecordReader(new ByteArrayInputStream(data.getBytes("UTF-8")));
            MarcRecord mr = reader.readRecord();

            if (mr == null) fail("No record retrieved");
            if (mr.getControlfields().size() != 1) fail("Controlfield count mismatch: 1 <> " + mr.getControlfields().size());
            if (mr.getDatafields().size() != 2) fail("Datafield count mismatch: 2 <> " + mr.getDatafields().size());
            if (mr.getDatafields("100").get(0).getSubfields().size() != 2) fail("Subfield count mismatch: 2 <> " + mr.getDatafields("100").get(0).getSubfields().size());
            
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void parseTestPath() {
        String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
                      "<root>" +
                      "  <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
                      record +
                      "  </collection>\n" +
                      "</root>\n";

        try {
            MarcXmlRecordReader reader = new MarcXmlRecordReader(new ByteArrayInputStream(data.getBytes("UTF-8")), "/root/collection/record");
            MarcRecord mr = reader.readRecord();
            
            if (mr == null) fail("No record retrieved");
            if (mr.getControlfields().size() != 1) fail("Controlfield count mismatch: 1 <> " + mr.getControlfields().size());
            if (mr.getDatafields().size() != 2) fail("Datafield count mismatch: 2 <> " + mr.getDatafields().size());
            if (mr.getDatafields("100").get(0).getSubfields().size() != 2) fail("Subfield count mismatch: 2 <> " + mr.getDatafields("100").get(0).getSubfields().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void parseTestDefaultNamespace() {
        String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
                      "<collection>\n" +
                      record +
                      "</collection>\n";

        try {
            MarcXmlRecordReader reader = new MarcXmlRecordReader(new ByteArrayInputStream(data.getBytes("UTF-8")), "/collection/record");
            MarcRecord mr = reader.readRecord();
            
            if (mr == null) fail("No record retrieved");

            reader = new MarcXmlRecordReader(new ByteArrayInputStream(data.getBytes("UTF-8")), "/collection/record", "http://www.loc.gov/MARC21/slim");
            mr = reader.readRecord();

            if (mr != null) fail("Record without namespace retrieved");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }    
}
