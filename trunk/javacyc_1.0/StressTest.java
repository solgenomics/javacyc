import java.util.*;

public class StressTest {
    public static void main(String[] args) {
	Javacyc cyc = new Javacyc("ARA");
	doTrialRun(cyc);
    }

    
    private static void doTrialRun(Javacyc cyc) {
	ArrayList result_enzrxn =
	    cyc.getClassAllInstances("|Enzymatic-Reactions|");
	for (int i = 0; i < result_enzrxn.size(); i++) {
	    String er = (String)result_enzrxn.get(i);
	    System.out.println(i);
	    System.out.println(cyc.getSlotValue(er,"Reaction"));
	    System.out.println(cyc.getSlotValue(er,"Reaction-Direction"));
	}
    }

}

