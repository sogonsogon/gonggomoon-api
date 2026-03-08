package com.sogonsogon.gonggomoon.domain.user.application;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.UserPrincipal;
import com.sogonsogon.gonggomoon.domain.user.domain.User;
import com.sogonsogon.gonggomoon.domain.user.domain.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserPrincipal loadUserPrincipalByPublicId(String publicId) {
        UUID uuid = UUID.fromString(publicId);

        User user = userRepository.findByPublicId(uuid)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + publicId));

        return UserPrincipal.create(user);
    }
}
