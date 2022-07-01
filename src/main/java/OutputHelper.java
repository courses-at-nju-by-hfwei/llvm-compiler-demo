public class OutputHelper {

    public static void printSemanticError(ErrorType errorType, int lineNo) {
        System.err.println("Error type " + errorType.getErrorNo() + " at Line " + lineNo
                + ": " + errorType.getErrorMsg());
    }

    public static void printSemanticError(ErrorType errorType, int lineNo, String errorName) {
        System.err.println("Error type " + errorType.getErrorNo() + " at Line " + lineNo
                + ": " + String.format(errorType.getErrorMsg(), errorName));
    }

}
