package com.ping.postservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "report_post")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "reporter_id")
    private Integer reporterId;

    @Column(name = "report_description")
    private String reportDescription;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;
}
