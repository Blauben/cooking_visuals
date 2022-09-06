package recipe.processing;

public class SimilarityComputation {

    private static int[][] levenshteinTable;

    public static double similarity(String s1, String s2) {
        String longer = s1.length() > s2.length() ? s1 : s2;
        String shorter = s1.length() > s2.length() ? s2 : s1;
        return 0f;
    }

    private static void calculateTable(String shorter, String longer) {
        levenshteinTable = new int[longer.length() + 1][shorter.length() + 1];
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
    }

    private static int levenshtein(String s1, String s2) {
        if (s1.length() == 0) {
            return s2.length();
        } else if (s2.length() == 0) {
            return s1.length();
        } else if (s1.charAt(0) == s2.charAt(0)) {
            return levenshtein(s1.substring(1), s2.substring(1));
        } else {
            String s1Sub = s1.substring(1);
            String s2Sub = s2.substring(1);
            return 1 + Math.min(levenshtein(s1Sub, s2Sub), Math.min(levenshtein(s1Sub, s2), levenshtein(s1, s2Sub)));
        }
    }

    public static int[][] testCalculateTable(String shorter, String longer) {
        calculateTable(shorter, longer);
        return levenshteinTable;
    }
}
