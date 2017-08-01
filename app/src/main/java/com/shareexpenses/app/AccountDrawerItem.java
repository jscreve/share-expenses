package com.shareexpenses.app;

/**
 * Created by jess on 08/09/2014.
 */
public class AccountDrawerItem extends DrawerItem{

    private Long id;

    public AccountDrawerItem(String name, Long accountId, boolean isAccount, boolean isSeparator) {
        super(name, isAccount, isSeparator);
        this.id = accountId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}