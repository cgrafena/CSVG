package org.apache.batik.constraint.xpath;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xpath.compiler.FunctionTable;
import org.apache.batik.constraint.xpath.functions.FuncId;
import org.apache.batik.constraint.xpath.operations.Bool;
import org.apache.batik.constraint.xpath.operations.Div;
import org.apache.batik.constraint.xpath.operations.Equals;
import org.apache.batik.constraint.xpath.operations.Gt;
import org.apache.batik.constraint.xpath.operations.Gte;
import org.apache.batik.constraint.xpath.operations.Lt;
import org.apache.batik.constraint.xpath.operations.Lte;
import org.apache.batik.constraint.xpath.operations.Minus;
import org.apache.batik.constraint.xpath.operations.Mod;
import org.apache.batik.constraint.xpath.operations.Mult;
import org.apache.batik.constraint.xpath.operations.Neg;
import org.apache.batik.constraint.xpath.operations.NotEquals;
import org.apache.batik.constraint.xpath.operations.Number;
import org.apache.batik.constraint.xpath.operations.Plus;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.UnaryOperation;

/**
 * Extension of the standard Xalan Compiler class that uses
 * a modified Variable class so that CSVG variable resolution
 * can happen, and also will compile in overloaded operators
 * for CSVG types.
 */
public class Compiler extends org.apache.xpath.compiler.Compiler {

    static {
        FunctionTable.installFunction(new FuncId(), FunctionTable.FUNC_ID);
    }
    
    /**
     * Create a new XPath compiler.
     */
    public Compiler(ErrorListener errorHandler, SourceLocator locator) {
        super(errorHandler, locator);
    }
    
    /**
     * Create an expression which represents a variable access.
     */
    protected Expression variable(int opPos)
        throws javax.xml.transform.TransformerException {

        Variable var = new Variable();
        
        opPos = getFirstChildPos(opPos);
        
        int nsPos = getOp(opPos);
        String namespace = (OpCodes.EMPTY == nsPos)
            ? null
            : (String) getTokenQueue().elementAt(nsPos);
        String localname = (String) getTokenQueue().elementAt(getOp(opPos + 1));
        QName qname = new QName(namespace, localname);

        var.setQName(qname);

        return var;
    }

    /**
     * Bottle-neck compilation of an operation with left and right operands.
     * Copied here from org.apache.xpath.compiler.Compiler since it is private.
     */
    protected Expression compileOperation(Operation operation, int opPos)
            throws TransformerException {

        int leftPos = getFirstChildPos(opPos);
        int rightPos = getNextOpPos(leftPos);

        operation.setLeftRight(compile(leftPos), compile(rightPos));

        return operation;
    }

    /**
     * Bottle-neck compilation of a unary operation.
     * Copied here from org.apache.xpath.compiler.Compiler since it is private.
     */
    protected Expression compileUnary(UnaryOperation unary, int opPos)
            throws TransformerException {

        int rightPos = getFirstChildPos(opPos);
        unary.setRight(compile(rightPos));
        return unary;
    }

    /**
     * Compile a '!=' operation.
     */
    protected Expression notequals(int opPos) throws TransformerException {
        return compileOperation(new NotEquals(), opPos);
    }

    /**
     * Compile a '=' operation.
     */
    protected Expression equals(int opPos) throws TransformerException {
        return compileOperation(new Equals(), opPos);
    }

    /**
     * Compile a '&lt;=' operation.
     */
    protected Expression lte(int opPos) throws TransformerException {
        return compileOperation(new Lte(), opPos);
    }

    /**
     * Compile a '&lt;' operation.
     */
    protected Expression lt(int opPos) throws TransformerException {
        return compileOperation(new Lt(), opPos);
    }

    /**
     * Compile a '&gt;=' operation.
     */
    protected Expression gte(int opPos) throws TransformerException {
        return compileOperation(new Gte(), opPos);
    }

    /**
     * Compile a '&gt;' operation.
     */
    protected Expression gt(int opPos) throws TransformerException {
        return compileOperation(new Gt(), opPos);
    }

    /**
     * Compile a '+' operation.
     */
    protected Expression plus(int opPos) throws TransformerException {
        return compileOperation(new Plus(), opPos);
    }

    /**
     * Compile a '-' operation.
     */
    protected Expression minus(int opPos) throws TransformerException {
        return compileOperation(new Minus(), opPos);
    }

    /**
     * Compile a '*' operation.
     */
    protected Expression mult(int opPos) throws TransformerException {
        return compileOperation(new Mult(), opPos);
    }

    /**
     * Compile a 'div' operation.
     */
    protected Expression div(int opPos) throws TransformerException {
        return compileOperation(new Div(), opPos);
    }

    /**
     * Compile a 'mod' operation.
     */
    protected Expression mod(int opPos) throws TransformerException {
        return compileOperation(new Mod(), opPos);
    }

    /**
     * Compile a unary '-' operation.
     */
    protected Expression neg(int opPos) throws TransformerException {
        return compileUnary(new Neg(), opPos);
    }

    /**
     * Compile a 'string(...)' operation.
     */
    protected Expression string(int opPos) throws TransformerException {
        return compileUnary(
                new org.apache.batik.constraint.xpath.operations.String(), 
                opPos);
    }

    /**
     * Compile a 'boolean(...)' operation.
     */
    protected Expression bool(int opPos) throws TransformerException {
        return compileUnary(new Bool(), opPos);
    }

    /**
     * Compile a 'number(...)' operation.
     */
    protected Expression number(int opPos) throws TransformerException {
        return compileUnary(new Number(), opPos);
    }
}
