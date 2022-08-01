import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;

public class UpdateOrgPasswordTest {

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
     * Tests null as argument for login
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

}
