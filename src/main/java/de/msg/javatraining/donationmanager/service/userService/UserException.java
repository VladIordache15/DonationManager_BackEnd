package de.msg.javatraining.donationmanager.service.userService;


public class UserException extends Exception{
    private String errorType;

    public UserException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
