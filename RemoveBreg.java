import java.io.IOException;

/**
 * 
 */

/**
 * @author yipeng
 *
 */
public class RemoveBreg {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print("hello");
		String currentDir = System.getProperty("user.dir");
        System.out.println("Current dir using System:" +currentDir);
        String str = System.getProperty("user.name");
        System.out.println(str);
//        try {
//		//	Process proc = Runtime.getRuntime().exec("notepad.exe");
//	       // Thread.sleep(1000);  
//	        System.out.println("destroying");  
//	      //  proc.destroy();  
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        regexTest();

	}
	public static void regexTest()
	{
		String line = "if((	\n)&& (bbb))";
		System.out.println(line);
		System.out.println(line.replaceAll("([\\(])(\\s+)[\\)](\\s+)", ""));
	}

}
