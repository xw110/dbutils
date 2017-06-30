package net.dongliu.dbutils.exception;

/**
 * Thrown when parameter not found
 * @author Liu Dong
 */
public class ParameterNotFoundException extends UncheckedSQLException {
    private static final long serialVersionUID = -8413383946018518722L;

    public ParameterNotFoundException(String name) {
        super("Parameter with name " + name + " not found");
    }
}
