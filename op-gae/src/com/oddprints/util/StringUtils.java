/*******************************************************************************
 * Copyright 2011 Matt Burns
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.oddprints.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.co.mattburns.pwinty.v2.CountryCode;

public class StringUtils {

    public static String formatMoney(int pennies) {
        return "&pound;" + formatMoneyNoSymbol(pennies);
    }

    public static String formatMoneyNoSymbol(int pennies) {
        return String.format("%.2f", ((double) pennies / 100));
    }

    // Don't show pennies if rounds to whole pounds
    public static String formatMoneyShort(int pennies) {
        if (pennies % 100 == 0) {
            return "&pound;" + (pennies / 100);
        } else {
            // revert to normal way
            return formatMoney(pennies);
        }
    }

    public static String replaceNull(String input) {
        return ((input == null) ? "" : input);
    }

    public static String estimatedDeliveryDate(Date orderDate,
            CountryCode countryCode) {
        int minDays = 3;
        int maxDays = 9;
        switch (countryCode) {
            case GB:
            case US:
                minDays = 2;
                maxDays = 5;
                break;
            default:

        }
        Calendar earliest = Calendar.getInstance();
        earliest.setTime(orderDate);
        addWeekdays(earliest, minDays);

        Calendar latest = Calendar.getInstance();
        latest.setTime(orderDate);
        addWeekdays(latest, maxDays);

        return formatDate(earliest) + " - " + formatDate(latest);
    }

    private static void addWeekdays(Calendar cal, int days) {
        // This looks crazy to me but I can't seem to describe the same
        // functionality in a clear way...
        while (days > 0) {
            while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                    || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                cal.add(Calendar.DATE, 1);
            }
            cal.add(Calendar.DATE, 1);
            days--;
            while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                    || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                cal.add(Calendar.DATE, 1);
            }
        }
    }

    private static String formatDate(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
        return sdf.format(cal.getTime())
                + getDayOfMonthSuffix(cal.get(Calendar.DAY_OF_MONTH));
    }

    private static String getDayOfMonthSuffix(final int n) {
        checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
