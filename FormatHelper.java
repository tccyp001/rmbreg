import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FormatHelper {
	private ArrayList<String> strs;
	private String BregStr; 
	private String BregValueStr; 
	public  FormatHelper(ArrayList<String> strs, String BregStr, String BregValueStr)
	{
		this.strs = strs;
		this.BregStr = BregStr;
		this.BregValueStr = BregValueStr;
	}
	public String GetMatchSubStr(String pStr, String pattern)
	{
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(pStr);
        if (m.find()) {
        	 return m.group(0);
        }
        else
        {
        	return "";
        }
	}
	public int GetDistanceBregAndLogicalop(String matchStr)
	{
		int indexBreg = matchStr.indexOf(BregStr);
		int indexOp = GetOpIndex(matchStr);
		if(indexBreg == -1 || indexOp == -1) 
			return -1;
		
		int start, end, cur;
		if(indexOp < indexBreg)
		{
			start = indexOp;
			end = indexBreg;
		}
		else
		{
			start = indexBreg;
			end = indexOp;
		}
		int count1 = 0, count2 = 0;
		while(start < end){
			start++;
			if(matchStr.charAt(start) ==')')count1 ++;
			if(matchStr.charAt(start) =='(')count2 ++;
		}
		return Math.abs(count1 - count2);
	}
	public int GetOpIndex(String matchStr) {
		int indexAnd = matchStr.indexOf("&&");
		int indexOr = matchStr.indexOf("||");
		int indexOp = indexAnd;
		if(indexAnd == -1) indexOp = indexOr;
		return indexOp;
	}
	public void UpdateStrs(String pStr, int sLine, int eLine) // need to clear strs first
	{
		StrsTraversalHelper strsTHelper = StrsTraversalHelper.getInstance(strs);
		String[] arrStrs = pStr.split("\n");
		int line = sLine;
	
		for(int i=0; i< arrStrs.length; i++)
		{
			strs.set(line, arrStrs[i]);
			line = strsTHelper.GetNextLine(line);
		}
		while(line<=eLine)
		{
			strs.subList(line, line+1).clear();
			line ++;
		}
	}
	public static String ltrim(String s) {
	    int i = 0;
	    while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
	        i++;
	    }
	    return s.substring(i);
	}

	public static String rtrim(String s) {
	    int i = s.length()-1;
	    while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
	        i--;
	    }
	    return s.substring(0,i+1);
	}

	public String MergeStr(String pStr)
	{
		StrsTraversalHelper strsTraversalHelper = StrsTraversalHelper.getInstance(strs);
		StringBuilder sb = new StringBuilder();
		pStr = strsTraversalHelper.RemoveEmptyBracket(pStr);
		String[] strs = pStr.split("\n");
		String lastStr = null;
		for(String str :strs)
		{
			if(lastStr == null || lastStr.isEmpty())
			{
				lastStr = str;
			}
			else if((FormatHelper.rtrim(lastStr).length() + str.trim().length() < 70)
				&&(lastStr.trim().length() < 20 || str.trim().length() < 20)) 
			{
				sb.append(FormatHelper.rtrim(lastStr) + " " + FormatHelper.ltrim(str));
				sb.append("\n");
				lastStr = null;
			}
			else
			{
				sb.append(lastStr);
				lastStr = str;
				sb.append("\n");
			}
		}
		if(lastStr!=null)
			sb.append(lastStr);
		return sb.toString();
		
	}
	public void FormatBlock(ArrayList<String> blockstrs, int baseIndence)
	 {
		 int localBaseIndence = 100;
		 for(int i=0;i<blockstrs.size();i++)
		 {
			 String mystr = blockstrs.get(i);
			 int space = GetIfIndence(mystr);
			 if(space<localBaseIndence && space>0) localBaseIndence = space; //find the "outside" one
		 }
		 int spaceDelta = localBaseIndence - baseIndence ;
		 for(int i=0;i<blockstrs.size();i++)
		 {
			 String mystr = blockstrs.get(i);
			 mystr = SetMargin(mystr,spaceDelta,baseIndence);
			 blockstrs.set(i, mystr);
		 }
		 // for the following case, remove inner bracket, kind of hack way 
		 // but don't want to spend more time on this
		 /*
		  * if(bre)
		  * {
		  * 	{
		  *         aaaa
		  *     }
		  * }
		  * 
		  */
		 if(blockstrs.size()>=2)
		 {
			 String firstLine = blockstrs.get(0);
			 String lastLine = blockstrs.get(blockstrs.size()-1);
			 if(firstLine.trim().equals("{") && lastLine.trim().equals("}"))
			 {
				 blockstrs.remove(blockstrs.size()-1);
				 blockstrs.remove(0);
			 }
		 }

	 }
	public String SetMargin(String str, int delta,  int baseIndence)
	 {
		 StringBuilder sb = new StringBuilder();
		 int space = GetLineIndence(str);
		 space = space - delta;
		 if(space < baseIndence) space = baseIndence; // for one line case
		 for(int i =0;i<space;i++)
		 {
			 sb.append(" ");
		 }
		 sb.append(FormatHelper.ltrim(str));
		 return sb.toString();
	 }
	public int GetLineIndence(String str)
	 {
        int i =0;
        int totalspace =0;
        while(i<str.length())
        {
       	 char ch = str.charAt(i);
       	 if(Character.isWhitespace(ch)){
       		 totalspace++;
       	 }
       	 else if(ch =='\t')
       	 {
       		 totalspace = totalspace +4;
       	 }
       	 else
       	 {
       		return totalspace; 
       	 }
       	 i++;
        }

		 return 0;
	 }
	public int GetIfIndence(String str)
	 {

		 int s = str.indexOf("if");
		 int totalspace = s;
		 for(int i=0;i<s;i++)
		 {
			 if(str.charAt(i) =='\t')  totalspace = totalspace+3;
		 }
		 return totalspace;
	 }


	public void ReplaceStrInRange(PosInfo pStart, PosInfo pEnd, String srcStr, String targetStr)
	{
		int endLine = pEnd.line ;
		if(endLine == -1) 
		{
			endLine = strs.size();
		}
		int line = pStart.line;
		while(line < pEnd.line)
		{
			String currentLine = strs.get(line);
			currentLine = currentLine.replace(srcStr, targetStr);
			strs.set(line, currentLine);
			line++;
		}
	}
	 public ArrayList<String> GetRangeStrs(PosInfo posS, PosInfo posEnd)
	 {
		 ArrayList<String> blockStrs = new ArrayList<String>();
		 String currStr =strs.get(posS.line); 
		 if(posS.line == posEnd.line)
		 {

			 String tmp = currStr.substring(posS.col, posEnd.col  +1);
			 blockStrs.add(tmp);
			 return blockStrs;
		 }
		 //fisrt line
		 int line = posS.line;
	     String tmp = currStr.substring(posS.col);
	     
	     String tmp2 =  tmp.trim();
//	     if(tmp2.length() >0 &&tmp2.charAt(0) == '{')
//	     {
//	    	StringBuilder mySb = new StringBuilder(tmp);
//	    	int indexb  = tmp.indexOf('{');
//	    	mySb.setCharAt(indexb, ' ');
//	    	tmp = mySb.toString();
//	     }
	     line++;
		 if(!tmp.trim().isEmpty())
		 {
		 	blockStrs.add(tmp);
		 }
		 
		 while(line < posEnd.line)
		 {
			 currStr =strs.get(line); 
			 blockStrs.add(currStr);
			 line++;
		 }

		 currStr =strs.get(line); 
		 tmp = currStr.substring(0,posEnd.col +1);
//		 tmp2 =  tmp.trim();
//	     if(tmp2.length() >0 &&tmp2.charAt(0) == '}')
//	     {
//	    	StringBuilder mySb = new StringBuilder(tmp);
//	    	int indexb  = tmp.indexOf('}');
//	    	mySb.setCharAt(indexb, ' ');
//	    	tmp = mySb.toString();
//	     }
		 if(!tmp.trim().isEmpty())
		 {
		 	blockStrs.add(tmp);
		 }

		 
		 return blockStrs;
	 }
	 //distance means how many bracket between && and breg, 
	 //need remove them on the other side
	 public String RemoveUnuseBrace(String matchStr, int distance)
	{
		int indexBreg = matchStr.indexOf(BregStr);
		int indexOp = GetOpIndex(matchStr);
		
		int index;
		if(indexOp == -1 || indexBreg == -1)
			return matchStr;
		
		if(indexBreg < indexOp)  // (((breg (&& <otherflag>
		{
		  index = indexBreg;
		  while(index > 0 && distance>0)
		  {
			  index --;
			  if(matchStr.charAt(index) == '(') distance --;
		  }
		  return matchStr.substring(index);
		}
		else//  <otherflag> && (breg))) 
		{
			index = indexBreg + BregValueStr.length();
			while(index < matchStr.length() - 1 && distance>0)
			{
				  index ++;
				  if(matchStr.charAt(index) == ')') distance --;
			}
			return matchStr.substring(0, index);
		}
	}
}
