package com.sogonsogon.gonggomoon.domain.file.domain;

public enum FileAssetStatus {
    UPLOADED, // 파일 업로드가 정상적으로 완료된 상태
    FAILED, // 파일 업로드 또는 저장 처리에 실패
    DELETED // 파일이 삭제된 상태
}
