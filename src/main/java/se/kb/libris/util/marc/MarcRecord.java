/*
 * MarcRecord.java
 *
 * Created on den 31 maj 2003, 15:05
 */

package se.kb.libris.util.marc;

import java.util.*;
import java.util.regex.*;

/**
 *
 * @author  Martin Malmsten
 */
public interface MarcRecord {
    public static int AUTHORITY      = 1;
    public static int BIBLIOGRAPHIC  = 2;
    public static int HOLDINGS       = 3;    
    public static int CLASSIFICATION = 4;
    public static int COMMUNITY      = 5;
    public static int UNKNOWN        = 6;

    public static int FIRST  = 1;
    public static int LAST   = 2;
    public static int SORTED = 3;
    
    public static byte START_OF_FILING = 0x1c;
    public static byte END_OF_RECORD   = 0x1d;
    public static byte END_OF_FIELD    = 0x1e;
    public static byte SUBFIELD_MARK   = 0x1f;
    
    public int getType();
    public String getLeader();
    public char getLeader(int idx);
    public void setLeader(String leader);
    public void setLeader(int i, char c);
    public void addField(Field f);
    public void addField(Field f, Comparator c);
    public List<? extends Field> getFields();
    public List<? extends Controlfield> getControlfields();
    public List<? extends Datafield> getDatafields();
    public List<? extends Field> getFields(String regexp);
    public List<? extends Controlfield> getControlfields(String regexp);
    public List<? extends Datafield> getDatafields(String regexp);
    public Iterator<? extends Field> iterator();
    public Iterator<? extends Field> iterator(String regexp);
    public Iterator<? extends Field> iterator(Pattern pattern);
    public ListIterator<? extends Field> listIterator();
    public ListIterator<? extends Field> listIterator(String regexp);
    public Controlfield createControlfield(String tag, String data);
    public Datafield createDatafield(String tag);
    public void setProperty(String property, String value);
    public String getProperty(String property);
    public String getProperty(String property, String def);
    public List<? extends Datafield> grep(String str);
}
