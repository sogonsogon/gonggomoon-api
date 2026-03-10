package com.sogonsogon.gonggomoon.domain.strategy.generator;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.strategy.content.ExperienceOrderingItem;
import com.sogonsogon.gonggomoon.domain.strategy.content.ExperienceStrategyPoint;
import com.sogonsogon.gonggomoon.domain.strategy.content.ImprovementGuide;
import com.sogonsogon.gonggomoon.domain.strategy.content.PortfolioStrategyContent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockPortfolioStrategyContentGenerator implements PortfolioStrategyContentGenerator{

    @Override
    public PortfolioStrategyContent generate(List<Experience> experiences, GeneratePortfolioStrategyRequest req) {
        return PortfolioStrategyContent.of(
                "대규모 트래픽 환경에서 안정성과 데이터 기반 의사결정을 설계하는 백엔드 개발자",
                List.of(
                        new ExperienceStrategyPoint (
                                ExperienceType.PROJECT,
                                "대용량 영상 업로드 시스템",
                                "청크 업로드, 재시도, 장애 복구를 중심으로 대용량 파일 처리 안정성을 개선한 경험으로 정리하세요."
                        ),
                        new ExperienceStrategyPoint (
                                ExperienceType.COMPETITION,
                                "리뷰 분석/요약 파이프라인",
                                "리뷰 데이터를 구조화하여 운영 의사결정과 사용자 전환율 개선에 기여한 경험으로 정리하세요."
                        )
                ),
                List.of(
                        new ExperienceOrderingItem(
                                1,
                                "대용량 영상 업로드 시스템",
                                "대규모 트래픽 처리와 안정성 설계 역량을 가장 강하게 보여줄 수 있는 핵심 경험이기 때문입니다."
                        ),
                        new ExperienceOrderingItem(
                                2,
                                "리뷰 분석/요약 파이프라인",
                                "데이터 기반 의사결정과 AI 활용 역량을 후반에 연결해 강점을 확장하기 좋기 때문입니다."
                        )
                ),
                // 강조 키워드
                List.of(
                        "트래픽 대응",
                        "안정성",
                        "장애 복구",
                        "데이터 기반 의사결정"
                ),
                // 강조 역량
                List.of(
                        "대용량 파일 업로드 처리",
                        "장애 대응 및 복구 설계",
                        "AI 파이프라인 연동",
                        "운영 지표 기반 개선"
                ),
                // KPI 체크리스트
                List.of(
                        "업로드 실패율 감소 수치 제시",
                        "장애 복구 시간 단축 사례 제시",
                        "리뷰 분석 결과 활용 지표 명시"
                ),
                // 보완 가이드
                List.of(
                        new ImprovementGuide(
                                "성과 수치 보완",
                                "전후 비교가 가능한 수치(실패율, 처리 시간, 응답 속도 등)를 함께 제시하면 설득력이 높아집니다."
                        ),
                        new ImprovementGuide(
                                "비즈니스 임팩트 연결",
                                "기술 구현 설명에 그치지 말고 사용자 경험 또는 운영 효율 개선과 연결해서 서술하는 것이 좋습니다."
                        )
                )
        );
    }
}
