package com.example.hashcache.models;

public class ContactInfo {
    private String email;
    private String phoneNumber;
    private final String emailRegex = ".*[@]{1}[a-zA-Z].*[.]{1}$";
    private final String phoneNumberRegex = "^\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d$";

    public ContactInfo(){
        this.email = "";
        this.phoneNumber = "";
    }

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

    public void setPhoneNumber(String phoneNumber){
        if(phoneNumber.matches(phoneNumberRegex)){
            this.phoneNumber = phoneNumber;
        }else{
            throw new IllegalArgumentException("Given phone number has an invalid format!");
        }

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}
