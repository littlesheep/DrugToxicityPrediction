import java.util.*;
import java.io.*;

public class pathway{
	public static void main(String[] args) throws IOException{
		rank_pathway();
	}

	public static void rank_pathway() throws IOException{
		Map<String, Double> gene_score = new HashMap<String, Double>();

		String file = "top_gene_feature.txt";
		BufferedReader r = new BufferedReader(new FileReader (file));
		String line;
		while((line = r.readLine())!=null) {
			String[] parts = line.split("\t");
			String gene = parts[0].trim();
			double wt = Double.parseDouble(parts[1]);
			gene_score.put(gene, wt);
		}
		r.close();


		Map<String, Set<String>> pathway_gset = read_pathway();

		Map<String, Double> pathway_score = new HashMap<String, Double>();
		for (String pathway : pathway_gset.keySet()) {
			double total = 0;
			Set<String> set = pathway_gset.get(pathway);
			// System.out.println(set.size());
			for (String gene : set) {
				if (gene_score.containsKey(gene)) {
					double score = gene_score.get(gene);
					total += score;
				}
			}
			// System.out.println(total);
			pathway_score.put(pathway, total);

		}

		pathway_score = utility.sortByValue(pathway_score);
		for (String pathway : pathway_score.keySet()) {
			if (pathway_score.get(pathway) == 0)
				break;
			System.out.println(pathway + " " + pathway_score.get(pathway));
		}
	}

	public static Map<String, Set<String>> read_pathway() throws IOException{
		Map<String, Set<String>> map = new HashMap<String, Set<String>> ();
		String file = "c2.cp.v5.1.symbols.gmt";
		BufferedReader r = new BufferedReader(new FileReader (file));
		String line;
		while((line = r.readLine())!=null) {
			String[] parts = line.split("\t");
			String path = parts[0];
			Set<String> set = new HashSet<String>();
			for (int i = 2; i<parts.length; i++){
				set.add(parts[i]);
			}
			map.put(path, set);
		}
		r.close();

		return map;

	}



}