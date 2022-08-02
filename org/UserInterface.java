import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class UserInterface {

    private DataManager dataManager;
    private Organization org;
    private Scanner in = new Scanner(System.in);
    private static Set<String> orgNames = new HashSet<>();
    private Map<Fund, List<AggregateDonationLine>> cachedAggregateDonations = new HashMap<Fund, List<AggregateDonationLine>>();

    public UserInterface(DataManager dataManager, Organization org) {
        this.dataManager = dataManager;
        this.org = org;
    }

    public void start() {
        
        //add name to orgName list
        orgNames.add(org.getName());
        System.out.println("orgNames: " + orgNames);
        
        
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
            int option = 0;
            boolean isInteger = false;
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
            } else if (option == -1) {
                logout();
                break;
            } else if (option < -1) {
                System.out.println(option + " is an invalid input. Please enter valid number");
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

        while (!input.matches("\\d+")) {
            System.out.println("Please enter a number.");
            input = in.nextLine();
        }
        long target = Integer.parseInt(input);
        Fund fund;

        while (true) {
            try {
                fund = dataManager.createFund(org.getId(), name, description, target);
                org.getFunds().add(fund);
                break;
            } catch (Exception e) {
                System.out.println(
                        "The fund cannot be created. Do you want to try again? Type 'y' for yes or enter another key to discontinue.");
                input = in.nextLine().toLowerCase();
                if (!input.equals("y")) {
                    break;
                }
            }
        }

    }
    

    //TASK 3.1
    public static String[] createNewOrganization(Scanner scanner) {
        
        String params[] = new String[4];
        
        System.out.println("\nLet's make a new organzation!\n");
        
        System.out.print("Create new login: ");
        String login = scanner.nextLine().trim();        
        while (login.isEmpty() || login.matches("\\s+")) {
        System.out.print("Login cannot be blank. Please re-enter a login name: ");
        login = scanner.nextLine().trim();
        }
        params[0] = login;
        
        System.out.print("Create new password: ");
        String password = scanner.nextLine().trim();        
        while (password.isEmpty() || password.matches("\\s+")) {
        System.out.print("Password cannot be blank. Please re-enter a password: ");
        password = scanner.nextLine().trim();
        }
        params[1] = password;
        
        System.out.print("Enter new Organization name: ");
        String name = scanner.nextLine().trim();        
        while (name.isEmpty() || name.matches("\\s+")) {
        System.out.print("Name cannot be blank. Please re-enter an Organization name: ");
        name = scanner.nextLine().trim();
        }
        boolean hasName = false;
        while(!hasName) {
            if(!orgNames.contains(name)) {
            params[2] = name;
            orgNames.add(name);
            hasName = true;
        } else {
            System.out.println("Sorry, that name is taken. Please enter a new name.");
            name = scanner.nextLine().trim();
            while (name.isEmpty() || name.matches("\\s+")) {
                System.out.print("Name cannot be blank. Please re-enter an Organization name: ");
                name = scanner.nextLine().trim();
                }
            }
        }

        System.out.println("orgNames: " + orgNames);
                
        System.out.print("Enter new Organization description: ");
        String description = scanner.nextLine().trim();        
        while (description.isEmpty() || description.matches("\\s+")) {
        System.out.print("Description cannot be blank. Please re-enter an Organization description: ");
        description = scanner.nextLine().trim();
        }
        params[3] = description;


//        System.out.println(params[0]);
//        System.out.println(params[1]);
//        System.out.println(params[2]);
//        System.out.println(params[3]);
        
        

        return params;

    }

    public void displayFund(int fundNumber) {

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
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy");

        for (Donation donation : donations) {

            Date date;
            try {
                date = originalFormat.parse(donation.getDate());
                String formattedDate = targetFormat.format(date);
                System.out.println("* " + donation.getContributorName() + ": $"
                        + donation.getAmount() + " on " + formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            donations_sum = donations_sum + donation.getAmount();
        }

        donations_percent = donations_sum * 100 / fund.getTarget();

        System.out.println("Total donation amount: $" + donations_sum + " (" + donations_percent
                + "% of target)\n");

        System.out.println("To view donations aggregated by contributor, type C.");
        System.out.println("To delete this fund, type 9.");
        System.out.println("Otherwise, press enter to go back to the listing of funds.");
        String finalInput = in.nextLine();

        if (finalInput.length() == 1) {
            if (finalInput.charAt(0) == 'c' || finalInput.charAt(0) == 'C') {
                displayAggregatedDonations(fund);
                System.out.println("Press any key to go back to the listing of funds.");
                in.nextLine();
            }
        }

        if (finalInput.equals("9")) {
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
                // pass this fund to delete fund
                deleteFund(fund);

            } else if ((delete.equals("no") || delete.equals("n"))) {
                System.out.println("");
            }
            System.out.println("Press the Enter key to go back to the listing of funds.");
            in.nextLine();

        }

    }

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

    public void deleteFund(Fund fund) {
        String id = fund.getId();
        boolean dm = false;
        while (true) {
            try {
                dm = dataManager.deleteFund(id);
                org.getFunds().remove(fund);
                break;
            } catch (Exception e) {
                System.out.println(
                        "The fund cannot be deleted. Do you want to try again? Type 'y' for yes or enter another key to discontinue.");
                String input = in.nextLine().toLowerCase();
                if (!input.equals("y")) {
                    break;
                }
            }
        }
        if (dm == false) {
            System.out.println("Something went wrong. " + fund.getName() + " was not deleted.");
        } else {
            System.out.println(fund.getName() + " has been successfully deleted.\n");
        }

    }

    public static String[] login(Scanner scanner) {
        
        String usernamePassword[] = new String[2];

        System.out.print("\nUsername: ");
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

    
//Task 3.1: Organization App new user registration
    
// 1.  Modify existing start/main() so that it is possible for a user to start the app without providing login/password credentials, 
    //and then they have the option of logging in (which would bring them to the existing functionality, assuming successful login) 
    //or of creating a new organization.  //DONE (I HOPE)

    
//2. To create a new organization, 
    //they would need to provide a login name, password, organization name, and organization description, 
    //which would then be sent to the server using the RESTful API. This would require a change to the API, but you can refer to 
    //the “/createOrg” endpoint in the Administrator App to get a sense of how to do this. //DONE (I HOPE)

//3.  The Organization App should not allow the user to leave any of the fields blank, //DONE
    
    //or to specify a login name that already exists in the database. //DONE -good enough? At least it's handled by UI

//4. If any error occurs, including an error communicating with the RESTful API, //FROM DATA MANAGER?? NEED TRY CATCH?
    //the app should display a meaningful error message and allow the user to make another attempt to create an organization. //DONE 

//5. If the user successfully creates an organization, they should then be shown the prompt for creating a new fund, 
    //and the existing functionality should continue from there.  //I THINK THIS IS DONE

    
    
    
    public static void main(String[] args) {
        Scanner firstin = new Scanner(System.in);
        DataManager ds = initializeDataManager(firstin);
        String login = null;
        String password = null;
        String name = null;
        String description = null;
        Organization org = null;
        if (args.length == 2) {
            login = args[0];
            password = args[1];
        }
        while (org == null && ds != null) {
            if (login == null || password == null) {
                //TASK 3.1
                System.out.print("Welcome!\n\n Please enter 1 to login, or 0 to register a new organization: ");
                String initial = firstin.nextLine();
                
                //error handling for initial prompt
                while(!initial.equals("1") || !initial.equals("0")) {
                    break;
                }
                
              //tests first login attempt
                if (initial.equals("1")) {  
                    String usernamePassword[] = login(firstin);
                    login = usernamePassword[0];
                    password = usernamePassword[1];
                    try {
                        org = ds.attemptLogin(login, password);
                    } catch (Exception e) {
                        System.out.println(
                                "Error in retrieving or parsing data from database. Would you like to try again? Type 'y' for yes or enter another key to discontinue.");
                        String input = firstin.nextLine().toLowerCase();
                        if (!input.equals("y")) {
                            System.out.println("Goodbye!");
                            break;
                        }
                    }
                } else if (initial.equals("0")) {
                    String newOrgParams[] = createNewOrganization(firstin);
                    login = newOrgParams[0];
                    password = newOrgParams[1];
                    name = newOrgParams[2];
                    description = newOrgParams[3]; 
                    //send new org params to dataManager
                    try {
                        org = ds.createNewOrg(newOrgParams);
                    } catch (Exception e){
                        System.out.println("Database Error. New organization not created. Press any key to try again.");
                        firstin.nextLine();
                        //String input = firstin.nextLine().toLowerCase();
                        String StringArray[] = new String[2];
                        main(StringArray);
                        //if (input.equals("y")) {
                        //System.out.println("Goodbye!");

                            //break;
                        //}
                    }    
                }
            } 
            //else {
//                try {
//                    org = ds.attemptLogin(login, password);
//                } catch (Exception e) {
//                    System.out.println(
//                            "Error in retrieving or parsing data from database. Do you want to try again? Type 'y' for yes or enter another key to discontinue.");
//                    String input = firstin.nextLine().toLowerCase();
//                    if (!input.equals("y")) {
//                        System.out.println("Goodbye!");
//                        break;
//                    }
//                }
//            } 
            //this will run if first login attempt fails
            else if (org == null) {
                if (login != null && password != null) {
                    System.out.println("Login attempt unsuccessful. Please re-enter your username and password.");
                }
                String usernamePassword[] = login(firstin);
                login = usernamePassword[0];
                password = usernamePassword[1];
                org = ds.attemptLogin(login, password);
            }
            
        } //IMPORTANT: end of while loop
        if (org != null) {
            UserInterface ui = new UserInterface(ds, org);
            ui.start();
        }
        firstin.close();
    }
}
