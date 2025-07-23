package co.za.kasi.enums;

public enum AccountStatus {
    There_is_another_device_logged_in_using_this_account("There is another device logged in using this account."),
    ANOTHER_ACCOUNT_SIGNED_IN("There is another device logged in using this account."),
    FORCE_TO_EXIT("Force_to_exit"),
    EXIT_APP("Exit app");

    AccountStatus(String s) {
    }
}
