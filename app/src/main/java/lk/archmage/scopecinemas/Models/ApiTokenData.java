package lk.archmage.scopecinemas.Models;

public class ApiTokenData {

    private String accessToken;
    private String scope;
    private String tokenType;
    private String expiresIn;
    private Long firstRequestTime;

    public ApiTokenData(String accessToken, String scope, String tokenType, String expiresIn, Long firstRequestTime) {
        this.accessToken = accessToken;
        this.scope = scope;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.firstRequestTime = firstRequestTime;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }


    public Long getFirstRequestTime() {
        return firstRequestTime;
    }

    public void setFirstRequestTime(Long firstRequestTime) {
        this.firstRequestTime = firstRequestTime;
    }


}
