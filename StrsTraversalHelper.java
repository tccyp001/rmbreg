import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StrsTraversalHelper {
	private ArrayList<String> strs;
	private static StrsTraversalHelper sm_instance;
	

	public static StrsTraversalHelper getInstance(ArrayList<String> strs)
	{
		 if ( sm_instance == null || sm_instance.strs != strs)
			sm_instance = new StrsTraversalHelper(strs);
		 return sm_instance;
	} 
	private  StrsTraversalHelper(ArrayList<String> strs)
	{
		this.strs = strs;
	}

	public  PosInfo GetNextLetterPosWithoutFirstBracket(int line, int col)
	 {
		 
		 col++;
		 boolean isFirstBracket = true;
		 if(line < 0 || col < 0) return new PosInfo( -1, -1);
		 while(line<strs.size())
		 {
	
			 String currStr = strs.get(line);
			 while(col<currStr.length())
			 {
			    
				 char ch = currStr.charAt(col);
				 if(ch =='{')
				 {
					 if(isFirstBracket)isFirstBracket = false;
					 else  return new PosInfo(line, col);
				 }
				 else if (!Character.isWhitespace(ch))  return new PosInfo(line, col);

				 col++;
			 }
			 line++;
			 col =0;
		 }
		 return new PosInfo( -1, -1);
	 }
	 public  PosInfo GetNextLetterPos(int line, int col)
	 {
		 
		 col++;
		 while(line<strs.size())
		 {
	
			 String currStr = strs.get(line);
			 while(col<currStr.length())
			 {
			
				 char ch = currStr.charAt(col);
				 if (Character.isLetterOrDigit(ch))  return new PosInfo(line, col);

				 col++;
			 }
			 line++;
			 col =0;
		 }
		 return new PosInfo( -1, -1);
	 }
	 public PosInfo GetNextCharPos(PosInfo posInfo)
	 {
		 return GetNextCharPos(posInfo.line, posInfo.col);
	 }
	 public PosInfo GetNextCharPos(int line, int col)
	 {

		 col++;
		 while(line<strs.size())
		 {
	
			 String currStr = strs.get(line);
			 while(col<currStr.length())
			 {
			
				 char ch = currStr.charAt(col);
				 if (!Character.isWhitespace(ch))  return new PosInfo(line, col);

				 col++;
			 }
			 line++;
			 col =0;
		 }
		 return new PosInfo( -1, -1);
	 }

	 public PosInfo GetLastStrPos(String str, PosInfo pos)
	 {
		 return GetLastStrPos(str, pos.line, pos.col);
	 }
	 public PosInfo GetLastStrPos(String str, int line, int col)
	 {
		 while(line>=0)
		 {
			 String currstr = strs.get(line);
			 int index = currstr.lastIndexOf(str);
			 if( index != -1) return new PosInfo(line, index);
			 line --;
		 }
		 return new PosInfo(-1, -1);
	 }
	 public PosInfo GetNextStrPos(String str, PosInfo pos)
	 {
		 return GetNextStrPos(str, pos.line, pos.col);
	 }
	 public PosInfo GetNextStrPos(String str, int line, int col)
	 {
		 if(line == -1) return new PosInfo(-1, -1);
		 while(line<strs.size())
		 {
			 String currstr = strs.get(line);
			 if(currstr != null)
			 {
				 int index = currstr.indexOf(str);
				 if( index != -1) return new PosInfo(line, index);
			 }
			 line++;
		 }
		 return new PosInfo(-1, -1);

	 }
	 // get previous line, but if currneline contains "if" return current line
	 public int GetPreLine(int line)
	 {
		 int preLine = line;
		 
		 if(!GetMatchSubStr(strs.get(line),"if\\s*\\(").isEmpty())
		 {
			 return line;
		 }
		 while(preLine >0)
		 {
			 preLine --;
			 if(!strs.get(preLine).trim().isEmpty())
			 {
				 break;
			 }
		 }
		 return preLine;
	 }
	 private String GetMatchSubStr(String pStr, String pattern)
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
	 public int GetNextLine(int line)
	 {
		 int preLine = line;
		 while(preLine  < strs.size() -1)
		 {
			 preLine ++;
			 if(!strs.get(preLine).trim().isEmpty())
			 {
				 break;
			 }
		 }
		 return preLine;
	 }
	 public PosInfo GetNextPos(PosInfo pInfo)
	{
		int currLine = pInfo.line;
		int currRol = pInfo.col;
		String currStr = strs.get(currLine);
		if(currRol<currStr.length()) 
			return new PosInfo(currLine, currRol +1);
		else
			return new PosInfo(currLine+1, 0);

	}
	 public PosInfo GetLastPos(PosInfo pInfo)
	{
		
		int currLine = pInfo.line;
		int currRol = pInfo.col;
		if(currRol> 0)
		{
			return new PosInfo(currLine, currRol -1);
		}
		else
		{
			currLine --;
			String currStr = strs.get(currLine);
			return new PosInfo(currLine, currStr.length()-1);
		}

	}

	 // get next 2 lines - nonempty neighbor lines
	 public String GetNeighborStr(int sline, int eLine)
	 {
		 StringBuilder sb = new StringBuilder(); 
		 String currentLine ;
		 int currLineNo = sline;
		 while(currLineNo < strs.size() -1 && currLineNo<eLine+1)
		 {
		
			 currentLine = strs.get(currLineNo);
			 if(!strs.get(currLineNo).trim().isEmpty())
			 {
				 sb.append(currentLine);
				 sb.append("\n");
			 }
			 currLineNo ++;
		 }
		 return sb.toString();
	 }
	 public boolean IsFront(PosInfo p1, PosInfo p2)
	 {
		 if(p1.line ==-1) return false;
		 if(p1.line<p2.line) return true;
		 if(p1.line == p2.line && p1.col<p2.col) return true;
		 return false;
	 }
	 public String RemoveEmptyBracket(String str)
	 {
		 return str.replace("(\\s*)", ""); 
	 }
	 


}
