
import java.util.*;


public class Main {
	public static void main(String args[]){
		
		System.out.println("Starting Search...");
		System.out.println("-----------------------");
		// arrayList for original domino set 
		ArrayList <Domino> dominoSet = new ArrayList <>();
		// hashtable will contain < top string , bot string > 
		Hashtable <String, String> avoidRecompTable = new Hashtable<>();
		Scanner scn = new Scanner( System.in);

		// first input = threshold 
		int threshold = scn.nextInt();
		//scn.nextLine();
		// create [D1, D2, D3, D4...]
		while( scn.hasNextLine()){
			try {
				String name = "D"+ scn.next();
				String top = scn.next();
				String bot = scn.next();
				dominoSet.add(new Domino(name,top,bot));
			}
			catch (NoSuchElementException e) {
				break;
			}	
		}

		scn.close();
		int size = dominoSet.size();
		
		// queue for BFS domino
		Queue <Domino> q = new LinkedList <Domino>();
		Domino root = new Domino("","","");
		q.add(root);

		// endstatus = 0 no solution within threshold
		// endstatus = 1 yes solution 
		// endstatus = 2 no solution 
		int endStatus = 2;
		int counter = 0;
		String answer = "";
		while( !q.isEmpty() ){
			Domino e = q.poll();
			
			for( int i = 0; i < size ; i++){
				Domino newDomino = e.add(dominoSet.get(i));

				// if the newDomino is an existing state continue with for loop
				 if( avoidRecompTable.containsKey(newDomino.top) && avoidRecompTable.get(newDomino.top).equals(newDomino.bot) )continue;

				// validate()
				// returns 0 if not valid 
				// returns 1 if valid state 
				// returns 2 if goal state
				int v = newDomino.validate();
				// goal state 
				if( v == 2) {
					q.clear();
					answer = String.format("[%s ] %s %s", newDomino.name, newDomino.top, newDomino.bot);
					System.out.println(answer);
					endStatus = 1;
					break;
				}

				// if valid state add domino to Hashtable to avoid recomputation 
				else if( v == 1) {

					// rached threshold terminate with status 0 for no solution within limit 
					if( counter == threshold ){
						q.clear();
						endStatus = 0;
						break;
					}

					System.out.format("[%s ] %s %s\n", newDomino.name, newDomino.top, newDomino.bot);

					avoidRecompTable.put( newDomino.top , newDomino.bot);
					q.add(newDomino);

					// increment counter only when it's a valid state
					counter++;

				}
			}
		}

		//according to the endStatus flag print 
		if( endStatus == 2)	System.out.println("No solution");
		else if( endStatus == 0) System.out.println("No solution within limits of search");
		else System.out.println("Solution: " + answer);
	}
}

 class Domino{
	public String name = "";
	public String top = "";
	public String bot = "";

	public Domino(){
	}

	public Domino(String name, String top, String bot){
		this.name = name;
		this.top = top;
		this.bot = bot;
	}

	// order vulnerable 
	public Domino add (Domino anotherDomino){
		Domino newDomino = new Domino();
		newDomino.name = this.name +" " + anotherDomino.name;
		newDomino.top = this.top + anotherDomino.top;
		newDomino.bot = this.bot + anotherDomino.bot;
		return newDomino;
	}
	
	// return 0 if not valid 
	// return 1 if valid state 
	// return 2 if goal state
	public int validate(){
		if( this.top.equals(this.bot)) return 2;
		else{
			// "aabbb" 
			// "aabc"
			for ( int i = 0; i < Math.min(this.top.length(), this.bot.length()) ; i ++){
				if(this.top.charAt(i) != this.bot.charAt(i)) return 0;
			}
			return 1; 
		}
	}

	public static void sweet(){
		System.out.println("sweet!");
	}
}
