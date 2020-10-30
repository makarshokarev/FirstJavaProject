package com.itproger.controllers;

import com.itproger.models.Review;
import com.itproger.models.Role;
import com.itproger.models.User;
import com.itproger.repo.ReviewRepository;
import com.itproger.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MainController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    // URL Templates

    //Tracking URL
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("name", "World");
        // This template will be displayed
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Page about us");
        return "about";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        Iterable<Review> reviews = reviewRepository.findAll();
        model.addAttribute("title", "Page with reviews");
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    @PostMapping("/reviews-add")
    public String reviewsAdd(@AuthenticationPrincipal User user, @RequestParam String title, @RequestParam String text, Model model) {
        Review review = new Review(title, text, user);
        reviewRepository.save(review);

        return "redirect:/reviews";
    }

    @GetMapping("/reviews/{id}")
    public String reviewInfo(@PathVariable(value = "id") long review_id, Model model) {
        Optional<Review> review = reviewRepository.findById(review_id);

        ArrayList<Review> result = new ArrayList<>();
        review.ifPresent(result::add);

        model.addAttribute("review", result);
        return "review-info";
    }

    @GetMapping("/reviews/{id}/update")
    public String reviewUpdate(@PathVariable(value = "id") long review_id, Model model) {

        Optional<Review> review = reviewRepository.findById(review_id);

        ArrayList<Review> result = new ArrayList<>();
        review.ifPresent(result::add);

        model.addAttribute("review", result);
        return "review-update";
    }

    @PostMapping("/reviews/{id}/update")
    public String reviewsUpdateForm(@PathVariable(value = "id") long review_id, @RequestParam String title, @RequestParam String text, Model model) throws ClassNotFoundException {
        Review review = reviewRepository.findById(review_id)
                .orElseThrow(() -> new ClassNotFoundException());
        review.setTitle(title);
        review.setText(text);
        reviewRepository.save(review);

        return "redirect:/reviews/" + review_id;
    }

    @PostMapping("reviews/{id}/delete")
    public String reviewsDelete(@PathVariable(value = "id") long review_id, Model model) throws ClassNotFoundException {
        Review review = reviewRepository.findById(review_id)
                .orElseThrow(() -> new ClassNotFoundException());

        reviewRepository.delete(review);
        return "redirect:/reviews";
    }

    @GetMapping("/reg")
    public String reg(){
        return "reg";
    }

    @PostMapping("/reg")
    public String addUser(User user, Model model){
        user.setEnabled(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/login";
    }
}
