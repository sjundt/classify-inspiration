import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map.Entry;

/**
 * For a file of quotes, prints features in SVMlight format.
 * 
 * @author sarah jundt, shannon lubetich, george price
 * @version 2- sarah jundt, 3/2015, for senior project
 *
 */
public class FeatureSelector {
	protected static final String FEATURE_PREFIX = "";
	private static final double STOP_LIST_PARAM = 0.01; //0.01
	private static final String SENTIMENT_DATA_FILE = "../NRC-Emotion-Lexicon-v0.92/positive_data.txt";
	public static final String TEST_FILE_EXT = ".test";
	public static final String TRAIN_FILE_EXT = ".train";
	public static final String POS_FILE_EXT = ".pos";
	
	protected Map<String, HashMap<String, Integer>> bigrams;
	protected HashMap<String, Integer> unigrams = new HashMap<String, Integer>();
	protected Set<String> vocabulary;
	protected Map<String, Integer> dictionaryMap;
	HashMap<String, Integer> bagOfWords;
	protected int maxGrams;
	protected boolean[] featureFlags;
	protected String posSentence, sentence;
	int numChars, numWords, uniqueCount, nextIndex;
	BufferedReader reader, posReader;
	PrintWriter featureW;
	Pattern pattern, posPattern;
	Matcher match;
	HashMap<String,Set<String>> sentimentLists;
	List<String> posFeatures;


	/**
	 * For testing: reads in quotations, reads in external dictionary file, and then
	 * outputs feature file w/ labels in different file.
	 * 
	 * @param fileBase
	 *            - file base for input/output files
	 * @param featureFlags
	 *            - which features to include
	 */
	public FeatureSelector(String fileBase, boolean[] featureFlags){
		vocabulary = new HashSet<String>();
		dictionaryMap = new HashMap<String, Integer>();
		this.featureFlags = featureFlags;
		readDictionary();
		posPattern = Pattern.compile("(\\S+)\\s*:\\s*(\\d+)$"); 
		writeFeatures(fileBase+TRAIN_FILE_EXT, fileBase+TRAIN_FILE_EXT+POS_FILE_EXT, true);
		writeFeatures(fileBase+TEST_FILE_EXT, fileBase+TEST_FILE_EXT+POS_FILE_EXT, false);
	}

	/**
	 * used to read in the dictionary from external file and save in
	 * dictionaryMap of String (unigram or bigram) to feature index
	 * 
	 * @param dictionaryFile
	 *            - file with word:feature_num
	 */
	private void readDictionary() {
		// read in vocab
		String wordEntry;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(WriteDictionary.DICTIONARY_FILE_STRING));
			while ((wordEntry = reader.readLine()) != null) {
				Matcher m = Pattern.compile("(.+):(.+)").matcher(wordEntry);
				if (m.matches()) {
					// store in dictionary map as word --> feature number
					int nextKey = Integer.parseInt(m.group(2));
					dictionaryMap.put(m.group(1), nextKey);
					if (maxGrams < nextKey) {
						maxGrams = nextKey;
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(
					"Issues loading dictionary file from training.");
		}
	}

	private void writeSentiment(){
		Set<String> sentimentsIncluded = new HashSet<String>();

		for (String word: bagOfWords.keySet()){
			for (String sentiment: sentimentLists.keySet()){
				if (sentimentLists.get(sentiment).contains(word)){
					sentimentsIncluded.add(sentiment);
				}
			}
		}
		
		List<String> sentiments = new ArrayList<String>();
		sentiments.add("anger");
		sentiments.add("fear");
		sentiments.add("anticipation");
		sentiments.add("trust");
		sentiments.add("surprise");
		sentiments.add("sadness");
		sentiments.add("joy");
		sentiments.add("disgust");
		sentiments.add("positive");
		sentiments.add("negative");

		for(int i = 0; i<10; i++){
			String sentiment = sentiments.get(i);
			if (sentimentsIncluded.contains(sentiment)){
				featureW.print(nextIndex+":"+1+" ");
			}
			nextIndex++;
		}
	}

//TODO: FIX THIS
	/**
	 * writeFeatures is called in both training or testing, with a boolean
	 * parameter to specify which in training, the actual like numbers are
	 * written out to the files with the feature vector in testing, a 0 is
	 * written out as the score with the feature vector, and a separate file is
	 * created of the actual likes received
	 * 
	 * @param inputFiles- files of quotations
	 * @param train - whether this is going to be used for training
	 */
	private void writeFeatures(String inputFile, String posDataFile, boolean train) {
		try {
			BufferedReader reader, posReader;
			TreeMap<Integer, Integer> gramFeatureMap;
			String featureFile;
			featureFile = inputFile + ".features";
			featureW = new PrintWriter(new FileWriter(featureFile)); // features of cases
			PrintWriter labelW;
			if (train){
				labelW = new PrintWriter(new FileWriter("tmp"));
			} else {
				labelW = new PrintWriter(new FileWriter(inputFile+ ".labels")); // labels of cases
			}

			//sentiment words: read in
			sentimentLists = new HashMap<String,Set<String>>();
			if (featureFlags[2]){
				pattern = Pattern.compile("(\\S+)\t(\\S+)\t1"); 
				reader = new BufferedReader(new FileReader(SENTIMENT_DATA_FILE));
				while ((sentence = reader.readLine()) != null) {
					match = pattern.matcher(sentence);
					if (!match.matches()){
						throw new RuntimeException("Error with pattern in sentiment data file");
					} else {
						Set<String> wordSet;
						if (sentimentLists.containsKey(match.group(2))){
							wordSet = sentimentLists.get(match.group(2));
						} else {
							wordSet = new HashSet<String>();
							sentimentLists.put(match.group(2), wordSet);
						}
						wordSet.add(match.group(1));
					}
				}
				reader.close();
			}

			//POS dictionary: read in POS features
			posFeatures = new ArrayList<String>();
			if (featureFlags[3]){
				reader = new BufferedReader(new FileReader(WriteDictionary.POS_DICTIONARY));
				while ((sentence = reader.readLine()) != null) {
					posFeatures.add(sentence);
				}
				reader.close();
			}

			Pattern linePattern = Pattern.compile("([+-]1 )(.*)$"); 
			reader = new BufferedReader(new FileReader(inputFile));
			posReader = new BufferedReader(new FileReader(posDataFile));
			String posString;
			while ((sentence = reader.readLine()) != null) {
				match = linePattern.matcher(sentence);
				if (!match.matches()){
					throw new RuntimeException("Invalid feature input file");
				}
				String label = match.group(1);
				String text = match.group(2);
				posString = posReader.readLine();
				if (posString==null){
					throw new RuntimeException("Unhappy null line in pos file "+posDataFile);
				}
				

				bagOfWords = new HashMap<String, Integer>();
				List<String> words = getWords(text);
				numChars = text.length();
				numWords = words.size();
				for (String word : words) {
					Integer wordCount = bagOfWords.get(word) != null ? bagOfWords
							.get(word) : 0;
					bagOfWords.put(word, wordCount + 1);
				}
				gramFeatureMap = new TreeMap<Integer, Integer>();

				//unigrams
				if (featureFlags[0]){
					for (String word : bagOfWords.keySet()) {
						Integer wordNum = dictionaryMap.get(word);
						if (wordNum != null) {
							gramFeatureMap.put(wordNum,bagOfWords.get(word));
						} else { 
							uniqueCount++;
						}
					}
				}
					
				//bigrams
				if (featureFlags[1]){
					HashMap<String,Integer> bigramBOW = new HashMap<String,Integer>();
					for (int i= 0; i < words.size()-1; i++){
						String bigram =words.get(i)+" "+words.get(i+1);
						int count = bigramBOW.get(bigram)==null ? 0: bigramBOW.get(bigram);
						bigramBOW.put(bigram, count+1);
					}
					for(String bigram : bigramBOW.keySet()){
						Integer wordNum = dictionaryMap.get(bigram);
						if(wordNum!=null){
							gramFeatureMap.put(wordNum, bigramBOW.get(bigram));
						}
					}
				}
				
				//print out label
				if (train){
					featureW.print(label);
				}else{
					labelW.print(label+"\n");
					featureW.print("0 ");
				}
				// unigrams & bigrams
				if (featureFlags[0] || featureFlags[1]) {
					while (!gramFeatureMap.isEmpty()) {
						Entry<Integer, Integer> feature = gramFeatureMap.pollFirstEntry();
						featureW.print(feature.getKey() + ":"
								+ feature.getValue() + " ");
					}
					nextIndex = maxGrams + 1;
				} else {
					nextIndex = 1;
				}

				if (featureFlags[2]){
					writeSentiment();
				}

				//POS tags
				if (featureFlags[3]){
					writePOS(posString);
				}
				featureW.print("\n");
			}
			reader.close();
			posReader.close();
			featureW.close(); // must close out of file loop in order to write
								// all features for all input files
			labelW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private void writePOS(String posString){
		HashMap<String,Integer> numPOS = new HashMap<String,Integer>();

		String[] wordPOSList = posString.split("\\s");
		for (int i= 0; i < wordPOSList.length; i++){
			String wordPOS = wordPOSList[i];
			match = posPattern.matcher(wordPOS);
			if (!match.matches()){
				System.out.println(wordPOS);
				throw new RuntimeException("Error with pattern in POS data file");
			} else {
				String fullPOS = match.group(1);
				for (String featurePOS: posFeatures){
					if (fullPOS.contains(featurePOS)){
						int prevNum = 0;
						if (numPOS.containsKey(featurePOS)){
							prevNum = numPOS.get(featurePOS);
						}
						numPOS.put(featurePOS,prevNum+Integer.parseInt(match.group(2)));
					}
				}

			}
		}	
		for (String pos: posFeatures){
			if (numPOS.containsKey(pos)){
				featureW.print(nextIndex+":"+numPOS.get(pos)+" ");
			}
			nextIndex++;
		}
	}

	/**
	 * returns a List of strings that are the items in the YikYak, split on
	 * whitespace
	 * 
	 * @param text- text to be split into words
	 * @return list of strings of words in YikYak
	 */
	public static List<String> getWords(String text) {
		// text = text.trim(); // eliminate trailing whitespace (which for some
							// reason was being seen as its own word
		String[] words = text.toLowerCase().split("\\s+");
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			while (word.matches("[.,!?\"'&/(#:-].+")) {
				result.add(word.substring(0, 1));
				word = word.substring(1, word.length());
			}
			List<String> afterWords = new ArrayList<String>();
			while (word.matches(".+[.,!?\"'&/)#-:]")) {
				afterWords.add(0, word.substring(word.length() - 1));
				word = word.substring(0, word.length() - 1);
			}
			result.add(word);
			result.addAll(afterWords);
		}
		return result;
	}

	/**
	 * Prints feature files for files given which features to include
	 * @param args- fileBase, which features to include. 1 if include feature.
	 *  CURRENTLY: 	0: unigrams
	 * 				1: bigrams
	 * 				2: sentiment categories
	 * 				3: POS
	 */
	public static void main(String[] args) {
		final int NUM_ARGS_BEFORE_FEATURE = 1;
		boolean[] featureFlags = new boolean[args.length-NUM_ARGS_BEFORE_FEATURE];
		for (int i = NUM_ARGS_BEFORE_FEATURE; i < args.length; i++) {
			featureFlags[i-NUM_ARGS_BEFORE_FEATURE] = (Integer.parseInt(args[i]) == 1);
		}

		new FeatureSelector(args[0],featureFlags);
	}

}
