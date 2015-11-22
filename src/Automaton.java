//Automaton.java

import java.util.TreeSet;
import java.util.Scanner;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import java.io.PrintStream;
import java.io.FileReader;
import java.io.BufferedReader;


public class Automaton{
//Class constants:
    public static final String EPSILON = "" + (char)163, NOSTATE = "-", PRIMARY_NAME = "EE\\TT";
    
//Attributes:
    protected String[][] transitionTable;
    protected TreeSet<Integer> finalStates;
    private MyTableRenderer tableRenderer = new MyTableRenderer();

//Operations:
    //Constructors:
    //Main constructor, it build an Automaton by creating two states with a transition s.
    public Automaton(){
    	transitionTable = new String[1][1];
    	transitionTable[0][0] = "No abriste ningún autómata";
    }
    public Automaton(String a){
        String[] headers = {"" + a, EPSILON};
        this.transitionTable = newTable(3, headers);
        this.transitionTable[1][1] = "2";
        this.finalStates = new TreeSet<Integer>();
        this.finalStates.add(2);
    }
    public Automaton(char type){
    	String[] headers;
    	this.finalStates = new TreeSet<Integer>();
    	switch(type){
    		case 'l':
    			headers = new String[53];
    			for(char i=0;i<='z'-'a';i++)
    				headers[i] = "" + (char)('a'+i);
    			for(char i=0;i<='Z'-'A';i++)
    				headers[i+26] = "" + (char)('A'+i);
    			headers[52] = EPSILON;
    			this.transitionTable = newTable(3, headers);
    			for(int i=1;i<this.transitionTable[0].length - 1;i++)
    				this.transitionTable[1][i] = "2";
    			break;
    		case 'd':
    			headers = new String[11];
    			for(int i=0;i<10;i++)
    				headers[i] = "" + (char)(i+'0');
    			headers[10] = EPSILON;
    			this.transitionTable = newTable(3, headers);
    			for(int i=1;i<11;i++)
    				this.transitionTable[1][i] = "2";
    			break;
    		case 'n':
    			headers = new String[2];
   				headers[0] = "-";
    			headers[1] = EPSILON;
    			this.transitionTable = newTable(3, headers);
    			this.transitionTable[1][1] = "2";
    			break;
    		case 's':
    			headers = new String[20];
    			headers[0] = ";";
    			headers[1] = ".";
    			headers[2] = ",";
    			headers[3] = "+";
    			headers[4] = "-";
    			headers[5] = "*";
    			headers[6] = "(";
    			headers[7] = ")";
    			headers[8] = ":";
    			headers[9] = "\'";
    			headers[10] = "\"";
    			headers[11] = "*";
    			headers[12] = "/";
    			headers[13] = "?";
    			headers[14] = "<";
    			headers[15] = "=";
    			headers[16] = ">";
    			headers[17] = "[";
    			headers[18] = "]";
    			headers[19] = EPSILON;
    			this.transitionTable = newTable(3, headers);
    			for(int i=1;i<transitionTable[0].length - 1;i++)
    				this.transitionTable[1][i] = "2";
    			break;
    		case 'p':
    			headers = new String[2];
   				headers[0] = ".";
    			headers[1] = EPSILON;
    			this.transitionTable = newTable(3, headers);
    			this.transitionTable[1][1] = "2";
    			break;
    	}
    	this.finalStates.add(2);
    }
    protected Automaton(String[][] table, int[] finals){
        this.transitionTable = table;
        this.finalStates = new TreeSet<Integer>();
        for(int i=0;i<finals.length;i++)
        	this.finalStates.add(finals[i]);
    }
    protected Automaton(String[][] table, TreeSet<Integer> finals){
        this.transitionTable = table;
        this.finalStates = finals;
    }
    protected Automaton(Automaton a){
    	this.transitionTable = a.transitionTable;
        this.finalStates = a.finalStates;
    }

    //Automaton's operations
    public Automaton concatenation(Automaton a){
        TreeSet<String> set = new TreeSet<String>();
        //This set help us to know how many transitions will have the next Automaton.
        for(int i=1;i<a.transitionTable[0].length;i++)
            set.add(a.transitionTable[0][i]);
        for(int i=1;i<this.transitionTable[0].length;i++)
            set.add(this.transitionTable[0][i]);
        //Create an String array according with bounds: statesXset's size.
        String[][] table = newTable(a.transitionTable.length + this.transitionTable.length - 2, set.toArray());
        //bound contains the number of states of the first table in order to add that quantity to the next table.
        int bound = this.transitionTable.length - 1;
        //Fill the table.
        for(int i=1;i<table.length;i++)
            for(int j=1;j<table[0].length;j++)
                if(i<bound)
                    //i<bound don't need to add anything to the states.
                    //getNextState returns a String with the next state with a given transition.
                    table[i][j] = this.getNextState(i, table[0][j]);
                else
                    //i>=bound means we are on the second table, so we use add.
                    //The add method edits the states to make them fit with the states of the first Automaton.
                    table[i][j] = add(a.getNextState(i - bound + 1, table[0][j]), bound - 1);

        int[] finalStates = {(table.length-1)};

        return new Automaton(table, finalStates);
    }
    //Union
    public Automaton union(Automaton a){
        TreeSet<String> set = new TreeSet<String>();
        //This set help us to know how many transitions will have the next Automaton.
        for(int i=1;i<this.transitionTable[0].length;i++)
            set.add(this.transitionTable[0][i]);
        for(int i=1;i<a.transitionTable[0].length;i++)
            set.add(a.transitionTable[0][i]);
        //Create an String array according with bounds: statesXset's size.
        String[][] table = newTable(this.transitionTable.length + a.transitionTable.length + 1, set.toArray());
        //bound contains the number of states of the first table in order to add that quantity to the next table.
        int bound = 1;
        //Fill the table.
        for(int i=1;i<this.transitionTable.length;i++)
            for(int j=1;j<table[0].length;j++)
                table[i + bound][j] = add(this.getNextState(i, table[0][j]), bound);

        bound = this.transitionTable.length;
        //Fill the table.
        for(int i=1;i<a.transitionTable.length;i++)
            for(int j=1;j<table[0].length;j++)
                table[i + bound][j] = add(a.getNextState(i, table[0][j]), bound);
        //Some union's characteristics.
        table[1][table[0].length - 1] = "{ 2, " + (this.transitionTable.length + 1) + "}";
        table[table.length - 2][table[0].length - 1] = "" + (table.length - 1);
        table[this.transitionTable.length][table[0].length - 1] = "" + (table.length - 1);

        int[] finalStates = {(table.length-1)};

        return new Automaton(table, finalStates);
    }
    //Kleene plus:
    public Automaton plus(){
        String[][] table = newTable(this.transitionTable.length + 2, this.transitionTable[0]);
        //Filling the table with the new values.
        for(int i=1;i<this.transitionTable.length;i++)
            for(int j=1;j<this.transitionTable[0].length;j++)
                table[i + 1][j] = add(this.transitionTable[i][j], 1);
        //Some plus's characteristics.
        table[1][table[0].length - 1] = "2";
        table[table.length - 2][table[0].length - 1] = "{ 2, " + (table.length - 1) + "}";

        int[] finalStates = {(table.length-1)};

        return new Automaton(table, finalStates);
    }
    //Kleene star:
    public Automaton star(){
        String[][] table = newTable(this.transitionTable.length + 2, this.transitionTable[0]);
        //Filling the table with the new values.
        for(int i=1;i<this.transitionTable.length;i++)
            for(int j=1;j<this.transitionTable[0].length;j++)
                table[i + 1][j] = add(this.transitionTable[i][j], 1);
        //Some star's characteristics.
        table[1][table[0].length - 1] = "{ 2, " + (table.length - 1) + "}";
        table[table.length - 2][table[0].length - 1] = "{ 2, " + (table.length - 1) + "}";

        int[] finalStates = {(table.length-1)};

        return new Automaton(table, finalStates);
    }
    //Optional
    public Automaton optional(){
        String[][] table = newTable(this.transitionTable.length + 2, this.transitionTable[0]);
        //Filling the table with the new values.
        for(int i=1;i<this.transitionTable.length;i++)
            for(int j=1;j<this.transitionTable[0].length;j++)
                table[i + 1][j] = add(this.transitionTable[i][j], 1);
        //Some optional's characteristics.
        table[1][table[0].length - 1] = "{ 2, " + (table.length - 1) + "}";
        table[table.length - 2][table[0].length - 1] = "" + (table.length - 1);

        int[] finalStates = {(table.length-1)};

        return new Automaton(table, finalStates);
    }
    //Automaton's move method.
    public String getNextState(int state, String transition){
        int j;
        //Look for a coincidence on the transitionTable.
        for(j=1;j<this.transitionTable[0].length;j++)
            if(this.transitionTable[0][j].equals(transition))
                break;
        //If v then there is no transition.
        if(j==this.transitionTable[0].length)
            return NOSTATE;
        //If there is a coincidence it returns the next state. 
        return this.transitionTable[state][j];
    }
    //Useful methods.
    public int size(){
    	return this.transitionTable.length - 1;
    }
    public boolean hasTransition(String t){
    	for(int i=1;i<transitionTable[0].length;i++)
    		if(transitionTable[0][i].equals(t))
    			return true;
    	return false;
    }
    public static String[][] newTable(int y, Object[] headers){
        if(headers[0]==PRIMARY_NAME){
            Object[] tmp = new Object[headers.length - 1];
            for(int i=0;i<tmp.length;i++)
                tmp[i] = headers[i+1];
            headers = tmp;
        }
        //Create a String array according to headers.length and y.
        //y is the number of the states for the table.
        String[][] table = new String[y][headers.length + 1];
        //Fill the table with NOSTATEs to avoid NullPointerException errors.
        for(int i=0;i<table.length;i++)
            for(int j=0;j<table[0].length;j++){
                if(i==0 && j>0)//The first row contains the name of the table headers.
                    table[0][j] = headers[j - 1].toString();
                else if(i==0 && j==0)//The first cell always will be EE//TT.
                    table[0][0] = PRIMARY_NAME;
                else if(j==0 && i>0)//The first column if row>0 would be the states name.
                    table[i][0] = "" + i;
                else
                    table[i][j] = NOSTATE;
            }

        return table;
    }
    public static String add(String s, int bound){
        String a;
        //Empty set, nothing to add.
        if(s==NOSTATE)
            return s;
        if(s.charAt(0)=='{'){
            //Obtain a String with only numbers considering the format: "{1, 2, ..., n}."
            s = s.replace('{', ' ');
            s = s.replace('}', ' ');
            s = s.replace(',', ' ');
            Scanner in = new Scanner(s);
            a = "{ ";
            for(int i=0;in.hasNextInt();i++){
                if(i>0) a += ", ";
                a += "" + (in.nextInt() + bound);
            }
            a += "}";
            in.close();
        }
        else{
            //If the String only contains one number we add directly.
            Scanner in = new Scanner(s);
            a = "" + (in.nextInt() + bound);
            in.close();
        }

        return a;
    }
    public Object[][] toArray(){
        return this.transitionTable;
    }
    public JTable getTable(){
        //The new JTable.
        String[][] table = new String[transitionTable.length][transitionTable[0].length];
        //Filling the Array in order to get a JTable.
        for(int i=0;i<table.length;i++)
            for(int j=0;j<table[i].length;j++)
            	if(j==0 && i==1){
            		table[i][j] = "(inicial)" + this.transitionTable[i][j];
            		if(finalStates.contains(i))
            			table[i][j] += "(final)";
            	}
            	else if(j==0 && i>0 && this.finalStates.contains(i))
                    table[i][j] = this.transitionTable[i][j] + "(final)";
                else
                    table[i][j] = this.transitionTable[i][j];
        //The table style.
        JTable out = new JTable(table, table[0]);
        out.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(int i=0;i<table[0].length;i++){
        	TableColumn c = out.getColumnModel().getColumn(i);
        	c.setCellRenderer(tableRenderer);
        	if(i==0)
        		c.setPreferredWidth(100);
        	else
        		c.setPreferredWidth(40);
        }

        out.setRowHeight(30);

        return out;
    }
    public void exportToFile(String fileName){
    	try{
    		PrintStream writer = new PrintStream(fileName);
    		String line;
    		//Exporting with the format our class use.
    		writer.println(transitionTable.length + " " + transitionTable[0].length);
    		for(int i=0;i<transitionTable.length;i++)
    			for(int j=0;j<transitionTable[i].length;j++){
    				if(i==1 && j==0){
    					//Setting the initial state.
                        line = "(inicial)" + this.transitionTable[i][j];
                        if(this.finalStates.contains(i))
                        	line += "(final)";
                	}
                	else if(j==0 && i>0 && this.finalStates.contains(i))
                		//Setting the final states.
                        line = this.transitionTable[i][j] + "(final)";
                    else
                    	//Just filling.
                        line = this.transitionTable[i][j];
    				writer.println(line);
    			}
    		writer.close();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    public static Automaton importFromFile(String path){
    	FileReader fr = null;
    	BufferedReader br = null;
    	if(!path.contains(".aut"))
    		throw new FormatException("Formato inválido, el archivo debe ser *.aut");
    	Automaton a = new Automaton();
    	TreeSet<Integer> finalStates = new TreeSet<Integer>();
    	int rows, columns;
    	try{
    		//Open the file.
    		fr = new FileReader(path);
    		br = new BufferedReader(fr);
    		//Getting the weight of the matrix.
    		String line = br.readLine();
    		Scanner in = new Scanner(line);
    		rows = in.nextInt(); columns = in.nextInt();
    		in.close();
    		String[][] table = new String[rows][columns];
    		//Setting the Automaton's new transitionTable.
    		for(int i=0;i<rows;i++)
    			for(int j=0;j<columns;j++){
    				line = br.readLine();
    				if(line.equals(PRIMARY_NAME))
    					table[i][j] = PRIMARY_NAME;
    				else if(line.equals(NOSTATE))
    					table[i][j] = NOSTATE;
    				else if(j==0){
    					if(line.contains("(final)"))
    						finalStates.add(i);
    					table[i][j] = "" + i;
    				}
    				else
    					table[i][j] = line;
    			}
    		a = new Automaton(table, finalStates);
    	}
    	catch(Exception e){
    		
    	}
    	finally{
    		try{
    			br.close();
    			fr.close();
    		}
    		catch(Exception e){}
    	}

    	return a;
    }
}
