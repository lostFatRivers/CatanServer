package com.joker.tools.algorithm;

/**
 * TODO description
 *
 * @author: Joker
 * @date: Created in 2020/10/22 22:51
 * @version: 1.0
 */
public class TwoDeepArraySearch extends AbstractAlgorithm {

    public static void main(String[] args) {
        AbstractAlgorithm obj = new TwoDeepArraySearch();
        obj.actionMonitor();
    }

    @Override
    protected void doAction() {
        int[][] matrix = {
                {1,   4,  7, 11, 15},
                {2,   5,  8, 12, 19},
                {3,   6,  9, 16, 22},
                {10, 13, 14, 17, 24},
                {18, 21, 23, 26, 30}
            };

        boolean contain = twoDeepArrayContain(matrix, 15);
        logger.info("contain {} is {}", 15, contain);
        boolean contain2 = twoDeepArrayContain(matrix, 25);
        logger.info("contain {} is {}", 25, contain2);
    }

    private boolean twoDeepArrayContain(int[][] matrix, int num) {
        int rowLimit = matrix.length;
        int colLimit = matrix[0].length;
        int row = rowLimit - 1;
        int col = 0;
        while (row >= 0 && col <= colLimit - 1) {
            if (num == matrix[row][col])
                return true;
            if (num < matrix[row][col]) {
                row--;
            } else {
                col++;
            }
        }
        return false;
    }
}
