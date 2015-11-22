//Frame.java

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

public class Frame extends JFrame implements ActionListener{
//Attributes:
	private static final long serialVersionUID = 2L;
    private JTable currentTable;
    private Automaton currentAutomaton;
    private Compiler currentCompiler;
    private String path = "C:\\Users\\ipiva\\workspace\\Compiler";
    private JFileChooser openFile;
    private JScrollPane currentPane;

//Operations:
    //Constructor:
    public Frame(){
        //Setting the Window.
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setLayout(new BorderLayout());
        super.setTitle("Compilador");
        setMenuBar();
        currentCompiler = new Compiler();
        super.setSize(1000, 500);
        super.setLocation(100, 100);
        super.setExtendedState(JFrame.MAXIMIZED_BOTH);
        super.setVisible(true);
    }
    //Listener methods:
    public void actionPerformed(ActionEvent event){
        JMenuItem menuItem = (JMenuItem)event.getSource();
        String option = menuItem.getText();
        try{
        	if(option.equals("Abrir expresión regular") || option.equals("Expresión regular AFN desde archivo"))
            	regularExpressionFromFileToAFN();
            else if(option.equals("Escribir expresión regular AFN"))
            	writeRegularExpressiontoAFN();
            else if(option.equals("Escribir expresión regular AFN hacia AFD"))
            	writeRegularExpressiontoAFD();
            else if(option.equals("AFN a AFD"))
            	AFNtoAFD();
            else if(option.equals("Cerrar"))
            	closeCurrentTable();
            else if(option.equals("Exportar autómata"))
            	exportTableToFile();
            else if(option.equals("Exportar autómata a excel"))
            	export(JOptionPane.showInputDialog(this, "Escribe el autómata a exportar"));
            else if(option.equals("Expresión regular AFN a AFD desde archivo"))
            	regularExpressionFromFileToAFD();
            else if(option.equals("Importar autómata"))
            	importTableFromFile();
            else if(option.equals("Analizador léxico"))
            	//Edit and make a good help window.
            	lexicalAnalyzer();
            else if(option.equals("Primeros y siguientes"))
            	firstAndNext(getCompleteFile());
            else
            	throw new FormatException("Esta función aún no está implementada.");
        }
        catch(FormatException e){
        	JOptionPane.showMessageDialog(this, e);
        }
        catch(Exception e){
        	JOptionPane.showMessageDialog(this, "Algo salió mal, revisa consola...");
        	e.printStackTrace();
        }

        super.setSize(1000, 500);
        super.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    //Methods called from ActionListener:
    private void importTableFromFile(){
    	path = getNewPath();
    	//Validate it is aut file.
    	if(!path.endsWith(".aut"))
    		throw new FormatException("No es un autómata");
    	setAutomaton(getAutomaton(path));
    }
    private void exportTableToFile() throws Exception{
    	if(currentTable==null)
    		throw new FormatException("No hay autómata que exportar");
    	String fileName = JOptionPane.showInputDialog("Nombre del archivo");
    	if(!fileName.endsWith(".aut"))
    		currentAutomaton.exportToFile(fileName + ".aut");
    	JOptionPane.showMessageDialog(this, "Exportado!");
    }
    public void export(String e) throws Exception{
    	ArrayList<JTable> table = new ArrayList<JTable>();
    	ArrayList<String> names = new ArrayList<String>();

    	table.add(Thompson.algorithm(e).getTable());
    	table.add(new AFD(e).getTable());
    	names.add("AFN");
    	names.add("AFD");

    	new Exporter(new File(JOptionPane.showInputDialog(this, "Archivo..") + ".xls"), table, names).export();
    }
    public static void export(String e, String path) throws Exception{
    	ArrayList<JTable> table = new ArrayList<JTable>();
    	ArrayList<String> names = new ArrayList<String>();

    	table.add(Thompson.algorithm(e).getTable());
    	//table.add(new AFD(e).getTable());
    	names.add("AFN");
    	//names.add("AFD");

    	new Exporter(new File(path + ".xls"), table, names).export();
    }
    private void closeCurrentTable(){
    	if(currentTable==null)
    		JOptionPane.showMessageDialog(this, "No hay nada que cerrar.");
    	else
    		super.remove(currentTable);
    	currentTable = null;
    }
    private void writeRegularExpressiontoAFN(){
    	String expression = JOptionPane.showInputDialog(null);
    	this.setAutomaton(Thompson.algorithm(expression));
    }
    private void writeRegularExpressiontoAFD(){
    	String expression = JOptionPane.showInputDialog(null);
    	setAutomaton(new AFD(expression));
    }
    private void regularExpressionFromFileToAFN() throws FileNotFoundException, IOException{
    	String expression = this.getRegularExpressionFromFile();
    	//Rename the JFrame.
        super.setTitle("Algoritmo de Thompson a: " + expression);
        setAutomaton(Thompson.algorithm(expression));
    }
    private void AFNtoAFD(){
    	importTableFromFile();
        setAutomaton(new AFD(currentAutomaton));
        //Rename the JFrame.
        super.setTitle("Algoritmo de Contrucción de Conjuntos: " + path);
    }
    private void regularExpressionFromFileToAFD() throws FileNotFoundException, IOException{
    	String expression = this.getRegularExpressionFromFile();
    	//Rename the JFrame.
        super.setTitle("Algoritmo de Thompson a: " + expression);
        setAutomaton(new AFD(expression));
    }
    private void lexicalAnalyzer() throws IOException, FileNotFoundException{
    	setAutomaton(currentCompiler.getLexicalRules());
  		currentCompiler.analyzeLexically(getCompleteFile());
    }
    private void firstAndNext(String s){
    	currentCompiler.setGrammar(s);
    	if(currentTable!=null)
    		super.remove(currentPane);
    	currentTable = currentCompiler.firstAndNext();
    	currentPane = new JScrollPane(this.currentTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	super.add(this.currentPane);
    }
    //Helping methods:
    public void setAutomaton(Automaton a){
    	if(currentTable!=null)
    		super.remove(this.currentPane);
    	currentAutomaton = a;
    	currentTable = a.getTable();
    	currentPane = new JScrollPane(this.currentTable,
    									JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
    									JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	super.add(this.currentPane);
    }
    private String getNewPath(){
    	if(openFile==null){
    		openFile = new JFileChooser(path);
    		openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	}
        int selection = openFile.showOpenDialog(this);
        if(selection == JFileChooser.APPROVE_OPTION)
        	return openFile.getSelectedFile().getAbsolutePath();

        return path;
    }
    private String getCompleteFile() throws IOException, FileNotFoundException{
    	path = getNewPath();
    	FileReader fr = new FileReader(path);
    	Scanner in = new Scanner(fr);
    	
    	String file = "";

    	for(int i=0;in.hasNextLine();i++)
    		if(i>0)
    			file += "\n" + in.nextLine();
    		else
    			file += in.nextLine();

    	in.close();
    	fr.close();

    	return file;
    }
    private String getRegularExpressionFromFile() throws FileNotFoundException, IOException{
    	path = this.getNewPath();

    	FileReader fr = new FileReader(path); 
        BufferedReader br = new BufferedReader(fr); 
        //String where the expression in the file will be saved.
        String expression = br.readLine();
        
        br.close();
        fr.close();
        
        return expression;
    }
    private Automaton getAutomaton(String path){
    	return Automaton.importFromFile(path);
    }
    //Setting JMenu:
    private void setMenuBar(){
    	//Setting the Menus
        JMenuBar menuBar = new JMenuBar();
        //File Menu
        JMenu menu = new JMenu("Archivo");
        JMenuItem menuItem = new JMenuItem("Abrir expresión regular");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Importar autómata");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Exportar autómata");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Exportar autómata a excel");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Cerrar");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);
        menuItem = new JMenuItem("Salir");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        //Lexical analyzer Menu
        menu = new JMenu("A. Léxico");
        menuItem = new JMenuItem("Expresión regular AFN desde archivo");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Escribir expresión regular AFN");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("AFN a AFD");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Expresión regular AFN a AFD desde archivo");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Escribir expresión regular AFN hacia AFD");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Analizador léxico");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);
        //Syntax analyzer Menu
        menu = new JMenu("A. Sintáctico");
        menuItem = new JMenuItem("Primeros y siguientes");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Colección canónica");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Tabla A. Sintáctico");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("A. Sintáctico");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);
        //Semantic analyzer menu
        menu = new JMenu("A. Sintáctico");
        menuBar.add(menu);
        //Help Menu
        menu = new JMenu("Ayuda");
        menuItem = new JMenuItem("Cómo usar");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);
        super.setJMenuBar(menuBar);
    }
    //Main:
    public static void main(String[]args) throws Exception{
    	//Beginning of testing area.
    	Scanner in = new Scanner(System.in);
    	new Frame();
    	//Ending of testing area.
    	in.close();
    }
}