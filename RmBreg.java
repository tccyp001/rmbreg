import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
public class RmBreg {

	/**
	 * @param args
	 */
	public static List<String> modifiedFiles = new ArrayList<String>();
	public static void main(String[] args) {

		storeUsage(args);
		ArgsParser argsParser = ArgsParser.getInstance();
		boolean ret = false;
		try {
			ret = argsParser.Parse(args);
		} catch (CommandLineFormatException e1) {
			System.out.print("Wrong Command Line Format, please use -h for more details.");
			e1.printStackTrace();
			return;
		}
		if(argsParser.pathType == PathType.GitPath ||
			argsParser.pathType == PathType.RoboPath )
		{
			try {
				GetFilesFromRemote();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		if(ret)
		{
			 System.out.println("May the breg be with you.");
			 RemoveBregInAllFiles(argsParser);
	         DiffAndConfirm();
		}

       
	}
	
	private static void storeUsage(String[] args)
	{
		String cmdStr = "";
		for(String str : args)
		{
			cmdStr = cmdStr + " " + str;
		}
		String usernameStr = System.getProperty("user.name");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		try{
    		String data = usernameStr + "|" + dateFormat.format(date) + "|" +cmdStr+ "\n";
			//String data = " This content will append to the end of the file";
    		File file =new File("/home/ycao3/bregtool_usage.txt");
 
    		//if file doesnt exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}
 
    		//true = append file
    		FileWriter fileWritter = new FileWriter(file.getAbsolutePath(),true);
    		fileWritter.write(data);
    		fileWritter.close();

	       
    	}catch(IOException e){
    		e.printStackTrace();
    	}
	}
	private static void RemoveBregInAllFiles(ArgsParser argsParser) {
		List<File> files = listf(argsParser.GetFullPath());
		System.out.println(argsParser.GetFullPath());
    	 for(int i =0;i<files.size();i++)
    	 {
    		 String fileStr = (files.get(i).getAbsolutePath());
    		 if(fileStr.endsWith(".f"))
			 {
			    System.out.println("Warning not support fortan code at this version");
			 }
    		 if(fileStr.endsWith(".c")||fileStr.endsWith(".cpp")
    		    ||fileStr.endsWith(".h")||fileStr.endsWith(".txt"))
    		 {
    			 RemoveBregInFile(fileStr);
    		 }
    	 }
	}
	private static void DiffAndConfirm() {
		for(int i =0 ;i< modifiedFiles.size();i++)
         {
		 	 System.out.println("Total file changed: " + modifiedFiles.size());
        	 String filename = modifiedFiles.get(i);
        	 System.out.println(filename);
        	 try {
				Runtime.getRuntime().exec("tkdiff -w "+ filename  +" " + filename+ ".bregrm");
				Thread.sleep(1000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       	      System.out.println("Do you want to save changes to "+ filename +"? (y/n)");
        	  Scanner user_input = new Scanner( System.in );
              String input = user_input.nextLine();
              if(input.equals("y"))
              {

             	 try {
             		 String cmd = "mv "+ filename + ".bregrm " + filename;
             		System.out.println(cmd);
      				Runtime.getRuntime().exec("mv "+ filename + ".bregrm " + filename);
      			} catch (IOException e) {
      				// TODO Auto-generated catch block
      				e.printStackTrace();
      			}
              }
              else
              {
             	 continue;
              }
         }
		return;
	}
	private static void GetFilesFromRemote() throws IOException, InterruptedException
	{
		 ArgsParser argsParser = ArgsParser.getInstance();
		 String cmdStr;
		 String remotePath = argsParser.GetRemoteFullPath();

		 if(argsParser.pathType == PathType.GitPath)
		 {
			 cmdStr = "git clone " +  remotePath;
		 }
		 else if(argsParser.pathType == PathType.RoboPath)
		 {
			 cmdStr = "cscopyout "+ remotePath;
		 }
		 else
		 {
			 return;
		 }
		 System.out.println(cmdStr);
		 Process p =Runtime.getRuntime().exec(cmdStr);
		 BufferedReader in = new BufferedReader(  
		             new InputStreamReader(p.getInputStream()));  
		      String line = null;  
		      while ((line = in.readLine()) != null) {  
		          System.out.println(line);  
		      }
		  p.waitFor(); 
		  return;
            
	}
	
    public static List<File> listf(String rootPath) {
        File root = new File(rootPath);
        
        List<File> resultList = new ArrayList<File>();
        if(root.isFile())
    	{
    		resultList.add(root);
    		return resultList;
    	}
        // get all the files from a directory
        File[] fList = root.listFiles();
        if(fList == null || fList.length<=0) return resultList;
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        return resultList;
    }
	private static void RemoveBregInFile(String fileName) {
		ArrayList<String> strs = new ArrayList<String>();
         try {
		     File file = new File(fileName);
		     BufferedReader  reader = new BufferedReader(new FileReader(file));
		
		     String text = null;
		     
		     while ((text = reader.readLine()) != null) {
		    	 strs.add(text);
		     }
         }
         catch(Exception ex)
         {
        	 System.out.print(ex.toString());
         }
         RemoveBregInStrs(strs,fileName);
	}
	 private static void writeToFile(ArrayList<String> strs, String fileStr) throws IOException
	 {
		 File file = new File(fileStr +".bregrm");
		 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i = 0; i< strs.size(); i++)
	    	{
				String str = strs.get(i);
	    	    bw.write(str);
	    	    bw.newLine();
	    	}
			bw.close();
	 }
    private static void RemoveBregInStrs(ArrayList<String> strs, String fileStr)
    {
    	ArgsParser argsParser = ArgsParser.getInstance();

    	BregBlockHelper bbHelper = new BregBlockHelper(strs, argsParser.mybregStr,
    			argsParser.mybregValueStr, argsParser.mybregBoolValue);

    	int line = 0 ;
    	do
    	{
        	try {
				line = bbHelper.removeNext(line);
			} catch (UnsupportBregExcption e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
    	} while(line>=0);

    	if(bbHelper.isModified)
    	{
    		modifiedFiles.add(fileStr);
        	try {
    			writeToFile(strs, fileStr);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}

    }
   
}
