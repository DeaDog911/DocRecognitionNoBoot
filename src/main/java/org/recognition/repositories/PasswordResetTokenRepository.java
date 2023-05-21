package org.recognition.repositories;

import org.recognition.entity.PasswordResetTokenEntity;
import org.recognition.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    PasswordResetTokenEntity findByToken(String token);
    PasswordResetTokenEntity findByUser(UserEntity user);
}
