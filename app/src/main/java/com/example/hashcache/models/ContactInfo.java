package com.example.hashcache.models;

public class ContactInfo {
    private String email;
    private String phoneNumber;
    private final String emailRegex = ".*[@]{1}[a-zA-Z][.]{1}$";
    private final String phoneNumberRegex = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$";

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
}
