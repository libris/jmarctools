package se.kb.libris.util.marc.io;

import java.io.InputStream;
import static org.junit.Assert.fail;
import org.junit.Test;

public class BufferedMarcRecordReaderTest {
    public void parse(int bufferSize, int nRecords) {
        try {
            BufferedMarcRecordReader reader = new BufferedMarcRecordReader(new MarcXmlRecordReader(new MarcXmlTestInputStream(nRecords)), bufferSize);
            
            int n=0;
            while (reader.readRecord() != null) n++;
            
            if (n != nRecords) fail("Expected " + nRecords + " records, got " + n);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParse() {
        parse(10, 5);
    }
    
    @Test
    public void testBlockingParse() {
        parse(5, 10);
    }

    @Test
    public void testClose() {
        try {
            InputStream stream = new MarcXmlTestInputStream(10,5);
            BufferedMarcRecordReader reader = new BufferedMarcRecordReader(new MarcXmlRecordReader(stream));
            
            // get five records
            for (int i=0;i<5;i++) {
                if (reader.readRecord() == null) fail("Expected record, got null");
            }

            try {
                if (reader.readRecord() == null) fail("Expected exception, got null");
                fail("Expected exception, got record");
            } catch (Exception e) {
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
