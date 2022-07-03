public enum ErrorType {
    // Undefined Variable
    UNDEF_VAR(1, "Undefined variable: %s."),
    // Undefined Function
    UNDEF_FUNC(2, "Undefined function: %s."),
    // Redefined Variable
    REDEF_VAR(3, "Redefined variable: %s."),
    // Redefined Function
    REDEF_FUNC(4, "Redefined function: %s."),
    // Mismatched Types Assigned
    MISMATCH_ASSIGN(5, "Type mismatched for assignment."),
    // Mismatched Types for Operands.
    MISMATCH_OPRAND(6, "Type mismatched for operands."),
    // Mismatched Return Type
    MISMATCH_RETURN(7, "Type mismatched for return."),
    // Mismatched Parameters for Called Function
    MISMATCH_PARAM(8, "Function is not applicable for arguments."),
    // Calling Non-Array
    NON_ARRAY(9, "Not an array: %s."),
    // Calling Non-Function
    NON_FUNC(10, "Not a function: %s.");

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