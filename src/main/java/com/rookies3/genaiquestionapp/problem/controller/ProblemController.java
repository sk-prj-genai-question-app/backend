package com.rookies3.genaiquestionapp.problem.controller;

import com.rookies3.genaiquestionapp.problem.controller.dto.ProblemDto;
import com.rookies3.genaiquestionapp.problem.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    public ResponseEntity<ProblemDto.ProblemDetailResponse> addProblem(@Valid @RequestBody ProblemDto.ProblemSaveRequest request) {
        ProblemDto.ProblemDetailResponse responseDto = problemService.addProblem(request);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemDto.ProblemDetailResponse> getProblemById(@PathVariable Long id) {
        ProblemDto.ProblemDetailResponse responseDto = problemService.getProblemById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<ProblemDto.ProblemPageResponse> getProblems(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String problemType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        ProblemDto.ProblemPageResponse responseDto;

        if (level != null && problemType != null) {
            // TODO: Implement combined search if needed
            return ResponseEntity.badRequest().build(); // Or throw an exception
        } else if (level != null) {
            responseDto = problemService.getProblemsByLevel(level, pageable);
        } else if (problemType != null) {
            responseDto = problemService.getProblemsByProblemType(problemType, pageable);
        } else {
            responseDto = problemService.getAllProblems(pageable);
        }
        return ResponseEntity.ok(responseDto);
    }
}
