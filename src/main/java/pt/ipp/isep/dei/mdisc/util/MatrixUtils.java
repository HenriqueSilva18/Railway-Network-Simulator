package pt.ipp.isep.dei.mdisc.util;

import java.util.*;

public class MatrixUtils {

    public static int[][] computeWalksMatrix(int power, Map<String, List<Edge>> graph, List<String> stationOrder) {
        int[][] M = buildAdjacencyMatrix(graph, stationOrder);
        return matrixPower(M, power);
    }

    public static boolean[][] computeTransitiveClosure(Map<String, List<Edge>> graph, List<String> stationOrder) {
        int n = stationOrder.size();
        int[][] base = buildAdjacencyMatrix(graph, stationOrder);
        boolean[][] closure = new boolean[n][n];

        for (int[][] powerMatrix : getPowers(base, n)) {
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (powerMatrix[i][j] > 0)
                        closure[i][j] = true;
        }
        return closure;
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) System.out.print(val + " ");
            System.out.println();
        }
    }

    public static void printMatrix(boolean[][] matrix) {
        for (boolean[] row : matrix) {
            for (boolean val : row) System.out.print((val ? 1 : 0) + " ");
            System.out.println();
        }
    }

    private static int[][] buildAdjacencyMatrix(Map<String, List<Edge>> graph, List<String> order) {
        int n = order.size();
        int[][] M = new int[n][n];
        for (int i = 0; i < n; i++) {
            String from = order.get(i);
            for (Edge edge : graph.getOrDefault(from, List.of())) {
                int j = order.indexOf(edge.to);
                M[i][j] = 1;
            }
        }
        return M;
    }

    private static List<int[][]> getPowers(int[][] M, int maxPower) {
        List<int[][]> powers = new ArrayList<>();
        int[][] current = M;
        powers.add(current);

        for (int p = 2; p <= maxPower; p++) {
            current = multiply(current, M);
            powers.add(current);
        }
        return powers;
    }

    private static int[][] matrixPower(int[][] matrix, int power) {
        int n = matrix.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) result[i][i] = 1;

        while (power > 0) {
            if ((power & 1) == 1) result = multiply(result, matrix);
            matrix = multiply(matrix, matrix);
            power >>= 1;
        }
        return result;
    }

    private static int[][] multiply(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    C[i][j] += A[i][k] * B[k][j];
        return C;
    }
}
