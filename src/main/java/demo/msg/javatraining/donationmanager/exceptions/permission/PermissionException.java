package demo.msg.javatraining.donationmanager.exceptions.permission;

public class PermissionException extends Exception{
    private String type;
    public PermissionException(String message, String type){
        super(message);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
