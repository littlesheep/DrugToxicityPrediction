import java.util.*;
import java.io.*;

public class StitchSample {
	static String featureFile = "./data/compound_gene_stitch.txt";
	static String labelFile = "./data/meddra_all_se.tsv";
	static String sampleFile = "NervStitchSample.txt";
	static String tkiFile = "./data/tkiname_cid_group.txt";

	public static void main (String[] args) throws IOException {

	}

	private static void debug_meddra_se() throws IOException {
		Set<String> dSet = utility.read_set("./data/NervSeMeddra.txt");
		Map<String, Set<String>> drug_seSet = new HashMap<String, Set<String>>();

		BufferedReader r = new BufferedReader(new FileReader(labelFile));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\t");
			String drug = String.valueOf(Integer.parseInt(parts[1].replace("CID", "")));
			String se = parts[5].toLowerCase();
			utility.push_item_set_map(drug_seSet, drug, se);

		}
		r.close();


		int cnt = 0;
		for (String drug : drug_seSet.keySet()) {
			Set<String> seSet = drug_seSet.get(drug);
			seSet.retainAll(dSet);
			if (seSet.isEmpty()) {
				continue;
			}
			if (!seSet.contains("nervous system disorder")) {
				// System.out.println(drug + " " + seSet);
				cnt++;
			}
		}

		System.out.println(cnt + " out of " + drug_seSet.size() + " drugs have nervous SEs but not the term");
	}

	public static void label_all_samples(Set<String> dSet) throws IOException {


		Set<String> sampleSet = utility.read_string_column(featureFile, 0);
		Set<String> tkiSet = utility.read_string_column(tkiFile, 1);

		Map<String, Integer> train_label = new HashMap<String, Integer>();
		Map<String, Integer> test_label = new HashMap<String, Integer>();

		// Set<String> seDebugSet = new HashSet<String>();
		
		BufferedReader r = new BufferedReader(new FileReader(labelFile));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\t");
			String drug = String.valueOf(Integer.parseInt(parts[1].replace("CID", "")));
			String se = parts[5].toLowerCase();
			if (sampleSet.contains(drug)) {
				int label = 0;
				if (dSet.contains(se)) {
					label = 1;
					// seDebugSet.add(se);
				} 
				
				if (tkiSet.contains(drug)) {
					// test set
					if (test_label.containsKey(drug) && test_label.get(drug) == 1) {

					} else {
						test_label.put(drug, label);
					}
				} else {
					// train set
					if (train_label.containsKey(drug) && train_label.get(drug) == 1) {

					} else {
						train_label.put(drug, label);
					}
				}

				
			}
		}
		r.close();

		System.out.println("training set size: " + train_label.size());
		System.out.println("test set size: " + test_label.size());

		int pcnt = 0;
		int ncnt = 0;
		BufferedWriter w = new BufferedWriter(new FileWriter(sampleFile));
		for (String drug : train_label.keySet()) {
			int label = train_label.get(drug);

			if (label == 1)
				pcnt++;
			else 
				ncnt++;

			w.write(drug + "|train|" + label + "\n");
			w.flush();

		}

		for (String drug : test_label.keySet()) {
			int label = test_label.get(drug);

			if (label == 1)
				pcnt++;
			else 
				ncnt++;
			w.write(drug + "|test|" + label + "\n");
			w.flush();
		}
		
		w.close();
		System.out.println(pcnt + " positive samples, " + ncnt + " negative samples");
		// System.out.println("SE debug set size: " + seDebugSet.size());
		// for (String se : seDebugSet) {
		// 	System.out.println(se);
		// }

	}
}