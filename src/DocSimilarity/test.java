package DocSimilarity;

import java.util.StringTokenizer;

public abstract class test {

	public static void main(String[] args) 
	{
		String s = "{43:	0.4507,	43:	0.4507,	125:	0.2616,	146:	0.2451,	159:	0.379}";
		System.out.println(s);
		StringTokenizer st=new StringTokenizer(s, "{:,\t}", false);
		while(st.hasMoreTokens())
		{
			String a;
			String b;
			if(st.hasMoreTokens())
			{
				a=st.nextToken();
				System.out.println("a:"+a);
			}
			if(st.hasMoreTokens())
			{
				b=st.nextToken();
				System.out.println("b:"+b);
			}	
			
		}
	}

	

}
