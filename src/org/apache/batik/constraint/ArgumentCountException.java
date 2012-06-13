package org.apache.batik.constraint;

/**
 * Wrong number of arguments passed to constraint function.
 */
public class ArgumentCountException extends ConstraintException {

    /**
     * Create a argument count exception.
     */
    public ArgumentCountException(String fn, int got, int expected) {
        super("Wrong number of arguments passed to function " + fn + " (got "
                + got + ", expected " + expected + ")");
    }

    /**
     * Create a argument count exception.
     */
    public ArgumentCountException(String fn, int got, int[] expected) {
        super("Wrong number of arguments passed to function " + fn + " (got "
                + got + ", expected " + join(expected) + ")");
    }

    protected static String join(int[] ns) {
        if (ns.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ns[0]);
        for (int i = 1; i < ns.length - 1; i++) {
            sb.append(", " + ns[i]);
        }
        if (ns.length > 1) {
            sb.append(" or " + ns[ns.length - 1]);
        }
        return sb.toString();
    }
}
