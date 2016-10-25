import java.util.*;
import java.io.*;

public class SampleFeature {
	
	public static void main (String[] args) throws IOException {
		// drug target feature matrix
		// drugTargetFeatureStitch(0);
		// drugTargetFeatureDrugbank();
		// drugTargetFeatureLincs();
		// drugEnzymeFeatureDrugbank();
		drugCombineFeatureDrugbank();

		// debug for phenotype feature
		// drugPhenFeature();



	}



	public static void drugCombineFeatureDrugbank() throws IOException {
		Map<String, Integer> drug_label = read_sample_label("nervDrugbankTargetSample.txt");
		Set<String> sampleSet = new HashSet<String>();
		// sampleSet.addAll(drug_label.keySet());
		// System.out.println("sample size: " + sampleSet.size());

		Map<String, Set<String>> drug_target = GeneticsFeature.read_drug_target_drugbank(sampleSet);
		Map<String, Set<String>> drug_enzyme = GeneticsFeature.read_drug_enzyme_drugbank(sampleSet);
		System.out.println(drug_enzyme.size() + " drugs have enzyme information");

		Map<String, Set<String>> drug_feature = combine_feature(drug_target, drug_enzyme);

		Map<String, String> drug_type = read_sample_type("nervDrugbankTargetSample.txt");

		Map<String, Integer> feature_idx = sample_label_binfeature_file(drug_label, drug_type, drug_feature, "NervDrugbankCombine.txt");

		feature_file(feature_idx, "NervDrugbankCombineFea.txt");
	}

	public static Map<String, Set<String>> combine_feature(Map<String, Set<String>> map1, Map<String, Set<String>> map2) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>> ();
		for (String key : map1.keySet()) {
			Set<String> valSet = map1.get(key);
			utility.push_item_set_map(map, key, valSet);
		}

		for (String key : map2.keySet()) {
			Set<String> valSet = map2.get(key);
			utility.push_item_set_map(map, key, valSet);
		}

		return map;
	}
	
	public static void drugEnzymeFeatureDrugbank() throws IOException {
		Map<String, Integer> drug_label = read_sample_label("nervDrugbankEnzymeSample.txt");
		Set<String> sampleSet = new HashSet<String>();
		sampleSet.addAll(drug_label.keySet());
		System.out.println("sample size: " + sampleSet.size());

		Map<String, Set<String>> drug_feature = GeneticsFeature.read_drug_enzyme_drugbank(sampleSet);

		Map<String, String> drug_type = read_sample_type("nervDrugbankEnzymeSample.txt");

		Map<String, Integer> feature_idx = sample_label_binfeature_file(drug_label, drug_type, drug_feature, "NervDrugbankEnzyme.txt");

		feature_file(feature_idx, "NervDrugbankEnzymeFea.txt");
	}



	public static void drugTargetFeatureLincs() throws IOException {
		Map<String, Integer> drug_label = read_sample_label("NervLincsSample.txt");
		Map<String, String> drug_type = read_sample_type("NervLincsSample.txt");
		System.out.println("sample size: " + drug_label.size());
		GeneticsFeature.write_drug_target_feature_lincs(drug_label, drug_type);

	}

	public static void drugTargetFeatureDrugbank() throws IOException {
		Map<String, Integer> drug_label = read_sample_label("nervDrugbankTargetSample.txt");
		Set<String> sampleSet = new HashSet<String>();
		sampleSet.addAll(drug_label.keySet());
		System.out.println("sample size: " + sampleSet.size());

		Map<String, Set<String>> drug_feature = GeneticsFeature.read_drug_target_drugbank(sampleSet);

		Map<String, String> drug_type = read_sample_type("nervDrugbankTargetSample.txt");

		Map<String, Integer> feature_idx = sample_label_binfeature_file(drug_label, drug_type, drug_feature, "NervDrugbank.txt");

		feature_file(feature_idx, "NervDrugbankFea.txt");
	}	

	public static void drugPhenFeature() throws IOException {
		Map<String, Integer> drug_label = read_sample_label("NervStitchSample.txt");
		Set<String> sampleSet = new HashSet<String>();
		sampleSet.addAll(drug_label.keySet());
		System.out.println("sample size: " + sampleSet.size());

		Map<String, Map<String, Double>> drug_feature = PhenFeature.drug_phen_score(
			sampleSet);

		Map<String, String> drug_type = read_sample_type("NervStitchSample.txt");

		Map<String, Integer> feature_idx = sample_label_wtfeature_file(drug_label, drug_type, 
			drug_feature, "NervPhen.txt");
	}

	public static void drugTargetFeatureStitch(double thres) throws IOException {
		String sample_file = "NervStitchSample.txt";
		// all samples
		Map<String, Integer> drug_label = read_sample_label(sample_file);
		Set<String> sampleSet = new HashSet<String>();
		sampleSet.addAll(drug_label.keySet());
		System.out.println("sample size: " + sampleSet.size());

		Map<String, Map<String, Double>> drug_feature = GeneticsFeature.read_drug_target_stitch(
			sampleSet, utility.read_set("./data/nervGene.txt"), thres);
		System.out.println(drug_feature.size() + " drugs have non-zero features");

		Map<String, String> drug_type = read_sample_type(sample_file);

		Map<String, Integer> feature_idx = sample_label_wtfeature_file(drug_label, drug_type, 
			drug_feature, "NervStitch.txt");

		feature_file(feature_idx, "NervStitchFea.txt");
	}

	public static void feature_file (Map<String, Integer> feature_idx, String outfile) throws IOException {
		feature_idx = utility.sortByValue(feature_idx);
		BufferedWriter w = new BufferedWriter(new FileWriter(outfile));
		for (String feature : feature_idx.keySet()) {
			w.write(feature + "\n");
			w.flush();
			// System.out.println(feature + " " + feature_idx.get(feature));
		}
		w.close();
	}

	public static Map<String, Integer> sample_label_binfeature_file(Map<String, Integer> sample_label, 
		Map<String, String> sample_type, Map<String, Set<String>> sample_feature, String outfile) throws IOException {
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
		BufferedWriter w1 = new BufferedWriter(new FileWriter("unlabelled_tki.txt"));

		for (String sample : sample_feature.keySet()) {

			int label = -1;
			if (sample_label.containsKey(sample))
				label = sample_label.get(sample);
			int type = 0;
			if (sample_type.containsKey(sample) && sample_type.get(sample).compareToIgnoreCase("tki") == 0) {
				type = 1;
				// System.out.println(sample);
			}

			if (sample_feature.containsKey(sample)) {
				// if sample has feature, write sample and label to file
				w.write(m + " " + type + " " + label + " ");
				w.flush(); 

				if (type == 1) {
					System.out.println(sample);
				}

				if (label == -1 && type == 1){
					w1.write(sample + "\n");
					w1.flush();
				}

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


	public static Map<String, Integer> sample_label_wtfeature_file(Map<String, Integer> sample_label, 
		Map<String, String> sample_type, Map<String, Map<String, Double>> sample_feature, 
		String outfile) throws IOException{
		// first get the list of features
		Map<String, Integer> feature_idx = new HashMap<String, Integer>();

		int idx = 0;
		for (String sample : sample_feature.keySet()) {
			Map<String, Double> feature_score = sample_feature.get(sample);
			for (String feature : feature_score.keySet()) {
				
				if (!feature_idx.containsKey(feature)) {
					feature_idx.put(feature, idx);
					idx++;
				}
			}
			
		}

		// then write features for each sample
		int n = feature_idx.size(); // feature size
		int m = 0; // sample size

		BufferedWriter w = new BufferedWriter(new FileWriter(outfile));

		for (String sample : sample_label.keySet()) {
			int label = sample_label.get(sample);
			int type = 0;
			if (sample_type.containsKey(sample) && sample_type.get(sample).compareToIgnoreCase("test") == 0) {
				type = 1;
			}

			if (sample_feature.containsKey(sample)) {
				// if sample has feature, write sample and label to file
				w.write(sample + " " + type + " " + label + " ");
				w.flush(); 

				double[] x = new double[n]; // feature vector
				Map<String, Double> feature_score = sample_feature.get(sample);
				for (String feature : feature_score.keySet()) {
					double score = feature_score.get(feature);
					int loc = feature_idx.get(feature);
					x[loc] = score;
				}

				for (int i = 0; i < x.length; i++) {
					w.write(x[i] + " ");
					w.flush();
				}

				w.write("\n");
				w.flush();

				m++;

			} else {

			}
		}

		System.out.println(m + " samples, " + n + " features");

		return feature_idx;

	}

	

	public static Map<String, Integer> read_sample_label (String sampleFile) throws IOException {
		Map<String, Integer> sample_label = new HashMap<String, Integer>();
		BufferedReader r = new BufferedReader(new FileReader(sampleFile));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\\|");
			String drug = parts[0];
			int label = Integer.parseInt(parts[2]);

			
			sample_label.put(drug, label);
			

		}
		r.close();

		return sample_label;
	}

 
	public static Map<String, String> read_sample_type (String sampleFile) throws IOException {
		Map<String, String> sample_type = new HashMap<String, String>();
		BufferedReader r = new BufferedReader(new FileReader(sampleFile));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\\|");
			String drug = parts[0];
			String type = parts[1];

			sample_type.put(drug, type);
			

		}
		r.close();

		return sample_type;
	}






}







