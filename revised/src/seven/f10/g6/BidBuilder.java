package seven.f10.g6;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;
import seven.ui.ScrabbleValues;
import seven.ui.GameEngine;	


public class BidBuilder {

	/**
	 * Create BidBuilder object
	 */

	private double value;
	private double posval;
	private boolean have7;
	private ArrayList<Character> seventh;
	private int failTime=-1;
	private boolean reachedSeven = false;
	private int current7WordScore = 0;
	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(org.apache.log4j.Level.DEBUG);
	}
	protected Logger l = Logger.getLogger(this.getClass());
	
	public BidBuilder()
	{
		//near7 = have7 = false;
		seventh = new ArrayList<Character>();
		//no-op
		
	}
	
	
	
	public void reset()
	{
		value = 0;
		have7 = false;
		failTime=-1;
		seventh.clear();
		reachedSeven = false;
		current7WordScore = 0;
	}
	
	public void wonletter(Letter let)
	{
		value = posval;
		//add code to check if we got our make 7er
		//if(got a)
		//	have7 = true;
		if(seventh.contains(let.getAlphabet()))
		{
			have7 = true;
			l.debug("SEVENTH WORKS");
		}
	}
	
	public int bid(Letter bidLetter, ArrayList<Character> letters, Word[] wordlist, Word[] slwl,
			ArrayList<PlayerBids> cachedBids,int currentPoint, int ourID)
	{
		l.debug("Value : " + value);
		/*
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
			*/
		return distance( bidLetter, letters, wordlist, slwl, cachedBids, currentPoint, ourID);
	}
	
	public void setHidden( ArrayList<Character> letters, Word[] wordlist)
	{
		double sum = 0;
		//for each word
		for(Word w: wordlist)
		{
			//get percentage
			//add percentage to total
			double prct = getPercentage(w, letters, null);
			sum += prct;
		}
		value = sum;		
	}
	
	private int distance(Letter bidLetter, ArrayList<Character> letters, Word[] wordlist, Word[] slwl, ArrayList<PlayerBids> cb,
			int cp, int id)
	{
		
		double sum = 0;
		//for each word
		int altbid = 0;
		for(Word w: wordlist)
		{
			//check if word is 7 letters
			//if so send to make7()
			if(w.length == 7)
			{	
				int b = make7(bidLetter, letters, cb, w, cp, id);
				//if near 7, bid return val
				if(b > altbid && altbid >= 0)
					altbid = b;
				if(b < 0 && b < altbid)
					altbid = b;
			}
	
			//get percentage
			//add percentage to total
			double prct = getPercentage(w, letters, bidLetter);
			sum += prct;
		}
		
		//get % difference
		double pdiff;
		if (value == 0 )
			pdiff = 0.5;
		else
			pdiff = (sum - value) / value;
		//multiply sum by 10
		posval = sum;
		
		if(altbid != 0)
			return Math.abs(altbid);
		
		if (value != 0)
		{
			if(pdiff < 1)
				return (int) Math.round(pdiff * 10);
			else if ((int) Math.round(pdiff+10) > 20)
				return 20;
			else
				return (int) Math.round(pdiff+10);
		}
		else
		{
			int i = initialBid(bidLetter);
			l.debug("Initialbid bids : " + i);
			return i;
		}
	}
	
	private int make7(Letter bidLetter, ArrayList<Character> letters, 
			ArrayList<PlayerBids> cachedBids, Word sevenWord, int currentPoint,int ourID)
	{
		if(letters.size()<5)
			return 0;
		if(getPercentage(sevenWord,letters,null)==1){
			have7 = true;
		}
		if(have7&&(getPercentage(sevenWord,letters,bidLetter)==(6.0/7.0))){
			if(current7WordScore<ScrabbleValues.getWordScore(sevenWord.word)){
				current7WordScore = ScrabbleValues.getWordScore(sevenWord.word);
			}
			return have7(bidLetter,letters, cachedBids,sevenWord,currentPoint,ourID);
		}
		if(getPercentage(sevenWord,letters,null)==(6.0/7.0)){
			reachedSeven = true;
		}
		double percent = getPercentage(sevenWord,letters,bidLetter);
		if(percent<(6.0/7.0)){
			return 0;
		}
		if(
				((percent==(6.0/7.0))&&(!reachedSeven))||
				((percent==1)&&reachedSeven)
				){
			int points = ScrabbleValues.getWordScore(sevenWord.word);
			int pointsLeft = getPointLeft(bidLetter,letters, cachedBids,sevenWord,currentPoint,ourID);
			double bidLetterScore = ScrabbleValues.letterScore(bidLetter.getAlphabet());
			double missingLetterScore = points-getScoreFromCurrentLetter(sevenWord,letters)-50;
			double bidMultiplier = bidLetterScore/missingLetterScore;
			if((pointsLeft>0)&&(pointsLeft<=currentPoint)){
				int x = (int)(bidMultiplier*pointsLeft);
				return x;
			}else{
				return 0;			
			}	
		}
		return 0;
	}

	private int getPointLeft(Letter bidLetter, ArrayList<Character> letters, 
			ArrayList<PlayerBids> cachedBids, Word sevenWord, int currentPoint,int ourID){
		
		int points = ScrabbleValues.getWordScore(sevenWord.word);
		//4. determine how much bids we have played so far.
		int pointPlayed = 0;
		int secondHighBid = 0;
		for(int i = 0;i<cachedBids.size();i++){

			if (ourID == cachedBids.get(i).getWinnerID()) {
				secondHighBid = 0;
				for(int j = 0;j<cachedBids.get(i).getBidvalues().size();j++){
					if(secondHighBid<cachedBids.get(i).getBidvalues().get(j)){
						secondHighBid = cachedBids.get(i).getBidvalues().get(j);
					}
				}
				
				pointPlayed += secondHighBid;
				
			}
			
		}
		//5. Calculate how much point we have left for bidding and not going to lose point in the end.
		return points-pointPlayed;
		
	}
	
	// adapt from Dan's getPercentage function.
	private double getScoreFromCurrentLetter(Word w, ArrayList<Character> letters)
	{
		double score = 0;
		ArrayList<Integer> usedletters = new ArrayList<Integer>(7);
		Character[] lets;
		lets = letters.toArray(new Character[letters.size()]);
		
		for(Character c: lets)
		{
			String word = w.word;
			int r = word.indexOf(c, 0);
			if(r==-1)
			{
				continue;
			}
			else
			{
				if(!usedletters.contains(r))
				{
					
					usedletters.add(r);
					score += ScrabbleValues.letterScore(c);
				}
				else
				{
					while(usedletters.contains(r) || r != -1)
					{
						r = word.indexOf(c, r+1);
					}
					if(r!=-1)
					{
						usedletters.add(r);
						score += ScrabbleValues.letterScore(c);
					}
				}
			}
		}
		
		return score;
	}
	
	private double getPercentage(Word w, ArrayList<Character> letters, Letter bidletter)
	{
		double total = w.length;
		double found = 0;
		ArrayList<Integer> usedletters = new ArrayList<Integer>(7);
		Character[] lets;
		if(bidletter != null)
		{
			lets = letters.toArray(new Character[letters.size()+1]);
			lets[lets.length-1] = bidletter.getAlphabet();
		}
		else
		{
			lets = letters.toArray(new Character[letters.size()]);
		}
		
		for(Character c: lets)
		{
			String word = w.word;
			int r = word.indexOf(c, 0);
			if(r==-1)
			{
				continue;
			}
			else
			{
				if(!usedletters.contains(r))
				{
					
					usedletters.add(r);
					found++;
				}
				else
				{
					while(usedletters.contains(r) || r != -1)
					{
						r = word.indexOf(c, r+1);
					}
					if(r!=-1)
					{
						usedletters.add(r);
						found++;
					}
				}
			}
		}
		
		return (found/total);
	}
	
	public int have7(Letter bidLetter, ArrayList<Character> letters, 
			ArrayList<PlayerBids> cachedBids, Word sevenWord, int currentPoint,int ourID)
	{
		int wordScore = ScrabbleValues.getWordScore(sevenWord.word);
		if(wordScore>current7WordScore){
			return -(wordScore-current7WordScore);
		}
		return 0;
	}
	

	//ArrayList<String> playernames = iocontroller.getPlayerList()
	public int initialBid(Letter bidLetter)
	{
		if(value == 0){
			int sum = 1;
			int total = InitialPlayer.sevenletterwordlist.length;
			failTime +=1;

			for(Word w: InitialPlayer.sevenletterwordlist)
			{
				int r= w.word.indexOf(bidLetter.getAlphabet());
				if(r!= -1)
				{	
					sum++;
				}
			}
			double FreqIn7 = (double)sum/total;
			int bidPrice = (int)Math.ceil(FreqIn7*10 + bidLetter.getValue());
			int playerNum = GameEngine.iocontroller.getPlayerList().size();
			if(failTime <= playerNum){
				return (bidPrice);
			}else {
				return (bidLetter.getValue()+7);
			}
			
		}else {
			return 0;
 		}
		
	}


}

	

