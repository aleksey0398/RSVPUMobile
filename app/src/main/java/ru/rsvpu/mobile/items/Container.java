package ru.rsvpu.mobile.items;

/**
 * Created by aleksej on 19.09.17.
 *
 */

public class Container {
    private String value, name, attr;


    Container() {
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
