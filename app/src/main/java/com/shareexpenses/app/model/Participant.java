package com.shareexpenses.app.model;

import java.io.Serializable;

/**
 * Created by jess on 22/09/2014.
 */
public class Participant implements Serializable {

    private String name;

    private String lastName;

    private Account account;

    private Long id;

    public Participant(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    //used to display a Participant in the Spinner
    public String toString() {
        return getName() + " " + getLastName();
    }

    public boolean equals(Object obj) {
        Participant input=(Participant)obj;
        return input.getLastName().equals(this.getLastName()) && input.getName().equals(this.getName());
    }

    public int hashCode() {
        return (this.name + this.lastName).hashCode();
    }
}
