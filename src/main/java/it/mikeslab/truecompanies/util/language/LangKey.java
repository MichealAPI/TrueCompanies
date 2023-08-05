/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.mikeslab.truecompanies.util.language;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LangKey {
    PREFIX("&c&lTrueCompanies &8» &r"),
    PERMS_CLICK_TO_ENABLE("&aClick to Enable &7(&6%perm%&7)"),
    PERMS_CLICK_TO_DISABLE("&cClick to Disable &7(&6%perm%&7)"),
    ERROR_UPDATING_PERMS("&cAn error occurred while updating permissions."),
    PERMS_UPDATED("&aPermissions updated successfully."),
    NEXT_PAGE("&aNext Page"),
    PREVIOUS_PAGE("&aPrevious Page"),
    SELECT_A_COMPANY("Select a Company"),
    CLICK_TO_SELECT_COMPANY("&aClick to select this Company."),
    COMPANY_ID("&aCompany ID: &6%id%"),
    COMPANY_NAME("&aCompany Name: &6%name%"),
    COMPANY_OWNER("&aCompany Owner: &6%owner%"),
    COMPANY_BALANCE("&aCompany Balance: &6%balance%"),
    COMPANY_EMPLOYEES("&aCompany Employees: &6%employees%"),
    COMPANY_DESCRIPTION("&aCompany Description: &6%description%"),
    YOUR_GROUP("&aYour Group: &6%group%"),
    PERMISSIONS("&aPermissions:"),
    YES("Yes"),
    NO("No"),
    CAN_WITHDRAW("&aCan Withdraw: &6%can_withdraw%"),
    CAN_DEPOSIT("&aCan Deposit: &6%can_deposit%"),
    CAN_HIRE("&aCan Hire: &6%can_hire%"),
    CAN_FIRE("&aCan Fire: &6%can_fire%"),
    CAN_PROMOTE("&aCan Promote: &6%can_promote%"),
    CAN_DEMOTE("&aCan Demote: &6%can_demote%"),
    PLUGIN_RELOADED("&aPlugin reloaded successfully."),
    PLUGIN_RELOADING("&aReloading plugin..."),
    PLUGIN_RELOAD_ERROR("&cAn error occurred while reloading the plugin."),
    SELECT_A_PLAYER_MANAGE("Select a player to manage."),
    CANNOT_PROMOTE_DEMOTE_SAME_RANK("You cannot promote or demote someone to the same rank."),
    PROMOTE("promote"),
    DEMOTE("demoted"),
    PROMOTED("promoted"),
    DEMOTED("demoted"),
    MANAGE_EMPLOYEES_NO_PERMS("&cYou do not have permission to %action% employees in this Company."),
    SELECT_TO_HIRE("Select a player to hire."),
    HIRE_EMPLOYEES_NO_PERMS("&cYou do not have permission to hire employees in this Company."),
    YOU_HIRED("&aYou hired &6%player%&a in Company &6%company%&a as &6%group%&a."),
    YOU_HAVE_BEEN_HIRED("&aYou have been hired in Company &6%company%&a as &6%group%&a."),
    FIRE_EMPLOYEES_NO_PERMS("&cYou do not have permission to fire employees in this Company."),
    SELECT_TO_FIRE("Select an Employee to fire."),
    YOU_HAVE_FIRED("&aYou fired &6%player%&a from Company &6%company%&a."),
    YOU_HAVE_BEEN_FIRED("&aYou have been fired from Company &6%company%&a."),
    LEFT_COMPANY_CHAT("&aYou left Company %company% Chat."),
    JOINED_COMPANY_CHAT("&aYou joined Company %company% Chat."),
    NOT_IN_COMPANY_CHAT("&cYou are not in a Company Chat."),
    COMPANY_DOESNT_EXISTS("&cThis Company doesn't exists."),
    YOU_ALREADY_IN_COMPANY_CHAT("&cYou are already in a Company Chat. Leave using '/company leavechat' and retry"),
    COMPANY_WITHDRAW_NO_PERMS("&cYou do not have permission to withdraw money from this Company."),
    COMPANY_DEPOSIT_NO_PERMS("&cYou do not have permission to deposit money in this Company."),
    WITHDREW_MONEY("&aYou withdrew &6%amount%&a from Company &6%company%&a."),
    DEPOSITED_MONEY("&aYou deposited &6%amount%&a in Company &6%company%&a."),
    DEPOSIT_NOT_ENOUGH_MONEY("&cYou do not have enough money to deposit."),
    WITHDRAW_NOT_ENOUGH_MONEY("&cThis Company doesn't have enough money to withdraw."),
    NOT_EMPLOYED_HERE("&cYou are not employed in this Company."),
    SELECT_THIS_PLAYER("&aClick to select this Player."),
    SELECT_A_GROUP("Select a Group"),
    GROUP("&aGroup: &6%group%"),
    HIRE("Hire"),
    FIRE("Fire"),
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    MANAGE_YOU_HAVE("You have %action% %player% to %group% in Company %company%"),
    MANAGE_YOU_HAVE_BEEN("You have been %action% to %group% in Company %company%"),
    NOT_OWNER("&cYou are not the owner of this Company."),;

    private final String defaultValue;

}
