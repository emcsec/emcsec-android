package com.aspanta.emcsec.tools;

import android.text.Html;
import android.text.Spanned;

import com.aspanta.emcsec.db.SPHelper;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_EXCHANGE_RATE_KEY;


public class StringUtils {

    public static Spanned concatBalanceRowEmc(String currency) {
        return Html.fromHtml("<b>~" + SPHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                currency + " (" + "<b>1 </b> EMC = <b>" + SPHelper.getInstance().getStringValue(EMC_EXCHANGE_RATE_KEY) + " </b>" +
                currency + ")");
    }

    public static Spanned concatBalanceRowBtc(String currency) {
        return Html.fromHtml("<b>~" + SPHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                currency + " (" + "<b>1 </b> BTC = <b>" + SPHelper.getInstance().getStringValue(BTC_EXCHANGE_RATE_KEY) + " </b>" +
                currency + ")");
    }

    public static String getCurrentCurrency() {
        String currentCurrency = SPHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }
        return currentCurrency;
    }
}
