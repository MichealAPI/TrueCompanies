package it.mikeslab.truecompanies.object;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Company {

    private String id;
    private String displayName;
    private String description;

    private double balance;
    private Map<Integer, Group> groups;
    private Map<String, Integer> employees;
    private String chatFormat;

    private List<String> fireCommands;

    public String getOwner() {
        for(Map.Entry<String, Integer> employeeEntry : employees.entrySet()) {

            if(employeeEntry.getValue() == 1) {
                return employeeEntry.getKey();
            }
        }

        return null;
    }

}
