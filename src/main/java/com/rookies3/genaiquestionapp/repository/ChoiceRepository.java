package com.rookies3.genaiquestionapp.repository;

import com.rookies3.genaiquestionapp.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findByProblem_Id(Long problemId);
}
