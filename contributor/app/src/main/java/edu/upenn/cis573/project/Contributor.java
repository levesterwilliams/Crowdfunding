package edu.upenn.cis573.project;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Contributor implements Serializable {

    private String id;
    private String name;
    private String email;
    private String creditCardNumber;
    private String creditCardCVV;
    private String creditCardExpiryMonth;
    private String creditCardExpiryYear;
    private String creditCardPostCode;
    private List<Donation> donations;

    public Contributor(String id, String name, String email, String creditCardNumber, String creditCardCVV, String creditCardExpiryMonth, String creditCardExpiryYear, String creditCardPostCode) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.creditCardNumber = creditCardNumber;
        this.creditCardCVV = creditCardCVV;
        this.creditCardExpiryMonth = creditCardExpiryMonth;
        this.creditCardExpiryYear = creditCardExpiryYear;
        this.creditCardPostCode = creditCardPostCode;
        donations = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getCreditCardCVV() {
        return creditCardCVV;
    }

    public String getCreditCardExpiryMonth() {
        return creditCardExpiryMonth;
    }

    public String getCreditCardExpiryYear() {
        return creditCardExpiryYear;
    }

    public String getCreditCardPostCode() {
        return creditCardPostCode;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }
}
