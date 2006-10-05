/**
   Javacyc is a Java interface for the Pathway Tools software.  Pathway Tools
   needs to run a special socket server for Javacyc to work.

   Javacyc uses J-BUDS for Unix domain sockets.

   Javacyc accesses Generic Frame Protocol (GFP) and Pathway Tools Internal
   Lisp (PTIL) functions.  These functions either return a boolean, a string,
   or a list.  The lists returned are always an ArrayList of strings unless
   specified otherwise.

   @author Thomas Yan
   Copyright (c) 2003; The Arabidopsis Information Resource
   See README file for license details
*/

import java.io.*;
import java.util.*;

public class Javacyc
{
    /**
       Constructor for Javacyc.
       @param organism the name of the organism
    */
    public Javacyc(String organism)
    {
	this.organism = organism;
	socketName = "/tmp/ptools-socket";
    }


    /**
       Get a socket connection with Pathway Tools using a Unix domain
       socket.
    */
    private void makeSocket() {
	try {
	    // Create socket and connect to the server
	    uds = new UnixDomainSocket(socketName);
	    out = new PrintWriter(uds.getOutputStream(), true);
	    in = new BufferedReader(
				    new InputStreamReader(uds.getInputStream()));
	} catch (IOException e) { 
	    e.printStackTrace();
	    throw new RuntimeException(); 
	}
    }

    /**
       Close the socket connection with Pathway Tools.
       @throws IOException if the socket connection cannot be closed
    */
    private void closeSocket() {
	try {
	    uds.close();
	    out.close();
	    in.close();
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new RuntimeException(); 
	}
    }


    // Methods that call the GFP Functions

    /**
       Calls the GFP function, get-slot-values.
       @param frame a frame id or object
       @param slotName a slot name
       @return an ArrayList of all values of slot of frame
    */
    public ArrayList getSlotValues(String frame, String slotName)
    {
	return callFuncArray("get-slot-values '" + frame + " '" + slotName);
    }

    /**
       Calls the GFP function get-slot-value.
       @param frame a frame id or object
       @param slotName a slot name
       @return the first value of slot of frame
    */
    public String getSlotValue(String frame, String slotName)
    {
	return callFuncString("get-slot-value '" + frame + " '" + slotName);
    }

    /**
       Calls the GFP function, get-class-slot-slotvalue.
       @param className the class
       @param slotName a slot name
       @param slotValue a slot value
       @return an ArrayList with the object names returned by 
       get-class-slot-slotvalue
    */
    public ArrayList getClassSlotSlotvalue(String className, String slotName,
					   String slotValue)
    {
	return callFuncArray("get-class-slot-slotvalue '" + className + " '" 
			     + slotName + " '" + slotValue);
    }

    /**
       Calls the GFP function, get-class-all-instances
       @param classFrame a class frame
       @return an ArrayList of all frames that are direct or indirect instances
       of classFrame
    */
    public ArrayList getClassAllInstances(String classFrame)
    {
	return callFuncArray("get-class-all-instances '" + classFrame);
    }

    /**
       Calls the GFP function, instance-all-instance-of-p
       @param classFrame  a class frame
       @param instance an instance frame
       @return true if instance is a direct or indirect child of class
    */
    public boolean instanceAllInstanceOfP(String classFrame, String instance)
    {
	return callFuncBool("instance-all-instance-of-p '" + instance + " '"
			    + classFrame);
    }

    /**
       Calls the GFP function, member-slot-value-p
       @param frame a frame id or object
       @param slot a slot name
       @param value a slot value 
       @return true if value is one of the values of slot of frame
    */
    public boolean memberSlotValueP(String frame, String slot, String value)
    {
	return callFuncBool("member-slot-value-p '" + frame + " '" + slot 
			    + " '" + value);
    }

    /**
       Calls the GFP function, current-kb
       @return the currently selected KB
    */
    public String currentKB()
    {
	return organism;
    }

    /**
       Calls the GFP function, put-slot-values
       @param frame a frame id or object
       @param slot a slot name
       @param values a set of values
       @return any results from the server in an ArrayList
    */
    public ArrayList putSlotValues(String frame, String slot, String values)
    {
	return callFuncArray("put-slot-values '" + frame + " '" + slot
			     + " '" + values);
    }

    /**
       Calls the GFP function, put-slot-value
       @param frame a frame id or object
       @param slot a slot name
       @param value a value
       @return any results from the server in an ArrayList
    */
    public ArrayList putSlotValue(String frame, String slot, String value)
    {
	return callFuncArray("put-slot-value '" + frame + " '" + slot 
			     + " '" + value);
    }

    /**
       Calls the GFP function, add-slot-value
       @param frame a frame id or object
       @param slot a slot name
       @param value a value
       @return any results from the server in an ArrayList
    */
    public ArrayList addSlotValue(String frame, String slot, String value)
    {
	return callFuncArray("add-slot-value '" + frame + " '" + slot
			     + " '" + value);
    }

    /**
       Calls the GFP function, replace-slot-value
       @param frame a frame id or object
       @param slot a slot name
       @param oldValue the value to be replaced
       @param newValue the value to replace oldValue with
       @return any results from the server in an ArrayList
    */
    public ArrayList replaceSlotValue(String frame, String slot, 
				      String oldValue, String newValue)
    {
	return callFuncArray("replace-slot-value '" + frame + " '" + slot
			     + " '" + oldValue + " '" + newValue);
    }

    /**
       Calls the GFP function, remove-slot-value
       @param frame a frame id or object
       @param slot a slot name
       @return any results from the server in an ArrayList
    */
    public ArrayList removeSlotValue(String frame, String slot)
    {
	return callFuncArray("remove-slot-value '" + frame + " '" + slot);
    }

    /**
       Calls the GFP function, coercible-to-frame-p
       @param thing a thing
       @return true if thing is a frame object, the name of a frame in kb, or
       handle of frame in kb
    */
    public boolean coercibleToFrameP(String thing)
    {
	return callFuncBool("coercible-to-frame-p '" + thing);
    }

    /**
       Calls the GFP function, class-all-type-of-p
       @param classFrame a class frame
       @param instance an instance
       @return true if instance is an all-instance of classFrame
    */
    public boolean classAllTypeOfP(String classFrame, String instance)
    {
	return callFuncBool("class-all-type-of-p '" + classFrame + " '" 
			    + instance);
    }

    /**
       Calls the GFP function, get-instance-direct-types
       @param instance an instance
       @return an ArrayList of the direct types of instance
    */
    public ArrayList getInstanceDirectTypes(String instance)
    {
	return callFuncArray("get-instance-direct-types '" + instance);
    }

    /**
       Calls the GFP function, get-instance-all-types
       @param instance an instance
       @return an ArrayList of all-types of instance
    */
    public ArrayList getInstanceAllTypes(String instance)
    {
	return callFuncArray("get-instance-all-types '" + instance);
    }

    /**
       Calls the GFP function, get-frame-slots
       @param frame a frame id or object
       @return an ArrayList of instance or template slots associated with frame
    */
    public ArrayList getFrameSlots(String frame)
    {
	return callFuncArray("get-frame-slots '" + frame);
    }

    /**
       Calls the GFP function, put-instance-types
       @param instance an instance
       @param newTypes the classes that instances becomes an instance of
       @return the results from the server in an ArrayList
    */
    public ArrayList putInstanceTypes(String instance, String newTypes)
    {
	return callFuncArray("put-instance-types '" + instance + " '" 
			     + newTypes);
    }

    /**
       Calls the GFP function, save-kb
       @return the results from the server in an ArrayList
    */
    public ArrayList saveKB()
    {
	return callFuncArray("save-kb");
    }

    /**
       Calls the GFP function, revert-kb
       @return the results from the server in an ArrayList
    */
    public ArrayList revertKB()
    {
	return callFuncArray("revert-kb");
    }

    /**
       Calls the GFP function, find-indexed-frame
       @param datum a datum
       @param className a class
       @return the results from the server in an ArrayList.  Some of the
       elements in the returned ArrayList may be ArrayLists themselves.
    */
    public ArrayList findIndexedFrame(String datum, String className)
    {
	return callFuncArray("multiple-value-list (find-indexed-frame ' "
			     + datum + " '" + className);
    }

    // Methods that call Pathway-Tools internal lisp (PTIL) functions

    /**
       Changes the organism.  Does not make a call to the select-organism
       lisp function in Pathway-Tools.  Does not make any calls to 
       Pathway-Tools functions.  The organism is prefixed to every query
       sent to the socket server.
       @param newOrganism the new organism
    */
    public void selectOrganism(String newOrganism)
    {
	organism = newOrganism;
    }

    /**
       Calls PTIL function, all-pathways
       @return an ArrayList containing all pathways in the current organism
    */
    public ArrayList allPathways()
    {
	return callFuncArray("all-pathways");
    }

    /**
       Calls PTIL function, all-orgs
       @return an ArrayList of orgkb-defstructs for all organisms currently 
       known to the Pathway Tools
    */
    public ArrayList allOrgs()
    {
	return callFuncArray("all-orgs");
    }

    /**
       Calls PTIL function, all-rxns
       @return an ArrayList of reactions in the current organism
    */
    public ArrayList allRxns()
    {
	return callFuncArray("all-rxns");
    }

    /**
       Calls the PTIL function, genes-of-reaction
       @param rxn a reaction frame
       @return an ArrayList of all genes that code for enzymes that catalyze
       the reaction rxn
    */
    public ArrayList genesOfReaction(String rxn)
    {
	return callFuncArray("genes-of-reaction '" + rxn);
    }

    /**
       Calls the PTIL function, substrates-of-reaction
       @param rxn a reaction frame
       @return an ArrayList of all substrates of the reaction rxn
    */
    public ArrayList substratesOfReaction(String rxn)
    {
	return callFuncArray("substrates-of-reaction '" + rxn);
    }

    /**
       Calls the PTIL function, products-of-reaction
       This is a hypothetical function that may not exist.
       @param rxn a reaction frame
       @return an ArrayList of all products of the reaction rxn
    */
    public ArrayList productsOfReaction(String rxn)
    {
	return callFuncArray("products-of-reaction '" + rxn);
    }

    /**
       Calls the PTIL function, enzymes-of-reaction
       @param rxn a reaction frame
       @return an ArrayList of all enzymes that catalyze the reaction rxn
    */
    public ArrayList enzymesOfReaction(String rxn)
    {
	return callFuncArray("enzymes-of-reaction '" + rxn);
    }

    /**
       Calls the PTIL function, reaction-reactants-and-products
       @param rxn a reaction frame
       @param pwy a pathway frame
       @return an ArrayList containing the reactants of rxn and the products
       of rxn.  Some of the elements of the returned ArrayList may be 
       ArrayLists themselves.
    */
    public ArrayList reactionReactantsAndProducts(String rxn, String pwy)
    {
	return callFuncArray("multiple-value-list (" +
			     "reaction-reactants-and-products '" + rxn +
			     " '" + pwy + ")");
    }

    /**
       Calls the PTIL function, get-predecessors
       @param rxn a reaction frame
       @param pwy a pathway frame
       @return an ArrayList of all reactions that are direct predecessors
       of rxn in pwy
    */
    public ArrayList getPredecessors(String rxn, String pwy)
    {
	return callFuncArray("get-predecessors '" + rxn + " '" + pwy);
    }

    /**
       Calls the PTIL function, get-successors
       @param rxn a reaction frame
       @param pwy a pathway frame
       @return an ArrayList of all reactions that are direct successors of
       rxn in pwy
    */
    public ArrayList getSuccessors(String rxn, String pwy)
    {
	return callFuncArray("get-successors '" + rxn + " '" + pwy);
    }

    /**
       Calls the PTIL function, get-reaction-list
       @param pwy a pathway frame
       @return an ArrayList of the reactions in pwy
    */
    public ArrayList getReactionList(String pwy)
    {
	return callFuncArray("get-reaction-list '" + pwy);
    }

    /**
       Calls the PTIL function, genes-of-pathway
       @param pwy a pathway frame
       @return an ArrayList of all genes that code for enzymes that catalyze a
       reaction in the pathway pwy
    */
    public ArrayList genesOfPathway(String pwy)
    {
	return callFuncArray("genes-of-pathway '" + pwy);
    }

    /**
       Calls the PTIL function, enzymes-of-pathway
       @param pwy a pathway frame
       @return an ArrayList of all enzymes that catalyze a reaction in pwy
    */
    public ArrayList enzymesOfPathway(String pwy)
    {
	return callFuncArray("enzymes-of pathway '" + pwy);
    }

    /**
       Calls the PTIL function, compounds-of-pathway
       @param pwy a pathway frame
       @return an ArrayList of of all substrates of reactions of pwy, with 
       duplicates removed
    */
    public ArrayList compoundsOfPathway(String pwy)
    {
	return callFuncArray("compounds-of-pathway '" + pwy);
    }

    /**
       Calls the PTIL function, substrates-of-pathway
       @param pwy a pathway frame
       @return an ArrayList of ArrayLists that contain the values returned
       by Pathway Tools
    */
    public ArrayList substratesOfPathway(String pwy)
    {
	return callFuncArray("multiple-value-list (substrates-of-pathway '" 
			     + pwy + ")");
    }

    /**
       Calls the PTIL function, all-transcription-factors
       @return all transcription factors in the current organism
    */
    public ArrayList allTranscriptionFactors()
    {
	return callFuncArray("all-transcription-factors");
    }

    /**
       Calls the PTIL function, transcription-factor?
       @param protein a protein
       @return true if protein is a trascription factor in the current
       organism
    */
    public boolean isTranscriptionFactor(String protein)
    {
	return callFuncBool("transcription-factor? '" + protein);
    }

    /**
       Calls the PTIL function, all-cofactors
       @return an ArrayList of all cofactors used by enzymes in the current
       organism
    */
    public ArrayList allCofactors()
    {
	return callFuncArray("all-cofactors");
    }

    /**
       Calls the PTIL function, all-modulators
       @return an ArrayList of all modulators that enzymes in the current 
       organism are sensitive to
    */
    public ArrayList allModulators()
    {
	return callFuncArray("all-modulators");
    }

    /**
       Calls the PTIL function, monomers-of-protein
       @param protein a protein
       @return an ArrayList of monomers that are subunits of protein
    */
    public ArrayList monomersOfProtein(String protein)
    {
	return callFuncArray("monomers-of-protein '" + protein);
    }

    /**
       Calls the PTIL function, components-of-protein
       @param protein a protein
       @return an ArrayList of components and their coefficients.  Some of 
       the elements in the returned ArrayList may be ArrayLists themselves.
    */
    public ArrayList componentsOfProtein(String protein)
    {
	return callFuncArray("multiple-value-list (components-of-protein '" 
			     + protein + ")");
    }

    /**
       Calls the PTIL function, genes-of-protein
       @param protein a protein
       @return an ArrayList of genes that code for protein and all of the
       subunits of protein
    */
    public ArrayList genesOfProtein(String protein)
    {
	return callFuncArray("genes-of-protein '" + protein);
    }

    /**
       Calls the PTIL function, reactions-of-enzyme
       @param enzyme an enzyme
       @return an ArrayList of all reactions that enzyme is linked to via
       enzymatic reactions
    */
    public ArrayList reactionsOfEnzyme(String enzyme)
    {
	return callFuncArray("reactions-of-enzyme '" + enzyme);
    }

    /**
       Calls the PTIL function, enzyme?
       @param protein a protein
       @return true if the specified protein is an enzyme
    */
    public boolean isEnzyme(String protein)
    {
	return callFuncBool("enzyme? '" + protein);
    }

    /**
       Calls the PTIL function, transporter?
       @param protein a protein
       @return true if the specified protein is a transporter
    */
    public boolean isTransporter(String protein)
    {
	return callFuncBool("transporter? '" + protein);
    }

    /**
       Calls the PTIL function, containers-of
       @param protein a protein
       @return a list of all containers of protein, including itself
    */
    public ArrayList containersOf(String protein)
    {
	return callFuncArray("containers-of '" + protein);
    }

    /**
       Calls the PTIL function, modified-forms
       @param protein a protein
       @return a list of modified forms of protein, including itself
    */
    public ArrayList modifiedForms(String protein)
    {
	return callFuncArray("modified-forms '" + protein);
    }

    /**
       Calls the PTIL function, modified-containers
       @param protein a protein
       @return a list of all containers of a protein including itself and all
       modified forms of a protein
    */
    public ArrayList modifiedContainers(String protein)
    {
	return callFuncArray("modified-containers '" + protein);
    }    

    /**
       Calls the PTIL function, top-containers
       @param protein a protein
       @return a list of all containers of protein that have no containers
    */
    public ArrayList topContainers(String protein)
    {
	return callFuncArray("top-containers '" + protein);
    }

    /**
       Calls the PTIL function, reactions-of-protein
       @param protein a protein
       @return an ArrayList of all reactions catalyzed by protein or subuinits
       of protein
    */
    public ArrayList reactionsOfProtein(String protein)
    {
	return callFuncArray("reactions-of-protein '" + protein);
    }

    /**
       Calls the PTIL function, regulon-of-protein
       @param protein a protein
       @return an ArrayList of transcription units regulated by any modified
       or unmodified form of protein
    */
    public ArrayList regulonOfProtein(String protein)
    {
	return callFuncArray("regulon-of-protein '" + protein);
    }

    /**
       Calls the PTIL function, transcription-units-of-protein
       @param protein a protein
       @return an ArrayList of transcripton units activated or inhibited by
       the supplied protein or modified protein frame
    */
    public ArrayList transcriptionUnitsOfProtein(String protein)
    {
	return callFuncArray("transcription-units-of-protein '" + protein);
    }

    /**
       Calls the PTIL function, regulator-proteins-of-transcription-unit
       @param tu a transcription unit
       @return an ArrayList of proteins that bind to binding sites within tu
    */
    public ArrayList regulatorProteinsOfTranscriptionUnit(String tu)
    {
	return callFuncArray("regulator-proteins-of-transcription-unit '"
			     + tu);
    }

    /**
       Calls the PTIL function, enzymes-of-gene
       @param gene a gene
       @return an ArrayList of all enzymes coded for by gene
    */
    public ArrayList enzymesOfGene(String gene)
    {
	return callFuncArray("enzymes-of-gene '" + gene);
    }

    /**
       Calls the PTIL function, all-products-of-gene
       @param gene a gene
       @return an ArrayList of all gene products of gene including those that
       are not enzymes
    */
    public ArrayList allProductsOfGene(String gene)
    {
	return callFuncArray("all-products-of-gene '" + gene);
    }

    /**
       Calls the PTIL function, reactions-of-gene
       @param gene a gene
       @return an ArrayList of all reactions catalyzed by proteins that are 
       products of gene
    */
    public ArrayList reactionsOfGene(String gene)
    {
	return callFuncArray("reactions-of-gene '" + gene);
    }

    /**
       Calls the PTIL function, pathways-of-gene
       @param gene a gene
       @return an ArrayList of all pathways containing reactions that are
       catalyzed by proteins that are products of gene
    */
    public ArrayList pathwaysOfGene(String gene)
    {
	return callFuncArray("pathways-of-gene '" + gene);
    }

    /**
       Calls the PTIL function, chromosome-of-gene
       @param gene a gene
       @return a String containing the chromosome on which gene resides
    */
    public String chromosomeOfGene(String gene)
    {
	return callFuncString("chromosome-of-gene '" + gene);
    }

    /**
       Calls the PTIL function, transcription-units-of-gene
       @param gene a gene
       @return an ArrayList of all transcription units that form the operon
       containing gene
    */
    public ArrayList transcriptionUnitsOfGene(String gene)
    {
	return callFuncArray("transcription-units-of-gene '" + gene);
    }

    /**
       Calls the PTIL function, transcription-unit-promoter
       @param tu a transcription unit
       @return a string containing the promoter of tu
    */
    public String transcriptionUnitPromoter(String tu)
    {
	return callFuncString("transcription-unit-promoter '" + tu);
    }

    /**
       Calls the PTIL function, transcription-unit-genes
       @param tu a transcription unit
       @return an ArrayList of genes within the transcription unit
    */
    public ArrayList transcriptionUnitGenes(String tu)
    {
	return callFuncArray("transcription-unit-genes '" + tu);
    }

    /**
       Calls the PTIL function, transcription-unit-binding-sites
       @param tu a transcription unit
       @return an ArrayList of DNA binding sites within the transcriptional
       unit
    */
    public ArrayList transcriptionUnitBindingSites(String tu)
    {
	return callFuncArray("transcriptional-unit-binding-sites '" + tu);
    }

    /**
       Calls the PTIL function, transcription-unit-transcription-factors
       @param tu a transcription unit
       @return an ArrayList of the transcription factors that control the
       transcription unit tu
    */
    public ArrayList transcriptionUnitTranscriptionFactors(String tu)
    {
	return callFuncArray("transcription-unit-transcription-factors '"
			     + tu);
    }

    /**
       Calls the PTIL function, transcription-unit-terminators
       @param tu a transcription unit
       @return an ArrayList of the transcription terminators(s) within the
       transcription unit
    */
    public ArrayList transcriptionUnitTerminators(String tu)
    {
	return callFuncArray("transcription-unit-terminators '" + tu);
    }

    /**
       Calls the PTIL function, all-transported-chemicals
       @return an ArrayList of chemicals that are transported by the set of all
       defined transport reactions in current organism
    */
    public ArrayList allTransportedChemicals()
    {
	return callFuncArray("all-transported-chemicals");
    }

    /**
       Calls the PTIL function, reactions-of-compound
       @param cpd a chemical
       @return an ArrayList of the reactions in which cpd occurs as a 
       reactant or a product
    */
    public ArrayList reactionsOfCompound(String cpd)
    {
	return callFuncArray("reactions-of-compound '" + cpd);
    }

    /**
       Calls the PTIL function, full-enzyme-name
       @param enzyme an enzyme
       @return the full name of the enzyme
    */
    public String fullEnzymeName(String enzyme)
    {
	return callFuncString("full-enzyme-name '" + enzyme);
    }

    /**
       Calls the PTIL function, enzyme-activity-name
       @param enzyme an enzyme
       @return the enzyme activity name
    */
    public String enzymeActivityName(String enzyme)
    {
	return callFuncString("enzyme-activity-name '" + enzyme);
    }

    // Private methods for querying, retrieving results, calling functions,
    // and lisp list parsing

    /**
       Private method to call a Pathway Tools function that returns a list.
       @param func the Pathway Tools function to call
       @return an ArrayList representation of the lisp list returned by
       Pathway Tools
    */
    private ArrayList callFuncArray(String func)
    {
	makeSocket();
	try {
	    String query = wrapQuery(func);
	    sendQuery(query);
	    ArrayList results = retrieveResultsArray();
	    // 	try
	    // 	{
	    // 	    close();
	    // 	}
	    // 	catch (IOException e)
	    // 	{
	    // 	    e.printStackTrace();
	    // 	}
	    return results;
	} finally {
	    closeSocket();
	}
    }

    /**
       Private method to call a Pathway Tools function that returns a string.
       @param func the Pathway Tools function to call
       @return string returned by Pathway Tools function call
    */
    private String callFuncString(String func)
    {
	makeSocket();
	try {
	    String query = "(with-organism (:org-id '" + organism +
		") (object-name (" + func + ")))";
	    sendQuery(query);
	    String results = retrieveResultsString();
	    // 	try
	    // 	{
	    // 	    close();
	    // 	}
	    // 	catch (IOException e)
	    // 	{
	    // 	    e.printStackTrace();
	    // 	}
	    return results;
	} finally {
	    closeSocket();
	}
    }

    /**
       Private method to call a Pathway Tools function that returns a boolean.
       @param func the Pathway Tools function to call
       @return true if the result of the function call is true
    */
    private boolean callFuncBool(String func)
    {
	String result = callFuncString(func);
	if (result.equals("NIL"))
	{
	    return false;
	}
	else
	{
	    return true;
	}
    }

    /**
       Private method that wraps a query.
       @param func the function call to wrap in a query
       @return a query
    */
    private String wrapQuery(String func)
    {
	return "(with-organism (:org-id '" + organism +
	    ") (mapcar #'object-name (" + func + ")))";
    }

    /**
       Private method to send a query to Pathway Tools.
       @param query the query to send to Pathway Tools
    */
    private void sendQuery(String query)
    {
	out.println(query);
    }

    /**
       Private method to retrieve a string result.
       @return the string result
    */
    private String retrieveResultsString()
    {
	try
	{
	    ArrayList results = new ArrayList();
	    String readStr = in.readLine();
	    while (readStr != null)
		{
		    // DEBUG ONLY
		    //System.out.println(readStr);

		    results.add(readStr);
		    readStr = in.readLine();
		}
	    String retStr = (String)results.get(0);

	    // DEBUG
	    //System.out.println("0th element: " + (String)results.get(0));

	    // If retStr is surrounded by quotation marks, remove them
	    if ((retStr.startsWith("\"")) && (retStr.endsWith("\"")))
		{
		    int endIndex = retStr.length() - 1;
		    return retStr.substring(1, endIndex);
		}
	    else
		{
		    return retStr;
		}
	}
	catch (IOException e)
	    {
		e.printStackTrace();
	    }
	return null; // if an IOException has occured
    }

    /**
       Private method to retrieve an ArrayList result.
       This method is like retrieve_results in perlcyc, not perlcyc's 
       retrieve_results_array subroutine.
       @return the ArrayList result
    */
    private ArrayList retrieveResultsArray()
    {
	LinkedList tokens = tokenize();
	return parseExpr(tokens);
    }

    /**
       Private method to tokenize a lisp expression.
       @return an LinkedList containing the tokens of the lisp expression
    */
    private LinkedList tokenize()
    {
	LinkedList tokens = new LinkedList();
	try
	{
	    StreamTokenizer tokenizer = new StreamTokenizer(in);
	    tokenizer.resetSyntax();
	    tokenizer.wordChars('a', 'z');
	    tokenizer.wordChars('A', 'Z');
	    tokenizer.wordChars('\u00A0', '\u00FF');
	    tokenizer.wordChars('0', '9');
	    tokenizer.wordChars('.', '.');
	    tokenizer.wordChars('-', '-');
	    tokenizer.wordChars('/', '/');
	    tokenizer.wordChars('\'', '\'');
	    tokenizer.whitespaceChars(' ', ' ');
	    tokenizer.whitespaceChars('\n', '\n'); 
	    tokenizer.quoteChar('"');
	    int type = tokenizer.nextToken();
	    while (type != StreamTokenizer.TT_EOF)
	    {
		if (type == StreamTokenizer.TT_NUMBER)
		{
		    tokens.add(Double.toString(tokenizer.nval));
		}
		else if (type == StreamTokenizer.TT_WORD)
		{
		    tokens.add(tokenizer.sval);
		}
		else if (type == '(')
		{
		    tokens.add("(");
		}
		else if (type == '"')
		{
		    tokens.add(tokenizer.sval);
		}
		else if (type == ')')
		{
		    tokens.add(")");
		}
		else if (type == '|')
		{
		    StringBuffer buffer = new StringBuffer();
		    buffer.append("|");
		    int typeLastTok = type;
		    type = tokenizer.nextToken();
		    while (type != '|')
		    {
			if (type == StreamTokenizer.TT_WORD)
			{
			    if ((typeLastTok == StreamTokenizer.TT_WORD) ||
				(typeLastTok == StreamTokenizer.TT_NUMBER) ||
				(typeLastTok == '"'))
			    {
				buffer.append(" ");
			    }
			    buffer.append(tokenizer.sval);
			}
			else if (type == StreamTokenizer.TT_NUMBER)
			{
			    if ((typeLastTok == StreamTokenizer.TT_WORD) ||
				(typeLastTok == StreamTokenizer.TT_NUMBER) ||
				(typeLastTok == '"'))
			    {
				buffer.append(" ");
			    }
			    buffer.append(Double.toString(tokenizer.nval));
			}
			else if (type == '"')
			{
			    if ((typeLastTok == StreamTokenizer.TT_WORD) ||
				(typeLastTok == StreamTokenizer.TT_NUMBER) ||
				(typeLastTok == '"'))
			    {
				buffer.append(" ");
			    }
			    buffer.append("\"");
			    buffer.append(tokenizer.sval);
			    buffer.append("\"");
			}
			else
			{
			    buffer.append((char)tokenizer.ttype);
			}
			typeLastTok = type;
			type = tokenizer.nextToken();
		    }
		    buffer.append("|");
		    tokens.add(buffer.toString());
		}
		else
		{
		    Character c = new Character((char)tokenizer.ttype);
		    tokens.add(c.toString());
		}
		type = tokenizer.nextToken();
	    }
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
	return tokens;
    }

    /**
       Private parser method.
       @param tokens a LinkedList containing the tokens of a lisp expression
       @return an ArrayList representation of the lisp list
    */
    private ArrayList parseExpr(LinkedList tokens)
    {
	String first = (String)tokens.getFirst();
	if (first.equals("("))
	{
	    tokens.removeFirst();
	    ArrayList listElements = new ArrayList();
	    while (!((String)tokens.getFirst()).equals(")"))
	    {
		String temp = (String)tokens.getFirst();
		if (temp.equals("("))
		{
		    listElements.add(parseExpr(tokens)); // add an inner list
		}
		else if (temp.equals("NIL"))
		{
		    listElements.add("NIL"); 
		    tokens.removeFirst();
		}
		else
		{
		    listElements.add(temp);
		    tokens.removeFirst();
		}
	    }
	    tokens.removeFirst(); // remove a )
	    return listElements;
	}
	else // return empty ArrayList
	{
	    return new ArrayList();
	}
    }

    private UnixDomainSocket uds; // J-BUDS Unix domain socket
    private String socketName; // name of the socket
    private String organism; // name of the organism
    private PrintWriter out; // output to the Pathway Tools server
    private BufferedReader in; // input from the Pathway Tools server
}

