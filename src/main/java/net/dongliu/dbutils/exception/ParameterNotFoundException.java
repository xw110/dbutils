package net.dongliu.dbutils.exception;

/**
 * Thrown when parameter not found
 * @author Liu Dong
 */
public class ParameterNotFoundException extends UncheckedSQLException {
    public ParameterNotFoundException(String name) {
        super("Parameter with name " + name + " not found");
    }
}
