/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <p>
 * <gerviba@gerviba.hu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.       Szabó Gergely
 */
package hu.gerviba.authsch.response;

import hu.gerviba.authsch.struct.Scope;

import java.io.Serializable;
import java.util.List;

/**
 * @author Gerviba
 */
public final class AuthResponse implements Serializable {

    private static final long serialVersionUID = 4140354200501250401L;

    private final String accessToken;
    private final long expiresIn;
    private final String tokenType;
    private final List<Scope> scopes;
    private final String refreshToken;

    public AuthResponse(String accessToken, long expiresIn,
                        String tokenType, List<Scope> scopes, String refreshToken) {

        this.accessToken = accessToken;
        this.expiresIn = System.currentTimeMillis() + (expiresIn * 1000);
        this.tokenType = tokenType;
        this.scopes = scopes;
        this.refreshToken = refreshToken;
    }

    /**
     * Used for API requests
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Timestamp of expire
     */
    public long getExpiresIn() {
        return expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    /**
     * Used for refreshing the access token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String toString() {
        return "AuthResponse [accessToken=" + accessToken + ", expiresIn=" + expiresIn + ", tokenType=" + tokenType
                + ", scopes=" + scopes + ", refreshToken=" + refreshToken + "]";
    }

}
