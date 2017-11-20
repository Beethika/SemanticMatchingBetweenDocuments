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
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Graph 
{
	static Heap heap[]; // for maintaining top-k elements
	
	// lexicographic ordering of all keywords present in document corpus
	public TreeSet<String> renamingWords() throws IOException
	{
		TreeSet<String> words = new TreeSet<String>();
		String word,word2;
	    File inFile = new File("14.txt");      //Reading from file	
	    BufferedReader br = new BufferedReader(new FileReader(inFile)); 
	    while (br.ready()) 
	    {
	    	StringTokenizer line = new StringTokenizer(br.readLine());
	        if(line.hasMoreTokens())
	        {
	        	word = line.nextToken();
	        	if(!words.contains(word))
	        		words.add(word);
	        }
	        if(line.hasMoreTokens())
	        {
	        	word2 = line.nextToken();
	        	if(!words.contains(word2))
	        		words.add(word2);
	        }
	    }
	    br.close();
	    
	    System.out.println(words.size());
	    System.out.println(words);
		return words;
	}
	
	// constructing the co-occurance graph
	public ArrayList<Node> contructGraph(TreeSet<String> t) throws IOException
	{
		ArrayList<Node> graph = new ArrayList<Node>();
		// creating node for each word
		for(int i= 0;i<t.size();i++)
		{
			Node n =  new Node();
			n.word1 = i;
			n.edges = new ArrayList<Edge>();
			graph.add(n);
		}
		//Scanner sc = new Scanner(new FileReader("14.txt"));
		//sc.useDelimiter("\\s");
		File inFile = new File("14.txt");      //Reading from file	
	    BufferedReader br = new BufferedReader(new FileReader(inFile)); 
	    while (br.ready()) 
	    {
	    	String w1 = null,w2 = null;
	    	float weight = 0;
	        StringTokenizer line = new StringTokenizer(br.readLine());
	        if(line.hasMoreTokens())
	           	w1 = line.nextToken();
	        if(line.hasMoreTokens())
	           	w2 = line.nextToken();
	        if(line.hasMoreTokens())
	           	weight = Float.parseFloat(line.nextToken());
	        
	     /*while(sc.hasNext()) // add all the feature value to tree set which automatically sorts it
			{
			String w1 = sc.next();
			String w2 = sc.next();
			float weight= Float.parseFloat(sc.next());*/
	        
			// convert string to integer
			int word1 = t.headSet(w1).size(); // to retrieve the index from a treeset
			int word2 = t.headSet(w2).size();
			/*---------- adding these words to graph---------*/
			// adding word2 in adjacency list of word1
			int index1 = word1;
			Node n1 = graph.get(index1);
			Edge e1 = new Edge();
			e1.word2 = word2;
			e1.weight = weight;
			n1.edges.add(e1);
			graph.set(index1, n1);
			// adding word1 in adjacency list of word2
			int index2 = word2;
			Node n2 = graph.get(index2);
			Edge e2 = new Edge();
			e2.word2 = word1;
			e2.weight = weight;
			n2.edges.add(e2);
			graph.set(index2, n2);
		}
		br.close();
		for(Node n: graph)
		{
			System.out.println("\nnode: "+n.word1);
			System.out.print("EdgeList:  ");
			for(Edge e:n.edges)
				System.out.print(e.word2+" "+e.weight+"\t");
				
		}
		return graph;
	}
	
	// extracting keywords from the document
	public HashSet<Integer> keyWordExtraction(String path, TreeSet<String> t) throws FileNotFoundException
	{
		// Initialise the tagger
		MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
		//Reading content from file
		File file = new File(path);
		Scanner scan = new Scanner(file);  
		scan.useDelimiter("\\Z");  
		String sample = scan.next();
		scan.close();
		// The tagged string
		String taggedFileContent = tagger.tagString(sample);
		System.out.println(taggedFileContent);
		// splitting file content
		String taggedText[]=taggedFileContent.split(" ");
		String taggedAdj[];
		HashSet<Integer> words = new HashSet<Integer>();
		String[] sw = {"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
		System.out.println(sw.length);
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
		        	int w = t.headSet(taggedAdj[0]).size();
					words.add(w);
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
		        	int w = t.headSet(taggedAdj[0]).size();
					words.add(w);
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
		        	int w = t.headSet(taggedAdj[0]).size();
					words.add(w);
				}
			}	
		}
		// adjectives, nouns and verbs
		for(int s : words)		 
			System.out.println(s);
		return words;
	}
	
	// computing ImportanceScore of each keyword in the document 
	// importance Score is the no. of times a keyword occurs in a document
	public HashMap<Integer, Float> keyWordImportanceScore(HashSet<Integer> keywords,String path1, TreeSet<String> t) throws IOException
	{
		HashMap<Integer, Float> words = new HashMap<Integer, Float>(); // contains word and its importance score
	    String word;
	    int total = 0; // holds total occurrences of keywords
	    File inFile = new File(path1);      //Reading from file	
	    BufferedReader br = new BufferedReader(new FileReader(inFile)); 
	    while (br.ready()) 
	    {
	        StringTokenizer line = new StringTokenizer(br.readLine()," ,!.:;-)(");   //Breaking the read line into tokens (indices and values)
	        while (line.hasMoreTokens())
	        {
	        	word = line.nextToken();
	        	// converting the word to its integer representation
	        	int w = t.headSet(word).size();
	        	if(keywords.contains(w))
	        	{
	 	    	   if(words.containsKey(w)) // when word already present increase its frequency 
	 	    	   {
	 	    		   Float val = words.get(w);
	 	    		   words.put(w,val+1); // updating frequency
	 	    	   }
	 	    	   else // otherwise add the new word to docSet
	 	    		   words.put(w,(float) 1);
	        	}
	        }
	    }
	    br.close();
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
	 	return words;
	}	

	// expand each keyword considering its importance score and returns the set of expanded words with frequency
	public HashMap<Integer, Integer> keyWordExpansion(ArrayList<Node> graph, HashMap<Integer, Float> words)
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
	       int k = (int) ((value * size)/100); // do we need to divide by 100 ??????
	       // retrieving top-k elements from graph
	       heap= new Heap[k];
	       for(int j=0;j<k;j++) // inserting first k elements into heap as heap is initially empty
	       {
	    	   Edge e = edgeList.get(j); 
	    	   Heap h = new Heap();
	    	   h.index = e.word2;
	    	   h.val = e.weight;
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
	       for(Heap h: heap)
	       {
	    	   int key1 = h.index;
	    	   if(docSet.containsKey(key1)) // when word already present increase its frequency 
	    	   {
	    		   int val1 = docSet.get(key1);
	    		   docSet.put(key1, val1+1); // updating frequency
	    	   }
	    	   else // otherwise add the new word to docSet
	    		   docSet.put(key1, 1);
	       }
	    }
	    return docSet;
	}
	
	// computing jaccard similarity between the documents
	public double jaccardSimilarity(HashMap<Integer, Integer> docSet1,HashMap<Integer, Integer> docSet2)
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
			denominator += docSet1.get(key);
			if(docSet2.containsKey(key))
			{
				intersection.add(key);
				numerator += min(docSet1.get(key),docSet1.get(key));
			}
		}
		// elements from doc2 for union
		Set<Map.Entry<Integer, Integer>> set2 = docSet2.entrySet();
		Iterator<Map.Entry<Integer, Integer>> i2 = set2.iterator();
		while(i2.hasNext())
		{
			Entry<Integer, Integer> me = i2.next();
			int key = me.getKey();
			union.add(key);
		}
		
		double similarity = (double)numerator/denominator;
		return similarity;
		
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
		Graph ob= new Graph();
		// renaming words
		TreeSet<String> t = ob.renamingWords();
		// constructing graph
		ArrayList<Node> graph = ob.contructGraph(t);
		
		//-------------------extracting keywords from document1----------------
		String path1 = "doc1.txt";
		HashSet<Integer> keywords1 = ob.keyWordExtraction(path1, t);
		// computing importance score of keywords
		HashMap<Integer, Float> words1 = ob.keyWordImportanceScore(keywords1, path1, t);
		// expanding the keywords on the co-occurrence graph
		HashMap<Integer, Integer> docSet1 = ob.keyWordExpansion(graph, words1);
		
		//-------------------extracting keywords from document1----------------
		String path2 = "doc2.txt";
		HashSet<Integer> keywords2 = ob.keyWordExtraction(path2, t);
		// computing importance score of keywords
		HashMap<Integer, Float> words2 = ob.keyWordImportanceScore(keywords2, path1, t);
		// expanding the keywords on the co-occurrence graph
		HashMap<Integer, Integer> docSet2 = ob.keyWordExpansion(graph, words2);
		
		//computing similarity between the set of documents
		double similarity = ob.jaccardSimilarity(docSet1, docSet2);
		System.out.println("Jaccard Similarity is "+similarity);
	}
	
	/*-----------------to maintain a priority queue for top k elements------------------*/
	public static void buildHeap(int n1) //for constructing the max heap
	{
		for(int i=(n1/2);i>=0;i--)
			minHeap(i,n1);
	}
	public static void minHeap(int i,int n1) //  to perform heapify on an max heap
	{
		int left=2*i;
		int right=2*i+1;
		int smallest;
		if(left<n1&&heap[left].val<heap[i].val)
			smallest=left;
		else
			smallest=i;
		if(right<n1&&heap[right].val<heap[smallest].val)
			smallest=right;
		if(smallest!=i)
		{
			Heap temp=heap[smallest];
			heap[smallest]=heap[i];
			heap[i]=temp;
			minHeap(smallest,n1);
		}
		
	}
	public static void heapInsert(int index,float elem, int k) // replacing the element with min element and reconstructing min heap
	{
		if(elem>heap[0].val)
		{
			heap[0].val=elem;
			heap[0].index=index;
			minHeap(0,k);	
		}
		
	}
}
