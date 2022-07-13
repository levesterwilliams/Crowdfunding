import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

/**
 * A testing suite to test DataManager.attemptLogin() method.
 * 
 * @author Levester Williams
 *
 */
public class AttemptLoginTest {

    /**
     * Tests DataManager.attemptLogin() with successful login.
     */
    @Test
    public void testSuccessfulLogin() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {

                JSONObject obj = new JSONObject(); // creates the JSONObject for entire request
                obj.put("status", "success");
                JSONObject data = new JSONObject();
                data.put("_id", "12345");
                data.put("name", "ACLU");
                data.put("description", "American Civil Liberties Union");

                JSONArray funds = new JSONArray(); // creates the JSONArray for funds
                JSONObject arrData = new JSONObject(); // creates the data to go into the JSONArray
                arrData.put("_id", "0200");
                arrData.put("name", "Privacy Rights fund");
                arrData.put("description", "Fundraising to preserve and protect privacy rights");
                arrData.put("target", 700000);

                JSONArray donations = new JSONArray(); // creates the JSONArray for donations
                JSONObject arrDatDonations = new JSONObject();
                arrDatDonations.put("contributor", "2223");
                arrDatDonations.put("amount", 250);
                arrDatDonations.put("date", "July 1st, 2001");
                donations.add(arrDatDonations); // adds JSON object of donation into donations

                // adds JSONArray donations as an JSONObject into arrData--the JSONObect for
                // funds
                arrData.put("donations", donations);

                funds.add(arrData); // places the JSONObject into the JSONArray funds
                data.put("funds", funds); // places the JSONArray into the JSONObject data
                obj.put("data", data);

                String jsonString = obj.toJSONString();
                return jsonString;
            }

        });
        Organization org = dm.attemptLogin("test", "test");
        assertNotNull(org);
        assertEquals("American Civil Liberties Union", org.getDescription());
        assertEquals("12345", org.getId());
        assertEquals("ACLU", org.getName());
        List<Fund> funds = org.getFunds();
        assertEquals("0200", funds.get(0).getId());
        assertEquals("Privacy Rights fund", funds.get(0).getName());
        assertEquals("Fundraising to preserve and protect privacy rights",
                funds.get(0).getDescription());
        assertEquals(700000, funds.get(0).getTarget());
        List<Donation> donations = funds.get(0).getDonations();
        assertEquals("0200", donations.get(0).getFundId());
        assertNull(donations.get(0).getContributorName());
        assertEquals(250, donations.get(0).getAmount());
        assertEquals("July 1st, 2001", donations.get(0).getDate());
    }

    /**
     * Tests DataManager.attemptLogin() with successful login.
     */
    @Test
    public void testUnsuccessfulLogin() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {

                JSONObject obj = new JSONObject(); // creates the JSONArray for entire request
                obj.put("status", "fail");
                JSONObject data = new JSONObject();
                data.put("_id", "12345");
                data.put("name", "ACLU");
                data.put("description", "American Civil Liberties Union");

                JSONArray funds = new JSONArray(); // creates the JSONArray for funds
                JSONObject arrData = new JSONObject(); // creates the data to go into the JSONArray
                arrData.put("_id", "0200");
                arrData.put("name", "Privacy Rights fund");
                arrData.put("description", "Fundraising to preserve and protect privacy rights");
                arrData.put("target", 700000);

                JSONArray donations = new JSONArray(); // creates the JSONArray for donations
                JSONObject arrDatDonations = new JSONObject();
                arrDatDonations.put("contributor", "2223");
                arrDatDonations.put("amount", 250);
                arrDatDonations.put("date", "July 1st, 2001");
                donations.add(arrDatDonations); // adds JSON object of donation into donations

                // adds JSONArray donations as an JSONObject into arrData--the JSONObect for
                // funds
                arrData.put("donations", donations);

                funds.add(arrData); // places the JSONObject into the JSONArray funds
                data.put("funds", funds); // places the JSONArray into the JSONObject data
                obj.put("data", data);

                String jsonString = obj.toJSONString();
                return jsonString;
            }

        });
        Organization org = dm.attemptLogin("test", "test");
        assertNull(org);
    }

    /**
     * Tests DataManager.attemptLogin() with bad JSONObject.
     */
    @Test
    public void testBadJSON() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "badstring";
            }

        });
        Organization org = dm.attemptLogin("test", "test");
        assertNull(org);
    }
}
