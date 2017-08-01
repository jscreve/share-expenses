package com.shareexpenses.app;

/**
 * Created by jess on 08/09/2014.
 */
public class DrawerItem {

    private String name;
    private Long id;

    public boolean isAccount;
    public boolean isSeparator;

    public DrawerItem(String name, boolean isAccount, boolean isSeparator) {
        this.name=name;
        this.isAccount=isAccount;
        this.isSeparator=isSeparator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAccount() {
        return isAccount;
    }

    public void setAccount(boolean isAccount) {
        this.isAccount = isAccount;
    }

    public boolean isSeparator() {
        return isSeparator;
    }

    public void setSeparator(boolean isSeparator) {
        this.isSeparator = isSeparator;
    }
}