package com.ocp.ocp_finalproject.blog.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "blog_type")
public class BlogType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_type_id")
    private Long id;

    @Column(name = "blog_type_name", length = 100)
    private String blogTypeName;

    @Column(name = "blog_base_url", length = 255)
    private String baseUrl;

    @Builder(builderMethodName = "createBuilder")
    public static BlogType create(String blogTypeName, String baseUrl) {
        BlogType blogType = new BlogType();
        blogType.blogTypeName = blogTypeName;
        blogType.baseUrl = baseUrl;
        return blogType;
    }
}