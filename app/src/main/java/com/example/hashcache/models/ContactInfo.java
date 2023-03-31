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
        if(testValidEmail(email)){
            this.email = email;
        }else{
            throw new IllegalArgumentException("The email format is invalid!");
        }
    }

    /**
     * Checks if the phoneNumber matches the ddd-ddd-dddd format
     * @param newPhoneNumber the phone number whose format needs to be checked
     * @return isValid true if the phone number matches the format, false otherwise
     */
    public boolean testValidPhoneNumber(String newPhoneNumber){
        boolean isValid = true;

        if(!newPhoneNumber.equals("")){
            isValid = newPhoneNumber.matches(phoneNumberRegex);
        }

        return isValid;
    }

    /**
     * Checks if the email matches the valid email format
     * @param newEmail the email whose format needs to be checked
     * @return isValid true if the email matches the format, false otherwise
     */
    public boolean testValidEmail(String newEmail){
        boolean isValid = true;

        if(!newEmail.equals("")){
            int dotPosition = newEmail.lastIndexOf(".");
            if (dotPosition == -1) {
                isValid = false;
            }

            if (isValid && !newEmail.substring(0, dotPosition + 1).matches(emailRegex)) {
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Sets the phone number for a player
     * @param phoneNumber The candidate phone number for the player
     * @throws IllegalArgumentException if the phone number does not have a valid form
     */
    public void setPhoneNumber(String phoneNumber){
        if(testValidPhoneNumber(phoneNumber)){
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
