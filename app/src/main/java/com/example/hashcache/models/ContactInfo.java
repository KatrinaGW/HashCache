package com.example.hashcache.models;

/**
 * Contains the contact information for a player
 */
public class ContactInfo {
    private String email;
    private String phoneNumber;
    private final String emailRegex = ".*[@]{1}[a-zA-Z].*[.]{1}$";
    private final String phoneNumberRegex = "^\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d$";

    public ContactInfo(){
        this.email = "";
        this.phoneNumber = "";
    }

    /**
     * Sets the email for a player
     *
     * @param email The candidate email to use for the player
     * @throws IllegalArgumentException if the candidate email does not have a valid form
     */
    public void setEmail(String email){
        int dotPosition = email.lastIndexOf(".");
        if(dotPosition==-1){
            throw new IllegalArgumentException("Given email does not contain a domain");
        }

        if(email.substring(0, dotPosition + 1).matches(emailRegex)){
            this.email = email;
        } else{
            System.out.println(email.substring(0, dotPosition + 1));
            throw new IllegalArgumentException("Given email has an invalid format!");
        }
    }

    /**
     * Sets the phone number for a player
     * @param phoneNumber The candidate phone number for the player
     * @throws IllegalArgumentException if the phone number does not have a valid form
     */
    public void setPhoneNumber(String phoneNumber){
        if(phoneNumber.matches(phoneNumberRegex)){
            this.phoneNumber = phoneNumber;
        }else{
            throw new IllegalArgumentException("Given phone number has an invalid format!");
        }

    }

    /**
     * Gets the phone number in this contact info
     * @return phoneNumber The registered phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the registered email for this contact info
     * @return email The registered email
     */
    public String getEmail() {
        return email;
    }
}
