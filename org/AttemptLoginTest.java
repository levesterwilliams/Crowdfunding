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
     * 
     * A class to help test attemptLogin() method using mock objects.
     */
    private class MockDataManager extends DataManager {
        public MockDataManager(WebClient client) {
            super(client);
        }

        @Override
        public String getContributorName(String id) {
            return "Sizwe Nvodlu";
        }
    }

    /**
     * Tests DataManager.attemptLogin() with successful login.
     */
    @Test
    public void testSuccessfulLogin01() {
        MockDataManager dm = new MockDataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // creates the JSONObject for entire request
                JSONObject obj = new JSONObject();
                obj.put("status", "success");
                JSONObject data = new JSONObject();
                data.put("_id", "12345");
                data.put("name", "ACLU");
                data.put("description", "American Civil Liberties Union");
                // creates the JSONArray for funds
                JSONArray funds = new JSONArray();
                // creates the data to go into the JSONArray for funds
                JSONObject arrDataFund01 = new JSONObject();
                arrDataFund01.put("_id", "0200");
                arrDataFund01.put("name", "Privacy Rights fund");
                arrDataFund01.put("description",
                        "Fundraising to preserve and protect privacy rights");
                arrDataFund01.put("target", 700000);
                // creates the JSONArray for donations
                JSONArray donations = new JSONArray();
                // creates the data to go into the JSONArray for donations
                JSONObject arrDatDonation01 = new JSONObject();
                arrDatDonation01.put("contributor", "2223");
                arrDatDonation01.put("amount", 250);
                arrDatDonation01.put("date", "July 1st, 2001");
                // adds JSON object of donation #1 into donations
                donations.add(arrDatDonation01);
                // creates the data to go into the JSONArray for donations
                JSONObject arrDatDonation02 = new JSONObject();
                arrDatDonation02.put("contributor", "2223");
                arrDatDonation02.put("amount", 200);
                arrDatDonation02.put("date", "July 2nd, 2001");
                // adds JSON object of donation #2 into donations
                donations.add(arrDatDonation02);

                // adds JSONArray donations as an JSONObject into arrData--the JSONObect for
                // funds
                arrDataFund01.put("donations", donations);
                // places the JSONObject into the JSONArray funds
                funds.add(arrDataFund01);
                // places the JSONArray of funds into the JSONObject data
                data.put("funds", funds);
                obj.put("data", data);
                String jsonString = obj.toJSONString();
                return jsonString;
            }
        });

        Organization org = dm.attemptLogin("info@aclu.org", "_mi3!dst55");
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
        assertEquals("Sizwe Nvodlu", donations.get(0).getContributorName());
        assertEquals(250, donations.get(0).getAmount());
        assertEquals("July 1st, 2001", donations.get(0).getDate());
        assertEquals("0200", donations.get(1).getFundId());
        assertEquals("Sizwe Nvodlu", donations.get(1).getContributorName());
        assertEquals(200, donations.get(1).getAmount());
        assertEquals("July 2nd, 2001", donations.get(1).getDate());
    }

    /**
     * Tests DataManager.attemptLogin() with unsuccessful login.
     */
    @Test
    public void testUnsuccessfulLogin() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                // creates the JSONArray for entire request
                JSONObject obj = new JSONObject();
                obj.put("status", "fail");
                String jsonString = obj.toJSONString();
                return jsonString;
            }
        });
        Organization org = dm.attemptLogin("info@aclu.org", "_mi3!dst55");
        assertNull(org);
    }
}
