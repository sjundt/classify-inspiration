import java.io.*;
import java.util.*;

/**
 * Runs through all possible combinations of features. 
 * For each combination, get featureFiles, train a model,
 * run time and rand testing sets. Output results in multiclassify_results.txt.
 * @author sarah jundt, shannon lubetich, george price
 * @version modified 3/2015 sarah jundt, for senior project
 *
 */
public class MultiClassify {
	
	/**
	 * get featureFiles, train a model, and for each model,
	 * run time and rand testing sets. Output results in results file.
	 * @param args- none
	 */
	public static void main(String[] args) {
		//unigrams, bigrams, sentiment, pos
		int numFeatures = 4;
		PrintWriter writer = null;
		String subsetString;
		try {
			BufferedReader reader;
			writer = new PrintWriter(new FileWriter(args[0]+".multiclassify_results.txt"));
			for(int i = 0; i < Math.pow(2,numFeatures); i++) {
				String[] subSet = new String[numFeatures];
				for(int b = 0; b < numFeatures; b++) {
					subSet[b] = "" + ((i >> b) & 1);
				}
				subsetString = Arrays.toString(subSet);
				String featureNumsString = "";
				for (String s: subSet){
					featureNumsString= featureNumsString+s;
				}
				System.out.println(subsetString);
				String[] featureSelectorInput = new String[subSet.length+1];
				featureSelectorInput[0] = args[0];
				for (int j=1; j <= subSet.length; j++){
					featureSelectorInput[j] = subSet[j-1];
				}
				FeatureSelector.main(featureSelectorInput);
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

				writer.print("features "+subsetString+":\t");
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