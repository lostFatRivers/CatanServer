package com.joker.tools.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * 在一个长度为 n 的数组里的所有数字都在 0 到 n-1 的范围内。数组中某些数字是重复的，
 * 但不知道有几个数字是重复的，也不知道每个数字重复几次。请找出数组中任意一个重复的数字。<br/>
 *
 * Input:
 * {2, 3, 1, 0, 2, 5}
 * <br/>
 *
 * Output:
 * 2
 *
 * @author: Joker
 * @date: Created in 2020/10/22 22:07
 * @version: 1.0
 */
public class RepeatNumber extends AbstractAlgorithm {

    public static void main(String[] args) {
        AbstractAlgorithm obj = new RepeatNumber();
        obj.actionMonitor();
    }

    @Override
    protected void doAction() {
        Integer[] arr = {2, 3, 1, 3, 2, 5};
        List<Integer> repeat = find(arr);
        logger.info("find repeat:{}", repeat);
    }

    private List<Integer> find(Integer[] arr) {
        List<Integer> repeat = new ArrayList<>();
        out:
        for (int i = 0; i < arr.length; i++) {
            int value = arr[i];
            while (value != i) {
                if (value == arr[value]) {
                    repeat.add(value);
                    break out;
                }
                swap(arr, i, value);
                value = arr[i];
            }
        }
        return repeat;
    }
}
