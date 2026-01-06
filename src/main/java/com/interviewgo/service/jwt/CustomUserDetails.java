package com.interviewgo.service.jwt;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.interviewgo.dto.MemberDTO; // MemberDTO import 필수

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor         // 모든 필드를 매개변수로 받는 생성자    
@NoArgsConstructor          // 기본 생성자  
public class CustomUserDetails implements UserDetails {

	private Long mb_Uid;
    private String username;
    private String password;
    private String role;

    // 🌟 [필수 추가] DB 데이터(MemberDTO)를 이 객체로 변환하는 생성자
    // 이 부분이 없으면 로그인할 때 "MemberDTO를 CustomUserDetails로 못 바꿉니다" 에러가 납니다.
    public CustomUserDetails(MemberDTO member) {
    	this.mb_Uid = member.getMb_uid();
        this.username = member.getUsername();
        this.password = member.getMb_password(); // DB의 mb_password를 시큐리티 password로 연결
        this.role = member.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 첫번째 코드의 간결한 스타일 유지 (람다식 활용)
        // role이 null일 경우를 대비해 빈 리스트 반환 처리만 살짝 추가하면 더 안전합니다.
        if (role == null) return List.of();
        return List.of(() -> role); 
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}