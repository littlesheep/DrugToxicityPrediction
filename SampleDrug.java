import java.util.*;
import java.io.*;

public class SampleDrug {

	
	public static void main (String[] args) throws IOException {
		// stitch_sample();
		drugbank_target_sample();
	}


	public static void lincs_sample() throws IOException {
		Set<String> dSet = new HashSet<String> ();
		dSet.add("nervous system disorder");
		LincsSample.label_all_samples(dSet);
	}

	public static void drugbank_target_sample() throws IOException {
		Set<String> dSet = new HashSet<String> ();
		dSet.add("nervous system disorder");
		DrugbankSample.label_all_samples(dSet, "./data/drug_bank.txt", "nervDrugbankTargetSample.txt");
	}

	public static void drugbank_enzyme_sample() throws IOException {
		Set<String> dSet = new HashSet<String> ();
		dSet.add("nervous system disorder");
		DrugbankSample.label_all_samples(dSet, "./data/drug_enzyme.txt", "nervDrugbankEnzymeSample.txt");
	}

	public static void stitch_sample() throws IOException {
		Set<String> dSet = new HashSet<String> ();
		dSet.add("nervous system disorder");
		StitchSample.label_all_samples(dSet);
	}





}