package com.rookies3.genaiquestionapp.problem.repository;

import com.rookies3.genaiquestionapp.problem.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Page<Problem> findByLevel(String level, Pageable pageable);
    Page<Problem> findByProblemType(String problemType, Pageable pageable);
}
