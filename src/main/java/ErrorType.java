public enum ErrorType {
    // Undefined Variable
    UNDEF_VAR(1, "Undefined variable: %s."),
    // Undefined Function
    UNDEF_FUNC(2, "Undefined function: %s."),
    // Redefined Variable
    REDEF_VAR(3, "Redefined variable: %s."),
    // Redefined Function
    REDEF_FUNC(4, "Redefined function: %s.");


    private final int errorNo;
    private final String errorMsg;

    ErrorType(int number, String msg) {
        this.errorNo = number;
        this.errorMsg = msg;
    }

    public int getErrorNo() {
        return this.errorNo;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }
}