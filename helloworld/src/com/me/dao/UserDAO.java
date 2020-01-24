package com.me.dao;

import com.me.pojo.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class UserDAO extends DAO{

    public User createUser(String fn, String ln, String pwd, String email){

        begin();
        int x = getSession().createCriteria(User.class).add(Restrictions.eq("email_address",email)).list().size();
        if(x >= 1){
            getSession().close();
            return null;
        }


        User u = new User();
        u.setFirst_name(fn);
        u.setLast_name(ln);
        u.setEmail_address(email);

        String hashed = BCrypt.hashpw(pwd, BCrypt.gensalt());
        u.setPassword(hashed);

        getSession().save(u);

        commit();
        close();

        return u;
    }

    public User getUser(String username, String password) {
        begin();

        Criteria criteria = getSession().createCriteria(User.class);

        criteria.add(Restrictions.eq("email_address",username));
        User u = (User)criteria.uniqueResult();

        if(u == null) return null;

        boolean match = BCrypt.checkpw(password,u.getPassword());

        commit();
        close();

        if(match){
            return u;
        }else{
            return null;
        }

    }

    public boolean updateUser(String fn,String ln,String email_address,String password){
        begin();

        Criteria criteria = getSession().createCriteria(User.class);
        criteria.add(Restrictions.eq("email_address",email_address));

        User u = (User)criteria.uniqueResult();

        boolean success = false;
        if(u != null){
            if(password != null){
                String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                u.setPassword(hashed);
            }
            if(fn != null){
                u.setFirst_name(fn);
            }
            if(ln != null){
                u.setLast_name(ln);
            }
            u.setAccount_updated();
            getSession().update(u);
            success = true;
        }

        commit();
        close();

        return success;
    }

}
