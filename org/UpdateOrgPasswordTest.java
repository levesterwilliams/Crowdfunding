import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;

public class UpdateOrgPasswordTest {

    /**
     * Tests for successful password update.
     */
    @Test
    public void testSuccessfulUpdatePassword() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // creates the JSONObject for entire request
                JSONObject obj = new JSONObject();
                obj.put("status", "success");
                String jsonString = obj.toJSONString();
                return jsonString;
            }
        });
        assertTrue(dm.updatePassword("12345", "new password"));
    }

    /**
     * Tests for unsuccessful password update.
     */
    @Test
    public void testUnSuccessfulUpdatePassword() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // creates the JSONObject for entire request
                JSONObject obj = new JSONObject();
                obj.put("status", "error");
                String jsonString = obj.toJSONString();
                return jsonString;
            }
        });
        assertFalse(dm.updatePassword("12345", "new password"));
    }

    /**
     * Tests null as argument for login.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullLogin() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"fail\"}";
            }
        });
        dm.updatePassword(null, "new password");
    }

    /**
     * Tests null as argument for the password.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullPassword() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"fail\"}";
            }
        });
        dm.updatePassword("hello", null);
    }

    /**
     * Tests malformed JSON string.
     */
    @Test(expected = IllegalStateException.class)
    public void testBadJsonSTring() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "bad string";
            }
        });
        dm.updatePassword("hello", "password");
    }

    /**
     * Tests for null WebClient passed to the DataManager class.
     */
    @Test(expected = IllegalStateException.class)
    public void testUpdateOrgPassword_WebClientIsNull() {
        DataManager dm = new DataManager(null);
        dm.updatePassword("login", "password");
        fail("DataManager.updatePassword does not throw IllegalStateException when WebClient is null");
    }

    /**
     * Tests for wrong port passed to WebClient.
     */
    @Test(expected = IllegalStateException.class)
    public void testUpdateOrgPassword_WebClientCannotConnectToServer() {
        // this assumes no server is running on port 3002
        DataManager dm = new DataManager(new WebClient("localhost", 3002));
        dm.updatePassword("login", "password");
        fail("DataManager.updatePassword does not throw IllegalStateException when WebClient cannot connect to server");
    }

    /**
     * Tests for exception thrown when WebClient returns a null response.
     */
    @Test(expected = IllegalStateException.class)
    public void testUpdateOrgPassword_WebClientReturnsNull() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.updatePassword("login", "password");
        fail("DataManager.updatePassword does not throw IllegalStateException when WebClient returns null");
    }

    /**
     * Tests error received when making RESTful request.
     */
    @Test(expected = IllegalStateException.class)
    public void testAttemptLogin_WebClientReturnsError() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"data\":\"An unexpected database error occurred\"}";
            }
        });
        dm.updatePassword("login", "password");
        fail("DataManager.updatePassword does not throw IllegalStateException when WebClient returns error");
    }

}
