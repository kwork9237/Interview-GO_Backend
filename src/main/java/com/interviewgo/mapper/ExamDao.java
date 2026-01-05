package com.interviewgo.mapper;

import com.interviewgo.mapper.ExamModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ExamDao {

    @Select("""
        SELECT
            ex_uid         AS exUid,
            ex_lang_uid    AS exLangUid,
            ex_title       AS exTitle,
            ex_content     AS exContent,
            ex_level       AS exLevel,
            ex_answer_list AS exAnswerList,
            view_count     AS viewCount
        FROM exam
        WHERE ex_uid IS NOT NULL
        ORDER BY ex_uid DESC
    """)
    List<ExamModel> findAll();

    @Select("""
        SELECT
            ex_uid         AS exUid,
            ex_lang_uid    AS exLangUid,
            ex_title       AS exTitle,
            ex_content     AS exContent,
            ex_level       AS exLevel,
            ex_answer_list AS exAnswerList,
            view_count     AS viewCount
        FROM exam
        WHERE ex_lang_uid = #{langId}
        ORDER BY ex_uid DESC
    """)
    List<ExamModel> findByLanguage(int langId);

    @Select("""
        SELECT
            ex_uid         AS exUid,
            ex_lang_uid    AS exLangUid,
            ex_title       AS exTitle,
            ex_content     AS exContent,
            ex_level       AS exLevel,
            ex_answer_list AS exAnswerList,
            view_count     AS viewCount
        FROM exam
        WHERE ex_uid = #{exUid}
    """)
    ExamModel findById(int exUid);

    @Update("""
        UPDATE exam
        SET view_count = view_count + 1
        WHERE ex_uid = #{exUid}
    """)
    void increaseViewCount(int exUid);
}
