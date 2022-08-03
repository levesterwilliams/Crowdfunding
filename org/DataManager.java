
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
        illegalStateNullChecker(client);
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
    private <E> void illegalStateNullChecker(E e) {
        if (e == null) {
            throw new IllegalStateException();
        }
    }

    /**
     * Checks to see if e is null and throw an IllegalArgumentException if so.
     * 
     * @param key
     * @throws IllegalArgumentException - if key is null.
     */
    private <E> void illegalArgumentNullChecker(E e) {
        if (e == null) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks to see if key equals "error" and throws IllegalStateException if so.
     * 
     * @param key
     * @throws IllegalStateException - if key equals error.
     */
    private void jsonErrorChecker(String key) {
        if (key.equals("error")) {
            throw new IllegalStateException();
        }
    }

    /**
     * Attempt to log the user into an Organization account using the login and
     * password. This method uses the /findOrgByLoginAndPassword endpoint in the API
     * 
     * @return an Organization object if successful; null if unsuccessful
     */
    public Organization attemptLogin(String login, String password) {

        illegalArgumentNullChecker(login);
        illegalArgumentNullChecker(password);
        Map<String, Object> map = new HashMap<>();
        map.put("login", login);
        map.put("password", password);
        String response = client.makeRequest("/findOrgByLoginAndPassword", map);

        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(response);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        String status = (String) json.get("status");
        jsonErrorChecker(status);
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
        } else {
            return null;
        }
    }

    //task 3.1
    /**
     * This method creates a new fund in the database using the /createOrg endpoint
     * in the API
     * 
     * @return a new Organization object if successful; null if unsuccessful
     */

    public Organization createNewOrg(String[] params) {
        String login = params[0];
        String password = params[1];
        String name = params[2];
        String description = params[3];
        List<Fund> funds = new LinkedList<>();

        illegalArgumentNullChecker(name);
        illegalArgumentNullChecker(description);
        illegalArgumentNullChecker(login);
        illegalArgumentNullChecker(password);
        Map<String, Object> map = new HashMap<>();
        map.put("login", login);
        map.put("password", password);
        map.put("name", name);
        map.put("description", description);
        map.put("funds", funds);
        String response = client.makeRequest("/createOrg", map);
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        String status = null;
        JSONObject data = null;
        try {
            json = (JSONObject) parser.parse(response);
            status = (String) json.get("status");
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        if (status.equals("error")) {
            data = (JSONObject) json.get("data");
            long errCode = (long) data.get("code");
            if (errCode == 11000) {
                System.out.println("\nDuplicate key error. Login name already in system. Choose a different login.");
            }
            throw new IllegalStateException();
        }
        if (status.equals("success")) {
            data = (JSONObject) json.get("data");
            String orgId = (String) data.get("_id");
            String nameJson = (String) data.get("name");
            String descriptionJson = (String) data.get("description");
            System.out.println("\nSuccess! You may add funds now.\n");
            return new Organization(orgId, nameJson, descriptionJson);
        } else {
            return null;
        }
    }
    
    /**
     * Look up the name of the contributor with the specified ID. This method uses
     * the in the API.
     * 
     * @return the name of the contributor on success; null if no contributor is
     *         found
     */
    public String getContributorName(String id) {
        illegalArgumentNullChecker(id);
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        String response = client.makeRequest("/findContributorNameById", map);
        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(response);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        String status = (String) json.get("status");
        jsonErrorChecker(status);
        if (status.equals("success")) {
            String name = "";
            if (cache.containsKey(id)) {
                name = cache.get(id);
            } else {
                name = (String) json.get("data");
                cache.put(id, name);
            }
            return name;
        } else {
            return null;
        }
    }

    /**
     * This method creates a new fund in the database using the /createFund endpoint
     * in the API
     * 
     * @return a new Fund object if successful; null if unsuccessful
     */
    public Fund createFund(String orgId, String name, String description, long target) {

        illegalArgumentNullChecker(orgId);
        illegalArgumentNullChecker(name);
        illegalArgumentNullChecker(description);
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
            throw new IllegalStateException();
        }
        String status = (String) json.get("status");
        jsonErrorChecker(status);
        if (status.equals("success")) {
            JSONObject fund = (JSONObject) json.get("data");
            String fundId = (String) fund.get("_id");
            return new Fund(fundId, name, description, target);
        } else {
            return null;
        }
    }

    /**
     * This method deletes a new fund in the database using the /deleteFund endpoint
     * in the API
     * 
     * @return true if successful; false if unsuccessful
     */
    public boolean deleteFund(String id) {
        illegalArgumentNullChecker(id);
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        String response = client.makeRequest("/deleteFund", map);
        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(response);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        String status = (String) json.get("status");
        if (status.equals("success")) {
            return true; // status came back correct
        } else
            return false; // did not work
    }





    /**
     * This method updates an organization using /updateOrg in the API Task 3.3
     * 
     * @return true if successful; false if unsuccessful
     */
    public boolean updateOrgName(String id, String name, String description) {
        illegalArgumentNullChecker(id);
        illegalArgumentNullChecker(name);
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("description", description);
        String response = client.makeRequest("/updateOrgName", map);
        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(response);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        String status = (String) json.get("status");
        if (status.equals("success")) {
            return true;
        } else
            return false;
    }

    /**
     * Updates the password of the Organization with specific id in the API.
     * 
     * @param login    The login for Organization.
     * @param password The updated password for Organization.
     * @return true if request is successful; otherwise, return false
     */
    public boolean updatePassword(String id, String password) {
        illegalArgumentNullChecker(id);
        illegalArgumentNullChecker(password);
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("password", password);
        String response = client.makeRequest("/updateOrgPassword", map);

        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(response);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        String status = (String) json.get("status");
        if (status.equals("success")) {
            return true;
        } else
            return false;
    }


}
