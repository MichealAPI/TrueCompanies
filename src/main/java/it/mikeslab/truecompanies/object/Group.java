package it.mikeslab.truecompanies.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class Group {

    public boolean
            canHire,
            canFire,
            canDeposit,
            canWithdraw,
            canPromote,
            canDemote;

    @Getter private String tag;
    @Getter private int id;

}
