package org.apache.batik.constraint.xpath;

import javax.xml.transform.ErrorListener;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.Expression;
import org.apache.xpath.compiler.XPathParser;

import org.apache.xpath.compiler.OpMap;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.compiler.FunctionTable;

/**
 * Extension of the standard Xalan XPath class that uses
 * a modified Compiler class so that CSVG variable resolution
 * can happen.
 */
public class XPath extends org.apache.xpath.XPath {

    /**
     * The compiler for this XPath.
     */
    protected Compiler compiler;
    
    /**
     * Create a new XPath object.
     * @param exprString the XPath expression string.
     * @param prefixResolver the object used to resolve namespace prefixes.
     */
    public XPath(String exprString, PrefixResolver prefixResolver)
            throws javax.xml.transform.TransformerException {
        super(null);
        ErrorListener errorListener = new org.apache.xml.utils.DefaultErrorHandler();
        XPathParser parser = new XPathParser(errorListener, null);
        compiler = new Compiler(errorListener, null);
        parser.initXPath(compiler, exprString, prefixResolver);
        // dumpOpMap();
        Expression expr = compiler.compile(0);
        setExpression(expr);
    }

    /**
     * Print the opmap for this XPath expression.
     */
    public void dumpOpMap() {
        try {
            rec(0, 0);
        } catch (javax.xml.transform.TransformerException te) {
            te.printStackTrace();
        }
    }
    
    /**
     * Get the Compiler used by this XPath expression.
     */
    public Compiler getCompiler() {
        return compiler;
    }

    protected String OP(int op) {
        switch (op) {
            case -1:
                return "ENDOP";
            case -2:
                return "EMPTY";
            case -3:
                return "ELEMWILDCARD";
            case 1030:
                return "NODETYPE_COMMENT";
            case 1031:
                return "NODETYPE_TEXT";
            case 1032:
                return "NODETYPE_PI";
            case 1033:
                return "NODETYPE_NODE";
            case 1034:
                return "NODETYPE_FUNCTEST";
        }
        if (op < 0 || op >= _OP.length) {
            return "[invalid op " + op + "]";
        }
        return _OP[op];
    }
    
    public String[] _OP = {
        "0",
        "OP_XPATH",
        "OP_OR",
        "OP_AND",
        "OP_NOTEQUALS",
        "OP_EQUALS",
        "OP_LTE",
        "OP_LT",
        "OP_GTE",
        "OP_GT",
        "OP_PLUS",
        "OP_MINUS",
        "OP_MULT",
        "OP_DIV",
        "OP_MOD",
        "OP_QUO",
        "OP_NEG",
        "OP_STRING",
        "OP_BOOL",
        "OP_NUMBER",
        "OP_UNION",
        "OP_LITERAL",

        "OP_VARIABLE",
        "OP_GROUP",
        "OP_EXTFUNCTION",
        "OP_FUNCTION",

        "OP_ARGUMENT",
        "OP_NUMBERLIT",
        "OP_LOCATIONPATH",
        "OP_PREDICATE",
        "OP_MATCHPATTERN",
        "OP_LOCATIONPATHPATTERN",

        "32",
        "33",

        "NODENAME",
        "NODETYPE_ROOT",
        "NODETYPE_ANYELEMENT",

        "FROM_ANCESTORS",
        "FROM_ANCESTOR_OR_SELF",
        "FROM_ATTRIBUTES",
        "FROM_CHILDREN",
        "FROM_DESCENDANTS",
        "FROM_DESCENDANTS_OR_SELF",
        "FROM_FOLLOWING",
        "FROM_FOLLOWING_SIBLINGS",
        "FROM_PARENT",
        "FROM_PRECEDING",
        "FROM_PRECEDING_SIBLINGS",
        "FROM_SELF",
        "FROM_NAMESPACE",
        "FROM_ROOT",

        "MATCH_ATTRIBUTE",
        "MATCH_ANY_ANCESTOR",
        "MATCH_IMMEDIATE_ANCESTOR"
    };
    
    protected void rec(int opPos, int level) throws javax.xml.transform.TransformerException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        String indent = sb.toString();

        int op = compiler.getOp(opPos);
        int end, tokenpos, token1pos, token2pos;
        Object token, token1, token2;
        System.out.println(indent + OP(op));
        switch (op) {
            case OpCodes.OP_XPATH:
                rec(compiler.getFirstChildPos(opPos), level + 1);
                break;
            case OpCodes.OP_OR:
            case OpCodes.OP_AND:
            case OpCodes.OP_NOTEQUALS:
            case OpCodes.OP_EQUALS:
            case OpCodes.OP_LTE:
            case OpCodes.OP_LT:
            case OpCodes.OP_GTE:
            case OpCodes.OP_GT:
            case OpCodes.OP_PLUS:
            case OpCodes.OP_MINUS:
            case OpCodes.OP_MULT:
            case OpCodes.OP_DIV:
            case OpCodes.OP_MOD:
            case OpCodes.OP_QUO:
                rec(compiler.getFirstChildPos(opPos), level + 1);
                rec(compiler.getNextOpPos(compiler.getFirstChildPos(opPos)), level + 1);
                break;
            case OpCodes.OP_NEG:
            case OpCodes.OP_STRING:
            case OpCodes.OP_BOOL:
            case OpCodes.OP_NUMBER:
            case OpCodes.OP_GROUP:
            case OpCodes.OP_ARGUMENT:
            case OpCodes.OP_PREDICATE:
                rec(compiler.getFirstChildPos(opPos), level + 1);
                break;
            case OpCodes.OP_UNION:
            case OpCodes.OP_MATCHPATTERN:
                end = compiler.getNextOpPos(op);
                opPos = compiler.getFirstChildPos(opPos);
                while (opPos < end) {
                    rec(opPos, level + 1);
                    opPos = compiler.getNextOpPos(opPos);
                }
                break;
            case OpCodes.OP_LITERAL:
            case OpCodes.OP_NUMBERLIT:
                token = compiler.getToken(compiler.getOp(compiler.getFirstChildPos(opPos)));
                System.out.println(indent + "  " + token);
                break;
            case OpCodes.OP_VARIABLE:
                token1pos = compiler.getOp(compiler.getFirstChildPos(opPos));
                token2pos = compiler.getOp(compiler.getFirstChildPos(opPos) + 1);
                token1 = token1pos == OpCodes.EMPTY ? null : compiler.getToken(token1pos);
                token2 = compiler.getToken(token2pos);
                System.out.println(indent + "  " + (token1 != null ? token1 + ":" : "") + token2);
                break;
            case OpCodes.OP_EXTFUNCTION:
                token1pos = compiler.getOp(compiler.getFirstChildPos(opPos));
                token2pos = compiler.getOp(compiler.getFirstChildPos(opPos) + 1);
                token1 = token1pos == OpCodes.EMPTY ? null : compiler.getToken(token1pos);
                token2 = compiler.getToken(token2pos);
                System.out.println(indent + "  " + (token1 != null ? token1 + ":" : "") + token2);
                end = compiler.getNextOpPos(op);
                opPos += 4;
                while (opPos < end) {
                    rec(opPos, level + 1);
                    if (compiler.getOp(opPos) == OpCodes.ENDOP) {
                        break;
                    }
                    opPos = compiler.getNextOpPos(opPos);
                }
                break;
            case OpCodes.OP_FUNCTION:
                int funcnum = compiler.getOp(compiler.getFirstChildPos(opPos));
                System.out.println(indent + "  " + FunctionTable.getFunction(funcnum).getClass().getName());
                // end = compiler.getNextOpPos(op);
                opPos += 3;
                while (true/*opPos < end*/) {
                    rec(opPos, level + 1);
                    if (compiler.getOp(opPos) == OpCodes.ENDOP) {
                        break;
                    }
                    opPos = compiler.getNextOpPos(opPos);
                }
                break;
            case OpCodes.OP_LOCATIONPATH:
            case OpCodes.OP_LOCATIONPATHPATTERN:
                end = compiler.getNextOpPos(opPos);
                opPos += 2;
                while (opPos < end) {
                    rec(opPos, level + 1);
                    if (compiler.getOp(opPos) == OpCodes.ENDOP) {
                        break;
                    }
                    opPos = compiler.getNextOpPos(opPos);
                }
                break;
            case OpCodes.FROM_ANCESTORS:
            case OpCodes.FROM_ANCESTORS_OR_SELF:
            case OpCodes.FROM_ATTRIBUTES:
            case OpCodes.FROM_CHILDREN:
            case OpCodes.FROM_DESCENDANTS:
            case OpCodes.FROM_DESCENDANTS_OR_SELF:
            case OpCodes.FROM_FOLLOWING:
            case OpCodes.FROM_FOLLOWING_SIBLINGS:
            case OpCodes.FROM_PARENT:
            case OpCodes.FROM_PRECEDING:
            case OpCodes.FROM_PRECEDING_SIBLINGS:
            case OpCodes.FROM_SELF:
            case OpCodes.FROM_NAMESPACE:
            case OpCodes.FROM_ROOT:
                end = compiler.getNextOpPos(opPos);
                opPos += 3;
                op = compiler.getOp(opPos);
                System.out.println(indent + "  " + OP(op));
                switch (op) {
                    case OpCodes.NODETYPE_COMMENT:
                    case OpCodes.NODETYPE_TEXT:
                    case OpCodes.NODETYPE_NODE:
                    case OpCodes.NODETYPE_ROOT:
                    case OpCodes.NODETYPE_ANYELEMENT:
                    case OpCodes.NODETYPE_FUNCTEST:
                        opPos++;
                        break;
                    case OpCodes.NODETYPE_PI:
                        tokenpos = compiler.getOp(opPos + 1);
                        System.out.println(indent + "  " + compiler.getToken(tokenpos));
                        opPos += 2;
                        break;
                    case OpCodes.NODENAME:
                        token1pos = compiler.getOp(opPos + 1);
                        token2pos = compiler.getOp(opPos + 2);
                        token1 = token1pos == OpCodes.EMPTY ? null : token1pos == OpCodes.ELEMWILDCARD ? "*" : compiler.getToken(token1pos);
                        token2 = token2pos == OpCodes.ELEMWILDCARD ? "*" : compiler.getToken(token2pos);
                        System.out.println(indent + "  " + (token1 != null ? token1 + ":" : "") + token2);
                        opPos += 3;
                        break;
                }
                while (opPos < end) {
                    rec(opPos, level + 2);
                    /*if (compiler.getOp(opPos) == OpCodes.ENDOP) {
                        break;
                    }*/
                    opPos = compiler.getNextOpPos(opPos);
                }
                break;
        }
    }
}
