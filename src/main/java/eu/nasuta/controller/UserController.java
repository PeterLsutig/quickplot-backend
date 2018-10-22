package eu.nasuta.controller;

import eu.nasuta.model.User;
import eu.nasuta.repository.UserRepository;
import eu.nasuta.security.service.JsonWebTokenService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JsonWebTokenService tokenService;

    @RequestMapping(method = RequestMethod.POST, value = "/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        userRepository.save(user); //ToDo: check if user already exists
        System.out.println(user.toString());
        System.out.println("new User created: " + user.getUsername());
        JSONObject json = new JSONObject();
        json.put("msg","user "+ user.getUsername() +"successfully created.");
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "auth")
    public ResponseEntity<?> authenticate(@RequestBody User user) {
        String token;
        try {
            token = tokenService.getToken(user.getUsername(), user.getPassword());
        }
        catch (Exception e){
            return new ResponseEntity<>("Authentication failed", HttpStatus.BAD_REQUEST);
        }
        if (token != null) {
            JSONObject json = new JSONObject();
            json.put("token",token);
            return new ResponseEntity<>(json.toJSONString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Authentication failed", HttpStatus.BAD_REQUEST);
        }
    }
}
