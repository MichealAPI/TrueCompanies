package it.mikeslab.truecompanies.menu.perms;

import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.apache.commons.lang3.StringUtils;

public enum GroupPermission {
    CAN_HIRE(StringUtils.capitalize(Language.getString(LangKey.HIRE, false)), 'a'),
    CAN_FIRE(StringUtils.capitalize(Language.getString(LangKey.FIRE, false)), 'b'),
    CAN_DEPOSIT(StringUtils.capitalize(Language.getString(LangKey.DEPOSIT, false)), 'c'),
    CAN_WITHDRAW(StringUtils.capitalize(Language.getString(LangKey.WITHDRAW, false)), 'd'),
    CAN_PROMOTE(StringUtils.capitalize(Language.getString(LangKey.PROMOTE, false)), 'e'),
    CAN_DEMOTE(StringUtils.capitalize(Language.getString(LangKey.DEMOTE, false)), 'f');

    private final String name;
    private final char placeholder;

    GroupPermission(String name, char placeholder) {
        this.name = name;
        this.placeholder = placeholder;
    }

    public String getName() {
        return name;
    }

    public char getPlaceholder() {
        return placeholder;
    }

    public boolean getStatus(GroupPermission groupPermission, Group group) {
        return switch (groupPermission) {
            case CAN_HIRE -> group.canHire;
            case CAN_FIRE -> group.canFire;
            case CAN_DEPOSIT -> group.canDeposit;
            case CAN_WITHDRAW -> group.canWithdraw;
            case CAN_PROMOTE -> group.canPromote;
            case CAN_DEMOTE -> group.canDemote;
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
        }
    }
}

