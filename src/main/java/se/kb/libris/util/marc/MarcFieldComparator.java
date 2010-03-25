package se.kb.libris.util.marc;

import java.util.*;

public class MarcFieldComparator {    
    public static java.util.Comparator strictSorted = new java.util.Comparator() {
        public int compare(Object f1, Object f2) { 
            return ((Field)f1).getTag().compareTo(((Field)f2).getTag()); 
        } 
    };
}
