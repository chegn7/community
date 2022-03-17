import org.apache.commons.lang3.CharUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalTest {
    @Test
    public void test2() {
        Map map = new HashMap();
        for (Object key : map.keySet()) {
            System.out.println(map.get(key));
        }
    }
    @Test
    public void test1() {
        String fileName = "asfjdlwe.jpg.png";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }

    @Test
    public void testFilter() {
        List<String> sensitiveWords = new ArrayList<>();
        sensitiveWords.add("wa");
        sensitiveWords.add("abc");
        sensitiveWords.add("oe");
        TrieNode root = new TrieNode();
        for (String sensitiveWord : sensitiveWords) {
            addWord(sensitiveWord, root);
        }
        String text = "fjaoew/asdfjowed***efaewa";
        System.out.println(text);
        System.out.println(filterWay1(text, root));
    }

    String filterWay1(String text, TrieNode root) {
        char[] chars = text.toCharArray();
        int n = chars.length;
        TrieNode curNode = root;
        StringBuilder stringBuilder = new StringBuilder();
        int begin = 0, end = 0;
        while (end < n) {
            char c = chars[end];
            // 如果是特殊字符，要跳过统计
            if (isSymbol(c)) {
                // 如果这时候curNode指向的是root，说明还没有检测到违禁字符串
                if (curNode == root) {
                    stringBuilder.append(c);
                    begin++;
                }
                end++;
                continue;
            }
            curNode = curNode.children.get(c);
            if (curNode == null) {
                curNode = root;
                stringBuilder.append(chars[begin]);
                end = ++begin;
            } else if (curNode.isEnd) {
                curNode = root;
                for (int i = 0; i < end + 1 - begin; i++) {
                    stringBuilder.append('*');
                }
                begin = ++end;
            } else {
                ++end;
            }
        }
        stringBuilder.append(text.substring(begin, end));
        return stringBuilder.toString();
    }

    boolean isSymbol(char c) {
        return !Character.isLetter(c);
    }

    void addWord(String word, TrieNode root) {
        int n = word.length();
        for (int i = 0; i < n; i++) {
            Character c = word.charAt(i);
            TrieNode child = root.children.get(c);
            if (child == null) {
                child = new TrieNode();
                root.addChild(c, child);
            }
            root = child;
            if (i == n - 1) root.isEnd = true;
        }
    }

    class TrieNode {

        boolean isEnd;
        Map<Character, TrieNode> children = new HashMap<>();

        void addChild(Character c, TrieNode child) {
            children.put(c, child);
        }
    }
}
