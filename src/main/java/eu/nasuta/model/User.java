package eu.nasuta.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static eu.nasuta.model.Authority.ROLE_USER;

public class User {

    private static final long serialVersionUID = 7954325925563724664L;

    @Id
    private String username;
    private String password;
    private List<Authority> authorities;

    @JsonCreator
    public User(@JsonProperty("username")String username, @JsonProperty("password")String password){
        this.username = username;
        this.password = password;
        this.setAuthorities(Arrays.asList(ROLE_USER));
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
