package com.example.attfirebase.units;

public class StudentUnitsItem {
    private final String unit;
    private final String unit_lecturer;
    private final String unit_date;

    public StudentUnitsItem(){
        this.unit = "No module info";
        this.unit_lecturer = "No lecturer info";
        this.unit_date = "No date info";
    }
    public StudentUnitsItem(String unit, String unit_lecturer, String unit_date) {
        this.unit = unit;
        this.unit_lecturer = unit_lecturer;
        this.unit_date = unit_date;
    }

//    public StudentModuleItem(String module) {
//        this.module = module;
//        this.module_lecturer = "No lecturer info";
//        this.module_date = "No date info";
//    }

    public String getUnit() {
        return unit;
    }
    public String getUnit_lecturer() {
        return unit_lecturer;
    }
    public String getUnit_date() {
        return unit_date;
    }



}


