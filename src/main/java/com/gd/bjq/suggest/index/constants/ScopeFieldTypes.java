package com.gd.bjq.suggest.index.constants;

public enum ScopeFieldTypes {
    PRODUCT("product"),
    UPC("upc");

    private final String type;

    ScopeFieldTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
