import java.io.*;
import java.util.*;

/**
 * Runs through reasonable percentages for unigram & bigram percentage stoplists.
 * For each combination, write dictionary, get featureFiles, train a model,
 * run testing set. Output results in stoplist_experiment_results.txt.
 * @author sarah jundt, shannon lubetich, george price
 * @version modified 3/2015 sarah jundt, for senior project
 *
 */
public class StoplistExperimenter {
	private static final double STOPLIST_PARAM = 0.001;
	private static final int NUM_FEATURES = 6;
	/**
	 * get dictionaries, gets featureFiles, train a model, and for each model,
	 * run time and rand testing sets. Output results in results file.
	 * @param args- none
	 */
	public static void main(String[] args) {
		//unigrams, bigrams, sentiment, pos
		PrintWriter writer = null;
		try {
			String subsetString, featureNumsString;
			BufferedReader reader;
			writer = new PrintWriter(new FileWriter(args[0]+".stoplist_experiment_results.txt"));
			

			for(int i = 0; i < Math.pow(2,NUM_FEATURES); i++) {
				String[] subSet = new String[NUM_FEATURES];
				for(int b = 0; b < NUM_FEATURES; b++) {
					subSet[b] = "" + ((i >> b) & 1);
				}
				subsetString = Arrays.toString(subSet);
				featureNumsString = "";
				for (String s: subSet){
					featureNumsString= featureNumsString+s;
				}
				System.out.println(subsetString);
				

				String[] writeDictionaryInput = new String[NUM_FEATURES+3];
				writeDictionaryInput[0] = args[0]+".train";
				for (int j=1; j <= subSet.length; j++){
					if (j<=2){
						if (subSet[j-1].equals("1")){
							writeDictionaryInput[j]=String.valueOf(STOPLIST_PARAM);
						} else {
							writeDictionaryInput[j]="0.0";
						}
					} else {
						writeDictionaryInput[j] = subSet[j-1];
					}
				}

				System.out.println(subsetString);
				WriteDictionary.main(writeDictionaryInput);

				boolean[] featureSelectorInput = new boolean[6];
				featureSelectorInput[0] = true;
				featureSelectorInput[1] = true;
				featureSelectorInput[2] = false;
				featureSelectorInput[3] = false;
				featureSelectorInput[4] = false;
				featureSelectorInput[5] = false;
				new FeatureSelector(args[0], featureSelectorInput);


				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec("../svm_light/svm_learn "+args[0]+".train.features "+args[0]+"."+featureNumsString+".model");
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line;
				while((line = reader.readLine()) != null) {
					System.out.print(line+"\t");
				}
				pr.waitFor();
				pr = rt.exec("../svm_light/svm_classify "+args[0]+".test.features "+args[0]+"."+featureNumsString+".model "+args[0]+".svm_output");
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				while((line = reader.readLine()) != null) {
					System.out.println(line+"\t");
				}
				pr.waitFor();
				pr = rt.exec("python accuracy.py "+args[0]+".test.labels "+args[0]+".svm_output");
				pr.waitFor();

				writer.print("stoplistParams: "+subsetString+":\t");
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				while((line = reader.readLine()) != null) {
					writer.print(line+"\n");
				}
				writer.print("\n");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("done");
			writer.close();
		}
	}
}