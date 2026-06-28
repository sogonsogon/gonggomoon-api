package com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain;

public enum PortfolioStrategyGenerateStatus {
    DRAFT, // 경험 선택 대기
    PROCESSING, // 전략 생성 진행 중
    READY, // 전략 생성 완료
    FAILED // 전략 생성 실패
}
