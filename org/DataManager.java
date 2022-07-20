
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataManager {

    private Map<String, String> cache;
    private final WebClient client;

    public DataManager(WebClient client) {
        illegalStateNullChecker(client, "Client could not be found. Please try again.");
        this.cache = new HashMap<>();
        this.client = client;
    }

    /**
     * Checks to see if e is null and throw an IllegalStateException if so.
     * 
     * @param <E> Type of e to be check for null
     * @param e   The object to be checked for null
     * @throws IllegaStateException - if e is null.
     */
    private <E> void illegalStateNullChecker(E e, String errMessage) {
        if (e == null) {
            throw new IllegalStateException(errMessage);
        }
    }

    /**
     * Checks to see if e is null and throw an IllegalArgumentException if so.
     * 
     * @param key
     * @param errMessage
     * @throws IllegalArgumentException - if key is null.
     */
    private <E> void illegalArgumentNullChecker(E e, String errMessage) {
        if (e == null) {
            throw new IllegalArgumentException(errMessage);
        }
    }

    /**
     * Checks to see if key equals "error" and throws IllegalStateException if so.
     * 
     * @param key
     * @param errMessage
     * @throws IllegalStateException - if key equals error.
     */
    private void jsonErrorChecker(String key, String errMessage) {
        if (key.equals("error")) {
            throw new IllegalStateException(errMessage);
        }
    }

    /**
     * Attempt to log the user into an Organization account using the login and
     * password. This method uses the /findOrgByLoginAndPassword endpoint in the API
     * 
     * @return an Organization object if successful; null if unsuccessful
     */
    public Organization attemptLogin(String login, String password) {

        illegalArgumentNullChecker(login, "No login or password entered. Please try again.");
        illegalArgumentNullChecker(password, "No login or password entered. Please try again.");

        Map<String, Object> map = new HashMap<>();
        map.put("login", login);
        map.put("password", password);
        String response = client.makeRequest("/findOrgByLoginAndPassword", map);

        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(response);
        } catch (Exception e) {
            throw new IllegalStateException("Error in connecting to server. Please try again.");
        }
        String status = (String) json.get("status");
        jsonErrorChecker(status, "An unexpected database error occurred. Please try again.");
        if (status.equals("success")) {
            JSONObject data = (JSONObject) json.get("data");
            String fundId = (String) data.get("_id");
            String name = (String) data.get("name");
            String description = (String) data.get("description");
            Organization org = new Organization(fundId, name, description);

            JSONArray funds = (JSONArray) data.get("funds");
            Iterator it = funds.iterator();
            while (it.hasNext()) {
                JSONObject fund = (JSONObject) it.next();
                fundId = (String) fund.get("_id");
                name = (String) fund.get("name");
                description = (String) fund.get("description");
                long target = (Long) fund.get("target");

                Fund newFund = new Fund(fundId, name, description, target);

                JSONArray donations = (JSONArray) fund.get("donations");
                List<Donation> donationList = new LinkedList<>();
                Iterator it2 = donations.iterator();
                while (it2.hasNext()) {
                    JSONObject donation = (JSONObject) it2.next();
                    String contributorId = (String) donation.get("contributor");
                    String contributorName = "";
                    if (cache.containsKey(contributorId)) {
                        contributorName = cache.get(contributorId);
                    } else {
                        contributorName = this.getContributorName(contributorId);
                    }
                    long amount = (Long) donation.get("amount");
                    String date = (String) donation.get("date");
                    donationList.add(new Donation(fundId, contributorName, amount, date));
                }

                newFund.setDonations(donationList);

                org.addFund(newFund);

            }

            return org;
        } else
            return null;

    }

    /**
     * Look up the name of the contributor with the specified ID. This method uses
     * the in the API.
     * 
     * @return the name of the contributor on success; null if no contributor is
     *         found
     */
    public String getContributorName(String id) {

        illegalArgumentNullChecker(id, "No id found. Please try again.");
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        String response = client.makeRequest("/findContributorNameById", map);
        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(response);
        } catch (Exception e) {
            throw new IllegalStateException("Error in connecting to server. Please try again.");
        }
        String status = (String) json.get("status");
        jsonErrorChecker(status, "An unexpected database error occurred. Please try again.");
        if (status.equals("success")) {
            String name = (String) json.get("data");
            cache.put(id, name);
            return name;
        } else
            return null;
    }

    /**
     * This method creates a new fund in the database using the /createFund endpoint
     * in the API
     * 
     * @return a new Fund object if successful; null if unsuccessful
     */
    public Fund createFund(String orgId, String name, String description, long target) {

        illegalStateNullChecker(client, "Client could not be found. Please try again.");
        illegalArgumentNullChecker(orgId, "No organization id has been entered. Please try again.");
        illegalArgumentNullChecker(name, "No name has been entered. Please try again.");
        illegalArgumentNullChecker(description,
                "No description has been entered. Please try again.");
        Map<String, Object> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("name", name);
        map.put("description", description);
        map.put("target", target);
        String response = client.makeRequest("/createFund", map);
        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(response);
        } catch (Exception e) {
            throw new IllegalStateException("Error in connecting to server. Please try again.");
        }
        String status = (String) json.get("status");
        jsonErrorChecker(status, "An unexpected database error occurred. Please try again.");
        if (status.equals("success")) {
            JSONObject fund = (JSONObject) json.get("data");
            String fundId = (String) fund.get("_id");
            return new Fund(fundId, name, description, target);
        } else
            return null;
    }
}
