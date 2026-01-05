package com.sliit.healthins.service;

import com.sliit.healthins.dto.PasswordResetConfirmDTO;
import com.sliit.healthins.dto.PasswordResetRequestDTO;
import com.sliit.healthins.model.PasswordResetToken;
import com.sliit.healthins.model.User;
import com.sliit.healthins.repository.PasswordResetTokenRepository;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.util.EmailSenderUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailSenderUtil emailSenderUtil;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                EmailSenderUtil emailSenderUtil,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailSenderUtil = emailSenderUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createResetToken(PasswordResetRequestDTO dto) {
        Optional<User> optionalUser = userRepository.findAll()
                .stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(dto.getEmail()))
                .findFirst();

        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();
        String tokenValue = UUID.randomUUID().toString();

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        tokenRepository.save(token);

        String link = "http://localhost:8080/reset-password.html?token=" + tokenValue;
        String body = "You requested a password reset for your HealthInsure account.\n\n" +
                "Click the link below to set a new password (valid for 1 hour):\n" +
                link + "\n\n" +
                "If you did not request this, you can safely ignore this email.";
        emailSenderUtil.sendEmail(user.getEmail(), "Password Reset Request", body);
    }

    @Transactional
    public boolean resetPassword(PasswordResetConfirmDTO dto) {
        PasswordResetToken token = tokenRepository.findByToken(dto.getToken())
                .orElse(null);
        if (token == null || token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        return true;
    }
}


