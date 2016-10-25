import java.util.*;
import java.io.*;

public class DrugbankSample{
	
	static String enzymeFile = "./data/drug_enzyme.txt";
	static String labelFile = "./data/meddra_all_se.tsv";
	static String tkiFile = "./data/tki_combine.txt";

	public static void main(String[] args) throws IOException {
		Set<String> dSet = new HashSet<String> ();
		dSet.add("nervous system disorder");
		label_all_samples(dSet, "", "");
	}


	public static void label_all_samples(Set<String> dSet, String featureFile, String sampleFile) throws IOException {
		Set<String> sampleSet = utility.read_string_column(featureFile, 0);
		Set<String> tkiSet = utility.read_string_column(tkiFile, 0);
		Map<String, Set<String>> cid_name = drug_cid_name_map();

		Map<String, Integer> sample_label = new HashMap<String, Integer>();

		BufferedReader r = new BufferedReader(new FileReader(labelFile));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\t");
			String se = parts[5].toLowerCase();
			int label = 0;
			if (dSet.contains(se)) {
				label = 1;
			}

			String cid = String.valueOf(Integer.parseInt(parts[1].replace("CID", "")));
			if (cid_name.containsKey(cid)) {

				Set<String> nameSet = cid_name.get(cid);
				nameSet.retainAll(sampleSet);

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

		System.out.println("Sample size: " + sample_label.size());

		int pcnt = 0;
		int ncnt = 0;

		BufferedWriter w = new BufferedWriter(new FileWriter(sampleFile));
		for (String sample : sample_label.keySet()) {
			String type = "others";
			if (tkiSet.contains(sample)) {
				type = "tki";
			}
			int label = sample_label.get(sample);
			if (label == 1)
				pcnt++;
			else 
				ncnt++;
			w.write(sample + "|" + type + "|" + label + "\n");
			w.flush();
		}
		w.close();

		System.out.println(pcnt + " positive samples, " + ncnt + " negative samples");

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

}