import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class BregHelper {


	public class IdentInfo{
        
		char ch;
		int line;
		int col;
		public IdentInfo(char c, int line, int col)
		{
			this.ch = c;
			this.line = line;
			this.col = col;
		}
	}
	 public enum BType{
		 BINCLUDE,ASSIGN, NEG_ASSIGN,IF_BREG,IF_AND_BREG, IF_BREG_AND,IF_NEG_BREG, 
		 IF_NEG_BREG_AND, IF_AND_NEG_BREG, UNKNOW
	 }
	 public ArrayList<String> strs;
	 public List<String> blockStrs = new ArrayList<String>();
	 public Stack myStack = new Stack();
	 public  String Breg;
	 public  String BregValue;
	 public int totalLine;
	 public boolean isNeg;
	 public int IndentSpaceNo;
	 public  void printArr()
     {
    	for(int i = 0; i< strs.size(); i++)
    	{
    		System.out.println(strs.get(i));
    	}
     }
	 public  int FindNextBregLine(int currentLineIndex)
	 {
		 for( int i = currentLineIndex;i<strs.size();i++)
		 {
			 String currStr = strs.get(i);
			 if(currStr.contains(Breg)) return i;
		 }
		 return -1;
	 }
	 public  IdentInfo FindLastChar(int line, int col)
	 {
		 while(line>0)
		 {
			 String currStr = strs.get(line);
			 while(col>0)
			 {
		
				 char ch = currStr.charAt(col);
				 if (!Character.isSpaceChar(ch))  return new IdentInfo(ch, line, col);
				 col--;
			 }
			 line--;
		 }
		 return new IdentInfo(' ', -1, -1);
	 }
	 public  IdentInfo FindLastCharEx(int line, int col, char c)
	 {
		 while(line>0)
		 {
			 String currStr = strs.get(line);
			 while(col>0)
			 {
				 
				 char ch = currStr.charAt(col);
				 if (ch == c) new IdentInfo(ch, line, col);;
				 col--;
			 }
			 line--;
		 }
		 return new IdentInfo(' ', -1, -1);
	 }
	 public  IdentInfo FindNextChar(int line, int col)
	 {
		 
	//	 int indexB = currStr.indexOf(Breg);
		 while(line<totalLine)
		 {
			 String currStr = strs.get(line);
			 while(col<currStr.length())
			 {
			
				 char ch = currStr.charAt(col);
				 if (!Character.isSpaceChar(ch))  return new IdentInfo(ch, line, col);

				 col++;
			 }
			 line++;
		 }
		 return new IdentInfo(' ', -1, -1);
	 }
	 public  IdentInfo FindNextCharEx(int line, int col, char c)
	 {
		 
	//	 int indexB = currStr.indexOf(Breg);
		 while(line<totalLine)
		 {
			 String currStr = strs.get(line);
			 while(col<currStr.length())
			 {
		
				 char ch = currStr.charAt(col);
			
				 if (ch == c) new IdentInfo(ch, line, col);;
				 col++;
			
			 }
			 line++;
		 }
		 return new IdentInfo(' ', -1, -1);
	 }
	 public BType GetCurrBregMType(int line)
	 {
		 String currStr = strs.get(line);
		 int indexS = currStr.indexOf(Breg);
		 int indexEnd = currStr.indexOf(BregValue) + BregValue.length() + 1;
		 if(currStr.contains("#include")) return BType.BINCLUDE;
		 
		 IdentInfo iinfo = FindLastChar(line, indexS);
		 if(iinfo.ch == '&') return BType.IF_AND_BREG;
		 
		 if(iinfo.ch == '!') 
		 {
			 iinfo = FindLastChar(iinfo.line, iinfo.col);
			 if(iinfo.ch == '&') return BType.IF_AND_NEG_BREG;
			 if(iinfo.ch == '=') return BType.NEG_ASSIGN;
			 if(iinfo.ch == '(') 
			 {
				 if(iinfo.ch == '&') return BType.IF_NEG_BREG_AND;
				 if(iinfo.ch == ')') {
					 IndentSpaceNo = GetIndent(iinfo.line, iinfo.col);
					 return BType.IF_NEG_BREG;
				 }
			 }
		 }
		 IdentInfo nextiinfo = FindNextChar(line, indexEnd);
		 if(nextiinfo.ch == '(') 
		 {
			 if(nextiinfo.ch == '&') return BType.IF_BREG_AND;
			 if(nextiinfo.ch == ')') 
			 {
			    IndentSpaceNo = GetIndent(iinfo.line, iinfo.col);
			 	return BType.IF_BREG;
			 }
		 }
		 return BType.UNKNOW;
		 
	 }
	 private int GetIndent(int line, int col)
	 {
		 IdentInfo iinfo = FindLastCharEx(line,col,'f');
		 int col1 = iinfo.col;
		 iinfo = FindLastCharEx(iinfo.line,iinfo.col,'i');
		 if(col1 != iinfo.col +1) return -1; //just check
		 
		 return iinfo.col;
	 }
	 public void StartRemove(int line)
	 {
		 while (line <totalLine)
		 {
			 String currstr = strs.get(line);
			 if(currstr.contains(Breg));
			 {
				 BType btype = GetCurrBregMType(line);
			 }
			 line++;
		 }
	 }
	 /*// : 'l'
	  *  /* : c 
	  *  " :q
	  *  ' :s
	  */
	 private boolean isCode(char c)
	 {
		 if(c =='c' || c =='l' ||c =='s' ||c =='p' ) return false;
		 return true;
	 }
	 public PosInfo FindNextRightBracket(int line, int col)
	 {
		 CharStack mystack = new CharStack();
		 char lastchar =' ';
		 while(line<totalLine)
		 {
			 String currstr = strs.get(line);
			 if(mystack.peek() =='l') mystack.pop();
			 for(int i =col;i<currstr.length();i++)
			 {
				 char c = currstr.charAt(i);
				 if(lastchar=='/' && c == '/' &&mystack.peek() !='q') mystack.push('l');
				 if(lastchar=='/' && c == '*' && mystack.peek() !='q') mystack.push('c');
				 if(lastchar=='*' && c == '/' &&  (mystack.peek() =='c')) mystack.pop();
				 
				 if(c=='"' )
				 {
					 if(mystack.peek() =='q') mystack.pop();
					 else if(isCode(mystack.peek())) mystack.push('q');
				 }
				 else if(c=='\'' )
				 {
						 if(mystack.peek() =='s') mystack.pop();
						 else if(isCode(mystack.peek()))  mystack.push('s');
				 }
				 else if((c == '{' || c=='}') 	&& 
			    	!isCode(mystack.peek())) // in comment ignore 
				 {
					 //donothing
				 }
				 else if (c == '{'){
					 mystack.push(c);
				 }
				 else if(c == '}' && mystack.peek() =='{' && mystack.size()>1)
				 {
					 mystack.pop();
				 }
				 else if(c == '}') //found
				 {
					 return new PosInfo(line,i);
				 }
				 lastchar =c;
				 
			 }
			 line++;
			 col =0;
		 }
		 return new PosInfo(-1,-1);
	 }
	 public PosInfo GetLastStrPos(String str, int line, int col)
	 {
		 while(line>0)
		 {
			 String currstr = strs.get(line);
			 int index = currstr.lastIndexOf(str);
			 if( index != -1) return new PosInfo(line, index);
			 line --;
		 }
		 return new PosInfo(-1, -1);
	 }
	 public PosInfo GetNextStrPos(String str, int line, int col)
	 {
		 while(line<totalLine)
		 {
			 String currstr = strs.get(line);
			 if(currstr != null)
			 {
				 int index = currstr.indexOf(str);
				 if( index != -1) return new PosInfo(line, col);
			 }
			 line++;
		 }
		 return new PosInfo(-1, -1);

	 }
	 public List<String> RemoveRangeStrs(int sLine, int sCol, int eLine, int eCol)
	 {
         
		
		 //fisrt line
		 String currStr =strs.get(sLine); 
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
		 
		 strs.subList(sLine,eLine-1).clear();

		 return strs;
	 }
	 public List<String> GetRangeStrs(int sLine, int sCol, int eLine, int eCol)
	 {
		 List<String> blockStrs = new ArrayList<String>();
		 //fisrt line
		 String currStr =strs.get(sLine); 
	     String tmp = currStr.substring(sCol);
		 sLine++;
		 if(!tmp.trim().isEmpty()) blockStrs.add(tmp);
		 
		 while(sLine < eLine)
		 {
			 currStr =strs.get(sLine); 
			 blockStrs.add(currStr);
			 sLine++;
		 }
		 currStr =strs.get(sLine); 
		 tmp = currStr.substring(0,eCol);
		 if(!tmp.trim().isEmpty()) blockStrs.add(tmp);
		 
		 return blockStrs;
	 }
	 public void RemoveBlock(int line, int col)
	 {
		    PosInfo endPos;
	    	PosInfo bregPos = GetNextStrPos(Breg, 0, 0);
    	
	    	PosInfo startPos = GetLastStrPos("if",bregPos.line, bregPos.col);
	    	PosInfo cPos = GetNextStrPos(";", bregPos.line, bregPos.col);
	    	PosInfo bPos = GetNextStrPos("{", bregPos.line, bregPos.col);
	    	
	    	if(isFront(cPos, bPos)) 
	    	{
	    		endPos = cPos;
	    	}
	    	else
	    	{
	    		endPos = FindNextRightBracket(bPos.line,bPos.col);
	    	}
	    	
	    	IdentInfo iinfo = FindNextChar(endPos.line, endPos.col);
	    	PosInfo elsePos = GetNextStrPos("else",endPos.line, endPos.col);
	    	
	    	if(iinfo.ch =='e' && iinfo.line == elsePos.line && iinfo.col == elsePos.col) //has else
	    	{
	    		PosInfo cePos = GetNextStrPos(";", endPos.line, endPos.col);
	    		PosInfo bePos = GetNextStrPos("{",endPos.line, endPos.col);
	    		if(isFront(cePos, bePos)) 
		    	{
		    		endPos = cePos;
		    	}
		    	else
		    	{
		    		endPos = FindNextRightBracket(bePos.line,bePos.col);
		    	}
	    	}
	    	RemoveRangeStrs(startPos.line, startPos.col, endPos.line, endPos.col);
	 }
	 private boolean isFront(PosInfo p1, PosInfo p2)
	 {
		 if(p1.line<p2.line) return true;
		 if(p1.line == p2.line && p1.col<p2.col) return true;
		 return false;
	 }
}
