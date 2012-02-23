/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sorklin.magictorches.Exceptions;

/**
 *
 * @author Sork
 */
public class InsufficientPermissionsException  extends Exception {
    
    private String message;
    
    public InsufficientPermissionsException(String message) {
        this.message = message;
    }
    
    public InsufficientPermissionsException() {
        this.message = "You do not have permissions to do that.";
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
