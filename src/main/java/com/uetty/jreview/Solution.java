package com.uetty.jreview;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Solution {

    /**
     * 进化
     */
    static void derivation(Set<Integer>[][] distMap, int[][] corridor, int[][] queries) {
        boolean changed = false;
        boolean[] colChanged = null;
        boolean[] colComplete = new boolean[queries.length];
        do {
            changed = false;
            colChanged = new boolean[queries.length];
            for (int i = 0; i < corridor.length; i++) {
                int[] distPair = corridor[i];
                int from = distPair[0];
                int to = distPair[1];

                for (int j = 0; j < queries.length; j++) {
                    if (colComplete[j]) continue;
                    if (distMap[j][from - 1] == null
                        || distMap[j][from - 1].size() == 0) continue;
                    
                    if (distMap[j][to - 1] == null) {
                        distMap[j][to - 1] = new HashSet<Integer>();
                    }
                    Iterator<Integer> itr = distMap[j][from - 1].iterator();
                    while (itr.hasNext()) {
                        int dist = itr.next();
                        dist = dist + distPair[2];
                        dist = dist % queries[j][2];
                        if (dist < 0) dist += queries[j][2];
                        boolean add = distMap[j][to - 1].add(dist);
                        colChanged[j] = colChanged[j] || add;
                        changed = changed || add;
                    }
                    
                }
            }
            
            for (int j = 0; j < queries.length; j++) {
                if (colComplete[j]) continue;
                if (distMap[j][queries[j][1] - 1] != null
                		&& distMap[j][queries[j][1] - 1].contains(queries[j][2] - 1)) {
                    colComplete[j] = true;
                    continue;
                }
                if (!colChanged[j]) {
                    colComplete[j] = true;
                    continue;
                }
            }

        } while (changed);
    }

    @SuppressWarnings("unchecked")
    static int[] calculateMaxMod(int[][] corridor, int max, int[][] queries) {
        Set<Integer>[][] distMap = new Set[queries.length][];
        for (int j = 0; j < queries.length; j++) {
            distMap[j] = new Set[max];
            int from = queries[j][0];
            Set<Integer> set = new HashSet<>();
            set.add(0);
            distMap[j][from - 1] = set;
        }

        derivation(distMap, corridor, queries);

        int[] maxMods = new int[queries.length];
        for (int j = 0; j < distMap.length; j++) {
            Iterator<Integer> itr = distMap[j][queries[j][1] - 1].iterator();
            while (itr.hasNext()) {
                int mod = itr.next() % queries[j][2];
                mod = mod < 0 ? mod + queries[j][2] : mod;
                if (mod > maxMods[j]) maxMods[j] = mod;
            }
        }

        return maxMods;
    }



    /*
     * Complete the longestModPath function below.
     */
    static int[] longestModPath(int[][] corridor, int[][] queries) {
        /*
         * Write your code here.
         */
        int[][] newCorrdor = new int[corridor.length * 2][];
        int max = 0;
        for (int i = 0; i < corridor.length; i++) {
            int from = corridor[i][0];
            int to = corridor[i][1];
            int dist = corridor[i][2];
            max = max > from ? max : from;
            max = max > to ? max : to;
            newCorrdor[i * 2] = new int[3];
            newCorrdor[i * 2][0] = from;
            newCorrdor[i * 2][1] = to;
            newCorrdor[i * 2][2] = dist;
            newCorrdor[i * 2 + 1] = new int[3];
            newCorrdor[i * 2 + 1][0] = to;
            newCorrdor[i * 2 + 1][1] = from;
            newCorrdor[i * 2 + 1][2] = -dist;
        }

        return calculateMaxMod(newCorrdor, max, queries);
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        int n = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])*");

        int[][] corridor = new int[n][3];

        for (int corridorRowItr = 0; corridorRowItr < n; corridorRowItr++) {
            String[] corridorRowItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])*");

            for (int corridorColumnItr = 0; corridorColumnItr < 3; corridorColumnItr++) {
                int corridorItem = Integer.parseInt(corridorRowItems[corridorColumnItr]);
                corridor[corridorRowItr][corridorColumnItr] = corridorItem;
            }
        }

        int q = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])*");

        int[][] queries = new int[q][3];

        for (int queriesRowItr = 0; queriesRowItr < q; queriesRowItr++) {
            String[] queriesRowItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])*");

            for (int queriesColumnItr = 0; queriesColumnItr < 3; queriesColumnItr++) {
                int queriesItem = Integer.parseInt(queriesRowItems[queriesColumnItr]);
                queries[queriesRowItr][queriesColumnItr] = queriesItem;
            }
        }

        int[] result = longestModPath(corridor, queries);

        for (int resultItr = 0; resultItr < result.length; resultItr++) {
            System.out.println(result[resultItr]);
        }

        scanner.close();
    }
}


