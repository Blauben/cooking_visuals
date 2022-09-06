package recipe.processing;

public class SimilarityComputation {

    public static int similarity(String s1, String s2) {
        String longer = s1.length() > s2.length() ? s1 : s2;
        String shorter = s1.length() > s2.length() ? s2 : s1;
        return calculateTable(shorter, longer);
    }

    private static int calculateTable(String shorter, String longer) {
        int[][] levenshteinTable = new int[longer.length() + 1][shorter.length() + 1];
        for (int i = 0; i < levenshteinTable[0].length; i++) {
            levenshteinTable[0][i] = i;
        }
        for (int j = 0; j < levenshteinTable.length; j++) {
            levenshteinTable[j][0] = j;
        }
        for (int j = 1; j < levenshteinTable.length; j++) {
            for (int i = 1; i < levenshteinTable[0].length; i++) {
                int substitution;
                if (shorter.charAt(i - 1) == longer.charAt(j - 1)) {
                    substitution = 0;
                } else {
                    substitution = 1;
                }
                levenshteinTable[j][i] = Math.min(Math.min(levenshteinTable[j - 1][i] + 1, levenshteinTable[j][i - 1] + 1), levenshteinTable[j - 1][i - 1] + substitution);
            }
        }
        return levenshteinTable[levenshteinTable.length - 1][levenshteinTable[0].length - 1];
    }

}
