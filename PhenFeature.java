import java.util.*;
import java.io.*;

public class PhenFeature{
	public static void main (String[] args) throws IOException {


	}



	public static Map<String, Map<String, Double>> drug_phen_score (Set<String> drugSet) throws IOException {
		Map<String, Map<String, Double>> drug_wtTarget = GeneticsFeature.read_drug_target_stitch(drugSet, 
			new HashSet<String>(), 0);
		Map<String, Set<String>> gene_mphen = read_gene_nervPhenLeave_map();

		Set<String> featureSet = new HashSet<String>();
		Map<String, Map<String, Double>> drug_wtPhen = new HashMap<String, Map<String, Double>> ();
		for (String drug : drug_wtTarget.keySet()) {
			Map<String, Double> gene_score = drug_wtTarget.get(drug);
			Map<String, Double> phen_score = new HashMap<String, Double>();

			for (String gene : gene_score.keySet()) {
				double gs = gene_score.get(gene);
				if (gene_mphen.containsKey(gene)) {
					Set<String> pset = gene_mphen.get(gene);

					featureSet.addAll(pset);

					for (String phen : pset) {
						if (phen_score.containsKey(phen)) {
							double ps = phen_score.get(phen);
							ps += gs;
							phen_score.put(phen, ps);

						} else {
							phen_score.put(phen, gs); 
						}
					}
				}
			}
			drug_wtPhen.put(drug, phen_score);

		}

		System.out.println("feature size: " + featureSet.size());

		return drug_wtPhen;
	}

	private static Map<String, Set<String>> read_gene_nervPhenLeave_map() throws IOException {
		// read gene nervPhen map
		Set<String> npSet = utility.read_set("./data/nervPhen.txt"); // nervous phenotype

		Map<String, Set<String>> gene_npSet = new HashMap<String, Set<String>>(); // gene to nervous phenotype
		BufferedReader r = new BufferedReader(new FileReader("./data/gene_mphen.txt"));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\\|");
			String gene = parts[0];
			String phen = parts[1];
			if (npSet.contains(phen)) {
				utility.push_item_set_map(gene_npSet, gene, phen);
			}

		}
		r.close();
		System.out.println("gene to nervous phenotype map size: " + gene_npSet.size());

		// retain only leaves
		Map<String, Set<String>> p2c = new HashMap<String, Set<String>>();
		BufferedReader r1 = new BufferedReader(new FileReader("./data/mphen_isa.txt"));
		while ((line = r1.readLine()) != null) {
			String[] parts = line.split("\\|");
			String child = parts[0];
			String parent = parts[1];
			utility.push_item_set_map(p2c, parent, child);
		}
		r1.close();

		Set<String> featureSet = new HashSet<String> ();
		for (String gene : gene_npSet.keySet()) {
			Set<String> valSet = gene_npSet.get(gene);
			Set<String> rmSet = new HashSet<String>();

			for (String nphen : valSet) {
				if (p2c.containsKey(nphen)) {
					Set<String> cset = p2c.get(nphen);
					cset.retainAll(valSet);
					if (!cset.isEmpty()) {
						// this phen has child, which is in the current phen set
						rmSet.addAll(cset);
					} else {
						// this phen has child, but is not in the current phen set
						// System.out.println(gene + ": " + nphen);
					}
				} else {
					// this phen is a leave
				}
			}

			valSet.removeAll(rmSet);
			gene_npSet.put(gene, valSet);
			featureSet.addAll(valSet);
		}

		System.out.println("map size after retaining only leave nodes: " + gene_npSet.size());
		System.out.println("relevant phenotype number: " + featureSet.size());

		return gene_npSet;
	}




}