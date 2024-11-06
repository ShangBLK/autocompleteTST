
import stdlib.*;
//import algs13.Queue;
/* ***********************************************************************
 *  Compilation:  javac TST.java
 *  Execution:    java TST < words.txt
 *  Dependencies: StdIn.java
 *
 *  Symbol table with string keys, implemented using a ternary search
 *  trie (TST).
 *
 *
 *  % java TST < shellsST.txt
 *  by 4
 *  sea 6
 *  sells 1
 *  she 0
 *  shells 3
 *  shore 7
 *  the 5

 *
 *  % java TST
 *  theory the now is the time for all good men

 *  Remarks
 *  --------
 *    - can't use a key that is the empty string ""
 *
 *************************************************************************/

public class TST<V> {
	private int N;       // size
	private Node<V> root;   // root of TST

	private static class Node<V> {
		public Node() { }
		public char c;                 // character
		public Node<V> left, mid, right;  // left, middle, and right subtries
		public V val;              // value associated with string
	}

	// return number of key-value pairs
	public int size() {
		return N;
	}

	/* ************************************************************
	 * Is string key in the symbol table?
	 **************************************************************/
	public boolean contains(String key) {
		return get(key) != null;
	}

	public V get(String key) {
		if (key == null || key.length() == 0) throw new Error("illegal key");
		Node<V> x = get(root, key, 0);
		if (x == null) return null;
		return x.val;
	}

	// return subtrie corresponding to given key
	private Node<V> get(Node<V> x, String key, int d) {
		if (key == null || key.length() == 0) throw new Error("illegal key");
		if (x == null) return null;
		char c = key.charAt(d);
		if      (c < x.c)              return get(x.left,  key, d);
		else if (c > x.c)              return get(x.right, key, d);
		else if (d < key.length() - 1) return get(x.mid,   key, d+1);
		else                           return x;
	}


	/* ************************************************************
	 * Insert string s into the symbol table.
	 **************************************************************/
	public void put(String s, V val) {
		if (!contains(s)) N++;
		root = put(root, s, val, 0);
	}

	private Node<V> put(Node<V> x, String s, V val, int d) {
		char c = s.charAt(d);
		if (x == null) {
			x = new Node<>();
			x.c = c;
		}
		if      (c < x.c)             x.left  = put(x.left,  s, val, d);
		else if (c > x.c)             x.right = put(x.right, s, val, d);
		else if (d < s.length() - 1)  x.mid   = put(x.mid,   s, val, d+1);
		else                          x.val   = val;
		return x;
	}


	/* ************************************************************
	 * Find and return longest prefix of s in TST
	 **************************************************************/
	public String longestPrefixOf(String s) {
		if (s == null || s.length() == 0) return null;
		int length = 0;
		Node<V> x = root;
		int i = 0;
		while (x != null && i < s.length()) {
			char c = s.charAt(i);
			if      (c < x.c) x = x.left;
			else if (c > x.c) x = x.right;
			else {
				i++;
				if (x.val != null) length = i;
				x = x.mid;
			}
		}
		return s.substring(0, length);
	}

	// all keys in symbol table
	public Iterable<String> keys() {
		Queue<String> queue = new Queue<>();
		collect(root, "", queue);
		return queue;
	}

	// all keys starting with given prefix
	public Iterable<String> prefixMatch(String prefix) {
		Queue<String> queue = new Queue<>();
		Node<V> x = get(root, prefix, 0);
		if (x == null) return queue;
		if (x.val != null) queue.enqueue(prefix);
		collect(x.mid, prefix, queue);
		return queue;
	}

	// all keys in subtrie rooted at x with given prefix
	private void collect(Node<V> x, String prefix, Queue<String> queue) {
		if (x == null) return;
		collect(x.left,  prefix,       queue);
		if (x.val != null) queue.enqueue(prefix + x.c);
		collect(x.mid,   prefix + x.c, queue);
		collect(x.right, prefix,       queue);
	}


	// return all keys matching given wilcard pattern
	public Iterable<String> wildcardMatch(String pat) {
		Queue<String> queue = new Queue<>();
		collect(root, "", 0, pat, queue);
		return queue;
	}

	private void collect(Node<V> x, String prefix, int i, String pat, Queue<String> q) {
		if (x == null) return;
		char c = pat.charAt(i);
		if (c == '.' || c < x.c) collect(x.left, prefix, i, pat, q);
		if (c == '.' || c == x.c) {
			if (i == pat.length() - 1 && x.val != null) q.enqueue(prefix + x.c);
			if (i < pat.length() - 1) collect(x.mid, prefix + x.c, i+1, pat, q);
		}
		if (c == '.' || c > x.c) collect(x.right, prefix, i, pat, q);
	}



	// test client
	public static void main(String[] args) {
		// build symbol table from standard input
		TST<Integer> st = new TST<>();
		for (int i = 0; !StdIn.isEmpty(); i++) {
			String key = StdIn.readString();
			st.put(key, i);
		}


		// print results
		for (String key : st.keys()) {
			StdOut.println(key + " " + st.get(key));
		}
	}
}
