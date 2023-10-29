package demo.msg.javatraining.donationmanager.controller.auth;

public class RefreshTokenResponse {
    private String renewedAccessToken;
    public RefreshTokenResponse(String renewedAccessToken){
        this.renewedAccessToken = renewedAccessToken;
    }

    public String getRenewedAccessToken() {
        return renewedAccessToken;
    }

    public void setRenewedAccessToken(String renewedAccessToken) {
        this.renewedAccessToken = renewedAccessToken;
    }
}
