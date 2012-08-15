/*
 * DatafieldImpl.java
 *
 * Created on den 18 augusti 2003, 19:41
 */

package se.kb.libris.util.marc.impl;

import java.util.*;
import se.kb.libris.util.marc.*;
import java.util.regex.Pattern;

/**
 *
 * @author  Martin Malmsten
 */
public class DatafieldImpl extends FieldImpl implements Datafield {
    List subfields = new LinkedList<Subfield>();
    char indicators[] = { ' ', ' ' };
    
    /** Creates a new instance of DatafieldImpl */
    public DatafieldImpl(String tag) {
        super(tag);
    }
    
    public DatafieldImpl(String tag, byte data[], int pos1, int pos2) {
        super(tag);
        
        String str = new String(data, pos1, (pos2-pos1+1));
        
        if (str.indexOf("[U+") != -1) {
            StringBuffer sb = new StringBuffer();
            String s[] = str.split("\\[U\\+");
            sb.append(s[0]);

            for (int i=1;i<s.length;i++) {
                try {
                    sb.append((char)Integer.decode("0x" + s[i].substring(0, 4)).intValue());
                    sb.append(s[i].substring(5));
                } catch (NumberFormatException e) {
                    sb.append("[U+" + s[i]);
                }
            }

            str = sb.toString();
        }        
        
        String fields[] = str.split(String.valueOf((char)MarcRecord.SUBFIELD_MARK));
        
        setIndicator(0, fields[0].charAt(0));
        setIndicator(1, fields[0].charAt(1));
        
        for (int i=1;i<fields.length;i++) {
            addSubfield(fields[i].charAt(0), fields[i].substring(1).replace((char)0xFFFF, ' '));
        }
    }    
    
    public DatafieldImpl(String tag, byte data[], int pos1, int pos2, String encoding) throws java.io.UnsupportedEncodingException {
        super(tag);
        
        String str = new String(data, pos1, (pos2-pos1+1), encoding);
        
        if (str.indexOf("[U+") != -1) {
            StringBuffer sb = new StringBuffer();
            String s[] = str.split("\\[U\\+");
            sb.append(s[0]);

            for (int i=1;i<s.length;i++) {
                try {
                    sb.append((char)Integer.decode("0x" + s[i].substring(0, 4)).intValue());
                    sb.append(s[i].substring(5));
                } catch (NumberFormatException e) {
                    sb.append("[U+" + s[i]);
                }
            }

            str = sb.toString();
        }        

        String fields[] = str.split(String.valueOf((char)MarcRecord.SUBFIELD_MARK));
        
        setIndicator(0, fields[0].charAt(0));
        setIndicator(1, fields[0].charAt(1));
        
        for (int i=1;i<fields.length;i++) {
            addSubfield(fields[i].charAt(0), fields[i].substring(1).replace((char)0xFFFF, ' '));
        }
    }    

    @Override
    public void addSubfield(Subfield f) { 
        subfields.add(f);
    }
    
    @Override
    public Datafield addSubfield(char code, String data) {
        subfields.add(new SubfieldImpl(code, data));
        
        return this;
    }
    
    @Override
    public char getIndicator(int idx) {
        return indicators[idx];
    }

    @Override
    public Datafield setIndicator(int idx, char c) {
	if (c >= 0x20 && c <= 0xff)
	        indicators[idx] = c;
        
        return this;
    }
    
    @Override
    public List<Subfield> getSubfields() {
        return subfields;
    }

    @Override
    public Iterator<Subfield> iterator() {
        return subfields.iterator();
    }

    @Override
    public ListIterator<Subfield> listIterator() {
        return subfields.listIterator();
    }
    
    @Override
    public List<Subfield> getSubfields(String regexp) {
        LinkedList list = new LinkedList();
        
        Iterator iter = iterator();
        while (iter.hasNext()) {
            Subfield sf = (Subfield)iter.next();
            
            if (Pattern.matches(regexp, String.valueOf(sf.getCode()))) {
                list.add(sf);
            }
        }
        
        return java.util.Collections.unmodifiableList(list);
    }
    
    @Override
    public Iterator<Subfield> iterator(String regexp) {
        return getSubfields(regexp).iterator();
    }
    
    @Override
    public ListIterator<Subfield> listIterator(String regexp) {
        return getSubfields(regexp).listIterator();
    }
    
    
    @Override
    public Subfield createSubfield(char code, String data) {
        return new SubfieldImpl(code, data);
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append(getTag());
        sb.append(" '" + getIndicator(0) + "' '" + getIndicator(1));
        
        Iterator iter = iterator();
        
        while (iter.hasNext()) {
            Subfield sf = (Subfield)iter.next();
            sb.append(" $" + sf.getCode());
            sb.append(sf.getData());
        }
        
        return sb.toString();
    }
}
