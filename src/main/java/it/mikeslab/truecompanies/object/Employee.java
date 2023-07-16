package it.mikeslab.truecompanies.object;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Employee {
    private final UUID playerUUID;
    private final int companyID;
    private final int salary;

    public boolean canFire;
    public boolean canHire;
    public boolean canWithdraw;
    public boolean canDeposit;

}
