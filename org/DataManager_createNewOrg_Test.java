import static org.junit.Assert.*;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;

/**
 * Testing for DataManager.deleteFund() method.
 * 
 * @author Lily Simmons
 *
 */

public class DataManager_createNewOrg_Test {

    // test if fund id is null - illegal arg exception thrown
    @Test(expected = IllegalArgumentException.class)
    public void testNullParam() {
        String[] params = {"login", "password", null, "This is new"};

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"fail\"}";

            }

        });

        dm.createNewOrg(params);

    }

    // test if JSON object is malformed - illegal state exception thrown
    @Test(expected = IllegalStateException.class)
    public void testBadJSONString() {
        String[] params = {"login", "password", "new Organization", "This is new"};
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "badJSONstring";
            }
        });
        dm.createNewOrg(params);
    }
    
    
    @Test
    public void testCreateNewOrgSuccessful() {
        String[] params = {"woof", "bark", "dog", "dog pound"};
        
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // creates the JSONObject for entire request
                JSONObject obj = new JSONObject();
                obj.put("status", "success");
                JSONObject data = new JSONObject();
                data.put("_id", "12345");
                data.put("name", "dog");
                data.put("description", "dog pound");
                obj.put("data", data);
                String jsonString = obj.toJSONString();
                return jsonString;
            }
        });
        Organization newOrg = dm.createNewOrg(params);
        assertNotNull(newOrg);
        assertEquals("dog pound", newOrg.getDescription());
        assertEquals("dog", newOrg.getName());
        assertEquals("12345", newOrg.getId());
    }
    
    @Test
    public void testCreateNewOrgUnSuccessful() {
        String[] params = {"woof", "bark", "dog", "dog pound"};
        
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // creates the JSONObject for entire request
                JSONObject obj = new JSONObject();
                obj.put("status", "fail");
                String jsonString = obj.toJSONString();             
                return jsonString;
            }
        });
        Organization newOrg = dm.createNewOrg(params);
        assertNull(newOrg);
    }
    
    //tests for error code exception
    @Test(expected = IllegalStateException.class)
    public void testDuplicateKeyException() {
        String[] params = {"woof", "bark", "dog", "dog pound"};
        

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // creates the JSONObject for entire request
                JSONObject obj = new JSONObject();
                JSONObject data = new JSONObject();
                obj.put("status", "error");
                data.put("code", 11000);  
                obj.put("data", data);
                String jsonString = obj.toJSONString();
                return jsonString;
            }
        });
        Organization newOrg = dm.createNewOrg(params);
    }
    
    
    //tests for error code exception
    @Test(expected = IllegalStateException.class)
    public void testDuplicateKeyCodeNot11000() {
        String[] params = {"woof", "bark", "dog", "dog pound"};
        

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // creates the JSONObject for entire request
                JSONObject obj = new JSONObject();
                JSONObject data = new JSONObject();
                obj.put("status", "error");
                data.put("code", 200);  
                obj.put("data", data);
                String jsonString = obj.toJSONString();
                return jsonString;
            }
        });
        Organization newOrg = dm.createNewOrg(params);
    }

    /**
     * Tests for null WebClient passed to the DataManager class.
     */
    @Test(expected = IllegalStateException.class)
    public void testUpdateOrgPassword_WebClientIsNull() {
        String[] params = {"woof", "bark", "dog", "dog pound"};
        
        DataManager dm = new DataManager(null);
        dm.createNewOrg(params);
        fail("DataManager.createNewOrg does not throw IllegalStateException when WebClient is null");
    }

    /**
     * Tests for wrong port passed to WebClient.
     */
    @Test(expected = IllegalStateException.class)
    public void testUpdateOrgPassword_WebClientCannotConnectToServer() {
        String[] params = {"woof", "bark", "dog", "dog pound"};
        
        // this assumes no server is running on port 3002
        DataManager dm = new DataManager(new WebClient("localhost", 3002));
        dm.createNewOrg(params);
        fail("DataManager.createNewOrg does not throw IllegalStateException when WebClient cannot connect to server");
    }
    
    /**
     * Tests for exception thrown when WebClient returns a null response.
     */
    @Test(expected = IllegalStateException.class)
    public void testUpdateOrgPassword_WebClientReturnsNull() {
        String[] params = {"woof", "bark", "dog", "dog pound"};
        
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.createNewOrg(params);
        fail("DataManager.createNewOrg does not throw IllegalStateException when WebClient returns null");
    }
    
    /**
     * Tests error received when making RESTful request.
     */
    @Test(expected = IllegalStateException.class)
    public void testAttemptLogin_WebClientReturnsError() {
        String[] params = {"woof", "bark", "dog", "dog pound"};
        
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"data\":\"An unexpected database error occurred\"}";
            }
        });
        dm.createNewOrg(params);
        fail("DataManager.createNewOrg does not throw IllegalStateException when WebClient returns error");
    }
}
