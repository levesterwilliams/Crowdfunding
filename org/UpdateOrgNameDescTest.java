import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;

public class UpdateOrgNameDescTest {

    /**
     * Tests for successful name and desc update.
     */
    @Test
    public void testSuccessfulUpdateName() {

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
        assertTrue(dm.updateOrgName("12345", "new name", "new desc"));
    }

    
    /**
     * Tests for unsuccessful name and desc update.
     */
    @Test
    public void testUnSuccessfulUpdateName() {

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
        assertFalse(dm.updateOrgName("12345", "new name", "new desc"));
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
        dm.updateOrgName(null, "new name", "new desc");
    }

    /**
     * Tests null as argument for the name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"fail\"}";
            }
        });
        dm.updateOrgName("12345", null, "new desc");
    }
    
    /**
     * Tests null as argument for the desc.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullDesc() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"fail\"}";
            }
        });
        dm.updateOrgName("12345", "new name", null);
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
        dm.updateOrgName("hello", "new name", "new desc");
    }

    /**
     * Tests for null WebClient passed to the DataManager class.
     */
    @Test(expected = IllegalStateException.class)
    public void testUpdateOrgName_WebClientIsNull() {
        DataManager dm = new DataManager(null);
        dm.updateOrgName("id", "name", "desc");
        fail("DataManager.updateOrgName does not throw IllegalStateException when WebClient is null");
    }

    /**
     * Tests for wrong port passed to WebClient.
     */
    @Test(expected = IllegalStateException.class)
    public void testUpdateOrgName_WebClientCannotConnectToServer() {
        // this assumes no server is running on port 3002
        DataManager dm = new DataManager(new WebClient("localhost", 3002));
        dm.updateOrgName("id", "name", "desc");
        fail("DataManager.updateOrgName does not throw IllegalStateException when WebClient cannot connect to server");
    }

    /**
     * Tests for exception thrown when WebClient returns a null response.
     */
    @Test(expected = IllegalStateException.class)
    public void testUpdateOrgName_WebClientReturnsNull() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.updateOrgName("id", "name", "desc");
        fail("DataManager.updateOrgName does not throw IllegalStateException when WebClient returns null");
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
        dm.updateOrgName("id", "name", "desc");
        fail("DataManager.updateOrgName does not throw IllegalStateException when WebClient returns error");
    }

}
