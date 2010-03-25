/*
 * Iso2709Deserializer.java
 *
 * Created on August 20, 2003, 11:42 AM
 */

package se.kb.libris.util.marc.io;

import se.kb.libris.util.marc.*;
import se.kb.libris.util.marc.impl.*;

/**
 *
 * @author  marma
 */
public class Iso2709Deserializer {
    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[]) {
        return deserialize(record, 0, record.length);
    }

    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[], int offset, int length) {
        String leader = new String(record, offset, 24);
        MarcRecord mr = new MarcRecordImpl();
        
        mr.setLeader(leader);
        mr.setLeader(10, '2');
        mr.setLeader(11, '2');
        mr.setLeader(20, '4');
        mr.setLeader(21, '5');
        mr.setLeader(22, '0');
        mr.setLeader(23, '0');
        
        int baseAddr = Integer.parseInt(leader.substring(12,17)), nFields = (baseAddr - 24) / 12;

        int pos1 = baseAddr;
        for (int pos2=baseAddr+offset, fieldNr=0;pos2<offset+length;pos2++) {
            if (record[pos2] == MarcRecord.END_OF_FIELD) {
                String tag = new String(record, 24 + 12*fieldNr, 3);
                
                if ((pos2-pos1) < 3 || record[pos1 + 2] != MarcRecord.SUBFIELD_MARK) {
                    //System.err.println("adding controlfield " + tag);
                    mr.addField(new ControlfieldImpl(tag, record, pos1, pos2-1));
                } else {
                    //System.err.println("adding datafield " + tag);
                    mr.addField(new DatafieldImpl(tag, record, pos1, pos2-1));
                }
                
                fieldNr++;
                pos1 = pos2+1;
            }
        }        
        
        return mr;
    }

    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[], String encoding) throws java.io.UnsupportedEncodingException {
        String leader = new String(record, 0, 24, encoding);
        MarcRecord mr = new MarcRecordImpl();
        
        mr.setLeader(leader);
        mr.setLeader(leader);
        mr.setLeader(10, '2');
        mr.setLeader(11, '2');
        mr.setLeader(20, '4');
        mr.setLeader(21, '5');
        mr.setLeader(22, '0');
        mr.setLeader(23, '0');
        
        int baseAddr = Integer.parseInt(leader.substring(12,17)), nFields = (baseAddr - 24) / 12;

        int pos1 = baseAddr;
        for (int pos2=baseAddr, fieldNr=0;pos2<record.length;pos2++) {
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
        
        return mr;
    }
    
    public static se.kb.libris.util.marc.MarcRecord deserialize(byte record[], int offset, int length, String encoding) throws java.io.UnsupportedEncodingException {
        String leader = new String(record, offset, 24, encoding);
        MarcRecord mr = new MarcRecordImpl();
        
        mr.setLeader(leader);
        mr.setLeader(leader);
        mr.setLeader(10, '2');
        mr.setLeader(11, '2');
        mr.setLeader(20, '4');
        mr.setLeader(21, '5');
        mr.setLeader(22, '0');
        mr.setLeader(23, '0');
        
        int baseAddr = Integer.parseInt(leader.substring(12,17)), nFields = (baseAddr - 24) / 12;

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
        
        return mr;
    }
}
