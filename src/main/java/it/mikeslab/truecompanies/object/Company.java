package it.mikeslab.truecompanies.object;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Company {
    private int id;
    private final String name;
    private final UUID owner;
    private final UUID[] employees;
    private int balance;
    private int salary;
}
