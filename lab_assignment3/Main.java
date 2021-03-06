import java.util.*;

public class Main {
	public static void main (String args[]){
		// arguments NSides, lower target LTarget, upper target UTarget,
		// NDice, hyperparameter float M, NGames
		if ( args.length != 6){
			System.out.println("Lacking/Exceeding parameters");
			return;
		}
		final int NSIDES = Integer.parseInt(args[0]); // n sided dice 
		final int LTARGET = Integer.parseInt(args[1]);
		final int UTARGET = Integer.parseInt(args[2]);
		final int NDICE = Integer.parseInt(args[3]); // maximum number of dice that can be rolled per turn 
		final float M = Float.parseFloat(args[4]); // hyperparameter M
		final int NGAMES = Integer.parseInt(args[5]);

		int[][][] winCount = new int [LTARGET][LTARGET][NDICE+1];
		int[][][] loseCount = new int [LTARGET][LTARGET][NDICE+1];
		int[][] printCorrectDice = new int [LTARGET][LTARGET];
		int[][] printCorrectDiceProbability = new int [LTARGET][LTARGET];
		Random rand = new Random();

		//for each game 
		//even gameCount is player 1s turn and odd gameCounts are the opponents 
		for (int gameCount = 0; gameCount < NGAMES; gameCount++){

			ArrayList <int []> p1StateList = new ArrayList <>();
			ArrayList <int []> p2StateList = new ArrayList <>();

			int p1Score = 0;
			int p2Score = 0;
		
			// for each turn (state)
			int turn = 0;
			while ( p1Score < LTARGET && p2Score < LTARGET){

				HashMap <Integer,Float > fjMap = new HashMap<>();  
				
				int t = 0; // t is the total number of games that have gone through same state <X,Y>
				int b = -1; // b is the number of dice to roll for maximum fj
				float fb = -1.0f; // highest probability of winning among fjs given player rolled b number of dice 
				for ( int j = 1; j < NDICE+1; j++){

					// add up total number of games that have the same state <X,Y>
					int temp = winCount[p1Score][p2Score][j] + loseCount[p1Score][p2Score][j];
					t += temp; 

					float fj; // fj is the probability of winning given player rolled j number of dice 
					if (temp == 0) fj = 0.5f;
					else fj = (float) winCount[p1Score][p2Score][j] / temp;

					if ( fj >= fb){
						b = j;
						fb = fj;	
					} 

					fjMap.put(j, fj);
 				}

 				// remove b to calculate for g 
 				fjMap.remove(b);
 				if ( turn % 2 == 0) printCorrectDice[p1Score][p2Score] = b; // <----------------error 
 				else printCorrectDice[p2Score][p1Score] = b;

 				// g is the sum of fj's where j != b 
 				float g = 0.0f;
 				for (Float fj : fjMap.values()) g+= fj;

 				// pb is the probabilty of player rolling b number of dice 
 				float pb = (t*fb + M)/(t*fb +( NDICE )* M);

 				int howManyDice = -1;
 				float throwDice = rand.nextFloat();
 				if ( throwDice <= pb) howManyDice = b;
 				else {
 					float temp = pb; // accumulate probability
 					for ( Map.Entry < Integer, Float > entry : fjMap.entrySet()){
 	
 						float fj = entry.getValue();
 						float pj = (1-pb) * (t*fj + M)/(g*t+(NDICE-1)*M);
 						
 						temp += pj;
 						// if within range set value of key fj as the number of dice to throw 

 						if( throwDice <= temp){
 							howManyDice = entry.getKey();
 							break;	
 						} 
 					}
 				}

 				// throw dice 
 				int rollSum = 0;
 				for ( int i = 0; i < howManyDice; i ++){
 					rollSum += rand.nextInt(NSIDES) + 1;
 				}

 				// player1 turn 
 				if ( turn % 2 == 0){
 					int [] state = {p1Score,p2Score,howManyDice};
 					p1StateList.add(state);
 					p1Score += rollSum;
 				}
 				// player2 turn 
 				else{
 					int [] state = {p2Score,p1Score,howManyDice};
 					p2StateList.add(state);
 					p2Score += rollSum;
 				}

 				// increment turn to next opponent
 				turn ++; 				
			}// end of while 

			// player 1 won 
			// insert p1StateList to winCount and p2StateList to loseCount  
			if ( p1Score > p2Score && p1Score <= UTARGET ){
				for ( int[] state : p1StateList){
					winCount[state[0]][state[1]][state[2]] += 1;
				}

				for ( int[] state : p2StateList){
					loseCount[state[0]][state[1]][state[2]] += 1;
				}
			}

			// player 2 won
			// vice versa 
			else {
				for ( int[] state : p2StateList){
					winCount[state[0]][state[1]][state[2]] += 1;
				}

				for ( int[] state : p1StateList){
					loseCount[state[0]][state[1]][state[2]] += 1;
				}
			}
		}// end of for

		System.out.printf("Game: NSides = %d LTARGET = %d UTARGET = %d NDice = %d\n\n",NSIDES , LTARGET, UTARGET, NDICE);
		// print two LTARGET * LTARGET array 
		System.out.println("Play =");
		for ( int i = 0; i < LTARGET; i++){
			for ( int j =0;j <LTARGET; j++){
				System.out.print( printCorrectDice[i][j] +" ");
			}
			System.out.println();
		}

		System.out.println();
		
		// print corresponding probability 
		System.out.println("Probability =");
		for ( int i = 0; i < LTARGET; i++){
			for ( int j =0;j <LTARGET; j++){
				int temp = winCount[i][j][printCorrectDice[i][j]] + loseCount[i][j][printCorrectDice[i][j]];
				if ( temp == 0) System.out.print("-1.0000  ");
				else System.out.printf("%7.4f  ", (float) winCount[i][j][printCorrectDice[i][j]]/ temp);
			}
			System.out.println();
		}
	}// end of main 
}// end of class 