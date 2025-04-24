package com.example.demo.Service;
import com.example.demo.model.UserRegistration;
import com.example.demo.repository.RegUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private RegUser regUser;

    public UserRegistration create(UserRegistration userReg) {

        return regUser.save(userReg);

    }

    public List<UserRegistration> getAllUsers() {
        return regUser.findAll();
    }
}
