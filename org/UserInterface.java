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
    private static String orgLogin; // track for 3.3

    public UserInterface(DataManager dataManager, Organization org) {
        this.dataManager = dataManager;
        this.org = org;
    }

    public void start() {

        // add name to orgName list
        orgNames.add(org.getName());
        // System.out.println("orgNames: " + orgNames);

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

            System.out.println("Enter 0 to create a new fund.");
            System.out.println("Enter -1 to logout.");
            System.out.println("Enter -2 to change the password.");
            System.out.println("Enter -3 to change the org name and description.");

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
            } else if (option == -2) {
                if (updatePassword()) {
                    System.out.println("Password successfully updated!");
                } else {
                    System.out.println("Password was not updated.");
                }
            } else if (option == -3) {
                updateNameDesc();
            } else if (option < -3) {
                System.out.println(option + " is an invalid input. Please enter valid number.");
            } else if (option > org.getFunds().size()) {
                System.out.println(option
                        + " is an invalid input. Please enter 0 to create a new fund, -1 to logout, -2 to modify password, -3 to update org name and description, or choose from the list of funds.");
            } else {
                displayFund(option);
            }
        }

    }

    // task 3.3
    public void updateNameDesc() {
        System.out.println("Please retype your password to continue:");
        String password = in.nextLine().trim();
        if (checkPassword(password)) {
            System.out.println("Thank you for confirming your password.");
            // Edit Org Name
            System.out.println("The current Organization Name is: " + org.getName());
            System.out.println(
                    "To leave as is, press Enter. Otherwise, type a new Organization Name Below:");
            String newOrgName = in.nextLine().trim();
            if (newOrgName.length() == 0) {
                System.out.println("The Organization Name will be left as is.");
                newOrgName = org.getName();
            } else {
                System.out.println("The Organization Name will be changed to: " + newOrgName);
            }
            // Edit Org Description
            System.out.println("The current Organization Description is: " + org.getDescription());
            System.out.println(
                    "To leave as is, press Enter. Otherwise, type a new Organization Description Below:");
            String newOrgDescription = in.nextLine().trim();
            if (newOrgDescription.length() == 0) {
                System.out.println("The Organization Description will be left as is.");
                newOrgDescription = org.getDescription();
            } else {
                System.out.println(
                        "The Organization Description will be changed to: " + newOrgDescription);
            }
            // Update Database and close
            try {
                dataManager.updateOrgName(org.getId(), newOrgName, newOrgDescription);
                System.out.println("Database updated successfully");
                org = dataManager.attemptLogin(orgLogin, password); // this is to refresh the data
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error updating database");
            }
            System.out.println("Press any key to go back to main menu.");
            in.nextLine();
        } else {
            System.out.println("Password incorrect. Press any key to go back to the main menu.");
            in.nextLine();
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

    // TASK 3.1
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
        while (!hasName) {
            if (!orgNames.contains(name)) {
                params[2] = name;
                orgNames.add(name);
                hasName = true;
            } else {
                System.out.println("Sorry, that name is taken. Please enter a new name.");
                name = scanner.nextLine().trim();
                while (name.isEmpty() || name.matches("\\s+")) {
                    System.out
                            .print("Name cannot be blank. Please re-enter an Organization name: ");
                    name = scanner.nextLine().trim();
                }
            }
        }
        System.out.print("Enter new Organization description: ");
        String description = scanner.nextLine().trim();
        while (description.isEmpty() || description.matches("\\s+")) {
            System.out.print(
                    "Description cannot be blank. Please re-enter an Organization description: ");
            description = scanner.nextLine().trim();
        }
        params[3] = description;

        return params;

    }

    // task 3.3
    public boolean checkPassword(String password) {

        Organization tempOrg = dataManager.attemptLogin(orgLogin, password);
        if (tempOrg == null) {
            return false;
        } else {
            return true;
        }

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

        if (fund.getTarget() != 0) {
            donations_percent = donations_sum * 100 / fund.getTarget();
        } else {
            donations_percent = 0;
        }

        System.out.println("Total donation amount: $" + donations_sum + " (" + donations_percent
                + "% of target)\n");

        System.out.println("To view donations aggregated by contributor, type C.");
        System.out.println("To edit the organization's account information, type E."); // Task 3.3
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

            if (delete.equals("yes") || delete.equals("y")) {
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

    public static void logout() {

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

    /**
     * Returns true only if the user correctly enters the current password once and
     * then enters new password exactly twice in addition to a successful request to
     * API to update the password.
     * 
     * @return @literal <true> if password is successfully updated; otherwise,
     *         return false.
     */
    public boolean updatePassword() {
        System.out.print("Please enter your current password:");
        String usernamePassword = in.nextLine().trim();
        String currentPassword = org.getPassword();
        if (!currentPassword.equals(usernamePassword)) {
            System.out.println("Incorrect password.");
            return false;
        } else {
            System.out.print("Please enter your new password:");
            String newPassword = in.nextLine().trim();
            System.out.print("Please enter your new password again:");
            String checkNewPassword = in.nextLine().trim();
            if (!newPassword.equals(checkNewPassword)) {
                System.out.println("Inputs do not match.");
                return false;
            } else {
                try {
                    if (dataManager.updatePassword(org.getId(), newPassword)) {
                        org.setPassword(newPassword);
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    System.out.println("Error in updating password.");
                    return false;
                }
            }

        }
    }

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
                // TASK 3.1
                System.out.print(
                        "\nWelcome!\n\n Please enter 1 to login, or 0 to register a new organization: ");
                String initial = firstin.nextLine();

                // error handling for initial prompt
                while (!initial.equals("1") || !initial.equals("0")) {
                    break;
                }

                // tests first login attempt
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
                            logout();
                        }
                    }
                } else if (initial.equals("0")) {
                    String newOrgParams[] = createNewOrganization(firstin);
                    login = newOrgParams[0];
                    password = newOrgParams[1];
                    name = newOrgParams[2];
                    description = newOrgParams[3];
                    // send new org params to dataManager
                    try {
                        org = ds.createNewOrg(newOrgParams);
                    } catch (Exception e) {
                        System.out.println(
                                "Database Error. New organization not created. Press any key to try again.");
                        firstin.nextLine();
                        String StringArray[] = new String[2];
                        main(StringArray);
                    }
                }
            } else if (org == null) {
                if (login != null && password != null) {
                    System.out.println(
                            "Login attempt unsuccessful. Please re-enter your username and password.");
                }
                String usernamePassword[] = login(firstin);
                login = usernamePassword[0];
                password = usernamePassword[1];
                org = ds.attemptLogin(login, password);
            }

        }

        if (org != null) {
            orgLogin = login;
            org.setPassword(password);
            UserInterface ui = new UserInterface(ds, org);
            ui.start();
        }
        firstin.close();
    }
}
