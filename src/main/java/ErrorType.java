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
    // Right-Value Expression Assigned
    NOT_LETF_EXP_ASSIGN(6, "The left-hand side of an assignment must be a variable."),
    // Mismatched Types for Operands.
    MISMATCH_OPRAND(7, "Type mismatched for operands."),
    // Mismatched Return Type
    MISMATCH_RETURN(8, "Type mismatched for return."),
    // Mismatched Parameters for Called Function
    MISMATCH_PARAM(9, "Function is not applicable for arguments."),
    // Calling Non-Array
    NON_ARRAY(10, "Not an array: %s."),
    // Calling Non-Function
    NON_FUNC(11, "Not a function: %s."),
    // Undefined Field
    UNDEF_FIELD(12, "Non-existent field: %s."),
    // Redefined Field
    REDEF_FIELD(13, "Redefined field: %s.");

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