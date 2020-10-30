package com.joker.tools.hutool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.dfa.WordTree;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Hutool 库测试字符串屏蔽;
 *
 * @author: Joker
 * @date: Created in 2020/10/21 19:04
 * @version: 1.0
 */
public class HutoolStringMatch {
    private static final Logger logger = LoggerFactory.getLogger("Match");

    public static void main(String[] args) {
        filterWordsConfigMatch();
    }

    private static void matchWordTest() {
        List<String> list = new ArrayList<>();
        list.add("安倍晋三");
        list.add("安=倍/晋*三");
        list.add("安=21倍/33晋*三");
        list.add("1安倍晋三");
        list.add("1安2倍晋三");
        list.add("安倍晋2三221");
        list.add("安()倍++-晋@三");
        list.add("安不大小倍晋三");
        list.add("安不大倍晋三");
        list.add("安=倍/op晋*三");

        wordTreeTest(list);
        deepWordTreeTest(list);
    }

    private static void wordTreeTest(List<String> list) {
        WordTree wordTree = new WordTree();
        wordTree.addWord("安倍晋三");
        wordTree.addWord("伊藤润二");
        wordTree.addWord("特朗普");

        list.forEach(str -> logger.info("wordTree 测试结果 [{}] 是否屏蔽:{}", str, wordTree.matchAll(str, 1, false, false).size() > 0));
    }

    private static void deepWordTreeTest(List<String> list) {
        DeepWordTree deepWordTree = new DeepWordTree();
        deepWordTree.setMatchDeep(2);
        deepWordTree.addWord("安倍晋三");
        deepWordTree.addWord("伊藤润二");
        deepWordTree.addWord("特朗普");

//        list.forEach(str -> logger.info("deepWordTree 测试结果 [{}] 是否屏蔽:{}", str, deepWordTree.matchAll(str, 1, false, false).size() > 0));

        long startTime = System.currentTimeMillis();
        for (String eachStr : list) {
            deepWordTree.matchAll(eachStr, 1, true, true);
        }
        logger.info("all cost time:{}", System.currentTimeMillis() - startTime);
    }

    private static List<String> loadFilterWords() {
        List<String> list = new ArrayList<>();
        ExcelReader reader = ExcelUtil.getReader("F:/workspace/CellsAtWork/trunk/src/Tables/屏蔽字库.xlsx", 1);
        for (int i = 5; i < reader.getRowCount(); i++) {
            List<Object> row = reader.readRow(i);
            if (row.size() < 2) {
                logger.info("row size error:{}", row);
                continue;
            }
            list.add(row.get(1).toString());
        }
        return list;
    }

    private static void filterWordsConfigMatch() {
        DeepWordTree deepWordTree = new DeepWordTree();
        List<String> words = loadFilterWords();
        for (String eachWord : words) {
            deepWordTree.addWord(eachWord);
        }
        deepWordTree.setMatchDeep(1);
        for (int i = 0; i < 10; i++) {
            long startTime = System.nanoTime();
            List<String> matchList = deepWordTree.matchAll("放大搞吸行毒少服都是发都是爱思服的服都是答复的撒");
            logger.info("match list:{}, cost time:{}", matchList, System.nanoTime() - startTime);
        }

        WordTree wordTree = new WordTree();
        for (String eachWord : words) {
            wordTree.addWord(eachWord);
        }

        for (int i = 0; i < 10; i++) {
            long startTime = System.nanoTime();
            List<String> matchList = wordTree.matchAll("放大搞吸行毒少服都是发都是爱思服的服都是答复的撒");
            logger.info("match list:{}, cost time:{}", matchList, System.nanoTime() - startTime);
        }
    }
}
