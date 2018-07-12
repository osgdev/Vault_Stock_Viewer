package uk.gov.dvla.osg.vault.session;

/**
 * Session information for the logged in user.
 */
public class Session {
    
    private String userName;
    private String password;
    private String token;

    /******************************************************************************************
     *                              SINGLETON PATTERN
     ******************************************************************************************/

    private static class SingletonHelper {
        private static final Session INSTANCE = new Session();
    }

    public static Session getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /*****************************************************************************************/
    
    private Session() { }
    
    /**
     * Gets the user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user name.
     *
     * @param userName the new user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the token.
     *
     * @param token the new token
     */
    public void setToken(String token) {
        this.token = token;
    }

}
