package it.mikeslab.truecompanies.object;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Company {

    private String id;
    private String displayName;
    private String description;

    private int balance;
    private Map<Integer, Group> groups;
    private Map<String, Integer> employees;
    private String chatFormat;

    public String getOwner() {
        for(Map.Entry<String, Integer> employeeEntry : employees.entrySet()) {
            System.out.println(employeeEntry.getKey() + " " + employeeEntry.getValue());
            if(employeeEntry.getValue() == 1) {
                return employeeEntry.getKey();
            }
        }
        System.out.println("No owner found for company " + id);
        return null;
    }

}
