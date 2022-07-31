import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;

public class UpdateOrgPasswordTest {

    private class MockDataManager extends DataManager {

        protected Organization org;

        public MockDataManager(WebClient client) {
            super(client);
        }

        @Override
        public String getContributorName(String id) {
            return "Sizwe Nvodlu";
        }

        @Override
        public Organization attemptLogin(String login, String password) {
            Organization org = new Organization("12345", "newOrg", "awesome");
            Fund fund = new Fund("54321", "Seeds All Day", "A fund for seeds", 500);
            org.addFund(fund);
            org.setPassword(password);
            this.org = org;
            Organization copy = new Organization(org.getId(), org.getName(), org.getDescription());
            copy.setPassword(org.getPassword());
            List<Fund> funds = org.getFunds();
            for (Fund soleFund : funds) {
                copy.addFund(new Fund(soleFund.getId(), soleFund.getName(),
                        soleFund.getDescription(), soleFund.getTarget()));
            }
            return copy;
        }

    }

    @Test
    public void testSuccessfulUpdatePassword() {

        MockDataManager dm = new MockDataManager(new WebClient("localhost", 3001) {
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

        MockDataManager dm = new MockDataManager(new WebClient("localhost", 3001) {
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

}
