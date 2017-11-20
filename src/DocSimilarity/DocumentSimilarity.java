package DocSimilarity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
public class DocumentSimilarity 
{
	//static Heap heap[]; // for maintaining top-k elements
	HashMap<String, Integer> dictionary;
	ArrayList<Node> graph; // co-occurrence graph
	static ArrayList<HashMap<Integer, Integer>> allDocsTerms;
	static double[][] NodeJaccardSimilarity; 
	static double[][] EdgeJaccardSimilarity;
	static double[][] CossineSimilarity;
	
	// creating Dictionary in main memory
	public void loadDictionary() throws NumberFormatException, IOException
	{
		dictionary = new HashMap<String, Integer>();
		String word = null;
		int order = 0;
		File inFile = new File("Dictionary.txt");      //Reading from file	
	    BufferedReader br = new BufferedReader(new FileReader(inFile)); 
	    while (br.ready()) 
	    {
	    	StringTokenizer line = new StringTokenizer(br.readLine());
	    	if(line.hasMoreTokens())
	           	order = Integer.parseInt(line.nextToken());
	    	if(line.hasMoreTokens())
	        	word = line.nextToken();
	                
	        dictionary.put(word, order);
	    }
	    br.close();
	    /*FileWriter writer = new FileWriter("DictionaryVerify.txt", true);
 	    Set<Entry<String, Integer>> map = dictionary.entrySet();
 	    Iterator<Entry<String, Integer>> i = map.iterator();
 	    while(i.hasNext())
 	    {
 	    	Entry<String, Integer> e = i.next();
 	        writer.write(e.getValue()+"\t"+e.getKey());
 	        writer.write("\r\n"); // write new line 
 	    }
 	    writer.close();*/
	}
	
	// forming co-occurrence graph in main memory
	public void loadCoOccurrenceGraph() throws NumberFormatException, IOException
	{
		graph = new ArrayList<Node>(); // co-occurrence graph;
		File inFile = new File("Co-OccurrenceGraph.txt");      //Reading from file	
	    BufferedReader br = new BufferedReader(new FileReader(inFile)); 
	    while (br.ready()) 
	    {
	    	Node n = new Node();
	    	StringTokenizer line = new StringTokenizer(br.readLine());
	        if(line.hasMoreTokens())
	        	n.word1 = Integer.parseInt(line.nextToken());
	        n.edges = new ArrayList<Edge>();
	        line = new StringTokenizer(br.readLine(),"{:,\t}", false);
	        while(line.hasMoreTokens())
	        {
	        	Edge e = new Edge();
	        	if(line.hasMoreTokens())
	               	e.word2 = Integer.parseInt(line.nextToken());
	        	if(line.hasMoreTokens())
	        		e.weight = Float.parseFloat(line.nextToken());
	        	n.edges.add(e);
	        }
	        graph.add(n);
	    }
	    br.close();
	    /*// verification
	    FileWriter writer = new FileWriter("Co-OccurrenceGraphVerify.txt", true);
		for(Node n: graph)
		{
			writer.write(String.valueOf(n.word1));
			writer.write("\r\n");
			//System.out.print("EdgeList:  ");
			int i=0;
			for(Edge e:n.edges)
			{
				if(i==0)
				{
					writer.write("{"+e.word2+":\t"+e.weight);
					i++;
				}
				else
					writer.write(",\t"+e.word2+":\t"+e.weight);
			}
				
			writer.write("}\r\n");	
		}
		writer.close();*/
	}
	
	public void parseFiles(String filePath) throws FileNotFoundException, IOException 
	{
		allDocsTerms = new ArrayList<HashMap<Integer,Integer>>();
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
                HashMap<Integer, Float> wordsImpScore = keyWordImportanceScore(keywords);
                HashMap<Integer, Integer> docSet = keyWordExpansion(wordsImpScore);
                allDocsTerms.add(docSet);
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
	
	// computing ImportanceScore of each keyword in the document 
	// importance Score is the no. of times a keyword occurs in a document
	public HashMap<Integer, Float> keyWordImportanceScore(ArrayList<String> keywords) throws IOException
	{
		HashMap<Integer, Float> words = new HashMap<Integer, Float>(); // contains word and its importance score
	    int total = keywords.size(); // holds total occurrences of keywords
	    for (String word: keywords) 
	    {
	        if(Debug.ON)
	        	System.out.println("word is "+word);
	        // converting the word to its integer representation
	        if(dictionary.containsKey(word))
	        {
	        	//---------------------check????
	        	int w = dictionary.get(word);
	 	    	if(words.containsKey(w)) // when word already present increase its frequency 
	 	    	{
	 	    	   Float val = words.get(w);
	 	    	   words.put(w,val+1); // updating frequency
	 	    	}
	 	    	else // otherwise add the new word to docSet
	 	    	   words.put(w,(float) 1);
	        }
	        else // word is not present in then ignore that word
	        	continue;
	    }
	    
	    // Normalising the Importance Score
	    // Get a set of the entries
	 	Set<Map.Entry<Integer, Float>> set = words.entrySet();
	 	// Get an iterator
	 	Iterator<Entry<Integer, Float>> i = set.iterator();
	 	while(i.hasNext()) 
	 	{
	 		Entry<Integer, Float> me = i.next();
	 	    int key = me.getKey();
	 	    float value = me.getValue();
	 	    value = value/total;
	 	    words.put(key, value);
	 	}
	 	if(Debug.ON)
	 		System.out.println("keyword size "+words.size());
	 	return words;
	}	

	// expand each keyword considering its importance score and returns the set of expanded words with frequency
	public HashMap<Integer, Integer> keyWordExpansion(HashMap<Integer, Float> words)
	{
		HashMap<Integer, Integer> docSet = new HashMap<Integer, Integer>(); // holds the expanded keyword with freq
		// Get a set of the entries
		Set<Map.Entry<Integer, Float>> set = words.entrySet();
	    // Get an iterator
	    Iterator<Entry<Integer, Float>> i = set.iterator();
	    while(i.hasNext()) 
	    {
	       Entry<Integer, Float> me = i.next();
	       int key = me.getKey();
	       float value = me.getValue();
	       Node n = graph.get(key);
	       ArrayList<Edge> edgeList = n.edges;
	       int size = edgeList.size();
	       int k = max((int) ((value * size)),1); // avoid k=0
	       if(Debug.ON)
	    	   System.out.println("k is"+ k );
	       // retrieving top-k elements from graph
	       /*heap= new Heap[k];
	       for(int j=0;j<k;j++) // inserting first k elements into heap as heap is initially empty
	       {
	    	   Edge e = edgeList.get(j); 
	    	   Heap h = new Heap();
	    	   h.index = e.word2;
	    	   h.val = e.weight;
	    	   heap[j]=h;
	       }
	       buildHeap(k); // constructing min heap of the given k elements
	       for(int j=k;j<size;j++) //inserting elements into heap to find top-k elements
	       {
	    	   Edge e = edgeList.get(j); 
	    	   if(e.weight>heap[0].val)
	    	   {
	    		   heapInsert(e.word2,e.weight,k);
	    	   }
	       }
	       // heap contains top-k element of graph
	       // insert these element into a HashMap while increasing count of their occurrence
	        
	        */
	       for(int j=0;j<k;j++)
	       {
	    	   Edge e = edgeList.get(j); 
	    	   int key1 = e.word2;
	    	   if(docSet.containsKey(key1)) // when word already present increase its frequency 
	    	   {
	    		   int val1 = docSet.get(key1);
	    		   docSet.put(key1, val1+1); // updating frequency
	    	   }
	    	   else // otherwise add the new word to docSet
	    		   docSet.put(key1, 1);
	       }
	       /*for(Heap h: heap)
	       {
	    	   int key1 = h.index;
	    	   if(docSet.containsKey(key1)) // when word already present increase its frequency 
	    	   {
	    		   int val1 = docSet.get(key1);
	    		   docSet.put(key1, val1+1); // updating frequency
	    	   }
	    	   else // otherwise add the new word to docSet
	    		   docSet.put(key1, 1);
	       }*/
	    }
	    if(Debug.ON)
	    	System.out.println("size "+docSet.size());
	    return docSet;
	}
	
	// computing jaccard similarity between the documents
	public void jaccardSimilarity(HashMap<Integer, Integer> docSet1,HashMap<Integer, Integer> docSet2 ,int i1, int j1)
	{
		HashSet<Integer> intersection = new HashSet<Integer>();
		HashSet<Integer> union = new HashSet<Integer>();
		int numerator = 0, denominator = 0;
		Set<Map.Entry<Integer, Integer>> set = docSet1.entrySet();
		Iterator<Map.Entry<Integer, Integer>> i = set.iterator();
		
		// computing intersection of nodes as well as union w.r.t docSet1
		// union denominator is sum of freq of keywords in both docs
		// intersection numerator is min of freq of keywords common in both docs 
		while(i.hasNext())
		{
			Entry<Integer, Integer> me = i.next();
			int key = me.getKey();
			union.add(key);
			if(docSet2.containsKey(key))
			{
				intersection.add(key);
				numerator += min(docSet1.get(key),docSet1.get(key));
				denominator += max(docSet1.get(key),docSet1.get(key));
			}
			else
				denominator += docSet1.get(key);
		}
		// elements from doc2 for union
		Set<Map.Entry<Integer, Integer>> set2 = docSet2.entrySet();
		Iterator<Map.Entry<Integer, Integer>> i2 = set2.iterator();
		while(i2.hasNext())
		{
			Entry<Integer, Integer> me = i2.next();
			int key = me.getKey();
			if(!union.contains(key))
			{
				union.add(key);
				denominator += docSet2.get(key);
			}
			
		}
		if(Debug.ON)
			System.out.println("numerator "+numerator+"denominator "+denominator);
		double nodeJaccardsimilarity = (double)intersection.size() / union.size();
		double edgeJaccardsimilarity = (double)numerator / denominator;
		System.out.println("Node Jaccard Similarity is "+nodeJaccardsimilarity);
		System.out.println("Edge Jaccard Similarity is "+edgeJaccardsimilarity);
		NodeJaccardSimilarity[i1][j1] = nodeJaccardsimilarity;
		EdgeJaccardSimilarity[i1][j1] = edgeJaccardsimilarity;
		//return similarity;
		
	}
	
	// Calculates the tf of term termToCheck
    public double tfCalculator(HashMap<Integer, Integer> docSet, int termToCheck, int total) 
    {
        double count = 0;  //to count the overall occurrence of the term termToCheck
        if(docSet.containsKey(termToCheck))
        	count = docSet.get(termToCheck);
        return count / total;
    }

    // Calculates idf inverse document frequency) score of term termToCheck
    public double idfCalculator(HashMap<Integer, Integer> docSet1,HashMap<Integer, Integer> docSet2, int termToCheck) 
    {
        double count = 0;
        int noOfDoc = 2;
        if(docSet1.containsKey(termToCheck))
        	count++;
        if(docSet2.containsKey(termToCheck))
        	count++;
        return 1 + Math.log(noOfDoc / count); 
    }
	
    // computing cosine similarity
    public double cosineSimilarity(double[] docVector1, double[] docVector2) 
    {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;

        for (int i = 0; i < docVector1.length; i++) //docVector1 and docVector2 must be of same length
        {
            dotProduct += docVector1[i] * docVector2[i];  //a.b
            magnitude1 += Math.pow(docVector1[i], 2);  //(a^2)
            magnitude2 += Math.pow(docVector2[i], 2); //(b^2)
        }

        magnitude1 = Math.sqrt(magnitude1); //sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2); //sqrt(b^2)

        if (magnitude1 != 0.0 | magnitude2 != 0.0) 
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        else 
            return 0.0;
        
        return cosineSimilarity;
    }
    
	public int min(Integer a, Integer b) 
	{
		return a<b?a:b;
	}
	
	public int max(Integer a, Integer b) 
	{
		return a>b?a:b;
	}

	
	
	public static void main(String[] args) throws IOException
	{
		DocumentSimilarity ob = new DocumentSimilarity();
		ob.loadDictionary();
		ob.loadCoOccurrenceGraph();
		String path = "testcase1_strategy"; 
		ob.parseFiles(path);
		NodeJaccardSimilarity = new double[allDocsTerms.size()][allDocsTerms.size()];
		EdgeJaccardSimilarity = new double[allDocsTerms.size()][allDocsTerms.size()];
		CossineSimilarity = new double[allDocsTerms.size()][allDocsTerms.size()];
		for(int i=0;i<allDocsTerms.size();i++)
		{
			HashMap<Integer, Integer> docSet1 = allDocsTerms.get(i);
			for(int j=0;j<allDocsTerms.size();j++)
			{
				HashMap<Integer, Integer> docSet2 = allDocsTerms.get(i);
				TreeSet<Integer> allterms = new TreeSet<Integer>(); // to hold all possible terms in 2 documents to be compared
				
				// total elements from doc1
				Set<Map.Entry<Integer, Integer>> set1 = docSet1.entrySet();
				Iterator<Map.Entry<Integer, Integer>> i1 = set1.iterator();
				int  total1 = 0; // total occurrences of keywords in document1
				while(i1.hasNext())
				{
					Entry<Integer, Integer> me = i1.next();
					total1 += me.getValue();
					allterms.add(me.getKey());
				}
				
				// total elements from doc2
				Set<Map.Entry<Integer, Integer>> set2 = docSet2.entrySet();
				Iterator<Map.Entry<Integer, Integer>> i2 = set2.iterator();
				int  total2 = 0; // total occurrences of keywords in document2
				while(i2.hasNext())
				{
					Entry<Integer, Integer> me = i2.next();
					total2 += me.getValue();
					allterms.add(me.getKey());
				}
				
				// computing node and edge Jaccard similarity between the set of documents
				//double jSimilarity = ob.jaccardSimilarity(docSet1, docSet2);
				 ob.jaccardSimilarity(docSet1, docSet2, i, j);
				//System.out.println("Jaccard Similarity is "+jSimilarity);
				
				// computing cosine similarity between the set of documents
				// computing tfidf vectors for the 2 documents to be compared 
				 double tf1,tf2; //term frequency
			    double idf; //inverse document frequency
			    double tfidf1,tfidf2; //term frequency inverse document frequency        
			    double[] tfidfvectors1 = new double[allterms.size()];
			    double[] tfidfvectors2 = new double[allterms.size()];
			    int count = 0;
			    for (int terms : allterms) 
			    {
			        tf1 = ob.tfCalculator(docSet1, terms, total1);
			        tf2 = ob.tfCalculator(docSet2, terms, total2);
			        idf = ob.idfCalculator(docSet1, docSet2, terms);
			        tfidf1 = tf1 * idf;
			        tfidf2 = tf2 * idf;
			        tfidfvectors1[count] = tfidf1;
			        tfidfvectors2[count] = tfidf2;
			        count++;
			    }
			    double cSimilarity = ob.cosineSimilarity(tfidfvectors1, tfidfvectors2);
			    System.out.println("Cossine Similarity is "+cSimilarity);
			    CossineSimilarity[i][j] = cSimilarity;
			}
		}
		//displaying pairwise node jaccard similarity
		System.out.println(" Node Jaccard Similarity is :");
		for(int i = 0;i < NodeJaccardSimilarity.length; i++)
		{
			for(int j = 0;j < NodeJaccardSimilarity.length; j++)
			{
				System.out.print(NodeJaccardSimilarity[i][j]+"\t");
			}
			System.out.println();
		}
		//displaying pairwise edge jaccard similarity
		System.out.println(" Edge Jaccard Similarity is :");
		for(int i = 0;i < EdgeJaccardSimilarity.length; i++)
		{
			for(int j = 0;j < EdgeJaccardSimilarity.length; j++)
			{
				System.out.print(EdgeJaccardSimilarity[i][j]+"\t");
			}
			System.out.println();
		}		
		//displaying pairwise node jaccard similarity
		System.out.println(" Cossine Similarity is :");
		for(int i = 0;i < CossineSimilarity.length; i++)
		{
			for(int j = 0;j < CossineSimilarity.length; j++)
			{
				System.out.print(CossineSimilarity[i][j]+"\t");
			}
			System.out.println();
		}		
	}
}
	

