package uk.gov.dvla.osg.vault.viewer;

/**
 * Session information for the logged in user.
 */
public class Session {
    private static String userName;
    private static String password;
    private static String token;

    /******************************************************************************************
     * SINGLETON PATTERN
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
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        Session.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        Session.password = password;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        Session.token = token;
    }

}
