//Thompson.java

import java.util.Stack;
import java.util.EmptyStackException;

public class Thompson{
//Class constants:
	public static final String NOSTATE = "-", EPSILON = "" + (char)163, PRIMARY_NAME = "EE\\TT";
    //Thompson's algorithm. With this we analyze the given expressions.
    public static Automaton algorithm(String s){
        Stack<Character> operators = new Stack<Character>();
        Stack<Automaton> values = new Stack<Automaton>();
        char c;
        //Give the necessary format.
        s = giveFormat(s);
        //Go for every char in the String.
        for(int i=0;i<s.length();i++){
        	if((c = s.charAt(i))=='¡'){
        		c = s.charAt(++i);
        		if(c=='(')
                    operators.push(new Character(c));
                else if(c==')'){
                    while(!operators.isEmpty() && operators.peek().charValue()!='(')
                        calculate(operators, values);
                    operators.pop();
                }
                else{
                    while(!operators.isEmpty() && operators.peek().charValue()!='(' && precedence(c)>=precedence(operators.peek().charValue()))
                        calculate(operators, values);
                    operators.push(new Character(c));
                }
        	}
            //According to s[i] we do:
        	else if(c!='l' && c!='d' && c!='s' && c!='p' && c!='n')
                values.push(new Automaton("" + c));
        	else
        		values.push(new Automaton(c));
        }
        //Finishing the operations stacked on the stack.
        while(!operators.isEmpty())
            calculate(operators, values);

        if(!operators.isEmpty())
            throw new FormatException("Tienes operadores de sobra!");

        return values.pop();
    }
    //Useful methods:
    //Priority values for the operators:
    public static int precedence(char a){
        if(a==')')
            return 0;
        if(a=='(')
            return 1;
        if(a=='+' || a=='*' || a=='?')
            return 2;
        if(a=='.')
            return 3;
        if(a=='|')
            return 4;
        return -1;
    }
    public static boolean isOperator(char a){
    	return a=='(' || a=='|' || a==')' || a=='.' || a=='*' || a=='+' || a=='?';
    }
    //It calculate the top stac operator's operation.
    public static void calculate(Stack<Character> operators, Stack<Automaton> values){
    	//We use a helping Automaton for binary operators.
        Automaton a;
        try{
        	switch(operators.pop().charValue()){
        		case '+':
                	values.push(values.pop().plus());
                	break;
            	case '*':
            		values.push(values.pop().star());
            		break;
            	case '|':
            		a = values.pop();
            		values.push(values.pop().union(a));
            		break;
            	case '.':
            		a = values.pop();
            		values.push(values.pop().concatenation(a));
            		break;
            	case '?':
            		values.push(values.pop().optional());
            		break;
        	}
        }
        catch(EmptyStackException e){
        	throw new FormatException("Tienes operadores de sobra!");
        }
    }
    //It gives the necessary format to the String so it can be processed.
    public static String giveFormat(String s){
        String a = "" + s.charAt(0);
        for(int i=1;i<s.length();i++)
            if(Character.isAlphabetic(s.charAt(i)) || s.charAt(i)=='('){
                if(s.charAt(i-1)!='(' && s.charAt(i-1)!='|' && s.charAt(i-1)!='.')
                    a += ".";
                a += s.charAt(i);
            }
            else
                a += s.charAt(i);
        s = "";
        for(int i=0;i<a.length();i++){
        	if(isOperator(a.charAt(i)))
				s += '¡';
			s += a.charAt(i);
        }

        return s;
    }
}