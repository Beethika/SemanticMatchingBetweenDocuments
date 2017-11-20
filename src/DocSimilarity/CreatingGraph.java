package DocSimilarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.TreeSet;



public class CreatingGraph
{
	TreeSet<String> dictionary; // unique keyword for each word
	HashMap<String,Integer> dictionaryMap;
	ArrayList<Node> graph; // co-occurrence graph	static Heap heap[]; // for maintaining top-k elements
	static Heap[] heap;
	
	// lexicographic ordering of all keywords present in document corpus
	public void renamingWords() throws IOException
	{
		dictionary = new TreeSet<String>();
		String word1 = null,word2 = null;
		String path = "14.txt";
		//String[] paths = {"1.txt", "2.txt", "3.txt", "4.txt", "5.txt", "6.txt", "7.txt", "8.txt", "9.txt", "10.txt", "11.txt", "12.txt", "13.txt", "14.txt"};
	    //for(String path: paths)
		{
	    	System.out.println(path);
	    	File inFile = new File(path);      //Reading from file
	    	BufferedReader br = new BufferedReader(new FileReader(inFile));
	    	String[] sw = {"a", "aa", "aaa", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
			HashSet<String> stopWords= new HashSet<String>();
			for(String s:sw)
				stopWords.add(s);
	    	while (br.ready())
	    	{
	    		StringTokenizer line = new StringTokenizer(br.readLine());
	    		if(line.hasMoreTokens())
	    		{
	    			word1 = line.nextToken();
	    			if(line.hasMoreTokens())
	    			{
	    				word2 = line.nextToken();
	    				if(!stopWords.contains(word1)&&!stopWords.contains(word2))
	    				{
	    					dictionary.add(word1);
	    					dictionary.add(word2);
	    				}
	    			}
	    		}
	    	}
	    	br.close();
	    	System.out.println("size of dict: "+dictionary.size());
		}
	    // write each data point into a file
	    try
	    {

	    	FileWriter writer = new FileWriter("Dictionary.txt", false);
	    	int i=0;
	    	for(String data: dictionary)
	    	{
	    		i++;
	 	   	  	writer.write(i+"\t"+data);
	 	   	  	writer.write("\r\n"); // write new line
	    	}
	    	writer.close();
	    }
	    catch (IOException e)
	    {
	    	e.printStackTrace();
	    }
	    System.out.println(dictionary.size());
	    //System.out.println(dictionary);
	}
	
	// constructing the co-occurance graph
	public void contructGraph() throws IOException
	{
		//TreeSet<String> t = dictionary;
		dictionaryMap = new HashMap<String,Integer>();
		graph = new ArrayList<Node>();
		int index = 1; 
		FileWriter writer = new FileWriter("Co-OccurrenceGraph.txt", false);
		for(String s:dictionary)
		{
			dictionaryMap.put(s, index);
			index++;
		}
		// creating node for each word
		for(int i= 0;i<dictionary.size();i++)
		{
			Node n =  new Node();
			n.word1 = i+1;
			n.edges = new ArrayList<Edge>();
			graph.add(n);
		}
		//Scanner sc = new Scanner(new FileReader("14.txt"));
		//sc.useDelimiter("\\s");
		String path = "14.txt";
		//String[] paths = {"1.txt", "2.txt", "3.txt", "4.txt", "5.txt", "6.txt", "7.txt", "8.txt", "9.txt", "10.txt", "11.txt", "12.txt", "13.txt", "14.txt"};
	    //for(String path: paths)
		//{
	    	System.out.println(path);
		File inFile = new File(path);      //Reading from file
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

	        // convert string to integer
			//int word1 = t.headSet(w1).size(); // to retrieve the index from a treeset
			//int word2 = t.headSet(w2).size();
	        if(dictionaryMap.containsKey(w1)&&dictionaryMap.containsKey(w2))
	        {
	        	int word1 = dictionaryMap.get(w1);
	            int word2 = dictionaryMap.get(w2);
				/*---------- adding these words to graph---------*/
	            // adding word2 in adjacency list of word1
	            
	            int index1 = word1;
	            Node n1 = graph.get(index1 -1);
	            Edge e1 = new Edge();
	            e1.word2 = word2;
	            e1.weight = weight;
	            n1.edges.add(e1);
	            graph.set(index1-1, n1);
	            // 	adding word1 in adjacency list of word2
	            int index2 = word2;
	            Node n2 = graph.get((int) index2-1);
	            Edge e2 = new Edge();
	            e2.word2 = word1;
	            e2.weight = weight;
	            n2.edges.add(e2);
	            graph.set(index2-1, n2);
	        }
		}
		br.close();
		//}
		//------------------------------------Optimising graph------------------------------------
		// heap sort to sort the edges
		for(Node n: graph)
		{
			int k = n.edges.size();
			heap= new Heap[k];
			Heap[] sortedEdges = new Heap[k]; // to store sorted array in descending order
			//int totalWeight = 0;
			for(int j=0;j<k;j++) // inserting first k elements into heap as heap is initially empty
			{
			 	Edge e = n.edges.get(j); 
			  	Heap h = new Heap();
			   	h.index = e.word2;
			   	h.val = e.weight;
			   	heap[j]=h;
			   	//totalWeight += e.weight;
			}
			buildHeap(k); // constructing max heap of the all the elements
			int n1=k;
			int i1=0;
			for(int i=n1-1;i>=0;i--)
			{
				sortedEdges[i1++]=heap[0];
				Heap t=heap[0];
				heap[0]=heap[i];
				heap[i]=t;
				n1--;
				maxHeap(0,n1);
				
			}
			/*for(int i=0;i<k;i++) // storing the sorted data in descending order for each dimension
			{
				sortedEdges[i]=heap[i];
			}*/
			// check for probabilities above threshold i.e. 0.05, prune away points
			writer.write(String.valueOf(n.word1));
			writer.write("\r\n");
			for(int i=0;i<k;i++)
			{
				if(i==0)
					writer.write("{"+heap[i].index+":\t"+heap[i].val);
				else
				{
					writer.write(",\t"+heap[i].index+":\t"+heap[i].val);
					if(heap[i].val<0.08)
						break;	
				}
			}
			writer.write("}\r\n");
		}
		writer.close();
		
		//-----------------------------------------------------------------------------------------
		
		/*for(Node n: graph)
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
	
	/*-----------------to maintain a priority queue for top k elements------------------*/
	public static void buildHeap(int k) //for constructing the max heap
	{
		for(int i=(k/2);i>=0;i--)
			maxHeap(i,k);
	}
	public static void maxHeap(int i,int k) //  to perform heapify on an max heap
	{
		int left=2*i;
		int right=2*i+1;
		int largest;
		if(left<k&&heap[left].val<heap[i].val)
			largest=left;
		else
			largest=i;
		if(right<k&&heap[right].val<heap[largest].val)
			largest=right;
		if(largest!=i)
		{
			Heap temp=heap[largest];
			heap[largest]=heap[i];
			heap[i]=temp;
			maxHeap(largest,k);
		}
		
	}
	
	/*// replacing the element with min element and reconstructing min heap
	public static void heapInsert(int index,float elem, int k) 
	{
		if(elem>heap[0].val)
		{
			heap[0].val=elem;
			heap[0].index=index;
			maxHeap(0,k);	
		}
		
	}*/
	
	public static void main(String args[]) throws IOException
	{
		CreatingGraph ob = new CreatingGraph();
		ob.renamingWords();
		ob.contructGraph();
	}
}
