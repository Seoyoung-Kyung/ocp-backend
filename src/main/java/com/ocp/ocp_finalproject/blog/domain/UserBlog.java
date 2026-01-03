package com.ocp.ocp_finalproject.blog.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_blog")
public class UserBlog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_blog_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_type_id")
    private BlogType blogType;

    @Column(name = "account_id", length = 100)
    private String accountId;

    @Column(name = "account_pw", length = 255)
    private String accountPassword;

    @Column(name = "blog_url", length = 500)
    private String blogUrl;

    public static UserBlog create(BlogType blogType, String accountId, String accountPassword, String blogUrl) {
        UserBlog userBlog = new UserBlog();
        userBlog.blogType = blogType;
        userBlog.accountId = accountId;
        userBlog.accountPassword = accountPassword;
        userBlog.blogUrl = blogUrl;
        return userBlog;
    }

    public void updateCredentials(String accountId, String accountPassword) {
        this.accountId = accountId;
        this.accountPassword = accountPassword;
    }

    public void updateBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

}
