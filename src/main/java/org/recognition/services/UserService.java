package org.recognition.services;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.recognition.entity.PasswordResetTokenEntity;
import org.recognition.entity.RoleEntity;
import org.recognition.entity.UserEntity;
import org.recognition.repositories.PasswordResetTokenRepository;
import org.recognition.repositories.RoleRepository;
import org.recognition.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;
    @Override
    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = null;
        try {
             user = userRepository.findByUsername(username);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (user == null) {
            System.out.println("User not found");
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public UserEntity findUserById(Long userId) {
        Optional<UserEntity> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new UserEntity());
    }

    public List<UserEntity> allUsers() {
        return userRepository.findAll();
    }

    public boolean saveUser(UserEntity user) {
        UserEntity userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        RoleEntity role = new RoleEntity(1L, "ROLE_USER");
        if (!roleRepository.findById(1L).isPresent()) {
            roleRepository.save(role);
        }
        user.setRoles(Collections.singleton(role));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public void createPasswordResetTokenForUser(UserEntity user, String token) {
        try {
            PasswordResetTokenEntity myToken = passwordResetTokenRepository.findByUser(user);
            System.out.println(myToken);
            if (myToken != null) {
                System.out.println("delete");
                passwordResetTokenRepository.delete(myToken);
                System.out.println("delete success");
            }
            myToken = new PasswordResetTokenEntity(token, user);
            passwordResetTokenRepository.save(myToken);
        } catch (DataIntegrityViolationException e) {}

    }

    public void changeUserPassword(UserEntity user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    public UserEntity getUserByPasswordResetToken(String token) {
        PasswordResetTokenEntity passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken != null) {
            return passwordResetToken.getUser();
        }else {
            return null;
        }
    }
}
