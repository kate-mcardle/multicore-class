package q4;

public class Main {
    public static void main (String[] args) {
    	int x = 9999;
    	int[] A = new int[10000];
    	for (int i = 0; i < A.length; i++) {
    		A[i] = i;
    	}
    	int numThreads = 3;
    	int result = PSearch.parallelSearch(x, A, numThreads);
    	System.out.println("result = " + result);
    }
}