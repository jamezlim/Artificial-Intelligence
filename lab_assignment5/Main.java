import java.util.*;
import java.io.*; 
import java.lang.*;

public class Main {
	public static void main (String args[]){
		Scanner input = new Scanner(System.in);

		String s = input.nextLine();
		System.out.printf("Input: %s \n", s);

		AbstractMap.SimpleEntry<Tree, Integer> tree = extractExpression(s, 0);

		
		Sentence stnc = getPhrases(tree.getKey()); 


		
	}

	public static void printTree( Tree tree){
		if (tree == null) return;
		System.out.println(tree.root);
		for ( Tree t : tree.children ){
			printTree(t);
		}
	}

	public static void traverseTree( Tree tree, Sentence stnc){
		if (tree == null) return;
		if (tree.root.equalsIgnoreCase("np")); //stnc.npList.add(tree);
		else if (tree.root.equalsIgnoreCase("verb")) stnc.verb = tree.children.getFirst().root;
		else if (tree.root.equalsIgnoreCase("prep")) stnc.prep = tree.children.getFirst().root; 
		for ( Tree t : tree.children){
			traverseTree(t, stnc);
		}
	}

	public static Sentence getPhrases( Tree t ){
		Sentence stnc = new Sentence();
		traverseTree(t, stnc);
		return stnc; 
	}



	public static AbstractMap.SimpleEntry<String, Integer> extractSymbol(String s, int k){
		while (s.charAt(k) == ' ') k++; // skip white space 

		if ( s.charAt(k) == '(' || s.charAt(k) == ',' || s.charAt(k) == ')' ){
			return new AbstractMap.SimpleEntry<String,Integer>(Character.toString(s.charAt(k)) , k+1);
		}
		if (!Character.isAlphabetic(s.charAt(k))){
			System.out.printf("Invalid character input at index %d \n", k);
			System.exit(1);
		} 
		String m = "";
		while (Character.isAlphabetic(s.charAt(k))){
			m = m + s.charAt(k);
			k++;
		}
		return new AbstractMap.SimpleEntry<String,Integer>(m,k);
	} // end extractSymbol 


	public static AbstractMap.SimpleEntry<Tree, Integer> extractExpression(String s, int k){
		AbstractMap.SimpleEntry<String, Integer> mq = extractSymbol(s,k);
		if ( !((mq.getKey() != null)  && (!mq.getKey().equals("")) && (mq.getKey().matches("^[a-zA-Z]*$")))){ // if string is not alphabetic 
			System.out.printf("Invalid String input at index %d \n", k);
			System.exit(1);
		}

		Tree t = new Tree(mq.getKey());
		AbstractMap.SimpleEntry<String, Integer> npeek = extractSymbol(s,mq.getValue());

		//System.out.printf("Peeked at next symbol = %s --> %d \n", npeek.getKey(), npeek.getValue());

		if ( !(npeek.getKey().equals("("))) {
			//System.out.printf("returned index %d\n", mq.getValue());
			return new AbstractMap.SimpleEntry<Tree,Integer>(t,mq.getValue());
		}
		int q = npeek.getValue();

		while(true){
			AbstractMap.SimpleEntry<Tree, Integer> cq = extractExpression(s,q);

			t.children.add(cq.getKey());

			AbstractMap.SimpleEntry<String, Integer> nq = extractSymbol(s,cq.getValue());

			// System.out.printf("%s -> ",nq.getKey());
			// System.out.println(nq.getValue());
			if ( nq.getKey().equals(")")) return new AbstractMap.SimpleEntry<Tree,Integer>(t,nq.getValue());
			if ( !(nq.getKey().equals(","))){
				System.out.printf("Invalid input at index %d \n", nq.getValue());
				System.exit(1);
			}

			q = nq.getValue();

		}// end while 
	} // end extractExpression
}

class Tree {
	public String root;
	public LinkedList<Tree> children;


	public Tree(String s){
		this.root = s;
		children = new LinkedList<>();
	}

}

class Sentence {
	public String verb ; 
	public String prep;
	public ArrayList <Tree> npList = new ArrayList<Tree>();
	public String [] transferArray; 

	//default no param constructor 

	public void routine (){
		
	}
}

