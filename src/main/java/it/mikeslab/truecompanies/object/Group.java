package it.mikeslab.truecompanies.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@AllArgsConstructor
public class Group {

    public boolean
            canHire,
            canFire,
            canDeposit,
            canWithdraw,
            canPromote,
            canDemote,
            canPaychecks;

    @Getter private String tag;
    @Getter private int id;

    @Getter private List<String>
            hireCommands,
            promoteCommands,
            demoteCommands;

}
