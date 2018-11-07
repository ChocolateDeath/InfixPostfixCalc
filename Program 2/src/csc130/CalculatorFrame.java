package csc130;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;


@SuppressWarnings("serial")
//Kevin Perez
//CSC130
//Prof. Graham
//Program 2
class CalculatorFrame extends JFrame implements ActionListener  {
	JTextField jtfInfix = new JTextField(21); // for infix 
	JTextField jtfPostfix = new JTextField();  // for postix
	JTextField result = new JTextField("0");   // for result
	
	JButton[][] calcButton = new JButton[4][5];
	
	JPanel calcPanel = new JPanel();	
	JPanel topPanel = new JPanel();    

	
	public CalculatorFrame(){
		String[][] buttonText = 
				new String[][]{{"7","8","9","\u00F7","C"},{"4","5","6","\u2217","B"},
				{"1","2","3","-","R"},{"0","(",")","+","="}};
				
		this.setTitle("CSC130 Calculator");
		this.setLayout(new BorderLayout(2,1));
		
		String expression = jtfPostfix.getText();
		expression = expression.replaceAll("\u00F7", "/");
		expression = expression.replaceAll("\u2217", "*");

		jtfInfix.setHorizontalAlignment(JTextField.RIGHT);
		jtfPostfix.setHorizontalAlignment(JTextField.RIGHT);
		result.setHorizontalAlignment(JTextField.RIGHT);
		jtfPostfix.setEnabled(false);
		result.setEnabled(false);
		//jtfInfix.setEditable(false); // hide text caret
		
		// set the font size to 34 for the text fields
		Font textFieldFont=new Font(jtfPostfix.getFont().getName(),jtfPostfix.getFont().getStyle(),24);
		jtfInfix.setFont(textFieldFont);
		jtfPostfix.setFont(textFieldFont);
		result.setFont(textFieldFont);
		
		topPanel.setLayout(new GridLayout(3,1));				
		topPanel.add(jtfInfix);		
		topPanel.add(jtfPostfix);
		topPanel.add(result);
		
		calcPanel.setLayout(new GridLayout(4,5,3,3));
		
		for (int i = 0; i < 4; i++) {
			for(int j = 0; j < 5; j++) {
				calcButton[i][j]= new JButton("" + buttonText[i][j]);
				calcButton[i][j].setForeground(Color.blue);
				calcButton[i][j].setFont(new Font("sansserif",Font.BOLD,42));
				calcButton[i][j].addActionListener(this);
				calcButton[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
				calcPanel.add(calcButton[i][j]);
			}
		}
		this.add(topPanel,BorderLayout.NORTH);
		this.add(calcPanel,BorderLayout.CENTER);
	}
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < 4; i++) {
			for(int j = 0; j < 5; j++) {				
				if(e.getSource() == calcButton[i][j]){
					// clear
					if(i==0 && j == 4){
						jtfInfix.setText(null);
						jtfPostfix.setText(null);
						result.setText("0");
					}
					// backspace
					else if(i==1 && j == 4){
						if(jtfInfix.getDocument().getLength()>0)
							try {
								jtfInfix.setText(jtfInfix.getText(0, jtfInfix.getDocument().getLength()-1));
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}
						
					}
					// number or operator
					else if(j<4){
						jtfInfix.setText(jtfInfix.getText()
							+ calcButton[i][j].getText());
						}
					// = button pressed
					else if(i==3&&j==4){
						// erase contents of the postfix textfield
						jtfPostfix.setText(null);  
						// update the postfix textfield with the String returned  
						jtfPostfix.setText(infixToPostfix(jtfInfix.getText()));
						// update the result textfield with the result of the computation
						result.setText("" + calculate(jtfPostfix.getText()));
					}
				}
			}
		}
    }

	public static String infixToPostfix(String input){		
			String result = "";
			char n;
			Stack<String>  stack = new Stack<String>();
			String delims = "/*-+()∗ ";
			StringTokenizer strToken = new StringTokenizer(input,delims,true);
			while (strToken.hasMoreTokens()) {
				String temp = strToken.nextToken();
				if(temp.equals("(")) //if next token is left parenthesis, push onto stack
					stack.push(temp);
				else if(temp.equals(")")){ // if its right parenthesis
					while(!(stack.peek().charAt(0)=='('))//pop elements off stack and append to result until the the top of stack is left parenthesis
						result += stack.pop()+ " ";
					stack.pop();//pop left parenthesis off the stack
				}
				
				
				else if(temp.equals("+")||temp.equals("-")||temp.equals("∗")||temp.equals("/")){// If it is an operator and there are other operators in stack
					int onstack =0;
					int offstack=0;
					if(stack.isEmpty())//if no other operators in the stack
						stack.push(temp);//just push
					
					else while(onstack>=offstack){
							if(temp.equals("+")||temp.equals("-")){//compare token to top of stack to determine precedence
								offstack=5;
								result += stack.pop()+ " ";
								if(!stack.isEmpty()&&(stack.peek().charAt(0)=='+'||stack.peek().charAt(0)=='-'||stack.peek().charAt(0)=='∗'||stack.peek().charAt(0)=='∗')){//If stack is not empty and there is an operator on top
									n = stack.peek().charAt(0);
								if(n=='/'||n=='∗')//find value of operator at top of stack
									onstack=10;
								else onstack=5;//top operator is a + or -
								}
								else {//if not operator, just push
									stack.push(temp);
									break;
								}
							}
							else if(temp.equals("∗")||temp.equals("/")){//If token is  * or /
							 n = stack.peek().charAt(0);
								offstack=10;
								if(n=='('){// if operator on top, push then leave loop
									stack.push(temp);
									break;
								}
								if(n=='/'||n=='∗'){//if top operator is * or / then pop out
									result+=stack.pop()+ " ";
									n = stack.peek().charAt(0);
									if(n=='/'||n=='∗')//find next operator at top of stack
										onstack=10;
									else{ 
										onstack=5;
										stack.push(temp);
									}
								}
									else{ 
										onstack=5;
										stack.push(temp);
									}
										
								}
								else{
									result+=temp+ " ";
									offstack=5;
								}
							
								
							}
							
				}
				else if(Character.isDigit(temp.charAt(0)))//If digit, push
					result += temp + " ";
			}
			while(!stack.isEmpty())
				result += stack.pop()+ " ";
			return result;
		}
	
	public static int calculate(String postfix) {
			String delims = "/*-+∗ ";
			Stack<Integer> stack = new Stack<Integer>();
			int result =0;
			StringTokenizer strToken = new StringTokenizer(postfix,delims,true);
			while (strToken.hasMoreTokens()) {//While there are more tokens
				String temp = strToken.nextToken();//remove first token
				if(temp.equals(" "))//if empty space
					continue;//go to next token
					
				else if (Character.isDigit(temp.charAt(0)))//If digit
					stack.push(Integer.parseInt(temp));//push to stack

				
				else {//else for operator
					//pop 2 top elements from stack
					char n = temp.charAt(0);
					int right =(int)(stack.pop());//value for right operator
					int left = (int)(stack.pop());//value for left of operator
					
					if(n=='+')//set result according to operator
						result = left+right;
					else if(n=='-')
						result = left-right;
					else if(n=='∗')
						result = left*right;
					else if(n=='/')
						result = left/right;
	 
					stack.push((Integer)result);
				}
			}

			while(!stack.isEmpty())
				result = (int)stack.pop();//append what is left in stack to the result
			return result;
	}
	
	
	static final int MAX_WIDTH = 398, MAX_HEIGHT = 440;
	
	public static void main(String arg[]){
		JFrame frame = new CalculatorFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(MAX_WIDTH,MAX_HEIGHT);	
		frame.setBackground(Color.white);		
		frame.setResizable(false);				
		frame.setVisible(true);
		
	}
}