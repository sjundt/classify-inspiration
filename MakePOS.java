import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Creates a POS file for each line in the input file
 * 
 * @author sarah jundt
 * @version 3/2015
 *
 */
public class MakePOS {
	public static final String FILE_BASE = "data/tmp/pos";
	
	/**
	 * 
	 * @param args- input file base
	 */
	public static void main(String[] args) {
		String inputFileBase = args[0];
		writePOSFiles(inputFileBase+".train");
		writePOSFiles(inputFileBase+".test");


		
	}
	public static void writePOSFiles(String inputFile){
		int i = 1;
		int numQuotes;
		PrintWriter writer;
		String line;
		Matcher match;
		
		try {

			Pattern linePattern = Pattern.compile("([+-]1 )(.*)$"); 

			//read each line to its own file
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			while ((line = reader.readLine())!=null){
				match = linePattern.matcher(line);
				if (!match.matches()){
					throw new RuntimeException("Invalid feature input file");
				}
				writer = new PrintWriter(new FileWriter(FILE_BASE+i));
				writer.println(match.group(2));
				writer.close();
				i++;
			}
			numQuotes = i-1;
			reader.close();

			//get the pos file for each line
			for (int j=1; j<=numQuotes; j++){
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec("../stanford-parser-full-2015-01-30/lexparsermy.sh "+FILE_BASE+j);
				writer = new PrintWriter(new FileWriter(FILE_BASE+j+".pos"));
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				while((line = reader.readLine()) != null) {
					writer.println(line+"\n");
				}
				pr.waitFor();
				writer.close();
				reader.close();
			}
			//write the POS for each quotation to a new file
			HashMap<String,Integer> numPOS; 
			writer = new PrintWriter(new FileWriter(inputFile+".pos"));
			Pattern posPattern = Pattern.compile(WriteDictionary.POS_PATTERN_STRING); 
			for (int j=1; j<=numQuotes; j++){
				numPOS = new HashMap<String,Integer>();
				reader = new BufferedReader(new FileReader(FILE_BASE+j+".pos"));
				String posString = "";
				while((line = reader.readLine())!=null){
					while(line!=null && line.matches("\\s*")){
						line = reader.readLine();
					}
					if (line!=null){
						String[] wordPOSList = line.split("\\s");
						for (int k= 0; k < wordPOSList.length; k++){
							String wordPOS = wordPOSList[k];
							match = posPattern.matcher(wordPOS);
							if (!match.matches()){
								System.out.println(wordPOS);
								throw new RuntimeException("Error with pattern in POS data file");
							} else {
								String pos = match.group(2);
								Integer num = numPOS.containsKey(pos) ? numPOS.get(pos) : 0;
								numPOS.put(pos,num+1);
								if (k==0 && pos.contains("VB")){
									//first word in sentence is verb
									posString = posString+"true ";
								}
							}	
						}
					}
				}
				reader.close();
				if (posString.equals("")){
					posString = posString+"false ";
				}
				for (String key: numPOS.keySet()){
					posString = posString + key+ ":"+numPOS.get(key)+" ";
				}
				writer.println(posString);
				System.out.println(posString);
			}
			writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("done with "+inputFile);
		}
	}
}