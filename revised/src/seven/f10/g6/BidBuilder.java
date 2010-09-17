package seven.f10.g6;

import java.util.ArrayList;
import java.util.Arrays;

import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;
import seven.ui.ScrabbleValues;

public class BidBuilder {

	/**
	 * Create BidBuilder object
	 */
	private boolean near7;
	private boolean have7;
	
	public BidBuilder()
	{
		near7 = have7 = false;
		
		//no-op
	}
	
	public int bid(Letter bidLetter, ArrayList<Character> letters, Word[] wordlist, Word[] slwl,
			ArrayList<PlayerBids> cachedBids,int currentPoint, int ourID)
	{
		//bid zero if we have 7.
		if(have7)
			return 0;
		else if (near7){
			
			//Dummy word
			Word sevenWord = new Word("Dummyyy");
			return make7(bidLetter, letters, cachedBids, sevenWord, currentPoint,ourID);
			
		}

		else 
			return distance( bidLetter, letters, wordlist, slwl);
	}
	
	public int distance(Letter bidLetter, ArrayList<Character> letters, Word[] wordlist, Word[] slwl)
	{
		//implement check to call make7() instead.
		return 0;
	}
	
	public int make7(Letter bidLetter, ArrayList<Character> letters, 
			ArrayList<PlayerBids> cachedBids, Word sevenWord, int currentPoint,int ourID)
	{
		
		//determine bid price that doesn't exceed what we will get
		//1. Using bidletter and letters we have
		//1.2 Count if it's 5-6 letters then continue, else return 0
		//2. See if these word is in sevenWord, if not return 0
		//2.1. determine points you will get in 7-letter word.
		//3. Plus 50 points bonus into calculation
		//4. determine how much bids we have played so far.
		//5. Calculate how much point we have left for bidding and not going to lose point in the end.
		//6. Get percentage value of that letter.
		//7. Bid
		
		ArrayList<Character> determiningLetter = new ArrayList<Character>();
		for(int i = 0; i<letters.size();i++){
			
			determiningLetter.add(letters.get(i));
			
		}
		determiningLetter.add(bidLetter.getAlphabet());
		//1.2 Count if it's 5-6 letters then continue, else return 0
		if(determiningLetter.size()<5)
			return 0;
		else{
			//2. See if these word is in sevenWord, if not return 0
			boolean foundAll = true;
			boolean found = false;
			
			for(int j=0;j<determiningLetter.size();j++){
				
				found = false;
				
				for(int i = 0; i<sevenWord.word.length();i++){
					
					if(determiningLetter.get(j).equals(sevenWord.word.charAt(i))){
						
						found = true;
						
					}
					
				}
				
				foundAll = foundAll&&found;
				
			}
			
			if(!foundAll){
				
				return 0;
				
			}else{
				//2.1. determine points you will get in 7-letter word.
				int points = ScrabbleValues.getWordScore(sevenWord.word);
				//3. Plus 50 points bonus into calculation
				points += 50;
				//4. determine how much bids we have played so far.
				int pointPlayed = 0;
				for(int i = 0;i<cachedBids.size();i++){
					
					pointPlayed += cachedBids.get(i).getBidvalues().get(ourID);
					
				}
				//5. Calculate how much point we have left for bidding and not going to lose point in the end.
				int pointsLeft = points-pointPlayed;
				//6. Get percentage value of that letter base on whole word value and point left.
				//7. Bid
				return (ScrabbleValues.letterScore(bidLetter.getAlphabet())*pointsLeft)/points;
				
			}
			
		}
		
	}
	
}
