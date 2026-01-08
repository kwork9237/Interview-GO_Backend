package com.interviewgo.dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data // Getter, Setter, ToString 등을 자동으로 생성해줍니다.
@Alias("worknews")
public class WorkNewsDTO {
    
    private String empSeqno;               // 공개채용공고순번 (DB의 wkseqno로 들어감)
    private String empWantedTitle;         // 채용제목 (DB의 wktitle로 들어감)
    private String empBusiNm;              // 채용업체명 (DB의 wk_busi_nm으로 들어감)
    private String coClcdNm;               // 기업구분명 (DB의 co_clcd_nm으로 들어감)
    private String empWantedEndt;          // 채용종료일자 (DB의 wk_endt로 들어감)
    private String empWantedHomepgDetail;  // 채용사이트URL (DB의 wk_homepg_url로 들어감)
}
