package com.ocp.ocp_finalproject.workflow.repository;

import com.ocp.ocp_finalproject.workflow.domain.RecurrenceRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurrenceRuleRepository extends JpaRepository<RecurrenceRule, Long> {
}