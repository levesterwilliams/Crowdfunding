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
                System.out.println(jsonString);
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
                System.out.println(jsonString);
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
                obj.put("status", "error");
                JSONObject data = new JSONObject();
                data.put("code", "11000");  
                obj.put("data", data);
                String jsonString = obj.toJSONString();
                System.out.println(jsonString);
                return jsonString;
            }
        });
        Organization newOrg = dm.createNewOrg(params);
    }
}
