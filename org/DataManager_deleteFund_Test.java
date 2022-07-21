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

    //TESTS THE UI- DOES NOT WORK. NOT SURE IF WE NEED THIS TEST.
//    @Test
//    public void testUIflowSuccessfulDeletion() {
//        
//        Organization org = new Organization("1234", "newOrg", "awesome");
//        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
//
//            @Override
//            public String makeRequest(String resource, Map<String, Object> queryParams) {
//                return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"123\",\"donations\":[],\"__v\":0}}";
//
//            }
//            
//            
//            public String makeRequest2(String resource, Map<String, Object> queryParams) {
//                return "{\"status\":\"success\",\"data\":{\"_id\":\"54321\",\"name\":\"another fund\",\"description\":\"test\",\"target\":500,\"org\":\"123\",\"donations\":[],\"__v\":0}}";
//
//            }
//
//        });
//
//        UserInterface ui = new UserInterface(dm, org);
//        Fund f1 = dm.createFund("12345", "new fund", "this is the new fund", 10000);
//        Fund f2 = dm.createFund("54321", "another fund", "test", 500);
//
//        org.addFund(f1);
//        org.addFund(f2);
//        System.out.println(org.getFunds());
//        
//       
//        assertEquals(2, org.getFunds().size());
//         //ui.displayFund(1);
//         
//         PrintStream sysOut = System.out;
//         InputStream sysIn = System.in;
//         // print out message and capture as string
//         OutputStream outputStream = new ByteArrayOutputStream();
//         PrintStream printStream = new PrintStream(outputStream);
//         System.setOut(printStream);
//         // allows the system to automatically receive the inputs
//         // "9" and "y"
//         String data = "1" + System.getProperty("line.separator") + "9" + System.getProperty("line.separator") +
//                 "y" + System.getProperty("line.separator");
//        
//         InputStream stringData = new ByteArrayInputStream(data.getBytes());
//         System.setIn(stringData);
//         
//      // run generate a wordlePlus test and playAnagramRound
//         Scanner newScan = new Scanner(System.in);
//         ui.displayFund(newScan.nextInt());
//         System.out.println("here");
//         String deleteMsg = outputStream.toString();
//         printStream.close();
//        //dm.deleteFund(f1.getId());
//         String expectedTest = "Press9todeletethisfund." +
//                 "Otherwise,pressentertogobacktothelistingoffunds" +
//                 "Areyousureyouwanttodeletethisfund?Entery/n" +
//                 "hasbeensuccessfullydeleted." +
//                 "PresstheEnterkeytogobacktothelistingoffunds";
//         deleteMsg = deleteMsg.replaceAll(System.getProperty("line.separator"), "");
//         deleteMsg = deleteMsg.replaceAll("\\s+", "");
//         assertEquals(expectedTest, deleteMsg);
//         newScan.close();
//         System.setOut(sysOut);
//         System.setIn(sysIn);
//         
//        System.out.println("print funds " + org.getFunds());
//
//        assertEquals(1, org.getFunds().size());
//        
//
//
//    }
    
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
