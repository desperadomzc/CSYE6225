package com.me.controller;

import com.me.dao.UserDAO;
import com.me.pojo.User;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.passay.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

@Controller
@RequestMapping("/v1/**")
public class UserController {

    @RequestMapping(value = "/v1/user", method = RequestMethod.POST, headers = "Accept=application/json")
    public void createUser(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
        String data = "";
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        data = builder.toString();
        JSONObject js = new JSONObject(data);
        String fn = (String) js.get("first_name");
        String ln = (String) js.get("last_name");
        String password = (String) js.get("password");
        String email = (String) js.get("email_address");

        if (fn == null || ln == null || password == null || email == null) {
            response.sendError(400, "Bad Request");
        } else {
            //validate username:
            String email_reg = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
            if(!email.matches(email_reg)){
                response.getWriter().write("Invalid username(must be in email format)");
                response.setStatus(400);
                return;
            }

            //validate password:
            PasswordValidator pv = new PasswordValidator(new LengthRule(8,10),
                    new CharacterRule(EnglishCharacterData.UpperCase,1),
                    new CharacterRule(EnglishCharacterData.LowerCase,1),
                    new CharacterRule(EnglishCharacterData.Digit),
                    new CharacterRule(EnglishCharacterData.Special));
            PasswordData pd = new PasswordData(password);
            RuleResult res = pv.validate(pd);
            if(!res.isValid()){
                response.getWriter().write("Invalid Password(must contain 1 Uppercase ,1 Lower case, digits and special characters)");
                response.setStatus(400);
                return;
            }

            //create user:
            UserDAO userDAO = new UserDAO();
            User u = userDAO.createUser(fn, ln, password, email);
            if (u != null) {
                response.setStatus(201);
                JSONObject json = new JSONObject();
                json.put("id", u.getId());
                json.put("first_name", u.getFirst_name());
                json.put("last_name", u.getLast_name());
                json.put("email_address", u.getEmail_address());
                json.put("account_created", u.getAccount_created());
                json.put("account_updated", u.getAccount_updated());
                response.setContentType("application/json");
                response.getWriter().write(json.toString());
            } else {
                response.setStatus(400);
            }
        }
    }

    @RequestMapping(value = "/v1/user/self", method = RequestMethod.GET, headers = "Accept=application/json")
    public void getUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException, Base64DecodingException, JSONException {
        String tokens = request.getHeader("Authorization");
        if (tokens != null && tokens.startsWith("Basic")) {
            String base64credentials = tokens.substring(5).trim();
            byte[] cred = Base64.decode(base64credentials);
            String credentials = new String(cred);
            String[] values = credentials.split(":", 2);//username, password
            User u = new UserDAO().getUser(values[0], values[1]);
            if (u != null) {
                response.setStatus(200);
                response.setContentType("application/json");
                JSONObject json = new JSONObject();
                json.put("id", u.getId());
                json.put("first_name", u.getFirst_name());
                json.put("last_name", u.getLast_name());
                json.put("email_address", u.getEmail_address());
                json.put("account_created", u.getAccount_created());
                json.put("account_updated", u.getAccount_updated());
                response.setContentType("application/json");
                response.getWriter().write(json.toString());
                return;
            }
        }
        response.sendError(400, "Unauthorized");
        return;
    }

    @RequestMapping(value = "/v1/user/self", method = RequestMethod.PUT, headers = "Accept=application/json")
    public void updateUserInfo(HttpServletRequest request, HttpServletResponse response) throws Base64DecodingException, IOException, JSONException {

        String data = "";
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        data = builder.toString();
        JSONObject js = new JSONObject(data);

        String tokens = request.getHeader("Authorization");
        if (tokens != null && tokens.startsWith("Basic")) {
            String base64credentials = tokens.substring(5).trim();
            byte[] cred = Base64.decode(base64credentials);
            String credentials = new String(cred);
            String[] values = credentials.split(":", 2);//username, password
            UserDAO udao = new UserDAO();
            User u = udao.getUser(values[0], values[1]);
            if (u != null) {
                String fn = (String) js.get("first_name");
                String ln = (String) js.get("last_name");
                String password = (String) js.get("password");
                Iterator<String> it = js.keys();
                while (it.hasNext()) {
                    String k = it.next();
                    if (!k.equals("first_name") && !k.equals("last_name") && !k.equals("password")) {
                        response.sendError(400);
                        return;
                    }
                }
                if (udao.updateUser(fn, ln, values[0], password)) {
                    response.setStatus(204);
                    return;
                }
            }
        }
        response.sendError(400, "Unauthorized");
        return;
    }
}
