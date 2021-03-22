package com.veracore.userservice.controllers;

import com.veracore.userservice.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    UserRepository userRepository;

    @GetMapping(path = "/check/{userName}")
    public boolean isExist(@PathVariable String userName) {
        return userRepository.existsPaidUserByUserName(userName);
    }
}
