/*
 * MarcRecordReader.java
 *
 * Created on den 8 juni 2003, 16:44
 */

package se.kb.libris.util.marc.io;

import java.io.*;
import se.kb.libris.util.marc.*;

/**
 *
 * @author  Martin Malmsten
 */
public interface MarcRecordReader {
    public MarcRecord readRecord() throws IOException;
    public void close();
}
