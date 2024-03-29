package com.ll.medium.domain.member.member.controller;

import com.ll.medium.domain.member.member.dto.MemberCreateForm;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.rq.Rq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/join")
    public String signup(MemberCreateForm memberCreateForm) {
        if (rq.isLogined()) return rq.redirectIfAccessError("/", "이미 회원가입 되어있습니다.");
        return "member/member/signup_form";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "member/member/signup_form";
        }
        if (!memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "패스워드가 일치하지 않습니다.");
            return "member/member/signup_form";
        }
        try {
            memberService.join(memberCreateForm.getUsername(),
                    memberCreateForm.getEmail(), memberCreateForm.getPassword1(), memberCreateForm.getNickname(), memberCreateForm.getProfileImg());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "member/member/signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "member/member/signup_form";
        }
        return rq.redirect("/member/login", "회원가입이 완료 되었습니다.");
    }

    @GetMapping("/login")
    public String login() {
        if (rq.isLogined()) return rq.redirectIfAccessError("/", "이미 로그인 되어있습니다.");
        return "member/member/login_form";
    }
}
