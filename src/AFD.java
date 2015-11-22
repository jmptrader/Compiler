//AFD.java

import java.util.Scanner;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

public class AFD extends Automaton{
//Attributes:
	//The same as the superclass.
//Operations:
	//Constructors:
	public AFD(String expression){
		this(Thompson.algorithm(expression));
	}
	public AFD(Automaton a){
		//It initializes an AFD with a the powerSetAlgorithm to an AFN a.
		this.finalStates = new TreeSet<Integer>();
		powerSetAlgorithm(a);
	}
	public AFD(AFD a){
		//It copies an AFD. 
		this.transitionTable = a.transitionTable;
		this.finalStates = a.finalStates;
	}
	//Methods:
	public void powerSetAlgorithm(Automaton a){
		//Set the first objects.
		TreeSet<HelpingState> marked = new TreeSet<HelpingState>();
		Vector<HelpingState> statesD = new Vector<HelpingState>();
		HelpingState currentHS, next, aux;
		currentHS = new HelpingState(1);

		String[] headers;
		if(a.transitionTable[0][a.transitionTable[0].length - 1].equals(EPSILON))
			headers = new String[a.transitionTable[0].length - 1];
		else
			headers = new String[a.transitionTable[0].length];
		for(int i=0;i<headers.length;i++)
			headers[i] = a.transitionTable[0][i];

		//Starting the algorithm.
		statesD.add(clausureE(a, currentHS));
		statesD.elementAt(0).setRealName("1");

		//While there is an unmarked state in statesD.
		while(statesD.size()>marked.size()){
			//Get the unmarked state.
			currentHS = statesD.elementAt(marked.size());
			//Get the state marked.
			marked.add(currentHS);
			//For each entry symbol a.
			for(int i=1;i<headers.length;i++){
				next = currentHS.getNextState(a, headers[i]);
				if(next.hasStates){
					aux = this.clausureE(a, next);
					if(!statesD.contains(aux)){
						aux.setRealName("" + (statesD.size() + 1));
						statesD.add(aux);
					}
					currentHS.addTransition(i, next);
				}
			}
		}
		//End of powerSetAlgorithm.
		//The last part of the AFD construction, format!
		this.transitionTable = super.newTable(statesD.size() + 1, headers);
		for(int i=0;i<statesD.size();i++){
			if(statesD.elementAt(i).contains(a.transitionTable.length - 1))
				finalStates.add(i + 1);
			//To table.
			//this.transitionTable[i + 1][0] = statesD.elementAt(i).toString();
		}
		//Filling the rest of the table.
		for(int i=1;i<this.transitionTable.length;i++)
			for(int j=1;j<this.transitionTable[i].length;j++)
					//The purpose of this is print the name of the state instead the states in subSet.
					if((next = statesD.elementAt(i - 1).getTransition(j))!=null)
						for(int k=0;k<statesD.size();k++){
							if(statesD.elementAt(k).compareTo(next)==0){
								transitionTable[i][j] = statesD.elementAt(k).realName();
								break;
							}
						}
					else
						//In case next==null means there is no transition.
						transitionTable[i][j] = NOSTATE;
	}
	public HelpingState clausureE(Automaton a, HelpingState clausure){
		Stack<Integer> stack = new Stack<Integer>();
		Integer t;

		Integer[] statesIn = clausure.statesIn();
		//The firsts elements of the stack would be the states in clausureE.
		for(int i=0;i<statesIn.length;i++)
			stack.push(statesIn[i]);
		while(!stack.isEmpty()){
			t = stack.pop();
			//To get access one by one of the elements in Automaton transition EPSILON.
			Integer[] array = this.split(a.getNextState(t, Automaton.EPSILON));
			for(int i=0;i<array.length;i++)
				if(!clausure.contains(array[i])){
					clausure.addState(array[i]);
					stack.push(array[i]);
				}
		}

		return clausure;
	}
	public Integer[] split(String s){
		//To split a set(s) into the states it has.
        //Empty set, nothing to return.
        if(s==NOSTATE)
            return new Integer[0];
        //Obtain a String with only numbers considering the format: "{1, 2, ..., n}."
        s = s.replace('{', ' ');
        s = s.replace('}', ' ');
        s = s.replace(',', ' ');

        Scanner in = new Scanner(s);
        TreeSet<Integer> set = new TreeSet<Integer>();

        while(in.hasNextInt())
        	set.add(new Integer(in.nextInt()));
        in.close();

        Integer[] out = new Integer[set.size()];
        for(int i=0;i<out.length;i++){
        	out[i] = set.first();
        	set.remove(out[i]);
        }

        return out;
	}
	public boolean process(String word){
    	int currentState = 1;
    	String s;

    	for(int i=0;i<word.length();i++){
    		s = this.getNextState(currentState, "" + word.charAt(i));
    		if(s==NOSTATE)
    			return false;
    		currentState = Integer.parseInt(s);
    	}

    	if(finalStates.contains(currentState))
    		return true;

    	return false;
    }
}