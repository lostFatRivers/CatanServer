package com.jokerbee.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 随机数
 * @author magicstone
 */
public class RandomUtil {
	private static final Logger logger = LoggerFactory.getLogger(RandomUtil.class);
	private static final Random random = new Random();
	/**
	 * 控制随机数自生产变量定义
	 */
	private static final int A = 48271;
	private static final int M = 2147483647;
	private static final int Q = M / A;
	private static final int R = M % A;
	private static int State = -1;

	/**
	 * 获取指令范围类的随机数,如果min=0的话。随机范围不包含max:[min,max)。否则随机范围为[min,max]
	 */
	public static int getRandom(int min,int max){
		if(min == max){
			return min;
		}
        int result = random.nextInt(max)%(max-min+1) + min;
        return result;
	}
	
	public static float getRandom(){
		return random.nextFloat();
	}
	
	 /**
	  * 排除已随机到的数 
	  * 
	 * 随机指定范围内N个不重复的数
	 * 在初始化的无重复待选数组中随机产生一个数放入结果中，
	 * 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换
	 * 然后从len-2里随机产生下一个随机数，如此类推
	 * @param max  指定范围最大值
	 * @param min  指定范围最小值
	 * @param n  随机数个数
	 * @return int[] 随机数结果集
	 */
	public static int[] getRandomArray(int min,int max,int n){
		int len = max-min+1;
		if(max < min || n > len){
			logger.error("随机数初始错误:min={},max={},n={}", min, max, n);
			return new int[0];
		}
		//初始化给定范围的待选数组
		int[] source = new int[len];
        for (int i = min; i < min+len; i++){
        	source[i-min] = i;
        }
        int[] result = new int[n];
        int index = 0;
        for (int i = 0; i < result.length; i++) {
        	//待选数组0到(len-2)随机一个下标
            index = Math.abs(random.nextInt() % len--);
            //将随机到的数放入结果集
            result[i] = source[index];
            //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }
        return result;
	}
	
	/**
	 * 随机整数;
	 */
	public static int randInt() {
		if (State < 0) {
			Random random = new Random(System.currentTimeMillis());
			State = random.nextInt();
		}

		int tmpState = A * (State % Q) - R * (State / Q);
		if (tmpState >= 0) {
			State = tmpState;
		} else {
			State = tmpState + M;
		}
		return State;
	}

	
	/**
	 * 限制上限的随机整数;
	 */
	public static int randInt(int max) {
		return randInt() % (max + 1);
	}
	
	/**
	 * 根据权重随机一个元素;
	 * 
	 * @param weight 权重集合;
	 * @param elements 元素集合;
	 * @return 根据权重随机出的元素;
	 */
	public static <T> T weightRand(List<Integer> weight, List<T> elements) {
		if (weight == null || elements == null || weight.size() <= 0 || weight.size() != elements.size()) {
			logger.error("weight random error, weight:{}, elements:{}, weight size:{}, elements size:{}", weight, elements, weight.size(), elements.size());
			return null;
		}
		int total = 0;
		for (int each : weight) {
			total += each;
		}
		int target = getRandom(0, total);
		int sum = 0;
		for (int i = 0; i < weight.size(); i++) {
			if (target >= sum && target < sum + weight.get(i)) {
				return elements.get(i);
			}
			sum += weight.get(i);
		}
		return null;
	}
	
	/**
	 * 前端指定随机数种子生成随机数（闭区间）;
	 */
	public static int getJSSeedCeilRandom(double[] seeds,int max,int min){
		double seed = seeds[0];
		seed = (seed * 9301 + 49297) % 233280.0;
		double rnd =seed / 233280.0f;
		seeds[0] = seed;
		return ((int) Math.floor(min + rnd * (max - min + 1)));
	}
	
	public static List<Integer> getRandomBySeed(int min,int max,int count,long seed){
		List<Integer> list = new ArrayList<>();
		Random r = new Random(seed);
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < 20; j++) {
				Integer num = r.nextInt(max)%(max-min+1) + min;
				if (!list.contains(num)) {
					list.add(num);
					break;
				}
			}
		}
		return list;
	}
	
	public static int getRandomBySeed(int min, int max, long seed){
		Random r = new Random(seed);
		return r.nextInt(max)%(max-min+1) + min;
	}
}
