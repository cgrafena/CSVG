package org.apache.batik.constraint.xpath;

import javax.xml.transform.TransformerException;

import org.apache.xpath.Expression;
import org.apache.xpath.objects.XObject;

/**
 * Interface for values wrapped in XObjects so that operator overloading
 * can be effected.
 */
public interface Operators {
    public XObject notequals(XObject r) throws TransformerException;
    public XObject notequalsRev(XObject l) throws TransformerException;
    public XObject equals(XObject r) throws TransformerException;
    public XObject equalsRev(XObject l) throws TransformerException;
    public XObject lte(XObject r) throws TransformerException;
    public XObject lteRev(XObject l) throws TransformerException;
    public XObject lt(XObject r) throws TransformerException;
    public XObject ltRev(XObject l) throws TransformerException;
    public XObject gte(XObject r) throws TransformerException;
    public XObject gteRev(XObject l) throws TransformerException;
    public XObject gt(XObject r) throws TransformerException;
    public XObject gtRev(XObject l) throws TransformerException;
    public XObject plus(XObject r) throws TransformerException;
    public XObject plusRev(XObject l) throws TransformerException;
    public XObject minus(XObject r) throws TransformerException;
    public XObject minusRev(XObject l) throws TransformerException;
    public XObject mult(XObject r) throws TransformerException;
    public XObject multRev(XObject l) throws TransformerException;
    public XObject div(XObject r) throws TransformerException;
    public XObject divRev(XObject l) throws TransformerException;
    public XObject mod(XObject r) throws TransformerException;
    public XObject modRev(XObject l) throws TransformerException;
    public XObject neg() throws TransformerException;
    public XObject string() throws TransformerException;
    public XObject boolean_() throws TransformerException;
    public XObject number() throws TransformerException;
}
