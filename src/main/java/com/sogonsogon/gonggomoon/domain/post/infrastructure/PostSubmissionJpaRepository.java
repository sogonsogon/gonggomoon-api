package com.sogonsogon.gonggomoon.domain.post.infrastructure;

import com.sogonsogon.gonggomoon.domain.post.domain.PostSubmission;
import com.sogonsogon.gonggomoon.domain.post.domain.PostSubmissionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostSubmissionJpaRepository extends JpaRepository<PostSubmission, Long>, PostSubmissionRepository {
}
