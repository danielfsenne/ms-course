package org.hruser.services;

import org.hruser.entities.User;
import org.hruser.feingclients.UserFeignClient;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserService {

    private static Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserFeignClient userFeignClient;

    public User findByEmail(String email) {
        User user = userFeignClient.findByEmail(email).getBody();
        if (user == null) {
            throw new IllegalArgumentException("Email not found");
        }
        logger.info("Email found" + email);

        return user;
    }
}
