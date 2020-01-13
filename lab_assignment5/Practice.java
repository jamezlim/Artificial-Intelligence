import java.util.*;


public class Practice{
	public static void main (String args[]){
		LinkedList<Integer> l = new LinkedList <Integer> (); 

		l.add(1);
		l.add(2);
		l.add(3);
		l.add(4);

		System.out.println( l.get(0));
		System.out.println( l.getFirst());
		System.out.println( l.peek());
		System.out.println( l.peekFirst());

		String s = "hello";
		String x = s; 

		System.out.println(x.equalsIgnoreCase("HELLO"));

		Tree t = new Tree("hi");

		System.out.println(t.root.equalsIgnoreCase("hi"));	

		traverseTree(t);	
	

	}

	public static void traverseTree( Tree tree){
		if (tree == null) return;
		String s = tree.root;
		int a = tree.root.length();
		System.out.println(a);
		boolean f = s.equals("hi");
		boolean x = s.equalsIgnoreCase("HI");
		if (f) System.out.println("equals");
		if (x )  System.out.println("equalsIgnoreCase");
		//if (s.euqalsIgnoreCase("np")); //stnc.npList.add(tree);
		//else if (tree.root.euqalsIgnoreCase("verb")) stnc.verb = tree.children.getFirst().root;
		//else if (tree.root.euqalsIgnoreCase("prep")) stnc.prep = tree.children.getFirst().root; 
		
	}


	public static class Tree {
		String root;

		public Tree(String s){
			this.root = s; 
		}
	}			

}

