import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class BregBlockHelper {
	 public enum BOPType{

		 CURRENT_LINE_DONE, CURRENT_LINE_REMOVE_OP, CURRENT_LINE_REMOVE_OP_NEG, 
		 NEXT_OP, NEXT_ROUND
		 
	 }

     public boolean mybregValue;
	 public ArrayList<String> strs;
	 public ArrayList<String> blockStrs = new ArrayList<String>();
	 public ArrayList<String> ifBlockStrs = new ArrayList<String>();
	 public ArrayList<String> elseBlockStrs = new ArrayList<String>();
	 public  String Breg;
	 public  String BregValue;
	 private  String BregRegStrValue;
	 public boolean isModified;
	 private StrsTraversalHelper strsTraversalHelper;
	 private BracePairHelper braceHelper;
	 private FormatHelper formathelper;
	 
	public BregBlockHelper(ArrayList<String> strs,String bregStr, 
	               String bregValueStr, boolean bregValue)
	{
		this.strs = strs;
		this.Breg = bregStr;
		this.BregValue = bregValueStr; 
		this.BregRegStrValue = bregValueStr.replace("()", "\\(\\)");// for regex
		this.mybregValue = bregValue;
		this.strsTraversalHelper = StrsTraversalHelper.getInstance(strs);
		this.braceHelper = new BracePairHelper(strs);
		this.formathelper = new FormatHelper(strs, bregStr, bregValueStr);
	}
	 public  void printArr()
     {
    	for(int i = 0; i< strs.size(); i++)
    	{
    		System.out.println(strs.get(i));
    	}
     }
     

	private BOPType CheckAndReplaceIFEqualStatement(int preLine, int nextLine, String pStr)
	{
		
		List<String> equalPatternTrueList = BregPatterns.GetEqualTruePatterns(BregRegStrValue);
		List<String> equalPatternFalseList = BregPatterns.GetEqualFalsePatterns(BregRegStrValue);
	
        for(int i = 0; i<equalPatternTrueList.size(); i++)
        {
        	String patternStr = equalPatternTrueList.get(i);
            Pattern r = Pattern.compile(patternStr);

            Matcher m = r.matcher(pStr);
            if (m.find()) {
            	pStr = pStr.replaceFirst(patternStr, "$1");
              	//update "global" array list
            	formathelper.UpdateStrs(pStr, preLine, nextLine);
            	return BOPType.NEXT_ROUND;
            }
        }
        for(int i = 0; i<equalPatternFalseList.size(); i++)
        {
        	String patternStr = equalPatternTrueList.get(i);
            Pattern r = Pattern.compile(patternStr);

            Matcher m = r.matcher(pStr);
            if (m.find()) {
            	pStr = pStr.replaceFirst(patternStr, "!$1");
            	//update "global" array list
            	formathelper.UpdateStrs(pStr, preLine, nextLine);
            	return BOPType.NEXT_ROUND;
            }
        }

	    return BOPType.NEXT_OP;// not found
	}
	public BOPType CheckAndRemoveAssign(String pStr, int preLine, int nextLine, int line) throws UnsupportBregExcption
	{
    	String patternStr = BregPatterns.GetAssignPattern(BregRegStrValue);
        Pattern r = Pattern.compile(patternStr);
        String key;
        String matchedStr;
        Matcher m = r.matcher(pStr);
        if (!m.find()) {
        	return BOPType.NEXT_OP;
        }
        
       	if(m.groupCount() != 2) throw new UnsupportBregExcption("Unsupport Breg case: "+pStr);
    	else
    	{
    		matchedStr = m.group(0);
    		key = m.group(1);
    	}

       	
        String declarePattern = "bool\\s*" + key;
        PosInfo ScopeStartPos = strsTraversalHelper.GetLastStrPos("{", line, 0);
        PosInfo ScopeEndPos = braceHelper.FindNextRightBrace(line, 0);
        
        //remove declare
        int index = ScopeStartPos.line;
        if(index == -1) index ++;
        
        int dindex = -1;
        while(index < ScopeEndPos.line)
        {
        	index ++;
        	String clineStr = strs.get(index);
        	if(!formathelper.GetMatchSubStr(clineStr,declarePattern).isEmpty())
        	{
        		dindex = index;
                break;
        	}
        }
        String myBregStr = BregValue;
       	if(matchedStr.contains("!"))
       	{
       		myBregStr = "!" + BregValue;
       	}

       	PosInfo posAfterAssign = new PosInfo(nextLine, 0);
       	formathelper.ReplaceStrInRange(posAfterAssign, ScopeEndPos, key, myBregStr); // can not from scope start, it will replace the declare line
        // remove declare and assign line
        if(dindex>line) throw new UnsupportBregExcption("Unsupport Breg case: "+pStr);
        

       	strs.remove(line);
        if(dindex < line && dindex >= 0)
        	strs.remove(dindex);
    
        index = 0;
        if(dindex == -1) // didn't find inside scope, search whole file
        {
        	while(index < strs.size() -1)
        	{
        		index ++;
        		String currStr = strs.get(index);
        		if(!formathelper.GetMatchSubStr(currStr,declarePattern).isEmpty()) 
        		{
        			if(dindex == -1) dindex = index;
        			else
        			{
        				throw new UnsupportBregExcption("Unsupport Breg case: "+pStr);
        			}
        		}
        	}
        	// only found once;
        	if(dindex > 0)
        		strs.remove(dindex);
        }
        
        return BOPType.NEXT_ROUND;
	}
	
	
	
	private BOPType TypeProcessAndORCase(String pStr, int sLine, int eLine)
	{
		Map<String, Integer> patternDistanceMap = new HashMap<String, Integer>();
		
		List<String> opPatternList = BregPatterns.GetIfOPPatterns(BregRegStrValue);
		for(String patternStr: opPatternList)
		{
			String matchStr = formathelper.GetMatchSubStr(pStr, patternStr);
			if(!matchStr.isEmpty())
			{
				// check which one is the nearest logic
				int distance = formathelper.GetDistanceBregAndLogicalop(matchStr);
				patternDistanceMap.put(matchStr, distance);
			}
		}
		
		String nearestStr = null;
		int min = 10000;
        for(String key : patternDistanceMap.keySet())
        {
        	int dis = patternDistanceMap.get(key);
        	if(dis < min)
        	{
        		min = dis;
        		nearestStr = key;
        	}
        }

        if(nearestStr != null && !nearestStr.isEmpty())
        {
            if((nearestStr.contains("||") && mybregValue == true)
            	||(nearestStr.contains("&&") && mybregValue == false))
            {
            	if(nearestStr.contains("!"))
            	{
                	return BOPType.CURRENT_LINE_REMOVE_OP;
            	}
            	else
            	{
            		return BOPType.CURRENT_LINE_REMOVE_OP_NEG;
            	}
            }
        	nearestStr = formathelper.RemoveUnuseBrace(nearestStr, min);
        	pStr = pStr.replace(nearestStr, "");
        	pStr = formathelper.MergeStr(pStr);
        	formathelper.UpdateStrs(pStr, sLine, eLine);
            return BOPType.CURRENT_LINE_DONE;
        }
		return BOPType.NEXT_OP;
        
	}
	
	 private BOPType TryProcessBregOnly(String pStr)
	 {
		 String pattern = BregPatterns.GetBregOnlyPattern(BregRegStrValue);
		 Pattern r = Pattern.compile(pattern);

         Matcher m = r.matcher(pStr);
         if (m.find()) {
         	if(m.group(0).contains("!"))
         		return BOPType.CURRENT_LINE_REMOVE_OP_NEG;
         	else
         		return BOPType.CURRENT_LINE_REMOVE_OP;
         }
         else
         {
         	return BOPType.NEXT_OP;
         }
	 }
	 public BOPType ProcessCurrBreg(int line) throws UnsupportBregExcption
	 {
		 BOPType btype = BOPType.NEXT_OP;
		 String currStr = strs.get(line);
		 if(currStr.contains("#include")) 
		 {
			 if( currStr.contains(Breg+".h")) // make sure it is breg.h not something like breg2.h
			 {
				 RemoveInclude(line);
			 }
			 btype = BOPType.CURRENT_LINE_DONE;
		 }
		 
		 int preLine = strsTraversalHelper.GetPreLine(line);
		 int nextLine = strsTraversalHelper.GetNextLine(line);
		 String pStr = strsTraversalHelper.GetNeighborStr(preLine, nextLine);
		 
	

		  if(btype == BOPType.NEXT_OP)
		  {
			  btype = CheckAndReplaceIFEqualStatement(preLine, nextLine, pStr);
		  }
		  if(btype == BOPType.NEXT_OP)
		  {
			  btype = CheckAndRemoveAssign(pStr, preLine, nextLine,line);
		  }
		  if(btype == BOPType.NEXT_OP)
		  {
			  btype = TypeProcessAndORCase(pStr, preLine, nextLine);
		  }
		  if(btype == BOPType.NEXT_OP)
		  {
			  btype =  TryProcessBregOnly(pStr);
		  }

			
		 if(btype == BOPType.CURRENT_LINE_REMOVE_OP)
         {
		    RemoveBlock(line,0 , !mybregValue);
		    btype = BOPType.CURRENT_LINE_DONE;
		 }
		 else if(btype == BOPType.CURRENT_LINE_REMOVE_OP_NEG)
         {
			RemoveBlock(line,0 , mybregValue);
			btype = BOPType.CURRENT_LINE_DONE;
		 }
         return btype;
		 
	 }




	 public List<String> RemoveRangeStrs(int sLine, int sCol, int eLine, int eCol)
	 {
		 //fisrt line
		 String currStr =strs.get(sLine); 
		 if(sLine == eLine)
		 {
			 String tmpStr = currStr.substring(0 , sCol) + currStr.substring(eCol);
			 strs.set(sLine, tmpStr);
			 return strs;
		 }
	     String tmp = currStr.substring(sCol);

		 if(!tmp.trim().isEmpty()) strs.set(sLine, tmp);
		 
		 int line = sLine;
		 line++;
		 while(line < eLine)
		 {
			 line++;
		 }
		 currStr =strs.get(line); 
		 tmp = currStr.substring(0,eCol);
		 if(!tmp.trim().isEmpty()) strs.set(line, tmp);
		 
		 strs.subList(sLine,eLine+1).clear();

		 return strs;
	 }

	 /*consider  following cases
	 *if(breg)
	 *   aaaa;
	 *else
	 *   bbbb;
	 *   
	 * if(breg)
	 * {
	 * 	aaa;
	 * 
	 * }
	 *else
	 *{
	 *
	 *}
	 */
	 
	 public void RemoveBlock(int line, int col, boolean isNeg) throws UnsupportBregExcption
	 {
	    	PosInfo bregPos = strsTraversalHelper.GetNextStrPos(BregValue, line, col);
    	
	    	PosInfo startPos = strsTraversalHelper.GetLastStrPos("if",bregPos.line, bregPos.col);
	    	PosInfo cPos = strsTraversalHelper.GetNextStrPos(";", bregPos.line, bregPos.col);
	    	PosInfo bPos = strsTraversalHelper.GetNextStrPos("{", bregPos.line, bregPos.col);
	    	PosInfo BlockAEnd;
	    	PosInfo cePos;
	    	PosInfo bePos;
	    	
	    	PosInfo BlockAStart = 
	    			strsTraversalHelper.GetNextLetterPosWithoutFirstBracket(bregPos.line, bregPos.col + BregValue.length());
	    	
	        String strStart = strs.get(startPos.line);
	    	ifBlockStrs.clear();
	    	elseBlockStrs.clear();
	    	if(strsTraversalHelper.IsFront(cPos, bPos)) 
	    	{
	    		BlockAEnd = cPos;
	    	}
	    	else
	    	{
	    		PosInfo bNextPos = strsTraversalHelper.GetNextPos(bPos);
	    		BlockAEnd = braceHelper.FindNextRightBrace(bNextPos);
	    	}

	    	ifBlockStrs = formathelper.GetRangeStrs(BlockAStart, 
	    			          strsTraversalHelper.GetLastPos(BlockAEnd));// get rid of last bracket
	    	
	    	PosInfo BlockElseStart = strsTraversalHelper.GetNextCharPos(BlockAEnd);
	    	PosInfo BlockElseEnd = BlockAEnd;
	    	String strElseFirstLine = strs.get(BlockElseStart.line);
	    	if(!formathelper.GetMatchSubStr(strElseFirstLine,"else\\s*if").isEmpty())
	    	{
	    		throw new UnsupportBregExcption("Unsupport Breg case: "+strElseFirstLine);
	    	}
	    	else if (!formathelper.GetMatchSubStr(strElseFirstLine,("else")).isEmpty())
	    	{
	    		cePos = strsTraversalHelper.GetNextStrPos(";", BlockElseStart);
	    		bePos = strsTraversalHelper.GetNextStrPos("{",BlockElseStart);
	    		

	    		BlockElseStart = 
	    				strsTraversalHelper.GetNextLetterPosWithoutFirstBracket(BlockElseStart.line, BlockElseStart.col + 4);
	    		
	    		
	    		PosInfo BlockElseInsideEnd = null;
	    		if(strsTraversalHelper.IsFront(cePos, bePos)) 
		    	{
	    			BlockElseEnd = cePos;
	    			BlockElseInsideEnd = BlockElseEnd;
		    	}
		    	else
		    	{
		    		
		    		BlockElseEnd = braceHelper.FindNextRightBrace(BlockElseStart);
		    		BlockElseInsideEnd = strsTraversalHelper.GetLastPos(BlockElseEnd);
		    	}
	    		elseBlockStrs = formathelper.GetRangeStrs(BlockElseStart, BlockElseInsideEnd);
	    	}
	    	

	    	int ifIndence = formathelper.GetIfIndence(strStart);
	    	RemoveRangeStrs(startPos.line, startPos.col, BlockElseEnd.line, BlockElseEnd.col);
	    	
	    	if(!isNeg)
	    	{
	    		formathelper.FormatBlock(ifBlockStrs,ifIndence );
		    	strs.addAll(startPos.line, ifBlockStrs);
	    	}
	    	else
	    	{
	    		formathelper.FormatBlock(elseBlockStrs,ifIndence );
	    		strs.addAll(startPos.line, elseBlockStrs);
	    	}
	 }

	 public void RemoveInclude(int line)
	 {
	 	strs.subList(line, line+1).clear();
     }
		

    public int removeNext(int line) throws UnsupportBregExcption
 	{

		PosInfo pi = strsTraversalHelper.GetNextStrPos(Breg, line, 0);
		if(pi.line == -1) return -1;
		isModified = true;
		line = pi.line;
		BOPType btype = ProcessCurrBreg(pi.line);
        if(btype == BOPType.NEXT_ROUND)
        {

        	return line;
        }
        else
        {
    		return line +1;
        }

	}
}
