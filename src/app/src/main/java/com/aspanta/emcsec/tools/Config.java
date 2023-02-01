package com.aspanta.emcsec.tools;

public class Config {

    public static final String BASE_URL_COIN_MARKET = "https://api.coinmarketcap.com/";
    public static final String BASE_URL_BTC = "http://electrum.emercoin.net:8100";
    public static final String INTENT_LIST_KEY = "List";
    public static final String TOKEN = "TOKEN";
    public static final String SEED = "SEED";
    public static final String COPY_MESSAGE = "Copied to clipboard";
    public static final String BTN_COURSE_PATH = "/v2/ticker/1/";
    public static final String EMC_COURSE_PATH = "/v2/ticker/558/";
    public static final String ERROR_MESSAGE = "Could not connect to the server";

    public static final String SEEKBAR_VALUE_KEY = "seekbar_value_key";
    public static final String SEEKBAR_POSITION_KEY = "seekbar_position_key";

    public static final String REGEX_EMC_ADDRESS = "^[E][a-km-zA-HJ-NP-Z0-9]{33}$";
    public static final String REGEX_BTC_ADDRESS = "^[13][a-km-zA-HJ-NP-Z1-9]{26,33}$";

    public static final String EXTRAS_TYPE = "type";
    public static final String EXTRAS_AMOUNT = "amount";
    public static final String EXTRAS_ADDRESS = "address";
    public static final String BITCOIN = "bitcoin";
    public static final String EMERCOIN = "emercoin";

    public static final String EMC_BALANCE_KEY = "emc_balance";
    public static final String EMC_BALANCE_IN_USD_KEY = "emc_balance_in_usd";
    public static final String EMC_EXCHANGE_RATE_KEY = "emc_course";
    public static final String BTC_BALANCE_KEY = "btc_balance";
    public static final String BTC_BALANCE_IN_USD_KEY = "btc_balance_in_usd";
    public static final String BTC_EXCHANGE_RATE_KEY = "btc_course";
    public static final String REGEX_AMOUNT = "^[0-9]*[.,][0-9]+(?:[eE][-+]?[0-9]+)?$";
    public static final String REGEX_WHOLE_AMOUNT = "^(?!0+$)[\\d]{0,9}$";
    public static final String ARG_PARAM_VIEW_PAGER_PAGE = "paramPage";

    //Keys for SharedPreferences
    public static final String CHANGE_ADDRESS_BTC = "change_address_btc";
    public static final String CHANGE_ADDRESS_EMC = "change_address_emc";
    public static final String SERVER_HOST_EMC = "server_host_emc";
    public static final String SERVER_PORT_EMC = "server_port_emc";
    public static final String SERVER_HOST_BTC = "server_host_btc";
    public static final String SERVER_PORT_BTC = "server_port_btc";
    public static final String SWITCH_SSL_EMC = "switch_ssl_emc";
    public static final String SWITCH_SSL_BTC = "switch_ssl_btc";

    //TODO will be removed
//    public static final String PIN_CODE = "PIN_CODE";
    public static final String SET_PIN_CODE_OR_NOT = "SET_PIN_CODE_OR_NOT";

    //pin
    public static final String PIN_CODE = "PIN_CODE";
    public static final String ENABLE_PIN = "ENABLE_PIN";
    public static final int ENABLE = 0;
    public static final int DISABLE = 1;
    public static final String ENABLE_FINGERPRINT = "ENABLE_FINGERPRINT";
    public static final String ALREADY_SHOWN = "ALREADY_SHOWN";
    public static final String FROM = "FROM";
    public static final int FROM_DASHBOARD = 0;
    public static final int FROM_SETTINGS = 1;

    public static boolean backFromQrOrSharing = false;

    public static final String CURRENT_CURRENCY = "CURRENT_CURRENCY";

    public static final String LAST_FEE_VALUE = "LAST_FEE_VALUE";

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    //rpc constants
    public static final String METHOD_LISTUNSPENT = "blockchain.address.listunspent";
    public static final String METHOD_TX_BROADCAST = "blockchain.transaction.broadcast";
    public static final String METHOD_GET_BALANCE = "blockchain.address.get_balance";
    public static final String METHOD_GET_HISTORY = "blockchain.address.get_history";
    public static final String METHOD_TRANSACTION_GET = "blockchain.transaction.get";
    public static final String METHOD_GET_HEADER = "blockchain.block.get_header";
}