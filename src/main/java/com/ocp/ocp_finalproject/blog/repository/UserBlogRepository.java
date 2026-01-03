package com.ocp.ocp_finalproject.blog.repository;

import com.ocp.ocp_finalproject.blog.domain.BlogType;
import com.ocp.ocp_finalproject.blog.domain.UserBlog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBlogRepository extends JpaRepository<UserBlog, Long> {
    UserBlog findByBlogTypeAndAccountId(BlogType blogType, String accountId);
}