import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
 * For a file of quotes, create a dictionary
 * 
 * @author sarah jundt, george price, shannon lubetich
 * @version 2- sarah jundt 3/2015, for senior project
 *
 */
public class WriteDictionary {
	protected static final String FEATURE_PREFIX = "";
	private static final double STOP_LIST_PARAM = 0.01; //0.01
	public static final String DATA_DIR_STRING = "data/";
	public static final String DICTIONARY_FILE_STRING = DATA_DIR_STRING+"dictionary.txt";
	public static final String REMOVED_UNIGRAM_FILE_STRING = DATA_DIR_STRING+"removed_unigrams.txt";
	public static final String REMOVED_BIGRAM_FILE_STRING = DATA_DIR_STRING+"removed_bigrams.txt";
	public static final String POS_DICTIONARY = DATA_DIR_STRING+"pos_dictionary.txt";
	public static final String POS_PATTERN_STRING = "(\\S+)/(\\S+)$";
	protected HashMap<String, Integer> unigrams = new HashMap<String, Integer>();
	protected HashMap<String, Integer> bigrams = new HashMap<String, Integer>();
	protected Set<String> vocabulary;
	protected Set<String> bigramVocabulary;
	protected Set<String> uniqueVocab;
	protected Set<String> uniqueBigrams;
	Set<String> removedWords= new HashSet<String>();
	protected Map<String, Integer> dictionaryMap;
	protected String fileExt;
	String sentence;
	Integer removedVocabSize= 0;
	Integer removedBigramsSize = 0;

	/**
	 * Creates dictionary file
	 * 
	 * @param inputFiles
	 *            - files of quotations
	 */
	public WriteDictionary(List<String> inputFiles) {
		vocabulary = new HashSet<String>();
		bigramVocabulary = new HashSet<String>();
		uniqueVocab = new HashSet<String>();
		uniqueBigrams = new HashSet<String>();
		dictionaryMap = new HashMap<String, Integer>();
		
		getDictionary(inputFiles, STOP_LIST_PARAM);
		// writePOSDictionary(posDataFiles);
	}


	private void removeStopWords(double commonFraction, boolean isUnigrams) throws IOException{
		String outputFile;
		HashMap<String, Integer> map;
		Set<String> unique;
		if (isUnigrams){
			outputFile = REMOVED_UNIGRAM_FILE_STRING;
			map = unigrams;
			unique = uniqueVocab;
		} else {
			outputFile = REMOVED_BIGRAM_FILE_STRING;
			map = bigrams;
			unique = uniqueBigrams;
		}


		TreeMap<Double, String> sorted = new TreeMap<Double, String>(
					Collections.reverseOrder());
		PrintWriter removedWordsFile = new PrintWriter(new FileWriter(outputFile));

		for (String k : map.keySet()) {
			Double thisDouble = map.get(k) + 0.0;
			while (sorted.containsKey(thisDouble)) {
				thisDouble = thisDouble + 0.00000000001;
			}
			sorted.put(thisDouble, k);
		}
		//remove most common words

		for (int i = 0; i < map.size() * commonFraction; i++) {
			Entry<Double, String> next = sorted.pollFirstEntry();
			removedWordsFile.println(next.getKey()+": "+next.getValue());
			sorted.remove(next.getKey());
			dictionaryMap.remove(next.getValue());
			if (isUnigrams){
				removedVocabSize +=1;
				removedWords.add(next.getValue());
			} else {
				removedBigramsSize+=1;
			}
		}
		//remove unique word
		for (String word : unique) {
			dictionaryMap.remove(word);
			removedWordsFile.println(word);
			if (isUnigrams){
				removedWords.add(word);
			} else {
				removedBigramsSize+=1;
			}
		}

		if (!isUnigrams){ 
			for (String gram: map.keySet()){
				String[] words = gram.split(" ");
				if (words.length!=2){
					throw new RuntimeException("Non-bigram in bigrams map");
				} else if (removedWords.contains(words[0]) && removedWords.contains(words[1])){
					removedWordsFile.println(gram);
					dictionaryMap.remove(gram);
					removedBigramsSize+=1;
				}
			}
		}
		removedWordsFile.close();
	}

	/**
	 * Reads in files and creates dictionary
	 * 
	 * @param inputFiles
	 *            - files of quotations
	 * @param commonFraction
	 *            - remove the top commonFraction most common words from our
	 *            feature set.
	 */
	private void getDictionary(List<String> inputFiles, double commonFraction) {
		try {
			BufferedReader reader;
			String sentence, text;

			for (String inputFile : inputFiles) {
				reader = new BufferedReader(new FileReader(inputFile));
				while ((sentence = reader.readLine()) != null) {
					parseWordsFirstPass(sentence);
				}
				reader.close();
			}
			
			removeStopWords(commonFraction, true);
			removeStopWords(commonFraction, false);

			removedVocabSize+=uniqueVocab.size();

			// write out dictionary
			PrintWriter writer = new PrintWriter(new FileWriter(
					DICTIONARY_FILE_STRING));
			for (String word : dictionaryMap.keySet()) {
				writer.println(word + ":" + dictionaryMap.get(word));
			}
			writer.close();

			System.out.println("Total Vocabulary Size (Unigrams): "+unigrams.keySet().size());
			System.out.println("Removed Unigrams (Most frequent "+STOP_LIST_PARAM+"/1 & unique words): "+removedVocabSize);
			System.out.println("Unigrams used as features: "+(unigrams.keySet().size()-removedVocabSize));
			System.out.println("Total Bigrams: "+bigrams.keySet().size());
			System.out.println("Bigrams used as features: "+(bigrams.keySet().size()-removedBigramsSize));
			System.out.println("Total features: "+(dictionaryMap.keySet().size()));


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// private void writePOSDictionary(List<String> posDataFiles){
	// 	try{
	// 		Set<String> posFeatures = new HashSet<String>();
	// 		Pattern pattern = Pattern.compile(POS_PATTERN_STRING); 
	// 		for (String posDataFile: posDataFiles){

				
	// 			BufferedReader reader = new BufferedReader(new FileReader(posDataFile));

	// 			while ((sentence = reader.readLine()) != null) {
	// 				String[] wordFeaturePairs = sentence.split("\\s");
	// 				for (int i= 0; i < wordFeaturePairs.length; i++){
	// 					String wordFeaturePair = wordFeaturePairs[i];
	// 					if (!wordFeaturePair.matches("\\s*")){
	// 						Matcher match = pattern.matcher(wordFeaturePair);
	// 						if (!match.matches()){
	// 							throw new RuntimeException("Error with pattern in POS data file");
								
	// 						} else {
	// 							posFeatures.add(match.group(2));
	// 						}
	// 					}
	// 				}
	// 			}
	// 			reader.close();
	// 		}
	// 		PrintWriter writer = new PrintWriter(new FileWriter(POS_DICTIONARY));
	// 		for (String posFeature: posFeatures){
	// 			writer.println(posFeature);
	// 		}
	// 		writer.close();
	// 	}
	// 	catch(IOException e){
	// 		throw new RuntimeException("Error with reading/writing POS file/dictionary"+e);
	// 	}	
	// }
	
	/**
	 * for a single quote, appropriately parses and stores all unigrams
	 * @param text - content of a quotation
	 */
	private void parseWordsFirstPass(String text) {
		List<String> words = getWords(text);
		// unigrams
		for (String word : words) {
			addToVocab(word, unigrams, vocabulary, uniqueVocab);
		}
		//bigrams
		for(int i = 0; i<words.size()-1;i++){
			int size = dictionaryMap.size();
			String bigram = words.get(i)+" "+words.get(i+1);
			addToVocab(bigram, bigrams, bigramVocabulary, uniqueBigrams);
		}
	}

	/**
	 * used for storing unigrams always adds to vocabulary only adds to
	 * uniqueVocab and dictionaryMap if this is the first time we're seeing it
	 * appropriately removes from uniqueVocab if we're seeing a word we've seen
	 * already
	 * 
	 * @param word- word to add to vocab
	 */
	private void addToVocab(String word, HashMap<String, Integer> map, Set<String> voc, Set<String> uniqueVoc) {

		int count = map.get(word) == null ? 0 : map.get(word);
		map.put(word, count + 1);

		// since is set, add(word) returns true if successfully added, false if
		// already there
		if (voc.add(word)) {
			uniqueVoc.add(word);
			dictionaryMap.put(word, dictionaryMap.size() + 1);
		} else {
			uniqueVoc.remove(word); // not actually unique, because already in
										// set of vocab
		}
	}

	/**
	 * returns a List of strings that are the items in the quotations, split on
	 * whitespace
	 * 
	 * @param text- text to be split into words
	 * @return list of strings of words in quote
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
	 * Writes out the dictionary file for the input files
	 * args- training file
	 */
	public static void main(String[] args) {
		
		List<String> inputFiles = new ArrayList<String>();
		//first_pass: dictionary
		inputFiles.add(args[0]);
		List<String> posDataFiles = new ArrayList<String>();
		new WriteDictionary(inputFiles);
	}

}
