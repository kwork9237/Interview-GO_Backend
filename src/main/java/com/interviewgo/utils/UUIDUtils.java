package com.interviewgo.utils;

import java.util.UUID;

// 면접 세션을 구분하기 위해서 사용함.

public class UUIDUtils {
	// UUID 생성
	public static String GenerateUUID() {
		// 랜덤 UUID 생성
		UUID uuid = UUID.randomUUID();
		
		// UUID를 String 로 변환 후 - 제거
		String replaceUUID = uuid.toString().replaceAll("-", "");
		
		// UUID 결과 반환5
		return replaceUUID;
	}
	
	// UUID 검증
	public static boolean isValid(String uuidStr) {
        if (uuidStr == null || uuidStr.trim().isEmpty()) {
            return false;
        }
        
        try {
        	String formatted = uuidStr.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", 
                "$1-$2-$3-$4-$5"
            );
        	
            UUID.fromString(formatted);
            return true;
        }
    	catch (IllegalArgumentException e) {
            return false;
        }
    }
}
