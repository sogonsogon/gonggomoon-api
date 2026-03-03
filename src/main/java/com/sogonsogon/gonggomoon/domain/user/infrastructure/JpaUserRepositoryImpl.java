package com.sogonsogon.gonggomoon.domain.user.infrastructure;

import com.sogonsogon.gonggomoon.domain.user.domain.User;
import com.sogonsogon.gonggomoon.domain.user.domain.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepositoryImpl extends JpaRepository<User, Long>, UserRepository {
}
