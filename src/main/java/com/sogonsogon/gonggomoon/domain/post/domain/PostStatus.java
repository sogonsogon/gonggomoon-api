package com.sogonsogon.gonggomoon.domain.post.domain;

/**
 * PENDING  : 요청 접수 후 AI 워커 처리 대기/진행 중
 * SUCCESS  : 분석 완료, PostAnalysis와 연결됨
 * FAILED   : 추출 실패 또는 AI 워커 처리 실패
 */
public enum PostStatus {
    PENDING,
    SUCCESS,
    FAILED
}
