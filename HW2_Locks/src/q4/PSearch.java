package q4;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PSearch implements Callable<Integer> {
    // Declare variables or constructors here;
    // however, they will not be accessed by TA's test driver.
	public static ExecutorService threadPool = Executors.newCachedThreadPool();
	
	int[] part;
	int[] indices;
	int target;
	
	public PSearch(int target, int[] part, int[] indices) {
		this.target = target;
		this.part = part;
		this.indices = indices;
	}

	public static int parallelSearch(int x, int[] A, int numThreads) {
        // your search algorithm goes here
		// Divide A into numThreads parts
		int[][] parts = new int[numThreads][];
		int[][] indices = new int[numThreads][];
		int r = A.length % numThreads;
		int idx = 0;
		for (int i = 0; i < numThreads; i++) {
			int n_elems = A.length / numThreads;
			if (i < r) {
				n_elems++;
			}
			parts[i] = new int[n_elems];
			indices[i] = new int[n_elems];
			for (int j = 0; j < n_elems; j++) {
				parts[i][j] = A[idx];
				indices[i][j] = idx;
				idx++;
			}
		}
		
		// Create numThreads threads
		PSearch[] searchers = new PSearch[numThreads];
		ArrayList<Future<Integer>> futures = new ArrayList<Future<Integer>>();
		for (int i = 0; i < numThreads; i++) {
			searchers[i] = new PSearch(x, parts[i], indices[i]);		
			// Have the thread start searching its part
			futures.add(threadPool.submit(searchers[i]));
		}
		
		// Look for the result
		int result = -1;
		for (int i = 0; i < numThreads; i++) {
			try {
				result = futures.get(i).get();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			threadPool.shutdown();
			if (result != -1) { // the target was in this thread's part
				return result;
			}
		}
		return -1; // return -1 if the target is not found
	}

	public Integer call() throws Exception {
        // your algorithm needs to use this method to get results
		for (int i = 0; i < part.length; i++) {
			if (part[i] == target) { return indices[i]; }
		}
		return Integer.valueOf(-1);
	}
}
