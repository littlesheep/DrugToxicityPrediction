import java.util.*;
import java.io.*;

public class GeneticsFeature{

	public static void main (String[] args) throws IOException {


	}

	public static void write_drug_target_feature_lincs(Map<String, Integer> drug_label, 
		Map<String, String> drug_type) throws IOException {
		Set<String> nervGeneSet = utility.read_string_column("./data/nervGene.txt", 0);

		BufferedWriter w = new BufferedWriter(new FileWriter("NervLincsFea.txt"));
		List<Integer> loc = new ArrayList<Integer>();
		BufferedReader r = new BufferedReader(new FileReader("./data/gene_lincs.txt"));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\t");
			String gene = parts[0];
			int idx = Integer.parseInt(parts[1]);
			if (nervGeneSet.contains(gene)) {
				loc.add(idx);
				w.write(gene + "\n");
				w.flush();
			}
		}
		r.close();
		w.close();
		System.out.println("Lincs target feature dimension: " + loc.size());

		int cnt = 0;// cnt of drugs;
		r = new BufferedReader(new FileReader("./data/drug_gene_lincs.txt"));
		
		BufferedWriter w1 = new BufferedWriter(new FileWriter("NervLincsDrug.txt"));
		BufferedWriter w2 = new BufferedWriter(new FileWriter("NervLincs.txt"));
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\t");
			String drug = parts[0].toLowerCase();
			if (drug_label.containsKey(drug)) {
				w1.write(drug + "\n");
				w1.flush();

				int label = drug_label.get(drug);
				int type = 0;
				if (drug_type.containsKey(drug) && drug_type.get(drug).compareToIgnoreCase("tki") == 0) {
					type = 1;
				}

				w2.write(cnt + " " + type + " " + label + " ");
				w2.flush();

				for (int idx : loc){
					String valstr = parts[idx + 1]; // value of score for the next gene feature 
					w2.write(valstr + " ");
					w2.flush();

				}
				w2.write("\n");
				w2.flush();
				cnt++;
			}
		}
		r.close();
		w1.close();
		w2.close();
	}


	public static Map<String, Set<String>> read_drug_target_drugbank (Set<String> drugSet) throws IOException {
		Set<String> featureSet = new HashSet<String>();
		Map<String, Set<String>> drug_target = new HashMap<String, Set<String>>();
		BufferedReader r = new BufferedReader(new FileReader("./data/drug_bank.txt"));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\\|");
			String drug = parts[0];
			String target = parts[3];
			if (!drugSet.isEmpty() && !drugSet.contains(drug)) {
				continue;
			}
			featureSet.add(target);
			utility.push_item_set_map(drug_target, drug, target);
		}
		r.close();
		System.out.println("Drugbank target feature dimension: " + featureSet.size());
		return drug_target;
	}

	public static Map<String, Set<String>> read_drug_enzyme_drugbank (Set<String> drugSet) throws IOException {
		Set<String> featureSet = new HashSet<String>();
		Map<String, Set<String>> drug_target = new HashMap<String, Set<String>>();
		BufferedReader r = new BufferedReader(new FileReader("./data/drug_enzyme.txt"));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\\|");
			String drug = parts[0];
			String target = parts[3];
			if (!drugSet.isEmpty() && !drugSet.contains(drug)) {
				continue;
			}
			featureSet.add(target);
			utility.push_item_set_map(drug_target, drug, target);
		}
		r.close();
		System.out.println("Drugbank enzyme feature dimension: " + featureSet.size());
		return drug_target;
	}



	public static Map<String, Map<String, Double>> read_drug_target_stitch (Set<String> drugSet, Set<String> geneSet, 
		double thres) throws IOException {
		Set<String> featureSet = new HashSet<String>();
		Map<String, Map<String, Double>> drug_wtTarget = new HashMap<String, Map<String, Double>>();
		BufferedReader r = new BufferedReader(new FileReader("./data/compound_gene_stitch.txt"));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\\|");
			String drug = parts[0];
			String target = parts[1];
			double score = Double.parseDouble(parts[2])/1000;
			if (!drugSet.contains(drug)) {
				continue;
			}

			if (!geneSet.isEmpty() && !geneSet.contains(target)) {
				continue;
			}

			if (score <  thres) {
				continue;
			}

			featureSet.add(target);

			if (!drug_wtTarget.containsKey(drug)) {
				Map<String, Double> target_score = new HashMap<String, Double>();
				target_score.put(target, score);
				drug_wtTarget.put(drug, target_score);

			} else {
				Map<String, Double> target_score = drug_wtTarget.get(drug);
				target_score.put(target, score);
				drug_wtTarget.put(drug, target_score);
			}



		}
		r.close();

		System.out.println("Stitch target feature dimension: " + featureSet.size());


		return drug_wtTarget;
	}

}