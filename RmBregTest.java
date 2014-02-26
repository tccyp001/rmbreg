import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RmBregTest {

	 public class BregPattern
     {
    	public static final String pAndBreg = "(&&)?\\s*\\(*\\s*!?ypbreg\\)*";
    	public static final String pBregAnd = "\\s*\\(*\\s*ypbreg\\)*(&&)";
    	public static final String pBregOnly = "\\s*\\(*\\s*ypbreg\\)*";
    	
    	public static final String pEqualTrue1 = "(ypbreg)(\\s*==\\s*true)";
    	public static final String pEqualTrue2 = "(ypbreg)(\\s*==\\s*BREG_BOOLEAN_TRUE)";
    	public static final String pEqualTrue3 = "(true\\s*==\\s*)(ypbreg)";
    	public static final String pEqualTrue4 = "(BREG_BOOLEAN_TRUE\\s*==\\s*)(ypbreg)";
    	
    	public static final String pEqualFalse1 = "(ypbreg)(\\s*==\\s*flase)";
    	public static final String pEqualFalse2 = "(ypbreg)(\\s*==\\s*BREG_BOOLEAN_TRUE)";
    	public static final String pEqualFalse3 = "(true\\s*==\\s*)(ypbreg)";
    	public static final String pEqualFalse4 = "(BREG_BOOLEAN_TRUE\\s*==\\s*)(ypbreg)";
     }
	 /**
	 * @param args
	 */
		public static String GitRootStr;
		public static String RoboRootStr;
		public static String DiffToolStr;
	public static void main(String[] args) {
		TestRegex();
		//LoadSetting();
	}
	public static void TestRegex()
	{
		 // String to be scanned to find the pattern.
	      String line = "       if( aaa&&(ypbreg))";
	      String pattern = "(&&)\\s*\\(*\\s*!?ypbreg\\)*";
	      // Create a Pattern object
	      Pattern r = Pattern.compile(pattern);
	      System.out.println(line.contains(pattern));
	      // Now create matcher object.
	      Matcher m = r.matcher(line);
	      if (m.find( )) {
	         // System.out.println("Found value: " + m.group(0) );
	          int count = m.groupCount();
	          System.out.println("group count is "+count);
	          for(int i=0;i<count;i++){
	              System.out.println(m.group(i));
	          }
	      //    System.out.println(line.replaceFirst(BregPattern.pEqualTrue1, "!$1"));

	       } else {
	          System.out.println("NO MATCH");
	       }
	}
	private static void LoadSetting()
	{
		String userHome = System.getProperty( "user.home" );
		String fileName = combine(userHome, "rmbreg.profile");
		ArrayList<String> settingsStrs = new ArrayList<String>();
        try {
		     File file = new File(fileName);

		     BufferedReader  reader = new BufferedReader(new FileReader(file));
		
		     String text = null;
		     
		     while ((text = reader.readLine()) != null) {
		    	 settingsStrs.add(text);
		     }
        }
        catch(Exception ex)
        {
       	 System.out.print(ex.toString());
        
        }
        GitRootStr = GetSettingValueByKey("GitRootStr",settingsStrs);
        RoboRootStr = GetSettingValueByKey("RoboRootStr",settingsStrs);
        DiffToolStr = GetSettingValueByKey("Difftool",settingsStrs);
		System.out.println(userHome);
		System.out.println(GitRootStr);
		System.out.println(RoboRootStr);
		System.out.println(DiffToolStr);
		
		
	}
	private static String GetSettingValueByKey(String key, ArrayList<String> settingsStrs)
	{
        for(String str : settingsStrs)
        {
        	if(str.contains(key))
        	{
        		String arrStr[] = str.split("=");
        		if(arrStr.length <2) return "";
        		else
        		{
        			return arrStr[1];
        		}
        	}
        	
        }
        return "";
	}
        private static String combine (String path1, String path2)
        {
            File file1 = new File(path1);
            File file2 = new File(file1, path2);
            return file2.getPath();
        }
}	
