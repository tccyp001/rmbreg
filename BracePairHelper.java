import java.util.ArrayList;


public class BracePairHelper {
	private ArrayList<String> strs;
	public  BracePairHelper(ArrayList<String> strs)
	{
		this.strs = strs;
	}
	 /*// : 'l' single line comment
	  *  /* : c  comment 
	  *  " :q quotes
	  *  ' :s single quote
	  */
	 private boolean isCode(char c)
	 {
		 if(c =='c' || c =='l' ||c =='s' ||c =='p' ) return false;
		 return true;
	 }
	 public PosInfo FindNextRightBrace(int line, int col)
	 {
		 return FindNextRightBracket(line, col,'{', '}');
	 }
	 public PosInfo FindNextRightBrace(PosInfo pos)
	 {
		 return FindNextRightBracket(pos.line,pos.col,'{', '}');
	 }
	 public PosInfo FindNextRightParenthes(PosInfo pos)
	 {
		 return FindNextRightBracket(pos.line,pos.col,'(', ')');
	 }
	 public PosInfo FindNextRightBracket(int line, int col, char leftc, char rightc)
	 {
		 CharStack mystack = new CharStack();
		 char lastchar =' ';
		 while(line<strs.size())
		 {
			 String currstr = strs.get(line);
			 if(mystack.peek() =='l') mystack.pop(); // single line comment
			 for(int i =col;i<currstr.length();i++)
			 {
				 char c = currstr.charAt(i);
				 if(lastchar=='/' && c == '/' &&isCode(mystack.peek())) mystack.push('l');
				 if(lastchar=='/' && c == '*' && isCode(mystack.peek())) mystack.push('c');
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
				 else if((c == leftc || c==rightc) 	&& 
			    	!isCode(mystack.peek())) // in comment ignore 
				 {
					 //donothing
				 }
				 else if (c == leftc){
					 mystack.push(c);
				 }
				 else if(c == rightc && mystack.peek() == leftc && mystack.size()>0)
				 {
					 mystack.pop();
				 }
				 else if(c == rightc) //found
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

}
