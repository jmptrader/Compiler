//HelpingState.java

/* This class is to help us to handle new state subSets.
 * 
 */

import java.util.TreeSet;
import java.util.TreeMap;

public class HelpingState implements Comparable<HelpingState>{
//Attributes:
	private String realName;//The name the subSet would have in our new AFD.
	private TreeSet<Integer> statesIn;//The states contained in this subSet.
	private TreeMap<Integer, HelpingState> transitions;//The transitions from this trough Integer transition. 
	public boolean hasStates;
//Operations:
	//Constructors:
	private HelpingState(){
		hasStates = false;
		realName = "" + (1);
		statesIn = new TreeSet<Integer>();
	}
	public HelpingState(Integer state){
		//This is the main constructor.
		realName = "1";
		statesIn = new TreeSet<Integer>();
		hasStates = true;
		statesIn.add(state);
	}
	//Methods:
	public void addState(Integer i){
		//This method will add the state i to subSet.
		statesIn.add(i);
		hasStates = true;
	}
	public void addState(String s){
		//In case it isn't an Integer, this method will split an add one by one every state.
		s = s.replace('{', ' ');
		s = s.replace('}', ' ');
		s = s.replace(',', ' ');
		java.util.Scanner in = new java.util.Scanner(s);
		while(in.hasNextInt())
			addState(in.nextInt());
		in.close();
	}
	public HelpingState getNextState(Automaton a, String transition){
		//We could call this method the move function.
		int j;
		HelpingState out = new HelpingState();
		Integer[] states = this.statesIn();
		for(int i=0;i<states.length;i++){
			//Look for a coincidence on the transitionTable.
			for(j=1;j<a.transitionTable[0].length;j++)
				if(a.transitionTable[0][j].equals(transition))
					break;
			//If v then there is no transition.
			if(j==a.transitionTable[0].length)
				continue;
			//If there is a coincidence it returns the next state.
			if(!a.transitionTable[states[i]][j].equals(AFD.NOSTATE))
				out.addState(a.transitionTable[states[i]][j]);
		}
		return out;
	}
	public Integer[] statesIn(){
		//This method return an array with the states contained in this subSet.
		Integer[] out = new Integer[statesIn.size()];
		for(int i=0;i<out.length;i++){
        	out[i] = statesIn.first();
        	statesIn.remove(out[i]);
        }
        for(int i=0;i<out.length;i++)
        	statesIn.add(out[i]);
        return out;
	}
	public void addTransition(Integer transition, HelpingState state){
		//Add a new transition to transitionSet.
		if(transitions==null)
			transitions = new TreeMap<Integer, HelpingState>();
		transitions.put(transition, state);
	}
	public HelpingState getTransition(int j){
		//Obtains the transition associated with the key j.
		if(transitions!=null && transitions.containsKey(j))
			return transitions.get(j);
		return null;
	}
	public boolean contains(Integer state){
		return statesIn.contains(state);
	}
	public boolean equals(Object o){
		HelpingState hs = (HelpingState)o;
		return statesIn.equals(hs.statesIn);
	}
	public int compareTo(HelpingState h){
		return this.statesIn.toString().compareTo(h.statesIn.toString());
	}
	public String statesInNames(){
		return statesIn.toString();
	}
	public void setRealName(String realName){
		this.realName = realName; 
	}
	public String realName(){
		return realName;
	}
	public String toString(){
		return realName + "-" + statesIn;
	}
}
