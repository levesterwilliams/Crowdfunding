import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;

/**
 * A testing suite to test DataManager.getContributorName() method.
 * 
 * @author Levester Williams
 *
 */
public class GetContributorNameTest {

    /**
     * Tests DataManager.getContributorName() with successful retrieval
     */
    @Test
    public void testSuccessfulGetContributorName() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {

                JSONObject obj = new JSONObject(); // creates the JSONObject for entire request
                obj.put("status", "success");
                obj.put("data", "Sizwe Nvodlu");
                String jsonString = obj.toJSONString(); // converts JSONObject to String
                return jsonString;
            }

        });
        String name = dm.getContributorName("0200");
        assertEquals("Sizwe Nvodlu", name);
    }

    /**
     * Tests DataManager.getContributorName() with unsuccessful retrieval
     */
    @Test
    public void testUnSuccessfulGetContributorName() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {

                JSONObject obj = new JSONObject(); // creates the JSONObject for entire request
                obj.put("status", "fail");
                obj.put("data", "Sizwe Nvodlu");
                String jsonString = obj.toJSONString(); // converts JSONObject to String
                return jsonString;
            }

        });
        assertNull(dm.getContributorName("0200"));
    }
}
