package com.uetty.jreview;
import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

public class Solution {

    /**
     * 进化
     */
    static void derivation(List<Set<Long>> distMap, int[][] corridor, int toIndex, int m) {
        boolean changed = false;
        long stopMod = m - 1;
        do {
            changed = false;
            for (int i = 0; i < corridor.length; i++) {
                int[] distPair = corridor[i];
                int from = distPair[0];
                int to = distPair[1];

                Set<Long> originSet = distMap.get(from - 1);
                if (originSet.size() == 0) continue;

                Set<Long> set = distMap.get(to - 1);
                Iterator<Long> itr = originSet.iterator();
                while (itr.hasNext()) {
                    long dist = itr.next();
                    dist = dist + distPair[2];
                    dist = dist % m;
                    if (dist < 0) dist += m;
                    boolean add = set.add(dist);
                    changed = changed || add;
                }
            }
            
            Set<Long> set = distMap.get(toIndex - 1);
            if (set.contains(stopMod)) {
                break;
            }

        } while (changed);
    }

    static int calculateMaxMod(int[][] corridor, int max, int from, int to, int m) {
        List<Set<Long>> distMap = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            distMap.add(new HashSet<Long>());
        }
        Set<Long> selfDist = distMap.get(from - 1);
        selfDist.add(0l);

        derivation(distMap, corridor, to, m);

        int maxMod = 0;
        Set<Long> distSet = distMap.get(to - 1);
        Iterator<Long> itr = distSet.iterator();
        while(itr.hasNext()) {
            int mod = (int) (itr.next() % m);
            mod = mod < 0 ? mod + m : mod;
            if (mod > maxMod) maxMod = mod;
        }
        return maxMod;
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

        int[] result = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int from = queries[i][0];
            int to = queries[i][1];
            int m = queries[i][2];
            result[i] = calculateMaxMod(newCorrdor, max, from, to, m);
        }
        return result;
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

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
            bufferedWriter.write(String.valueOf(result[resultItr]));

            if (resultItr != result.length - 1) {
                bufferedWriter.write("\n");
            }
        }

        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }
}

