package it.mikeslab.truecompanies.menu.perms;

import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.apache.commons.lang3.StringUtils;

public enum GroupPermission {
    CAN_HIRE(StringUtils.capitalize(Language.getString(LangKey.HIRE, false))),
    CAN_FIRE(StringUtils.capitalize(Language.getString(LangKey.FIRE, false))),
    CAN_DEPOSIT(StringUtils.capitalize(Language.getString(LangKey.DEPOSIT, false))),
    CAN_WITHDRAW(StringUtils.capitalize(Language.getString(LangKey.WITHDRAW, false))),
    CAN_PROMOTE(StringUtils.capitalize(Language.getString(LangKey.PROMOTE, false))),
    CAN_DEMOTE(StringUtils.capitalize(Language.getString(LangKey.DEMOTE, false))),
    CAN_PAYCHECKS(StringUtils.capitalize(Language.getString(LangKey.PAYCHECKS, false)));

    private final String name;

    GroupPermission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public boolean getStatus(GroupPermission groupPermission, Group group) {
        return switch (groupPermission) {
            case CAN_HIRE -> group.canHire;
            case CAN_FIRE -> group.canFire;
            case CAN_DEPOSIT -> group.canDeposit;
            case CAN_WITHDRAW -> group.canWithdraw;
            case CAN_PROMOTE -> group.canPromote;
            case CAN_DEMOTE -> group.canDemote;
            case CAN_PAYCHECKS -> group.canPaychecks;
        };
    }

    public void toggleStatus(Group group) {
        switch (this) {
            case CAN_HIRE: group.canHire = !group.canHire; break;
            case CAN_FIRE: group.canFire = !group.canFire; break;
            case CAN_DEPOSIT: group.canDeposit = !group.canDeposit; break;
            case CAN_WITHDRAW: group.canWithdraw = !group.canWithdraw; break;
            case CAN_PROMOTE: group.canPromote = !group.canPromote; break;
            case CAN_DEMOTE: group.canDemote = !group.canDemote; break;
            case CAN_PAYCHECKS: group.canPaychecks = !group.canPaychecks; break;
        }
    }
}

