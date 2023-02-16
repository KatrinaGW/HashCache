package com.example.hashcache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.hashcache.models.ContactInfo;

import org.junit.jupiter.api.Test;

public class ContactInfoTest {

    @Test
    void AddPhoneNumberThrows(){
        ContactInfo contactInfo = new ContactInfo();

        assertThrows(IllegalArgumentException.class, () -> {
            contactInfo.setPhoneNumber("abc");
        });
    }

    @Test
    void AddPhoneNumber(){
        String validPhoneNumber = "403-999-9999";
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setPhoneNumber(validPhoneNumber);
        assertEquals(validPhoneNumber, contactInfo.getPhoneNumber());
    }

    @Test
    void AddEmailThrowsNoDomain(){
        ContactInfo contactInfo = new ContactInfo();

        assertThrows(IllegalArgumentException.class, () -> {
            contactInfo.setEmail("abc@gmail");
        });
    }

    @Test
    void AddEmailThrowsInvalidFormat(){
        ContactInfo contactInfo = new ContactInfo();

        assertThrows(IllegalArgumentException.class, () -> {
            contactInfo.setEmail("abc@gmailcom");
        });
    }

    @Test
    void AddEmail(){
        String validEmail = "tony.stank@ualberta.ca";
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(validEmail);
        assertEquals(validEmail, contactInfo.getEmail());
    }
}
