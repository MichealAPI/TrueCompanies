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
    CONFIRM_MENU("Confirm Menu"),
    CONFIRM("&aConfirm"),
    CANCEL("&cCancel"),
    FIRED_SUCCESSFULLY("&a{player} has been fired successfully fired from {company}"),
    YOU_ARE_NOT_IN_A_COMPANY("&cYou are not in a company"),
    YOU_DO_NOT_HAVE_PERMISSION("&cYou do not have permission to do this"),
    TARGET_NOT_EMPLOYED("&c{player} is not employed at {company}"),
    SUBJECT_NOT_EMPLOYED("&cYou are not employed at {company}"),
    COMPANY_NOT_FOUND("&cCompany '&6{companyID}'&c not found!"),
    CLICK_TO_SELECT("&aClick to select"),
    NEXT_PAGE("&aNext Page"),
    PREVIOUS_PAGE("&aPrevious Page"),
    SEARCH("&6Search"),
    SEARCH_LORE("&8(Right-Click) &7to search for a company"),
    ENTER_QUERY("Enter a query"),;

    private final String defaultValue;

}
