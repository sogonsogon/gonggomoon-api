package com.sogonsogon.gonggomoon.domain.post.infrastructure;

import com.sogonsogon.gonggomoon.domain.post.domain.Post;
import com.sogonsogon.gonggomoon.domain.post.domain.PostRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostJpaRepository extends JpaRepository<Post, Long>, PostRepository {
}
