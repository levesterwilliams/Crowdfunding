
//Test 1.8
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class UserInterface {

    private DataManager dataManager;
    private Organization org;
    private Scanner in = new Scanner(System.in);

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
            //Task 1.7 input error handling
            int option = 0;
            boolean isInteger = false;
            //Task 1.7 checks user input is an integer
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
              //Task 1.7  checks if input is less than 0
            } else if (option < 0) {
                System.out.println(option + " is an invalid input. Please enter number greater than 0");
              //Task 1.7  checks input does not exceed size of fund list
            } else if (option > org.getFunds().size() && option != 0) {
                System.out.println(option
                        + " is an invalid input. Please enter 0 to create a new fund, or choose from list of funds.");
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
        
        //Task 1.7 checks that fund target is a number
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
                + "% of target)");


        System.out.println("Press the Enter key to go back to the listing of funds");
        in.nextLine();

    }

    public static void main(String[] args) {

        DataManager ds = new DataManager(new WebClient("localhost", 3001));

        String login = args[0];
        String password = args[1];

        Organization org = ds.attemptLogin(login, password);

        if (org == null) {
            System.out.println("Login failed.");
        } else {

            UserInterface ui = new UserInterface(ds, org);

            ui.start();

        }
    }

}
