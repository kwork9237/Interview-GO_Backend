package com.interviewgo.mapper;

import com.interviewgo.dto.WorkNewsDTO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Work24Mapper {
    // 쿼리는 XML(work24Mapper.xml)에서 처리.
    void insertNews(WorkNewsDTO dto);

	List<WorkNewsDTO> selectNewsList();
}