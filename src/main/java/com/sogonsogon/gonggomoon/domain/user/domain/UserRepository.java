package com.sogonsogon.gonggomoon.domain.user.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
        User save(User user);

        Optional<User> findByEmail(String email);

        Optional<User> findById(Long id);

        Optional<User> findByPublicId(UUID publicId);

        void delete(User user);
}
