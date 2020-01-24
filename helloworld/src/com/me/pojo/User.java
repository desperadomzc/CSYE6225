package com.me.pojo;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String first_name;
    @Column
    private String last_name;
    @Column
    private String password;
    @Column(unique = true)
    private String email_address;
    @Column
    private String account_created;
    @Column
    private String account_updated;

    public User(){
        this.account_created = new Date().toString();
        this.account_updated = new Date().toString();
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getAccount_created() {
        return account_created;
    }

    public String getAccount_updated() {
        return account_updated;
    }

    public void setAccount_updated(){
        this.account_updated = new Date().toString();
    }
}
