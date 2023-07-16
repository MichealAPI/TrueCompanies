package it.mikeslab.truecompanies.util.itemstack;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CustomHeadEnum {
    enm("test");

    private final String value;

    public String getValue() {
        return value;
    }


}
