package com.rookies3.genaiquestionapp.admin.controller;

import com.rookies3.genaiquestionapp.auth.entity.User;
import com.rookies3.genaiquestionapp.auth.repository.UserRepository;
import com.rookies3.genaiquestionapp.problem.repository.ProblemRepository;
import com.rookies3.genaiquestionapp.record.repository.AnswerRecordRepository;
import com.rookies3.genaiquestionapp.userquestion.repository.UserQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final UserQuestionRepository userQuestionRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/login")
    public String adminLogin() {
        return "admin/login";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users/list";
    }

    @GetMapping("/users/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newUserForm(Model model) {
        model.addAttribute("user", User.builder().build());
        return "admin/users/new";
    }

    @PostMapping("/users/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveNewUser(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUserForm(@PathVariable Long id, Model model) {
        userRepository.findById(id).ifPresent(user -> model.addAttribute("user", user));
        return "admin/users/edit";
    }

    @PostMapping("/users/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        userRepository.findById(id).ifPresent(existingUser -> {
            existingUser.setEmail(user.getEmail());
            existingUser.setIsAdmin(user.getIsAdmin());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.save(existingUser);
        });
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/problems")
    @PreAuthorize("hasRole('ADMIN')")
    public String listProblems(Model model) {
        model.addAttribute("problems", problemRepository.findAll());
        return "admin/problems/list";
    }

    @GetMapping("/problems/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProblem(@PathVariable Long id) {
        problemRepository.deleteById(id);
        return "redirect:/admin/problems";
    }

    @GetMapping("/answer-records")
    @PreAuthorize("hasRole('ADMIN')")
    public String listAnswerRecords(Model model) {
        model.addAttribute("answerRecords", answerRecordRepository.findAll());
        return "admin/answer-records/list";
    }

    @GetMapping("/answer-records/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAnswerRecord(@PathVariable Long id) {
        answerRecordRepository.deleteById(id);
        return "redirect:/admin/answer-records";
    }

    @GetMapping("/user-questions")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUserQuestions(Model model) {
        model.addAttribute("userQuestions", userQuestionRepository.findAll());
        return "admin/user-questions/list";
    }

    @GetMapping("/user-questions/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUserQuestion(@PathVariable Long id) {
        userQuestionRepository.deleteById(id);
        return "redirect:/admin/user-questions";
    }
}
