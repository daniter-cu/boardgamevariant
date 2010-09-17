package seven.f10.g6;

import java.util.ArrayList;
import java.util.Arrays;

import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

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
	
	public int bid(Letter bidLetter, ArrayList<Character> letters, Word[] wordlist)
	{
		//bid zero if we have 7.
		if(have7)
			return 0;
		else if (near7)
			return make7();
		else 
			return distance();
	}
	
	public int distance()
	{
		//implement check to call make7() instead.
		return 0;
	}
	
	public int make7()
	{
		return 0;
	}
	
}
