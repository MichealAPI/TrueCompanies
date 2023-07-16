package it.mikeslab.truecompanies.object;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Company {
    private int id;
    private final String name;
    private final UUID ownerUUID;
    private List<Employee> employees;
    private int balance;

    // internal
    private List<UUID> firedEmployees;

    public void fireEmployee(UUID employeeUUID) {
        List<Employee> updatedEmployees = employees;

        for(Employee employee : employees) {
            if(employee.getPlayerUUID().equals(employeeUUID)) {
                updatedEmployees.remove(employee);
            }
        }

        this.employees = updatedEmployees;

        firedEmployees.add(employeeUUID);
    }

}
