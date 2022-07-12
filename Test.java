import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


public class Test
{
	public static Test test;

	public static void main(String[] args)
	{
		test = new Test();

		String formula = "x ^ 2";
		Double rangeStart = 3.0;
		Double rangeEnd = 6.0;
		int split = 10000000;

		Double result = 0.0;
		Double n = (rangeEnd - rangeStart) / split;
		Formula f = test.splitElements(formula.split(" "));
		for(int i = 1; i <= split; i++)
		{
			Double r = f.calculate(rangeStart + n * i) * n;
//			System.out.println("\n" + (rangeStart + n * i) + " " + r + "\n");
			result += r;
		}

		System.out.println(result);

//		String formula = "x ^ ( 10 + 2 / 5 ) + 5 * x ^ 2 + 100";
//		Formula f = test.splitElements(formula.split(" "));
//		test.printFormula(f);
//		System.out.println("\n" + f.calculate( 1));
	}

	private HashMap<Character, Integer> operator = new HashMap<>();
	public Test()
	{

		operator.put('+', 1);
		operator.put('-', 1);
		operator.put('*', 2);
		operator.put('/', 2);
		operator.put('^', 3);
		Test.test = this;
	}
	public int operatorPriority(char operator)
	{
		return this.operator.get(operator);
	}

	private void printFormula(Formula formula)
	{
		for(int i = 0; i < formula.size(); i++)
		{
			CalElement e = formula.getValue(i);
			if(e instanceof Number)
			{
				System.out.print(((Number)e).getNumber() + " ");
			}
			if(e instanceof Operator)
			{
				System.out.print(((Operator)e).getOperator() + " ");
			}
			if(e instanceof Variable)
			{
				System.out.print(((Variable)e).getVariable() + " ");
			}
			if(e instanceof Formula)
			{
				System.out.print("( ");
				printFormula((Formula)e);
				System.out.print(") ");
			}
		}
	}

	public Formula splitElements(String[] formula)
	{
		return splitElements(formula, 0);
	}
	public Formula splitElements(String[] formula, int startIndex)
	{
		Formula f = new Formula();
		for(int i = startIndex; i < formula.length; i++)
		{
			switch (formula[i].charAt(0))
			{
				case '9':
				case '8':
				case '7':
				case '6':
				case '5':
				case '4':
				case '3':
				case '2':
				case '1':
				case '0':
					f.addElement(new Number(Double.valueOf(formula[i])));
					break;
				case '*':
				case '/':
				case '+':
				case '-':
				case '^':
					f.addElement(new Operator(formula[i].charAt(0)));
					break;
				case 'x':
					f.addElement(new Variable('x'));
					break;
				case '(':
					Formula f_temp = splitElements(formula, i + 1);
					i += f_temp.size() + 1;
					f.addElement(f_temp);
					break;
				case ')':
					return f;
			}
		}
		return f;
	}

}




class CalElement
{
	public Object getValue()
	{
		return null;
	}
}

class Operator extends CalElement
{
	public Operator(char o)
	{
		this.operator = o;
	}
	private Character operator;
	public Character getOperator()
	{
		return operator;
	}
	public Object getValue()
	{
		return operator;
	}
}

class Number extends CalElement
{
	public Number(double n)
	{
		this.number = n;
	}
	private Double number;
	public Double getNumber()
	{
		return number;
	}
	public Object getValue()
	{
		return number;
	}
}

class Variable extends CalElement
{
	public Variable(char v)
	{
		this.variable = v;
	}
	private Character variable;
	public Character getVariable()
	{
		return variable;
	}
	public Object getValue()
	{
		return variable;
	}
}

class Formula extends CalElement
{
	private ArrayList<CalElement> formula = new ArrayList<>();

	public void addElement(CalElement element)
	{
		formula.add(element);
	}
	public CalElement getValue(int index)
	{
		return formula.get(index);
	}
	public Double calculate(double x)
	{
		Stack<Number> numberElements = new Stack<>();
		Stack<Operator> operatorElement = new Stack<>();
		for(int i = 0; i < formula.size(); i++)
		{
			// System.out.println("" + numberElements.size() + " " + operatorElement.size());
			CalElement element;
			if(formula.get(i) instanceof Variable)
			{
				numberElements.add(new Number(x));
				continue;
			}
			if(formula.get(i) instanceof Formula)
			{
				// System.out.println("");
				numberElements.add(new Number(((Formula)formula.get(i)).calculate(x)));
				// System.out.println("");
				continue;
			}


			if(formula.get(i) instanceof Operator)
			{
				if(operatorElement.empty())
				{
					operatorElement.add((Operator) formula.get(i));
					continue;
				}
				if(Test.test.operatorPriority(operatorElement.peek().getOperator()) > Test.test.operatorPriority(((Operator)formula.get(i)).getOperator()))
				{
					if(!calculate_(numberElements, operatorElement))
						return null;
					operatorElement.add(((Operator)formula.get(i)));
					continue;
				}
				operatorElement.add(((Operator) formula.get(i)));

			}
			if(formula.get(i) instanceof Number)
			{
				numberElements.add((Number) formula.get(i));
				continue;
			}
		}
		if(numberElements.size() > 1) calculate_(numberElements, operatorElement);
		// System.out.println("end " + numberElements.size() + " " + operatorElement.size());
		return numberElements.peek().getNumber();
	}
	public boolean calculate_(Stack<Number> number, Stack<Operator> operators)
	{
//		if(number.size() - 1 != operators.size())
//		{
//			return false;
//		}

		while(!number.empty() && !operators.empty())
		{
			char operator = operators.pop().getOperator();
			double num1 = number.pop().getNumber(), num2 = number.pop().getNumber();
			switch (operator)
			{
				case '*':
					number.add(new Number(num2 * num1));
					break;
				case '/':
					number.add(new Number(num2 / num1));
					break;
				case '+':
					number.add(new Number(num2 + num1));
					break;
				case '-':
					number.add(new Number(num2 - num1));
					break;
				case '^':
					number.add(new Number(Math.pow(num2, num1)));
					break;
			}
		}
		// System.out.println("cal_  " + number.peek().getNumber());
		return true;
	}

	public int size()
	{
		return formula.size();
	}
}