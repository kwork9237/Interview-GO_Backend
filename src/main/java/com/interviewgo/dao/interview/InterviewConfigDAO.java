package com.interviewgo.dao.interview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.interviewgo.mapper.interview.InterviewHistoryMapper;

@Service
public class InterviewConfigDAO {
	@Autowired
	private InterviewHistoryMapper mapper;
}


/*



@Service
public class MemberService {

	@Autowired
	private MemberMapper dao;

	public int insert(Member member) {
		return dao.insert(member);
	}

	public Member select(String username) {
		return dao.select(username);
	}

	public int update(Member member) {
		return dao.update(member);
	}

	public int delete(String id) {
		return dao.delete(id);
	}
}


*/