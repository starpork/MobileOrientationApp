package com.orientation.mobile.mobileorientation;

/**
 * Created by user on 26/09/2017.
 */

public class UserDetails {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String destinations;

    public UserDetails(String firstName, String lastName, String username, String email, String destinations) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.destinations = destinations;
    }
    public UserDetails(String firstName, String lastName, String username, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDestinations() {
        return destinations;
    }

    public void setDestinations(String destinations) {
        this.destinations = destinations;
    }
    public UserDetails() {
    }
}
