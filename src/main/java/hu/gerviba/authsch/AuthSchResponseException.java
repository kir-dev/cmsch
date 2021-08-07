/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <p>
 * <gerviba@gerviba.hu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.       Szabó Gergely
 */
package hu.gerviba.authsch;

public class AuthSchResponseException extends RuntimeException {

    private static final long serialVersionUID = -3986811567199003859L;

    public AuthSchResponseException(String message) {
        super(message);
    }

    public AuthSchResponseException(String message, Throwable t) {
        super(message, t);
    }

}
