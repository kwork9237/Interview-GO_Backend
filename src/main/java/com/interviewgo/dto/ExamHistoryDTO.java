package com.interviewgo.dto;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("examHistory")
public class ExamHistoryDTO {
	private int hist_uid;
	private Long mb_uid;
	private int ex_uid;
	private int ex_lang_uid;
	private Double hist_score;
	private Date hist_date;
}
