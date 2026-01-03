package com.ocp.ocp_finalproject.siterequest.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import com.ocp.ocp_finalproject.siterequest.enums.SiteRequestState;
import com.ocp.ocp_finalproject.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "site_request",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_site_request_url",
                        columnNames = {"site_url"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "site_url", length = 255, nullable = false)
    private String siteUrl;

    @Column(name = "site_name", length = 20, nullable = false)
    private String siteName;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private SiteRequestState state;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 정적 팩토리 메서드
    public static SiteRequest create(
            User user,
            String siteUrl,
            String siteName,
            String description
    ){
        SiteRequest request = new SiteRequest();
        request.user = user;
        request.siteUrl = siteUrl;
        request.siteName = siteName;
        request.description = description;
        request.state = SiteRequestState.RECEIVED;
        return request;
    }

    // 비즈니스 메서드
    public void approve() {
        this.state = SiteRequestState.APPROVED;
    }

    public void reject() {
        this.state = SiteRequestState.REJECTED;
    }

}
