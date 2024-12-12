package com.service.productorder.services;

import com.service.productorder.payloads.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    public User getUserByEmail(String email)
    {
        return restTemplate.getForObject("http://localhost:8080/api/user/email/{email}", User.class, email);
    }

}
