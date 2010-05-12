/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.kb.libris.util.marc.test;

import java.io.IOException;
import se.kb.libris.util.marc.MarcRecord;
import se.kb.libris.util.marc.impl.MarcRecordImpl;
import se.kb.libris.util.marc.io.DomSerializer;
import se.kb.libris.util.marc.io.MarcXmlRecordReader;

/**
 *
 * @author marma
 */
public class XmlTest {
    public static void main(String args[]) throws IOException {
        MarcRecord mr = new MarcRecordImpl();
        mr.setLeader("0000000000000000000000000000");
        mr.addField(mr.createControlfield("001", "12345"));
        mr.addField(mr.createDatafield("100").addSubfield('a', "Strindberg, August,").addSubfield('d', "1849-1912"));
        System.out.println(mr);

        String str = DomSerializer.serialize(mr).toString();

        for (byte b: str.getBytes("UTF-8"))
            System.out.println("" + b + " " + (char)b);

        for (char c: str.toCharArray())
            System.out.println("" + (int)c + " " + c);

        System.out.println(str);

        MarcRecord mr2 = MarcXmlRecordReader.fromXml(str);
        System.out.println(mr2);
    }
}
