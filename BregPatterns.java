import java.util.LinkedList;
import java.util.List;



	 public class BregPatterns
     {
    	public static final String pAndBreg = "(&&)\\s*\\(*\\s*!?ypbreg\\s*\\)*";
    	public static final String pBregAnd = "\\s*\\(*\\s*!?ypbreg\\s*\\)*\\s*(&&)";
    	public static final String pOrBreg = "(\\|\\|)\\s*\\(*\\s*!?ypbreg\\s*\\)*";
    	public static final String pBregOr = "\\s*\\(*\\s*!?ypbreg\\s*\\)*\\s*(\\|\\|)";
    	public static final String pBregOnly = "\\s*\\(*\\s*!?ypbreg\\s*\\)*";
    	public static final String pAssign =  "\\s*(\\w*)\\s*=\\s*(!?ypbreg)";
    	
    	public static final String pEqualTrue1 = "(ypbreg)(\\s*==\\s*true)";
    	public static final String pEqualTrue2 = "(ypbreg)(\\s*==\\s*BREG_BOOLEAN_TRUE)";
    	public static final String pEqualTrue3 = "(true\\s*==\\s*)(ypbreg)";
    	public static final String pEqualTrue4 = "(BREG_BOOLEAN_TRUE\\s*==\\s*)(ypbreg)";
    	public static final String pEqualTrue5 = "(ypbreg)(\\s*!=\\s*flase)";
    	public static final String pEqualTrue6 = "(ypbreg)(\\s*!=\\s*BREG_BOOLEAN_FALSE)";
    	public static final String pEqualTrue7 = "(flase\\s*!=\\s*)(ypbreg)";
    	public static final String pEqualTrue8 = "(BREG_BOOLEAN_FALSE\\s*!=\\s*)(ypbreg)";
    	
    	public static final String pEqualFalse1 = "(ypbreg)(\\s*==\\s*flase)";
    	public static final String pEqualFalse2 = "(ypbreg)(\\s*==\\s*BREG_BOOLEAN_FALSE)";
    	public static final String pEqualFalse3 = "(flase\\s*==\\s*)(ypbreg)";
    	public static final String pEqualFalse4 = "(BREG_BOOLEAN_FALSE\\s*==\\s*)(ypbreg)";
    	public static final String pEqualFalse5 = "(ypbreg)(\\s*!=\\s*true)";
    	public static final String pEqualFalse6 = "(ypbreg)(\\s*!=\\s*BREG_BOOLEAN_TRUE)";
    	public static final String pEqualFalse7 = "(true\\s*!=\\s*)(ypbreg)";
    	public static final String pEqualFalse8 = "(BREG_BOOLEAN_TRUE\\s*!=\\s*)(ypbreg)";
    	public static List<String> GetEqualTruePatterns(String BregRegStrValue)
    	{
    		List<String> patternList = new LinkedList<String>();
    		patternList.add(pEqualTrue1.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualTrue2.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualTrue3.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualTrue4.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualTrue5.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualTrue6.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualTrue7.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualTrue8.replace("ypbreg", BregRegStrValue));
    		return patternList;
    	}
    	public static List<String> GetEqualFalsePatterns(String BregRegStrValue)
    	{
    		List<String> patternList = new LinkedList<String>();
    		patternList.add(pEqualFalse1.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualFalse2.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualFalse3.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualFalse4.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualFalse5.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualFalse6.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualFalse7.replace("ypbreg", BregRegStrValue));
    		patternList.add(pEqualFalse8.replace("ypbreg", BregRegStrValue));
    		return patternList;
    	}
    	public static List<String> GetIfOPPatterns(String BregRegStrValue)
    	{
    		List<String> patternList = new LinkedList<String>();
    		patternList.add(pAndBreg.replace("ypbreg", BregRegStrValue));
    		patternList.add(pBregAnd.replace("ypbreg", BregRegStrValue));
    		patternList.add(pBregOr.replace("ypbreg", BregRegStrValue));
    		patternList.add(pBregOr.replace("ypbreg", BregRegStrValue));
    		return patternList;
    	}
    	public static String GetAssignPattern(String BregRegStrValue)
    	{
    		return pAssign.replace("ypbreg", BregRegStrValue);
    	}
    	public static String GetBregOnlyPattern(String BregRegStrValue)
    	{
    		return pBregOnly.replace("ypbreg", BregRegStrValue);
    	}
    }
