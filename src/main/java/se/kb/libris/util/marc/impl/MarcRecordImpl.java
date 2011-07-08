package se.kb.libris.util.marc.impl;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import se.kb.libris.util.marc.*;
import se.kb.libris.util.marc.impl.FieldImpl;

public class MarcRecordImpl implements MarcRecord {
    public SortedMap<String, String> properties = new TreeMap<String, String>();
    char leader[] = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' };
    List<Field> fields = new LinkedList<Field>();
    byte data[] = null;
    
    /** Creates a new instance of MarcRecordImpl */
    public MarcRecordImpl() {
    }
    
    @Override
    public void addField(Field f) {
        fields.add((FieldImpl)f);
    }
    
    @Override
    public void addField(Field f, Comparator c) {
        if (fields.isEmpty()) {
            fields.add((FieldImpl)f);
        } else {
            ListIterator i = fields.listIterator();
            
            while (i.hasNext()) {
                Field f2 = (Field)i.next();
                int n = c.compare(f, f2);
                
                if (n < 0) {
                    i.previous();
                    i.add(f);
                    
                    return;
                }
            }
            
            fields.add((FieldImpl)f);
        }
    }
    
    @Override
    public List<Field> getFields() {
        return fields;
    }

    @Override
    public List<Controlfield> getControlfields() {
        List<Controlfield> f = new LinkedList<Controlfield>();

        for (Field field: fields)
            if (f instanceof ControlfieldImpl)
                f.add((ControlfieldImpl)field);

        return f;
    }

    @Override
    public List<Datafield> getDatafields() {
        List<Datafield> f = new LinkedList<Datafield>();

        for (Field field: fields)
            if (f instanceof Datafield)
                f.add((Datafield)field);

        return f;
    }

    @Override
    public Iterator<Field> iterator() {
        return fields.iterator();
    }
    
    @Override
    public ListIterator<Field> listIterator() {
        return fields.listIterator();
    }
    
    @Override
    public String getLeader() {
        return new String(leader);
    }
    
    @Override
    public char getLeader(int idx) {
        return leader[idx];
    }
    
    @Override
    public void setLeader(String leader) {
        this.leader = leader.toCharArray();
    }
    
    @Override
    public void setLeader(int i, char c) {
        leader[i] = c;
    }
    
    @Override
    public Controlfield createControlfield(String tag, String data) {
        return new ControlfieldImpl(tag, data);
        
    }
    
    @Override
    public Datafield createDatafield(String tag) {
        return new DatafieldImpl(tag);
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("leader: " + getLeader() + "\n");
        
        Iterator<Field> iter = iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            
            if (f instanceof Controlfield) {
                Controlfield cf = (Controlfield)f;
                sb.append(cf.getTag() + " " + cf.getData() + "#\n");
            } else {
                Datafield df = (Datafield)f;
                
                sb.append(df.getTag() + " '" + df.getIndicator(0) + "' '" + df.getIndicator(1) + "'");
                
                Iterator sfiter = df.iterator();
                while (sfiter.hasNext()) {
                    Subfield sf = (Subfield)sfiter.next();
                    sb.append("$" + sf.getCode() + sf.getData().replace('\r', ' ').replace('\n', ' '));
                }
                
                sb.append("#\n");
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public int getType() {
        char c = getLeader(6);
        if (c == 'z') {
            return AUTHORITY;
        } else if (c == 'u' ||
                   c == 'v' ||
                   c == 'x' ||
                   c == 'y') {
            return HOLDINGS;
        } else if (c == 'a' ||
                   c == 'c' ||
                   c == 'd' ||
                   c == 'e' ||
                   c == 'f' ||
                   c == 'g' ||
                   c == 'i' ||
                   c == 'j' ||
                   c == 'k' ||
                   c == 'm' ||
                   c == 'o' ||
                   c == 'p' ||
                   c == 'r' ||
                   c == 't' ) {
            return BIBLIOGRAPHIC;
        } else if (c == 'w') {
            return CLASSIFICATION;
        } else if (c == 'q') {
            return COMMUNITY;
        }
        
        return UNKNOWN;
    }
    
    @Override
    public List getFields(String regexp) {
        LinkedList list = new LinkedList();
        
        Iterator iter = iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            
            if (Pattern.matches(regexp, f.getTag())) {
                list.add(f);
            }
        }
        
        return list;
    }
    
    public List<Field> getFields(Pattern pattern) {
        LinkedList list = new LinkedList();

        Iterator iter = iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();

            if (pattern.matcher(f.getTag()).find()) {
                list.add(f);
            }
        }

        return list;
    }

    @Override
    public List<Controlfield> getControlfields(String regexp) {
        LinkedList<Controlfield> list = new LinkedList();

        Iterator iter = iterator(regexp);
        while (iter.hasNext()) {
            ControlfieldImpl f = (ControlfieldImpl)iter.next();

            if (Pattern.matches(regexp, f.getTag())) {
                list.add(f);
            }
        }

        return java.util.Collections.unmodifiableList(list);
    }

    @Override
    public List<Datafield> getDatafields(String regexp) {
        LinkedList<Datafield> list = new LinkedList();

        Iterator iter = iterator(regexp);
        while (iter.hasNext()) {
            DatafieldImpl f = (DatafieldImpl)iter.next();

            if (Pattern.matches(regexp, f.getTag())) {
                list.add(f);
            }
        }

        return java.util.Collections.unmodifiableList(list);
    }

    @Override
    public Iterator<Field> iterator(String regexp) {
        return getFields(regexp).iterator();
    }
    
    @Override
    public Iterator<Field> iterator(Pattern pattern) {
        return getFields(pattern).iterator();
    }
    
    @Override
    public ListIterator<Field> listIterator(String regexp) {
        return getFields(regexp).listIterator();
    }
    
    public ListIterator<Field> listIterator(Pattern pattern) {
        return getFields(pattern).listIterator();
    }
    
    @Override
    public void setProperty(String property, String value) {
        properties.put(property, value);
    }
    
    @Override
    public String getProperty(String property) {
        return properties.get(property);
    }
    
    @Override
    public String getProperty(String property, String def) {
        if (properties.get(property) != null) return properties.get(property);
        else return def;
    }

    @Override
    public SortedMap<String, String> getProperties() {
        return properties;
    }
    
    @Override
    public List<Datafield> grep(String str) {
        // example 100a,b,c,w=g;h
        Map<String, Set<String>> filter = new HashMap<String, Set<String>>();
        String tag = str.substring(0,3), subfields[]=str.substring(3).split(",");
        Set<String> set = new HashSet<String>();

        for (String subfield: subfields) {
            Set<String> hs = new HashSet<String>();

            if (subfield.length() > 1 && subfield.charAt(1) == '=') {
                for (String value: subfield.substring(2).split(";")) {
                    hs.add(value);
                }

                filter.put(tag + subfield.charAt(0), hs);
            } else {
                hs.add("*");
                filter.put(tag + subfield.charAt(0), hs);
            }


            set.add(tag + subfield.charAt(0));
        }

        List<Datafield> ret = new LinkedList<Datafield>();
        Iterator<Field> fiter = iterator(tag);

        while (fiter.hasNext()) {
            DatafieldImpl df = (DatafieldImpl)fiter.next();
            Set<String> set2 = new HashSet<String>(set);

            Iterator sfiter = df.iterator();

            while (sfiter.hasNext()) {
                Subfield sf = (Subfield)sfiter.next();
                Set<String> set3 = filter.get(df.getTag() + sf.getCode());

                if (set3 != null && (set3.contains("*") || set3.contains(sf.getData())))
                    set2.remove(df.getTag() + sf.getCode());
            }

            if (set2.size() == 0)
                ret.add(df);
        }

        return ret;
    }

    @Override
    public byte[] getOriginalData() {
        return data;
    }

    @Override
    public void setOriginalData(byte[] _data) {
        data = _data;
    }
}
