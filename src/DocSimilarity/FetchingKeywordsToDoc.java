package DocSimilarity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
public class FetchingKeywordsToDoc
{	
	public void parseFiles(String filePath) throws FileNotFoundException, IOException 
	{
		//allDocsTerms = new ArrayList<HashMap<Integer,Integer>>();
        File[] allfiles = new File(filePath).listFiles();
        BufferedReader in = null;
        for (File f : allfiles) 
        {
            if (f.getName().endsWith(".txt")) 
            {
                in = new BufferedReader(new FileReader(f));
                StringBuilder sb = new StringBuilder();
                String s = null;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                }
                StringBuilder sb1 = new StringBuilder();
                String[] tokenizedTerms = sb.toString().replaceAll("[\\W&&[^\\s]]", "").split("\\W+");   //to get individual terms
                System.out.println("extracted terms from doc "+f.getName());
                for (String term : tokenizedTerms) 
                {
                	sb1.append(term);
                	System.out.println(term);
                	sb1.append(" ");
                    /*if (!allTerms.contains(term)) {  //avoid duplicate entry
                        allTerms.add(term);
                    }*/
                }
                //termsDocsArray.add(tokenizedTerms);
                ArrayList<String> keywords = keyWordExtraction(sb1.toString());
                FileWriter writer = new FileWriter("Doc/"+f.getName(), true);
         	    for(String word:keywords)
         	    {
         	        writer.write(word+"\t");
         	        writer.write("\r\n"); // write new line 
         	    }
         	    writer.close();
            }
        }
    }
	
	// extracting keywords from the document
	public ArrayList<String> keyWordExtraction(String sample) throws FileNotFoundException
	{
		// Initialise the tagger
		MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
		// The tagged string
		String taggedFileContent = tagger.tagString(sample);
		System.out.println(taggedFileContent);
		// splitting file content
		String taggedText[]=taggedFileContent.split(" ");
		String taggedAdj[];
		ArrayList<String> words = new ArrayList<String>();
		String[] sw = {"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
		//System.out.println(sw.length);
		HashSet<String> stopWords= new HashSet<String>();
		for(String s:sw)
			stopWords.add(s);
		for(int i=0;i<taggedText.length;i++)
		{
			// identify adjectives
			if(taggedText[i].contains("_JJ") || taggedText[i].contains("_JJR") || taggedText[i].contains("_JJS"))
			{
				taggedAdj=taggedText[i].split("(_JJ){1,1}R{0,1}S{0,1}");
				taggedAdj[0]=taggedAdj[0].toLowerCase();
				if(!stopWords.contains(taggedAdj[0]))
				{
					// converting the word to its integer representation
		        	//int w = t.headSet(taggedAdj[0]).size();
					//int w = dictionary.get(taggedAdj[0]);
					words.add(taggedAdj[0]);
				}
			}
			// identify Verbs
			else if(taggedText[i].contains("_VB") || taggedText[i].contains("_VBD") || taggedText[i].contains("_VBG") || taggedText[i].contains("_VBN") || taggedText[i].contains("_VBP") || taggedText[i].contains("_VBZ"))
			{
				taggedAdj=taggedText[i].split("(_VB){1,1}D{0,1}G{0,1}N{0,1}P{0,1}Z{0,1}");
				taggedAdj[0]=taggedAdj[0].toLowerCase();
				if(!stopWords.contains(taggedAdj[0]))
				{
					// converting the word to its integer representation
		        	//int w = t.headSet(taggedAdj[0]).size();
					//int w = dictionary.get(taggedAdj[0]);
					words.add(taggedAdj[0]);
				}
			}
			// identify Nouns except proper noun
			else if(taggedText[i].contains("_NN") || taggedText[i].contains("_NNS") )
			{
				taggedAdj=taggedText[i].split("(_NN){1,1}S{0,1}");
				taggedAdj[0]=taggedAdj[0].toLowerCase();
				if(!stopWords.contains(taggedAdj[0]))
				{
					// converting the word to its integer representation
		        	//int w = t.headSet(taggedAdj[0]).size();
					//int w = dictionary.get(taggedAdj[0]);
					words.add(taggedAdj[0]);
				}
			}	
		}
		// adjectives, nouns and verbs
		if(Debug.ON)
			for(String s : words)		 
				System.out.println(s);
		return words;
	}
	 
	public static void main(String[] args) throws IOException
	{
		FetchingKeywordsToDoc ob = new FetchingKeywordsToDoc();
		String path = "testcase1_strategy"; 
		ob.parseFiles(path);
	}
}
	

