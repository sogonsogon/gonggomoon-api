package com.sogonsogon.gonggomoon.domain.post.application;

import com.sogonsogon.gonggomoon.domain.post.dto.response.TavilyExtractResponse;
import com.sogonsogon.gonggomoon.domain.post.error.PostErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import com.sogonsogon.gonggomoon.global.post.TavilyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostContentExtractionService {

    private final TavilyClient tavilyClient;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36";

    private static final Pattern BRACKET_PATTERN = Pattern.compile("\\[(.*?)\\]");

    // H1(#)만 기준으로 블록 분리. ##, ###... 하위 섹션은 같은 블록 안에 남음.
    // "새 공고 시작"은 항상 새 H1이므로, 이 기준으로 쪼개면 다른 공고와 자동으로 경계가 나뉨.
    private static final Pattern H1_SPLIT_PATTERN = Pattern.compile("(?m)(?=^#\\s)");

    /**
     * 최소한의 SSRF 방어: 지원 도메인이 아니면 아예 네트워크 요청을 시도하지 않음.
     * 새 사이트 지원 시 이 목록에만 추가하면 됨.
     * (완전한 화이트리스트/사설 IP 검증은 추후 보강 예정)
     */
    private static final List<String> ALLOWED_DOMAIN_KEYWORDS = List.of("saramin.co.kr");

    public String extract(String url) {
        validateDomain(url);

        String pageTitle = fetchOgTitle(url);

        TavilyExtractResponse response = tavilyClient.extract(url);
        List<TavilyExtractResponse.Result> results = response.results();

        if (results == null || results.isEmpty()) {
            log.warn("Tavily 추출 실패: url={}, failedResults={}", url, response.failedResults());
            throw new BaseException(PostErrorCode.EXTRACTION_FAILED);
        }

        String rawContent = results.get(0).rawContent();
        return filterTargetContent(rawContent, pageTitle);
    }

    private void validateDomain(String url) {
        boolean allowed = url != null
                && ALLOWED_DOMAIN_KEYWORDS.stream().anyMatch(url::contains);

        if (!allowed) {
            log.warn("지원하지 않는 도메인 요청 차단: url={}", url);
            throw new BaseException(PostErrorCode.EXTRACTION_FAILED);
        }
    }

    /** og:title (없으면 <title>)을 가볍게 가져옴. 실패해도 전체 흐름은 계속 진행 (null 반환) */
    private String fetchOgTitle(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(5000)
                    .get();

            String ogTitle = doc.select("meta[property=og:title]").attr("content");
            if (!ogTitle.isBlank()) {
                return ogTitle;
            }
            return doc.title();
        } catch (Exception e) {
            log.warn("og:title 획득 실패 (진행은 계속됨): url={}", url);
            return null;
        }
    }

    /**
     * 원문을 H1(#) 단위 블록으로 쪼갠 뒤, 대상 공고 키워드가 헤딩 라인에 포함된 블록 하나만 반환.
     */
    private String filterTargetContent(String rawContent, String pageTitle) {
        if (rawContent == null || rawContent.isBlank()) {
            return rawContent;
        }

        List<String> keywords = extractKeywords(pageTitle);
        String[] blocks = H1_SPLIT_PATTERN.split(rawContent);

        if (blocks.length <= 1) {
            return rawContent.trim(); // H1 헤딩 자체가 없는 특이 케이스 -> 원본 그대로
        }

        if (!keywords.isEmpty()) {
            for (String block : blocks) {
                if (containsInHeadingLine(block, keywords)) {
                    return block.trim();
                }
            }
            log.warn("제목 키워드와 일치하는 블록을 못 찾음 (구조 변경 가능성). keywords={}", keywords);
        }

        // 제목 매칭 실패 시 fallback: 가장 긴 블록(보통 본문이 제일 김) 하나만 반환
        return Arrays.stream(blocks)
                .max(Comparator.comparingInt(String::length))
                .orElse(rawContent)
                .trim();
    }

    /** 블록의 "첫 줄(헤딩 라인)"에만 키워드가 있는지 확인 (본문 중간 다른 텍스트로 인한 오탐 방지) */
    private boolean containsInHeadingLine(String block, List<String> keywords) {
        String firstLine = block.strip().split("\n", 2)[0];
        return keywords.stream().anyMatch(firstLine::contains);
    }

    /** "[엔에이치엔(주)] [NHN]AI 전환 백엔드 개발" -> ["엔에이치엔(주)", "NHN"] */
    private List<String> extractKeywords(String pageTitle) {
        List<String> keywords = new ArrayList<>();
        if (pageTitle == null) {
            return keywords;
        }
        Matcher m = BRACKET_PATTERN.matcher(pageTitle);
        while (m.find()) {
            String keyword = m.group(1).trim();
            if (!keyword.isBlank()) {
                keywords.add(keyword);
            }
        }
        return keywords;
    }
}

