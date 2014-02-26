import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class ArgsParser {

	public PathType pathType;
	public String filePath;
	private String currentLocalPath;
	public String mybregStr;
	public String mybregValueStr;
	
	public String GitRootStr = "";
	public String RoboRootStr = "";
	public String DiffToolStr = "";
	public boolean isFullPath = false;
	public boolean mybregBoolValue = true;
	private static ArgsParser sm_instance;
	
	private  ArgsParser()
	{
		currentLocalPath = System.getProperty("user.dir");
		LoadSetting();
	}
	public static ArgsParser getInstance()
	{
	 if ( sm_instance == null )
		sm_instance = new ArgsParser();
	 return sm_instance;
	} 
	
	private void SetBreg(String[] args, int index) throws CommandLineFormatException
	{
		if(args.length<index+1)
		{
			throw new CommandLineFormatException("Missing argument breg!");
		}
		String breg = args[index];
		if(!breg.contains("bbit"))
		{
			throw new CommandLineFormatException(breg + " is not a valid breg!");
		}
		String valueKeyWord = "__value";
		if(breg.contains(valueKeyWord))
		{
			int vIndex = breg.indexOf(valueKeyWord);
			mybregStr = breg.substring(0, vIndex);
			mybregValueStr = breg.substring(0,vIndex+valueKeyWord.length()) + "()";
		}
		else
		{
			mybregStr = breg;
			mybregValueStr = breg + valueKeyWord + "()";
		}
		if(args.length > index +1)
		{
			if(args[index +1].equalsIgnoreCase("true"))
			{
				mybregBoolValue = true;
			}
			else if(args[index +1].equalsIgnoreCase("false"))
			{
				mybregBoolValue = false;
			}
			else
			{
				int k = index + 2;
				throw new CommandLineFormatException(args[index +1] + ": "+ k 
						+ " arg need to be true or false.");
			}
			mybregBoolValue = Boolean.valueOf(args[index +1]);

		}
	}
	public String GetRemoteFullPath()
	{
		if(pathType == PathType.GitPath)
		{
			String path = filePath;
			if(!RoboRootStr.isEmpty() && !isFullPath)
			{
				path = combine(GitRootStr,filePath);
			}
			System.out.print(path);
			return path;		

		}
		if(pathType == PathType.RoboPath)
		{
			String path = GetRobosuffixStr(filePath);
			if(!RoboRootStr.isEmpty() && !isFullPath)
			{
				path = combine(RoboRootStr,filePath);
				return GetRobosuffixStr(path);
			}
			return path;
		}
		return "";
	}
	public String GetRobosuffixStr(String path)
	{
		if(path == null || path.isEmpty())return "";
		if(path.charAt(path.length()-1) == '*') return path;
		if(path.charAt(path.length()-1) == '/') return path + "*";
		 return path + "/*";
	}
	public String GetFullPath()
	{
		if(pathType == PathType.AbsPath)
		{
			return filePath;
		}

		String remoteStr = GetRemoteFullPath();
		remoteStr =remoteStr.replace("/*", "");
		return combine(currentLocalPath,remoteStr);		
	}
	private String combine (String path1, String path2)
	{
		if(path1==null )return path2;
		if(path2 == null) return path1;
	    File file1 = new File(path1.trim());
	    File file2 = new File(file1, path2.trim());
	    return file2.getPath();
	}
	private String GetPathFromArg(String[] args) throws CommandLineFormatException
	{
		if(args.length<3)
		{
			throw new CommandLineFormatException("Missing argument!");
	    }
		else
		{
			String path = args[1];
			if(path.charAt(path.length()-1)=='/')
				path = path.substring(0, path.length() -1);
			return path;
		}
	}
	public boolean Parse(String[] args) throws CommandLineFormatException
	{
		 if(args.length ==0) {

	             printHelp();
	         }
	         else if (args.length >=1)
	         {
	        	 if(!args[0].contains("-"))
    			 {
	        		 pathType = PathType.LocalPath;
	        		 filePath = GetPathFromArg(args);
	        		 SetBreg(args, 1);
    			 }
	        	 else if(args[0].equals("-l"))
	        	 {
	        		 pathType = PathType.LocalPath;
	        		 SetBreg(args, 1);
	        	 }

	        	 else if(args[0].contains("-h"))
	        	 {
	        		 printHelp();
	        		 return false;
	        	 }
	        	 else 
	        	 {
		        	  if(args[0].equals("-s"))
		        	 {
		        		 pathType = PathType.SingleFile;
		        	 }
		        	  else if(args[0].equals("-sf"))
		        	 {
		        		 pathType = PathType.AbsPath;
		        	 }
		        	  else if(args[0].equals("-r"))
		        	 {
		        		 pathType = PathType.RoboPath;
		        	 }
		             else if(args[0].equals("-rf"))
		        	 {
		            	 isFullPath = true;
		        		 pathType = PathType.RoboPath;
		        	 }
		        	 else if(args[0].equals("-g"))
		        	 {
		        		 pathType = PathType.GitPath;
		        	 }
		          	 else if(args[0].equals("-gf"))
		        	 {
		          		 isFullPath = true;
		        		 pathType = PathType.GitPath;
		        	 }
		        	 else if(args[0].equals("-p"))
		        	 {
		        		 pathType = PathType.AbsPath;
		        	 }
		        	 else if(args[0].equals("-debug"))
		        	 {        		
		        		 System.out.println("May the breg be with you! version 1.0");
		        		 System.out.println("GitRootStr: "+GitRootStr);
		        		 System.out.println("RoboRootStr: "+RoboRootStr);
		        		 System.out.println("DiffToolStr: "+DiffToolStr);
		        		 return false;
		        	 }
		        	 else if(args[0].equals("-yptest"))
		        	 {        		
			             filePath = "/home/ycao3/bregtest.txt";
			             mybregStr = "bbit_update_ptfd_and_use_new_prio" ;
			             mybregValueStr = "bbit_update_ptfd_and_use_new_prio__value()";
			             pathType = PathType.AbsPath;
		        		 return true;
		        	 }
		        	 else {
		        		 printHelp();
		        		 return false;
		        	 }
	        		 filePath = GetPathFromArg(args);
	        		 SetBreg(args, 2);
	        	 }
	        	 
	         }
		 return true;
	}

	private  void LoadSetting()
	{
		String userHome = System.getProperty( "user.home" );
		String fileName = combine(userHome, "rmbreg.profile");
		ArrayList<String> settingsStrs = new ArrayList<String>();
        try {
		     File file = new File(fileName);
		     if(!file.exists()) return;
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


	}
	private  String GetSettingValueByKey(String key, ArrayList<String> settingsStrs)
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
	public void printHelp()
	{
		System.out.println("May the breg be with you!");
		System.out.println("Please contact Yipeng Cao ycao64@bloomberg.net for any bugs or suggestions.");
		System.out.println("Usage:  rmbreg [options] [Path] [breg] .\n");
		System.out.println("\t-s[f]   single file[absolute path], rmbreg -s foo.c <breg12345>.");
		System.out.println("\t-l       current folder, rmbreg -l <breg12345>.");
		System.out.println("\t         will traversal all files in current folder.");
		System.out.println("\t-r[f]   robo folder[absolute path], rmbreg -r <breg12345>");
		System.out.println("\t         will cscopyout from robo first.");
		System.out.println("\t-g[f]   git folder[absolute path], rmbreg -g <gitPath> <breg12345>.");
		System.out.println("\t         will git clone from devgit first.\n");
	}
}
