import java.util.*;
import java.io.*;

public class TKISample{
	public static void main (String[] args) throws IOException {
		Map<String, Integer> drug_label = tkiLabel();
		Map<String, Set<String>> drug_feature = tkiFeature();
		tki_label_feature_file(drug_label, drug_feature, "TKIDrugBankCombine.txt");
	}

	public static Map<String, Integer> tkiLabel() throws IOException {
		Map<String, String> tki_app = read_tki_app();
		Set<String> seSet = new HashSet<String> ();
		seSet.add("nervous system disorder");
		Map<String, Integer> drug_label = label_all_samples(seSet, tki_app.keySet(), "./data/meddra_all_se.tsv");
		return drug_label;
	}

	private static Map<String, Integer> label_all_samples(Set<String> seSet, Set<String> dSet, String labelFile) throws IOException {
		Map<String, Set<String>> cid_name = drug_cid_name_map();

		Map<String, Integer> sample_label = new HashMap<String, Integer>();

		BufferedReader r = new BufferedReader(new FileReader(labelFile));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\t");
			String se = parts[5].toLowerCase();
			int label = 0;
			if (seSet.contains(se)) {
				label = 1;
			}

			String cid = String.valueOf(Integer.parseInt(parts[1].replace("CID", "")));
			if (cid_name.containsKey(cid)) {

				Set<String> nameSet = cid_name.get(cid);
				nameSet.retainAll(dSet);

				if (!nameSet.isEmpty()) {
					if (nameSet.size() > 1){
						System.out.print (nameSet);
					}

					for (String drug : nameSet){
						if (sample_label.containsKey(drug) && sample_label.get(drug) == 1) {

						} else {
							sample_label.put(drug, label);
						}
					}
				}
			} // if the CID has a corresponding name, 
		} // finish reading the side effect file

		System.out.println(sample_label.size() + " have labels");

		int pcnt = 0;
		int ncnt = 0;

		
		for (String sample : sample_label.keySet()) {
			
			int label = sample_label.get(sample);
			if (label == 1)
				pcnt++;
			else 
				ncnt++;
			
		}


		System.out.println(pcnt + " positive samples, " + ncnt + " negative samples");

		return sample_label;

	}


	private static Map<String, Set<String>> drug_cid_name_map() throws IOException {
		Map<String, Set<String>> cid_name = new HashMap<String, Set<String>>();
		String file = "./data/drugname_cid_group.txt";
		String line;
		BufferedReader r = new BufferedReader(new FileReader(file));
		while ((line = r.readLine()) != null) {
			String[] parts= line.split("\\|");
			String name = parts[0];
			String cid = parts[1];
			utility.push_item_set_map(cid_name, cid, name);
		}
		r.close();

		return cid_name;
	} 

	public static Map<String, Set<String>> tkiFeature() throws IOException {
		Map<String, String> tki_app = read_tki_app();

		Map<String, Set<String>> drug_target = GeneticsFeature.read_drug_target_drugbank(tki_app.keySet());
		Map<String, Set<String>> drug_enzyme = GeneticsFeature.read_drug_enzyme_drugbank(tki_app.keySet());
		Map<String, Set<String>> drug_feature = SampleFeature.combine_feature(drug_target, drug_enzyme);

		System.out.println(drug_target.size() + " have features");
		for (String tki : drug_target.keySet()) {
			Set<String> tar = drug_target.get(tki);
			// System.out.println(tki + " " + tki_app.get(tki) + " " + tar.size());
		}

		return drug_feature;
	}

	private static Map<String, String> read_tki_app () throws IOException {
		Map<String, String> tki_app = new HashMap<String, String>();
		BufferedReader r = new BufferedReader(new FileReader("./data/tki_combine.txt"));
		String line;
		while((line = r.readLine()) != null) {
			String[] parts = line.split("\\|");
			String tki = parts[0];
			String app = parts[1];
			tki_app.put(tki, app);
		}
		r.close();

		return tki_app;
	}

	public static Map<String, Integer> tki_label_feature_file(Map<String, Integer> sample_label, 
		 Map<String, Set<String>> sample_feature, String outfile) throws IOException {
		// get the list of features
		Map<String, Integer> feature_idx = new HashMap<String, Integer>();
		int idx = 0;
		for (String sample : sample_feature.keySet()) {
			Set<String> featureSet = sample_feature.get(sample);
			for (String feature : featureSet) {
				if (!feature_idx.containsKey(feature)) {
					feature_idx.put(feature, idx);
					idx++;
				}
			}
		}

		int n = feature_idx.size();
		int m = 0;

		BufferedWriter w = new BufferedWriter(new FileWriter(outfile));


		for (String sample : sample_feature.keySet()) {
			int label = -1;
			if (sample_label.containsKey(sample)) {
				label = sample_label.get(sample);
			}

			if (label == -1) {
				System.out.println(sample);
			}
			

			if (sample_feature.containsKey(sample)) {
				// if sample has feature, write sample and label to file
				w.write(m + " " + label + " ");
				w.flush(); 

				double[] x = new double[n]; // feature vector
				Set<String> featureSet = sample_feature.get(sample);
				for (String feature : featureSet) {
					int loc = feature_idx.get(feature);
					x[loc] = 1;
				}

				for (int i = 0; i < x.length; i++) {
					w.write(x[i] + " ");
					w.flush();
				}

				w.write("\n");
				w.flush();

				m++;

			}
		}
		System.out.println(m + " samples, " + n + " features");

		return feature_idx;

	}
}