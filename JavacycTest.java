/**
   A program to test Javacyc.
*/

import java.util.ArrayList;
import java.io.*;

public class JavacycTest
{
    public static void printLists(ArrayList list)
    {
	for (int i = 0; i < list.size(); i++)
	{
	    Object obj = list.get(i);
	    if (obj instanceof String)
	    {
		String str = (String)obj;
		System.out.println(str);
	    }
	    else if (obj instanceof ArrayList)
	    {
		System.out.println("*begin inner list*");
		ArrayList aList = (ArrayList)obj;
		printLists(aList);
		System.out.println("*end inner list*");
	    }
	    else
	    {
		System.out.println("WARNING THIS SHOULD NOT HAPPEN!");
	    }
	}
    }

    public static void main(String[] args) throws IOException
    {
	Javacyc cyc = new Javacyc("ARA");
	BufferedReader in = new BufferedReader(
					       new InputStreamReader(System.in));

	// test a function that returns a boolean
	System.out.println("Testing a function that returns a boolean: "
			   + "coercible-to-frame-p");
	System.out.print("Enter a value for frame: ");
	String thing = in.readLine();
	boolean result1 = cyc.coercibleToFrameP(thing);
	if (result1)
	    {
		System.out.println("The result: true\n");
	    }
	else
	    {
		System.out.println("The result: false\n");
	    }

	// test a function that returns a string
	System.out.println("Testing a function that returns a string: "
			   + "full-enzyme-name");
	System.out.print("Enter a value for enzyme: ");
	String enzyme = in.readLine();
	String result2 = cyc.fullEnzymeName(enzyme);
	System.out.println("The result: " + result2 +"\n");

	// test a function that returns an ArrayList
	System.out.println("Testing a function that returns an ArrayList: "
			   + "genes-of-pathway 'PWY-581");
	ArrayList result3 = cyc.genesOfPathway("PWY-581");
	System.out.println("The returned values: ");
	for (int i = 0; i < result3.size(); i++)
	    {
		String rxn = (String)result3.get(i);
		System.out.println(rxn);
	    }

	// test a function that returns multiple lists
	System.out.println("\nTesting a function that returns multiple"
			   + " lists: reaction-reactants-and-products");
	System.out.print("Enter a value for reaction: ");
	String rxn = in.readLine();
	System.out.print("Enter a value for pathway: ");
	String pwy = in.readLine();
	ArrayList result4 = cyc.reactionReactantsAndProducts(rxn, pwy);
	System.out.println("The returned values: ");
	printLists(result4);
    }
}
