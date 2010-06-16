/*
 * Iso2709Deserializer.java
 *
 * Created on August 20, 2003, 11:42 AM
 */

package se.kb.libris.util.marc.io;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;
import se.kb.libris.util.marc.*;
import se.kb.libris.util.marc.impl.*;

/**
 *
 * @author  marma
 */
public class Iso2709Deserializer {
    static private Pattern controlfieldPattern = Pattern.compile("00\\w");
    static private Pattern datafieldPattern = Pattern.compile("\\w\\w\\w");

    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[]) {
        return deserialize(record, 0, record.length, System.getProperty("file.encoding"), false);
    }

    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[], int offset, int length) {
        return deserialize(record, offset, length, System.getProperty("file.encoding"), false);
    }

    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[], String encoding) {
        return deserialize(record, 0, record.length, encoding, false);
    }
    
    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[], int offset, int length, String encoding) {
        return deserialize(record, offset, length, encoding, false);
    }


    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[], boolean trust_directory) {
        return deserialize(record, 0, record.length, trust_directory);
    }

    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[], int offset, int length, boolean trust_directory) {
        return deserialize(record, offset, length, System.getProperty("file.encoding"), trust_directory);
    }

    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[], String encoding, boolean trust_directory) {
        return deserialize(record, 0, record.length, encoding, trust_directory);
    }

    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[], int offset, int length, String encoding, boolean trust_directory) {
        try {
            String leader = new String(record, offset, 24, "ASCII");
            MarcRecord mr = new MarcRecordImpl();

            mr.setLeader(leader);
            mr.setLeader(10, '2');
            mr.setLeader(11, '2');
            mr.setLeader(20, '4');
            mr.setLeader(21, '5');
            mr.setLeader(22, '0');
            mr.setLeader(23, '0');

            int baseAddr = Integer.parseInt(leader.substring(12,17)), nFields = (baseAddr - 24) / 12;

            if (trust_directory) {
                for (int i=0;i<nFields;i++) {
                    int pos1 = offset + baseAddr + Integer.parseInt(new String(record, offset + 24 + i*12 + 3 + 4, 5, "ASCII"));
                    int pos2 = pos1 + Integer.parseInt(new String(record, offset + 24 + i*12 + 3, 4, "ASCII"));
                    String tag = new String(record, offset + 24 + i*12, 3, "ASCII");

                    if (Pattern.matches("00\\w", tag)) {
                        mr.addField(new ControlfieldImpl(tag, record, pos1, pos2-1, encoding));
                    } else {
                        mr.addField(new DatafieldImpl(tag, record, pos1, pos2-1, encoding));
                    }
                }
            } else {
                int pos1 = baseAddr;
                for (int pos2=baseAddr+offset, fieldNr=0;pos2<offset+length;pos2++) {
                    if (record[pos2] == MarcRecord.END_OF_FIELD) {
                        String tag = new String(record, 24 + 12*fieldNr, 3, encoding);

                        if ((pos2-pos1) < 3 || record[pos1 + 2] != MarcRecord.SUBFIELD_MARK) {
                            //System.err.println("adding controlfield " + tag);
                            mr.addField(new ControlfieldImpl(tag, record, pos1, pos2-1, encoding));
                        } else {
                            //System.err.println("adding datafield " + tag);
                            mr.addField(new DatafieldImpl(tag, record, pos1, pos2-1, encoding));
                        }

                        fieldNr++;
                        pos1 = pos2+1;
                    }
                }
            }

            return mr;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
