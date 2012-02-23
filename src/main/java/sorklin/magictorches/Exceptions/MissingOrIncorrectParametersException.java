/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sorklin.magictorches.Exceptions;

/**
 *
 * @author Sork
 */
public class MissingOrIncorrectParametersException extends Exception {
    
    private String message;
    
    public MissingOrIncorrectParametersException(String message) {
        this.message = message;
    }
    
    public MissingOrIncorrectParametersException() {
        this.message = "Missing or incorrect parameters.";
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
}
