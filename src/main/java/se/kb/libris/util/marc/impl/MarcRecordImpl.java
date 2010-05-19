package se.kb.libris.util.marc.impl;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import se.kb.libris.util.marc.*;
import se.kb.libris.util.marc.impl.FieldImpl;

public class MarcRecordImpl implements MarcRecord {
    public Map<String, String> properties = null;
    char leader[] = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' };
    List<FieldImpl> fields = new LinkedList<FieldImpl>();
    
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
    public List<FieldImpl> getFields() {
        return fields;
    }

    @Override
    public List<ControlfieldImpl> getControlfields() {
        List<ControlfieldImpl> f = new LinkedList<ControlfieldImpl>();

        for (FieldImpl field: fields)
            if (f instanceof ControlfieldImpl)
                f.add((ControlfieldImpl)field);

        return f;
    }

    @Override
    public List<DatafieldImpl> getDatafields() {
        List<DatafieldImpl> f = new LinkedList<DatafieldImpl>();

        for (FieldImpl field: fields)
            if (f instanceof DatafieldImpl)
                f.add((DatafieldImpl)field);

        return f;
    }

    @Override
    public Iterator<FieldImpl> iterator() {
        return fields.iterator();
    }
    
    @Override
    public ListIterator<FieldImpl> listIterator() {
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
        
        Iterator<FieldImpl> iter = iterator();
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
    
    public List<FieldImpl> getFields(Pattern pattern) {
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
    public List<ControlfieldImpl> getControlfields(String regexp) {
        LinkedList<ControlfieldImpl> list = new LinkedList();

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
    public List<DatafieldImpl> getDatafields(String regexp) {
        LinkedList<DatafieldImpl> list = new LinkedList();

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
    public Iterator<FieldImpl> iterator(String regexp) {
        return getFields(regexp).iterator();
    }
    
    @Override
    public Iterator<FieldImpl> iterator(Pattern pattern) {
        return getFields(pattern).iterator();
    }
    
    @Override
    public ListIterator<FieldImpl> listIterator(String regexp) {
        return getFields(regexp).listIterator();
    }
    
    public ListIterator<FieldImpl> listIterator(Pattern pattern) {
        return getFields(pattern).listIterator();
    }
    
    @Override
    public void setProperty(String property, String value) {
        if (properties == null) properties = new HashMap<String, String>();
        
        properties.put(property, value);
    }
    
    @Override
    public String getProperty(String property) {
        if (properties == null) return null;
        else return properties.get(property);
    }
    
    @Override
    public String getProperty(String property, String def) {
        if (properties == null) return def;
        else if (properties.containsKey(property)) return properties.get(property);
        else return def;
    }
    
    @Override
    public List<DatafieldImpl> grep(String str) {
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

        List<DatafieldImpl> ret = new LinkedList<DatafieldImpl>();
        Iterator<FieldImpl> fiter = iterator(tag);

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
}
