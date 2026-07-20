package com.sogonsogon.gonggomoon.domain.post.domain;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository {

    Post save(Post newPost);

    Optional<Post> findById(Long postId);

    Optional<Post> findByIdAndCreatedBy(Long postId, Long createdBy);

    Optional<Post> findByPublicIdAndCreatedBy(UUID publicId, Long createdBy);

    List<Post> findAllById(Iterable<Long> ids);

    <S extends Post> List<S> saveAll(Iterable<S> entities);

    void delete(Post post);
}
