package com.ll.medium.domain.user.user.controller;

import com.ll.medium.domain.user.user.dto.UserCreateForm;
import com.ll.medium.domain.user.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class UserController {

    private final UserService userService;

    @GetMapping("/join")
    public String signup(UserCreateForm userCreateForm){
        return "user/user/signup_form";
    }

    @PostMapping("/join")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "user/user/signup_form";
        }
        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2","passwordInCorrect",
                    "패스워드가 일치하지 않습니다.");
            return "user/user/signup_form";
        }
        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "user/user/signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "user/user/signup_form";
        }
        return "redirect:/";
    }

}
