// 작동처리를 위해 주석처리
/*
 * package com.interviewgo.service.jwt;
 * 
 * import org.springframework.security.core.userdetails.UserDetails; import
 * org.springframework.security.core.userdetails.UserDetailsService; import
 * org.springframework.security.core.userdetails.UsernameNotFoundException;
 * import org.springframework.stereotype.Service;
 * 
 * import com.interviewgo.dto.MemberDTO; import
 * com.interviewgo.mapper.MemberMapper;
 * 
 * import lombok.RequiredArgsConstructor;
 * 
 * @Service
 * 
 * @RequiredArgsConstructor public class CustomUserDetailsService implements
 * UserDetailsService {
 * 
 * private final MemberMapper mbMapper;
 * 
 * @Override public UserDetails loadUserByUsername(String username) throws
 * UsernameNotFoundException { // 임시 MemberDTO member =
 * mbMapper.getMember(username);
 * 
 * if (member == null) { throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: "
 * + username); }
 * 
 * return new CustomUserDetails( // member.getMb_uid(), member.getUsername(),
 * member.getMb_password(), member.getRole() ); } }
 */