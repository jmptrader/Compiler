//Compiler.java

import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JTable;
import java.util.TreeSet;
import java.util.TreeMap;

public class Compiler{
//Attributes:
	//From Lexical analyzer.
	private AFD afd;
	private Vector<Integer> tLine;
	private Vector<String> tokenList, symbols;
	private Vector<String> errors;
	private TreeSet<String> reservedWords;
	private TreeMap<String, TreeSet<String> > firsts, nexts; 
	private TreeMap<String, String> symbolCharacters;
	private String[][] rules;
	private String lexicalRules = "l(l|d)*|n?d+(pd+|d*)|s+";
//Operators:
	//Constructors:
    public Compiler(){
    	afd = new AFD(lexicalRules);
    	Scanner in = new Scanner(getReservedWords());
    	reservedWords = new TreeSet<String>();
    	symbolCharacters = new TreeMap<String, String>();
    	while(in.hasNext())
    		reservedWords.add(in.next());
    	in.close();
    	in = new Scanner(getSymbols());
    	while(in.hasNext())
    		symbolCharacters.put(in.next(),in.next());
    	in.close();
    }
    public AFD getLexicalRules(){
    	return afd;
    }
    public String compile(String file){
    	analyzeLexically(file);
    	return null;
    }
    public void analyzeLexically(String file){
    	tLine = new Vector<Integer>();
    	tokenList = new Vector<String>();
    	symbols = new Vector<String>();
    	errors = new Vector<String>();

    	String input = giveFormat(file), currentLine;
    	Scanner in = new Scanner(input);
		String[] words = new String[0];
		String[][] tiraTokens, symbolTable = new String[1][2];
		symbolTable[0][0] = "Token";
		symbolTable[0][1] = "id";
		String[][] errorTable = new String[1][2];
		errorTable[0][0] = "#Línea";
		errorTable[0][1] = "Error";

    	for(int i=1;in.hasNextLine();i++){
    		currentLine = in.nextLine();
			words = currentLine.split(" ");
    		for(int j=0;j<words.length;j++){
    			if(words[j].length()==0)
    				continue;
    			//Process every word
    			if(afd.process(words[j])){
    				tLine.add(i);
    				tokenList.add(words[j]);
    				if(!symbols.contains(words[j]) && !reservedWords.contains(words[j]) && Character.isAlphabetic(words[j].charAt(0)))
    					symbols.add(words[j]);
    			}
    			else
   					errors.add(i + " " + words[j]);
			}
		}

    	in.close();
    	
    	if(symbols.size()>0){
    		symbolTable = new String[symbols.size() + 1][];
    		symbolTable[0] = new String[]{"Número", "Símbolo"};
    		
    		for(int i=1;i<symbolTable.length;i++)
    			symbolTable[i] = new String[]{"" + i, symbols.elementAt(i - 1)};
    	}
    	if(errors.size()>0){
    		errorTable = new String[errors.size() + 1][];
    		errorTable[0] = new String[]{"token", "id"};
    		
    		for(int i=1;i<errorTable.length;i++){
    			errorTable[i] = errors.elementAt(i - 1).split(" ");
    			errorTable[i][1] = "Símbolo " + errorTable[i][1] + " no está definido.";
    		}
    	}

    	tiraTokens = new String[tLine.size() + 1][3];
    	tiraTokens[0][0] = "#Linea";
    	tiraTokens[0][1] = "Token";
    	tiraTokens[0][2] = "Lexema";
    	for(int i=1;i<tiraTokens.length;i++){
    		tiraTokens[i][0] = tLine.elementAt(i - 1).toString();
    		tiraTokens[i][2] = tokenList.elementAt(i - 1);
    		if(reservedWords.contains(tokenList.elementAt(i - 1)))
    			tiraTokens[i][1] = "Palabra reservada";
    		else if(symbolCharacters.containsKey(tokenList.elementAt(i - 1)))
    			tiraTokens[i][1] = symbolCharacters.get(tokenList.elementAt(i - 1));
    		else if(Character.isAlphabetic(tokenList.elementAt(i - 1).charAt(0)))
    			tiraTokens[i][1] = "id";
    		else
    			tiraTokens[i][1] = "Número";
    	}
    	print(tiraTokens);
    	System.out.println();
    	print(errorTable);
    	System.out.println();
    	print(symbolTable);
    }
    public void analyzeSyntaxis(){
    	
    }
    public void setGrammar(String s){
    	String[] lines = s.split("\n");
    	rules = new String[lines.length][];
    	//First calculation
    	for(int i=0;i<rules.length;i++)
    		rules[i] = lines[i].split(" ");
    }
    public JTable firstAndNext(){
    	firsts = new TreeMap<String, TreeSet<String> >();
    	nexts = new TreeMap<String, TreeSet<String> >();

    	for(int i=0;i<rules.length;i++)
    		for(int j=0;j<rules[i].length;j++)
    			firsts(rules[i][j]);

    	for(int i=0;i<rules.length;i++)
    		nexts(rules[i][0]);
    	
    	String[][] table = new String[firsts.size() + 1][3];
    	int i=1;
    	table[0][0] = "Primeros y siguientes";
    	table[0][1] = "Primeros";
    	table[0][2] = "Siguientes";
    	for(java.util.Map.Entry<String, TreeSet<String> > e: firsts.entrySet()){
    		String f = "", n = "";
    		for(String s: e.getValue()){
    			f += s + " ";
    		}
    		if(nexts.get(e.getKey())!=null)
    			for(String s: nexts.get(e.getKey())){
    				n += s + " ";
    			}
    		table[i][0] = e.getKey();
    		table[i][1] = f;
    		table[i++][2] = n;
    	}

    	return new JTable(table, table[0]);
    }
    public void print(String[][] t){
    	for(int i=0;i<t.length;i++){
    		for(int j=0;j<t[i].length;j++)
    			System.out.print(t[i][j] + "\t");
    		System.out.println();
    	}
    }
    //Helping methods:
    public boolean isValid(String c){
    	return this.afd.hasTransition(c);
    }
    private String giveFormat(String input){
    	String output = "";
    	for(int i=0;i<input.length();i++){
    		char c = input.charAt(i); 
    		if(Character.isAlphabetic(c) || Character.isDigit(c))
    			output += c;
    		else if(c=='.'){
    			if(i==0)
    				output += " " + c + " ";
    			else if(Character.isDigit(input.charAt(i-1)))
    				output += c;
    			else
    				output += " " + c + " ";
    		}
    		else if(c=='\t' || c==' ')
    			output += " ";
    		else// if(this.isValid("" + c))
    			output += " " + c + " ";
    	}

    	return output;
    }
    private String getReservedWords(){
    	FileReader fr;
    	String file = "";
    	try{
    		fr = new FileReader("ReservedWords.txt");
    		Scanner in = new Scanner(fr);
    	
    		for(int i=0;in.hasNextLine();i++)
    			if(i>0)
    				file += "\n" + in.nextLine();
    			else
    				file += in.nextLine();

    		in.close();
    		fr.close();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}

    	return file;
    }
    private void firsts(String s){
    	TreeSet<String> value = new TreeSet<String>();

    	for(int i=0;i<rules.length;i++)
    		if(rules[i][0].equals(s)){
    			if(!firsts.containsKey(rules[i][1])){
    				if(!rules[i][1].equals(s))
    					firsts(rules[i][1]);
    				else
    					continue;
    			}
    			value.addAll(firsts.get(rules[i][1]));
    		}

    	if(value.size()==0)
    		value.add(s);

    	firsts.put(s, value);
    }
    private void nexts(String s){
    	if(nexts.containsKey(s))
    		return;

    	TreeSet<String> value = new TreeSet<String>();
    	
    	if(s.equals(rules[0][0]))
    		value.add("$");

    	for(int i=0;i<rules.length;i++)
    		for(int j=1;j<rules[i].length;j++){
    			if(rules[i][j].equals(s)){
    				if(j+1==rules[i].length){
    					if(!s.equals(rules[i][0])){
    						nexts(rules[i][0]);
    						value.addAll(nexts.get(rules[i][0]));
						}
    				}
    				else{
    					value.addAll(firsts.get(rules[i][j + 1]));
    					for(int k=1;value.contains("epsilon");k++){
    						value.remove("epsilon");
    						if(j+k+1==rules[i].length){
    							if(!s.equals(rules[i][0])){
    	    						nexts(rules[i][0]);
    	    						value.addAll(nexts.get(rules[i][0]));
    							}
    						}
    						else
    							value.addAll(firsts.get(rules[i][j+k+1]));
    					}
    				}
    			}
    		}
    	nexts.put(s, value);
    }
    private String getSymbols(){
    	FileReader fr;
    	String file = "";
    	try{
    		fr = new FileReader("ReservedSymbol.txt");
    		Scanner in = new Scanner(fr);

    		for(int i=0;in.hasNextLine();i++)
    			if(i>0)
    				file += "\n" + in.nextLine();
    			else
    				file += in.nextLine();

    		in.close();
    		fr.close();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}

    	return file;
    }
}
