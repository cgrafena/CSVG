package org.apache.batik.constraint;

import org.apache.batik.constraint.VariableElement;
import org.apache.batik.dom.util.DoublyIndexedTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.Set;
import java.util.Iterator;

public class DependenciesMap extends DoublyIndexedTable {
    public void dump() {
        for (int i = 0; i < table.length; i++) {
            Entry e = table[i];
            while (e != null) {
                String nm = (String) e.key2;
                Set rds = (Set) e.value;
                if (nm.charAt(0) == ':') {
                    nm = nm.substring(1);
                }
                if (e.key1 instanceof Document) {
                    System.out.print("\tdocument." + nm + " <-- { ");
                } else {
                    Element el = (Element) e.key1;
                    String id = el.getAttributeNS(null, "id");
                    if (el instanceof VariableElement) {
                        System.out.print("\t$" + el.getAttributeNS(null, "name") + " <-- { ");
                    } else {
                        System.out.print("\t" + el.getNodeName() + (!id.equals("") ? "#" + id : "") + "." + nm + " <-- { ");
                    }
                }
                Iterator it = rds.iterator();
                while (it.hasNext()) {
                    Constraint c = (Constraint) it.next();
                    System.out.print(c.toString() + ", ");
                }
                System.out.print("}\n");
                e = e.next;
            }
        }
    }
}
