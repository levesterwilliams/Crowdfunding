import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

/**
 * Testing for DataManager.deleteFund() method.
 * 
 * @author Lily Simmons
 *
 */
public class DataManager_deleteFund_Test {

    //test if fund id is null - illegal arg exception
    @Test(expected = IllegalArgumentException.class)
    public void testNullFundId() {
        
        Fund fund = new Fund(null, "New Fund", "This is new", 10000);
        
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"fail\"}";

            }
            
        });
        
        dm.deleteFund(fund.getId());

    }
    
    // test if JSON object is malformed - illegal state exception
    @Test(expected = IllegalStateException.class)
    public void testBadJSONString() {
        Fund fund = new Fund("203", "New Fund", "This is new", 10000);
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "badJSONstring";
            }
        });
        dm.deleteFund(fund.getId());
    }
    
    /**
     * Test deleteFund() method with status as successful"
     */
    
    @Test
    public void testDeleteSuccessful() {
        
        Organization org = new Organization("1234", "newOrg", "awesome");
        
        Fund fund = new Fund("54321", "Seeds All Day", "A fund for seeds", 500);
        
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"data\":{\"name\":\"newOrg\",\"description\":\"awesome\",\"funds\":[{\"name\":\"Seeds All Day\",\"description\":\"A fund for seeds\",\"_id\":\"54321\",\"target\":500},{}],\"_id\":\"1234\"},\"status\":\"success\"}";

            }
            
        });
        
        assertTrue(dm.deleteFund(fund.getId()));

    }
    
    /**
     * Test deleteFund() method with status as unsuccessful"
     */

    @Test
    public void testDeleteUnSuccessful() {
        
        Organization org = new Organization("1234", "newOrg", "awesome");
        
        Fund fund = new Fund("54321", "Seeds All Day", "A fund for seeds", 500);
        
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"data\":{\"name\":\"newOrg\",\"description\":\"awesome\",\"funds\":[{\"name\":\"Seeds All Day\",\"description\":\"A fund for seeds\",\"_id\":\"54321\",\"target\":500},{}],\"_id\":\"1234\"},\"status\":\"fail\"}";

            }
            
        });
        
        assertFalse(dm.deleteFund(fund.getId()));

    }
    
}
