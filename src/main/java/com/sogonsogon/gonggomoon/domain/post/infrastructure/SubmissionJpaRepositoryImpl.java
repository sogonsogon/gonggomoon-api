package com.sogonsogon.gonggomoon.domain.post.infrastructure;

import com.sogonsogon.gonggomoon.domain.post.domain.Submission;
import com.sogonsogon.gonggomoon.domain.post.domain.SubmissionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionJpaRepositoryImpl extends JpaRepository<Submission, Long>, SubmissionRepository {
}
