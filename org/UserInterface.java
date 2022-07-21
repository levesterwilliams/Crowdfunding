import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserInterface {

    private DataManager dataManager;
    private Organization org;
    private Scanner in = new Scanner(System.in);
    private Map<Fund, List<AggregateDonationLine>> cachedAggregateDonations = new HashMap<Fund, List<AggregateDonationLine>>();

    public UserInterface(DataManager dataManager, Organization org) {
        this.dataManager = dataManager;
        this.org = org;
    }

    public void start() {

        while (true) {
            System.out.println("\n\n");
            if (org.getFunds().size() > 0) {
                System.out.println(
                        "There are " + org.getFunds().size() + " funds in this organization:");

                int count = 1;
                for (Fund f : org.getFunds()) {

                    System.out.println(count + ": " + f.getName());

                    count++;
                }
                System.out.println("\nEnter the fund number to see more information.");
            }
            System.out.println("Enter 0 to create a new fund");
            System.out.println("Enter -1 to logout");
            // Task 1.7 input error handling
            int option = 0;
            boolean isInteger = false;
            // Task 1.7 checks user input is an integer
            while (!isInteger) {
                try {
                    option = in.nextInt();
                    isInteger = true;
                } catch (InputMismatchException e) {
                    System.out.println("That was not a number. Please try again.");
                    in.nextLine();
                }
            }
            in.nextLine();
            if (option == 0) {
                createFund();
                // Task 1.7 checks if input is less than 0
            } else if (option == -1) {
                logout();
                break;
            } else if (option < -1) {
                System.out.println(option + " is an invalid input. Please enter valid number");
                // Task 1.7 checks input does not exceed size of fund list
            } else if (option > org.getFunds().size()) {
                System.out.println(option
                        + " is an invalid input. Please enter 0 to create a new fund, -1 to logout, or choose from list of funds.");
            } else {
                displayFund(option);
            }
        }

    }

    public void createFund() {

        System.out.print("Enter the fund name: ");
        String name = in.nextLine().trim();

        System.out.print("Enter the fund description: ");
        String description = in.nextLine().trim();

        System.out.print("Enter the fund target: ");
        String input = in.nextLine();

        // Task 1.7 checks that fund target is a number
        while (!input.matches("\\d+")) {
            System.out.println("Please enter a number.");
            input = in.nextLine();
        }
        long target = Integer.parseInt(input);

        Fund fund = dataManager.createFund(org.getId(), name, description, target);
        org.getFunds().add(fund);

    }

    public void displayFund(int fundNumber) {

        // Task_1.8
        // Task 1.3 test

        long donations_sum = 0;
        long donations_percent = 0;

        Fund fund = org.getFunds().get(fundNumber - 1);

        System.out.println("\n\n");
        System.out.println("Here is information about this fund:");
        System.out.println("Name: " + fund.getName());
        System.out.println("Description: " + fund.getDescription());
        System.out.println("Target: $" + fund.getTarget());

        List<Donation> donations = fund.getDonations();
        System.out.println("Number of donations: " + donations.size());

        // Task 1.8
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy");

        for (Donation donation : donations) {

            // Task 1.8
            Date date;
            try {
                date = originalFormat.parse(donation.getDate());
                String formattedDate = targetFormat.format(date);
                System.out.println("* " + donation.getContributorName() + ": $"
                        + donation.getAmount() + " on " + formattedDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Task 1.3
            donations_sum = donations_sum + donation.getAmount();
        }

        // Task 1.3
        donations_percent = donations_sum * 100 / fund.getTarget();

        // Task 1.3
        System.out.println("Total donation amount: $" + donations_sum + " (" + donations_percent
                + "% of target\n)");

        // Task 2.3
        System.out.println("To view donations aggregated by contributor, type C");
        // Task 2.7
        System.out.println("Press 9 to delete this fund.");
        System.out.println("Otherwise, press enter to go back to the listing of funds");
        //System.out.println("Press any other key to go back to the listing of funds");
        String finalInput = in.nextLine();

        // Task 2.3
        //if (finalInput.length() == 1) {
            if (finalInput.charAt(0) == 'c' || finalInput.charAt(0) == 'C') {
                displayAggregatedDonations(fund);
                System.out.println("Press any key to go back to the listing of funds");
                in.nextLine();
            //}
        }
        
        // Task 2.7

        //String input = in.nextLine();

            else if (finalInput.equals("9")) {
            System.out.println("Are you sure you want to delete this fund? Enter y/n");
            String delete = in.nextLine();
            delete = delete.replaceAll("[^A-za-z]+", "");
            delete = delete.toLowerCase();

            while (!((delete.equals("yes") || delete.equals("y") || delete.equals("no")
                    || delete.equals("n")))) {

                System.out.println("Please enter y/n.");
                delete = in.nextLine();
                delete = delete.replaceAll("[^A-za-z]+", "");
                delete = delete.toLowerCase();
            }

            // if yes, delete fund
            if (delete.equals("yes") || delete.equals("y")) {
                //pass this fund to delete fund
                deleteFund(fund);
                
            } else if ((delete.equals("no") || delete.equals("n"))) { 
                System.out.println("");
            } 
        System.out.println("Press the Enter key to go back to the listing of funds");
        in.nextLine();
            
    
        }

    }

    // Task 2.3
    public void displayAggregatedDonations(Fund fund) {

        if (!cachedAggregateDonations.containsKey(fund)) {

            Map<String, AggregateDonationLine> donationMap = new HashMap<String, AggregateDonationLine>();
            List<Donation> donations = fund.getDonations();

            for (Donation donation : donations) {
                if (donationMap.containsKey(donation.getContributorName())) {
                    AggregateDonationLine updatedLine = donationMap
                            .get(donation.getContributorName());
                    updatedLine.addDonation(donation.getAmount());
                    donationMap.put(donation.getContributorName(), updatedLine);
                } else {
                    AggregateDonationLine newLine = new AggregateDonationLine(
                            donation.getContributorName(), donation.getAmount());
                    donationMap.put(donation.getContributorName(), newLine);
                }
            }

            List<AggregateDonationLine> donationLines = new ArrayList<AggregateDonationLine>(
                    donationMap.values());
            Collections.sort(donationLines);
            cachedAggregateDonations.put(fund, donationLines);

        }

        List<AggregateDonationLine> donationLines = cachedAggregateDonations.get(fund);

        for (AggregateDonationLine line : donationLines) {
            System.out.println(line.getName() + ", " + line.getDonationCount() + " donations, $"
                    + line.getDonationSum() + " total");
        }
    }

    // Task 2.3
    public class AggregateDonationLine implements Comparable<AggregateDonationLine> {

        private String contributorName;
        private long donationSum;
        private int donationCount;

        // constructor, getters, setters

        public AggregateDonationLine(String contributorName, long donationSum) {
            this.contributorName = contributorName;
            this.donationSum = donationSum;
            this.donationCount = 1;
        }

        public long getDonationSum() {
            return this.donationSum;
        }

        public String getName() {
            return this.contributorName;
        }

        public int getDonationCount() {
            return this.donationCount;
        }

        public void addDonation(long donation) {
            this.donationSum += donation;
            this.donationCount += 1;
        }

        // override equals and hashCode
        @Override
        public int compareTo(AggregateDonationLine employee) {
            return (int) (employee.getDonationSum() - this.donationSum);
        }
    }

    //task 2.7
    public void deleteFund(Fund fund) {
       // System.out.println("test to get inside deleteFund");
        String id = fund.getId();
        boolean dm = dataManager.deleteFund(id);
        org.getFunds().remove(fund);
        if(dm == false) {
           System.out.println("Something went wrong " + fund.getName() + " was not deleted.");
        }
        System.out.println(fund.getName() + " has been successfully deleted.\n");
    }
    
    
    // Task 2.8
    public static String[] login(Scanner scanner) {

        String usernamePassword[] = new String[2];

        System.out.print("Username: ");
        usernamePassword[0] = scanner.nextLine().trim();
        System.out.print("Password: ");
        usernamePassword[1] = scanner.nextLine().trim();

        System.out.println();

        return usernamePassword;

    }

    // Task 2.8
    public void logout() {
        System.out.println("You logged out!");
        System.out.println();
        String StringArray[] = new String[2];
        main(StringArray);
    }

    /**
     * Initializes a DataManager object with a new WebClient at localhost port 3001
     * only if no errors occur and asks the user to retry the initialization if any
     * errors occurs.
     * 
     * @param firstin Scanner object
     * @return DataManager ds
     */
    private static DataManager initializeDataManager(Scanner firstin) {
        DataManager ds = null;
        boolean connectedToAPI = false;
        while (!connectedToAPI) {
            try {
                ds = new DataManager(new WebClient("localhost", 3001));
                connectedToAPI = true;
            } catch (Exception e) {
                System.out.println(
                        "Error in performing HTTP request. Cannot connect to API. Do you want to try again? Type 'y' for yes or enter another key to discontinue.");
                String input = firstin.nextLine().toLowerCase();
                if (!input.equals("y")) {
                    System.out.println("Goodbye!");
                    break;
                }
            }
        }
        return ds;
    }
    

    // Updated for Task 2.8
    public static void main(String[] args) {
        Scanner firstin = new Scanner(System.in);
        DataManager ds = initializeDataManager(firstin);
        String login = null;
        String password = null;
        Organization org = null;
        if (args.length == 2) {
            login = args[0];
            password = args[1];
        }
        while (org == null && ds != null) {
            if (login == null || password == null) {
                System.out.println("Please enter your username and password to begin.");
            } else {
                try {
                    org = ds.attemptLogin(login, password);
                } catch (Exception e) {
                    System.out.println(
                            "Error in retrieving or parsing data from database. Do you want to try again? Type 'y' for yes or enter another key to discontinue.");
                    String input = firstin.nextLine().toLowerCase();
                    if (!input.equals("y")) {
                        System.out.println("Goodbye!");
                        break;
                    }
                }
            }
            if (org == null) {
                if (login != null && password != null) {
                    System.out.println("Please reenter your username and password.");
                }
                String usernamePassword[] = login(firstin);
                login = usernamePassword[0];
                password = usernamePassword[1];
            }
        }
        if (org != null) {
            UserInterface ui = new UserInterface(ds, org);
            ui.start();
        }
        firstin.close();
    }
}
