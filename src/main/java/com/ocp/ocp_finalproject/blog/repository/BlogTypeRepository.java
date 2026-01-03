package com.ocp.ocp_finalproject.blog.repository;

import com.ocp.ocp_finalproject.blog.domain.BlogType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogTypeRepository extends JpaRepository<BlogType, Long> {
}
