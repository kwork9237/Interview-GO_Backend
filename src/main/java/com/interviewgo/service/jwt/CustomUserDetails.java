package com.interviewgo.service.jwt;

import java.util.Collection;
import java.util.Collections; // List.of 대신 안정적인 Collections 사용

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.interviewgo.dto.MemberDTO; // MemberDTO import 필수!

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String role;
    
    // 🌟 [핵심 추가] MemberDTO를 받아서 내 필드에 채워넣는 생성자
    public CustomUserDetails(MemberDTO member) {
        this.username = member.getUsername();
        
        // 🚨 여기가 제일 중요합니다! 
        // DTO의 'mb_password'를 Security의 'password'로 매핑
        this.password = member.getMb_password(); 
        
        this.role = member.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // role이 null일 경우 방어 로직 추가
        if (role == null) return Collections.emptyList();
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    // 계정 상태 체크 (무조건 true 반환)
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}