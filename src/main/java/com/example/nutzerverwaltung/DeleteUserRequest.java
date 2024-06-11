package com.example.nutzerverwaltung;

public class DeleteUserRequest {
    private String username;
    private String password;
    private Long targetUserId;


    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public Long getTargetUserId(){
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId){
        this.targetUserId = targetUserId;
    }

}
