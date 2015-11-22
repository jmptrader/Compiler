//FormatException.java

public class FormatException extends NumberFormatException{
//Class constants:
	public static final long serialVersionUID = 1551515L;

//Atributes:
	private String why;

//Operations:
	//Constructors:
	public FormatException(){
		super();
		why = new String("Unknown");
	}
	public FormatException(String s){
		super(s);
		why = new String(s);
	}
	//Inherited methods:
	@Override
	public String toString(){//From Object
		return why;
	}
	@Override
	public void printStackTrace(){//From Exception
		super.printStackTrace();
		System.out.println(this);
	}
}
