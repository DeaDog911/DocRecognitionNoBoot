package org.recognition.services;

import org.recognition.entity.PasswordResetTokenEntity;
import org.recognition.repositories.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class SecurityService {
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;
    public String validatePasswordResetToken(String token) {
        final PasswordResetTokenEntity passToken = passwordResetTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetTokenEntity passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetTokenEntity passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
