/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <p>
 * <gerviba@gerviba.hu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.       Szabó Gergely
 */
package hu.gerviba.authsch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.gerviba.authsch.response.AuthResponse;
import hu.gerviba.authsch.response.ProfileDataResponse;
import hu.gerviba.authsch.response.ProfileDataResponse.ProfileDataResponseBuilder;
import hu.gerviba.authsch.struct.Scope;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

/**
 * <h1>How to init:</h1>
 * Set client id and key with {@link #setClientIdentifier(String)} and {@link #setClientKey(String)}.
 * Store it in application scope.
 *
 * @author Gerviba
 * @see {@link #validateAuthentication(String)}
 * @see {@link #refreshToken(String)}
 * @see {@link #getProfile(String)}
 */
public class AuthSchAPI implements Serializable {

    private static final long serialVersionUID = 3441712708900902459L;
    private static final String FAILED_TO_PARSE_AUTH_RESPONSE = "Failed to parse auth hu.gerviba.authsch.response";

    private String tokenUrlBase = "https://auth.sch.bme.hu/oauth2/token";
    private String loginUrlBase = "https://auth.sch.bme.hu/site/login";
    private String apiUrlBase = "https://auth.sch.bme.hu/api";
    private String clientIdentifier = "testclient";
    private String clientKey = "testpass";

    /**
     * Token endpoint url
     *
     * @return Default: https://auth.sch.bme.hu/oauth2/token
     */
    public String getTokenUrlBase() {
        return tokenUrlBase;
    }

    /**
     * Sets token endpoint url
     *
     * @param tokenUrlBase (default: https://auth.sch.bme.hu/oauth2/token)
     */
    public void setTokenUrlBase(String tokenUrlBase) {
        this.tokenUrlBase = tokenUrlBase;
    }

    /**
     * Login base url
     *
     * @return Default: https://auth.sch.bme.hu/site/login
     */
    public String getLoginUrlBase() {
        return loginUrlBase;
    }

    /**
     * Sets login base url
     *
     * @param loginUrlBase (default: https://auth.sch.bme.hu/site/login)
     */
    public void setLoginUrlBase(String loginUrlBase) {
        this.loginUrlBase = loginUrlBase;
    }

    /**
     * API endpoint base url
     *
     * @return Default: https://auth.sch.bme.hu/api
     */
    public String getApiUrlBase() {
        return apiUrlBase;
    }

    /**
     * Sets API endpoint base url
     *
     * @param apiUrlBase (default: https://auth.sch.bme.hu/api)
     */
    public void setApiUrlBase(String apiUrlBase) {
        this.apiUrlBase = apiUrlBase;
    }

    /**
     * Sets client's identifier
     *
     * @param clientIdentifier about 20 digit numbers
     */
    public void setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }

    /**
     * Sets client's secret key
     *
     * @param clientKey about 80 chars [a-zA-Z0-9]
     */
    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientIdentifier() {
        return clientIdentifier;
    }

    public String getClientKey() {
        return clientKey;
    }

    /**
     * Validate authentication
     *
     * @param code Received `code` value from HTTPS parameters
     * @throws AuthSchResponseException
     */
    public AuthResponse validateAuthentication(String code) {
        return httpPost("grant_type=authorization_code&code=" + code, false);
    }

    /**
     * Validate authentication
     *
     * @param code Received `code` value from HTTPS parameters
     * @throws AuthSchResponseException
     */
    public AuthResponse refreshToken(String refreshToken) {
        return httpPost("grant_type=refresh_token&refresh_token=" + refreshToken, true);
    }

    /**
     * Login URL generator
     *
     * @param uniqueId A unique identifier for the user. Must be hashed! (eg. sha256(JSESSIONID))
     * @param scopes A list of used scopes
     * @return Generated login url
     */
    public String generateLoginUrl(String uniqueId, List<Scope> scopes) {
        return String.format("%s?response_type=code&client_id=%s&state=%s&scope=%s",
                loginUrlBase, clientIdentifier, uniqueId, Scope.buildForUrl(scopes));
    }

    /**
     * Login URL generator
     *
     * @param uniqueId A unique identifier for the user. Must be hashed! (eg. sha256(JSESSIONID))
     * @param scopes A list of used scopes
     * @return Generated login url
     */
    public String generateLoginUrl(String uniqueId, Scope... scopes) {
        return String.format("%s?response_type=code&client_id=%s&state=%s&scope=%s",
                loginUrlBase, clientIdentifier, uniqueId, Scope.buildForUrl(scopes));
    }

    /**
     * Load profile info
     *
     * @param accessToken
     * @throws AuthSchResponseException
     */
    public ProfileDataResponse getProfile(String accessToken) {
        return httpGet("profile", accessToken);
    }

    private ProfileDataResponse httpGet(String service, String accessToken) {
        URL obj = newUrl(apiUrlBase + "/" + service + "/?access_token=" + accessToken);
        HttpsURLConnection con = newGetConnection(obj);
        setGetHeaders(con);
        processResponseCode(con);

        return mapProfileDataResponse(readData(con));
    }

    private HttpsURLConnection newGetConnection(URL obj) {
        HttpsURLConnection con;
        try {
            con = (HttpsURLConnection) obj.openConnection();
        } catch (IOException e) {
            throw new AuthSchResponseException("Failed to open connection", e);
        }
        return con;
    }

    private void setGetHeaders(HttpsURLConnection con) {
        con.setRequestProperty("User-Agent", System.getProperty("hu.gerviba.authsch.useragent", "AuthSchJavaAPI"));
        con.setRequestProperty("Accept", "application/json");
    }

    ProfileDataResponse mapProfileDataResponse(String rawJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode obj = objectMapper.readTree(rawJson);

            ProfileDataResponseBuilder response = ProfileDataResponse.newBuilder();

            for (Scope scope : Scope.values())
                if (scope.canApply(obj))
                    scope.apply(response, obj);

            return response.build();
        } catch (NullPointerException | IOException e) {
            throw new AuthSchResponseException(FAILED_TO_PARSE_AUTH_RESPONSE, e);
        }
    }

    private AuthResponse httpPost(String parameters, boolean reAuth) {
        URL obj = newUrl(tokenUrlBase);
        HttpsURLConnection con = newPostConnection(obj);
        setPostHeaders(con);
        writePostParameters(parameters, con);
        processResponseCode(con);
        String response = readData(con);

        return reAuth
                ? mapAuthResponse(response, parameters)
                : mapAuthResponse(response);
    }

    private URL newUrl(String url) {
        URL obj;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            throw new AuthSchResponseException("Failed to create new URL", e);
        }
        return obj;
    }

    private HttpsURLConnection newPostConnection(URL obj) {
        HttpsURLConnection con;
        try {
            con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
        } catch (IOException e) {
            throw new AuthSchResponseException("Failed to open connection", e);
        }
        return con;
    }

    @SuppressWarnings("java:S2647" // ignore Http basic authentication
    )
    private void setPostHeaders(HttpsURLConnection con) {
        con.setRequestProperty("User-Agent", System.getProperty("hu.gerviba.authsch.useragent", "AuthSchJavaAPI"));
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", "Basic " + Base64.getEncoder()
                .encodeToString((clientIdentifier + ":" + clientKey).getBytes()));

        con.setDoOutput(true);
    }

    private void writePostParameters(String parameters, HttpsURLConnection con) {
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(parameters);
            wr.flush();
        } catch (IOException e) {
            throw new AuthSchResponseException("Failed to write post data", e);
        }
    }

    private String readData(HttpsURLConnection con) {
        String inputLine;
        String response;
        final StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            response = stringBuilder.toString();
        } catch (IOException e) {
            throw new AuthSchResponseException("Failed to write post data", e);
        }
        return response;
    }

    private int processResponseCode(HttpsURLConnection con) {
        int responseCode = -1;
        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            throw new AuthSchResponseException("Failed to get hu.gerviba.authsch.response code", e);
        }

        if (responseCode != 200)
            throw new AuthSchResponseException("HTTP hu.gerviba.authsch.response code: " + responseCode);
        return responseCode;
    }

    AuthResponse mapAuthResponse(String rawJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode obj = objectMapper.readTree(rawJson);
            return new AuthResponse(
                    obj.get("access_token").asText(),
                    obj.get("expires_in").asLong(),
                    obj.get("token_type").asText(),
                    Scope.listFromString(" ", obj.get("scope").asText()),
                    obj.get("refresh_token").asText());
        } catch (NullPointerException | IOException e) {
            throw new AuthSchResponseException(FAILED_TO_PARSE_AUTH_RESPONSE, e);
        }
    }

    AuthResponse mapAuthResponse(String rawJson, String parameters) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode obj = objectMapper.readTree(rawJson);
            return new AuthResponse(
                    obj.get("access_token").asText(),
                    obj.get("expires_in").asLong(),
                    obj.get("token_type").asText(),
                    Scope.listFromString(" ", obj.get("scope").asText()),
                    parameters.substring(parameters.lastIndexOf('=') + 1));
        } catch (NullPointerException | IOException e) {
            throw new AuthSchResponseException(FAILED_TO_PARSE_AUTH_RESPONSE, e);
        }
    }

}
