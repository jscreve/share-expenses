package com.shareexpenses.app.model;

import java.io.Serializable;

/**
 * Created by jess on 17/09/2014.
 */
public class Currency implements Serializable {

   private String name;

    public Currency(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
