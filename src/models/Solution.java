package models;

public class Solution {
    private final int cost;
    private final int[] sequence;
    private final int[][] result;
    private final int[][] ktns;
    private final int[] switches;

    public Solution(int cost, int[] sequence, int[][] result, int[][] ktns, int[] switches) {
        this.cost = cost;
        this.sequence = sequence;
        this.result = result;
        this.ktns = ktns;
        this.switches = switches;
    }

    public int getCost() {
        return cost;
    }

    public int[] getSwitches() {
        return switches;
    }

    public int[] getSequence() {
        return sequence;
    }

    public int[][] getResult() {
        return result;
    }

    public int[][] getKtns() {
        return ktns;
    }
}
