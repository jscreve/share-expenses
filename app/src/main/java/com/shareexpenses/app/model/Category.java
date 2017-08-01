package com.shareexpenses.app.model;

import java.io.Serializable;

/**
 * Created by jess on 17/09/2014.
 */
public class Category implements Serializable {

    private String name;
    private Long id;
    private Account account;
    public static final String EMPTY_CATEGORY = "default";

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account=account;
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category category = (Category) o;

        if (id != null ? !id.equals(category.id) : category.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
