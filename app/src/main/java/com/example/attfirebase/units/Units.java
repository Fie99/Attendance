package com.example.attfirebase.units;

import java.util.List;

public class Units {

    private List<String> unitGroup;

    public void Unit() {

    }

    public void Unit(List<String> unitGroup) {
        this.unitGroup = unitGroup;
    }

    public List<String> getUnitGroup() {
        return unitGroup;
    }

    public void setUnitGroup(List<String> unitGroup) {
        this.unitGroup = unitGroup;
    }
}
