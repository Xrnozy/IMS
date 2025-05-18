package InventoryManagement;

/**
 * A utility class to store the logged-in user's session information.
 */
public class UserSession {

    // Static field to store the logged-in user's name
    private static String loggedInUser = "Default User";

    /**
     * Sets the logged-in user's name.
     * @param username The username of the logged-in user.
     */
    public static void setLoggedInUser(String username) {
        loggedInUser = username;
    }

    /**
     * Gets the logged-in user's name.
     * @return The username of the logged-in user.
     */
    public static String getLoggedInUser() {
        return loggedInUser;
    }
}
