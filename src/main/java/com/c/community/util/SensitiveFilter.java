package com.c.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Component
public class SensitiveFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    // root node
    private TrieNode root = new TrieNode();

    // 从配置文件构造树
    @PostConstruct
    public void init() {
        try (
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                addKeyword(keyword);
            }
        } catch (Exception e) {
            LOGGER.error("加载敏感词出错" + e.getMessage());
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本字符串
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) return null;
        char[] chars = text.toCharArray();
        int n = chars.length;
        TrieNode cur = root;
        int start = 0, end = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while (end < n) {
            char c = chars[end];
            // 跳过符号
            if (isSymbol(c)) {
                if (cur == root) {
                    stringBuilder.append(c);
                    start++;
                }
                end++;
                continue;
            }
            cur = cur.getTrieNode(c);
            if (cur == null) {
                // chars[start, end] 非敏感词，移动start
                stringBuilder.append(chars[start]);
                end = ++start;
                cur = root;
            } else if (cur.isEnd) {
                // chars[start, end] 为敏感词
                stringBuilder.append(REPLACEMENT);
                start = ++end;
                cur = root;
            } else {
                // chars[start, end] 可能是敏感词的一部分
                end++;
            }
        }
        // 最后一批字符计入结果
        stringBuilder.append(text.substring(start));
        return stringBuilder.toString();
    }

    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2e80 || c > 0x9fff);
    }


    private void addKeyword(String keyword) {
        char[] chars = keyword.toCharArray();
        TrieNode cur = root;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            TrieNode child = cur.getTrieNode(c);
            if (child == null) {
                child = new TrieNode();
                cur.addChild(c, child);
            }
            cur = child;
            if (i == chars.length - 1) cur.setEnd(true);
        }
    }

    private class TrieNode {

        private boolean isEnd = false;

        private Map<Character, TrieNode> children = new HashMap<>();

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        public TrieNode getTrieNode(Character c) {
            return children.get(c);
        }

        public void addChild(Character c, TrieNode trieNode) {
            children.put(c, trieNode);
        }
    }
}
