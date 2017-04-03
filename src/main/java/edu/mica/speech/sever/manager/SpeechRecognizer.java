package edu.mica.speech.sever.manager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.frontend.util.StreamCepstrumSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.mica.speech.sever.tools.SpeechResult;
import edu.mica.speech.sever.tools.StreamSpeechRecognizer;

public class SpeechRecognizer {
	private Recognizer recognizer;
	private ConfigurationManager cm;
	private ArrayList<float[]> allFeatures = new ArrayList<float[]>();
	private int lenghtFeatures = 0;
	private int lenghtAllFeatures = 0;
	
	public SpeechRecognizer(ConfigurationManager cm, Recognizer recognizer){
		this.cm = cm;
		this.recognizer = recognizer;
	}
	
	public String recognize() throws Exception{
		String finalRessult = "";
//		InputStream is = this.dumpFeatures();
//		if(is == null){
//			throw new Exception("Error: all feature not match. Must be set all features before recognize!");
//		}
		InputStream is = new DataInputStream(new FileInputStream("/home/thinh/Music/UntitledFolder/test402.mfc"));
        StreamCepstrumSource dataSource = (StreamCepstrumSource) cm.lookup("streamCepstrumSource");
        dataSource.setInputStream(is,true);
        Result result = null;
        
        while((result = recognizer.recognize()) != null){
        	finalRessult+= " " + result.getBestResultNoFiller();
        	finalRessult = finalRessult.replaceAll("_", " ");
        	System.out.println(finalRessult);
        }
		return finalRessult.trim();
	}
	
	public String recognize(boolean var) throws Exception{
		Configuration cm = new Configuration();
//		String acousticModelPath = ClassLoader.getSystemResource("an4/an4.cd_cont_200").getPath();
//		String dictionaryPath = ClassLoader.getSystemResource("an4/an4.dic").getPath();
//		String languageModelPath = ClassLoader.getSystemResource("an4/an4.dmp").getPath();
//		String acousticModelPath = ClassLoader.getSystemResource("digitmodel/digit.cd_cont_200").getPath();
//		String dictionaryPath = ClassLoader.getSystemResource("digitmodel/digit.dic").getPath();
//		String languageModelPath = ClassLoader.getSystemResource("digitmodel/digit.lm.bin").getPath();
		String acousticModelPath = ClassLoader.getSystemResource("an4/an4.cd_cont_200").getPath();
		String dictionaryPath = ClassLoader.getSystemResource("an4/an4.dic").getPath();
		String languageModelPath = ClassLoader.getSystemResource("an4/an4.dmp").getPath();
		cm.setAcousticModelPath(acousticModelPath);
		cm.setDictionaryPath(dictionaryPath);
		cm.setLanguageModelPath(languageModelPath);
		LiveSpeechRecognizer liverecognizer = new LiveSpeechRecognizer(cm);

		
		StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(cm);
		
		//File filetest = new File(ClassLoader.getSystemResource("StevenLevitt_2005G_0.wav").getPath());
		//File filetest = new File(ClassLoader.getSystemResource("an427-fvap-b.wav").getPath());
		File filetest = new File("/home/thinh/Music/UntitledFolder/digit4016mymodel.mfc");
		InputStream stream = this.dumpFeatures(); 
		//InputStream stream = new FileInputStream(filetest);
		
		recognizer.startRecognition(stream);
		System.out.println("\n test file: " + filetest.getPath());
		if(filetest.exists()){
			System.out.println("\n exist");
		} else {
			System.out.println("\n not exist");
		}
		SpeechResult result;
		String text = "";
        while ((result = recognizer.getResult()) != null) {
        	 System.out.format("Hypothesis: %s\n", result.getHypothesis());

//             System.out.println("List of recognized words and their times:");
//             for (WordResult r : result.getWords()) {
//                 System.out.println(r);
//             }
//
//             System.out.println("Best 3 hypothesis:");
//             for (String s : result.getNbest(3))
//                 System.out.println(s);
             text+= " " + result.getHypothesis();
        }
        
        System.out.println("\nResult: " + text);
        recognizer.stopRecognition();
		return text;
	}
	/**
	 * Dump all features to binary!
	 * because default of ByteBuffer is big endian and default of InputStream is big endian too,
	 * so return of this method is and object inputstream big endian*/
	private InputStream dumpFeatures() throws java.lang.Exception{
		InputStream is = null;
		if(allFeatures.isEmpty() || allFeatures.size() != lenghtAllFeatures || lenghtAllFeatures == 0 || lenghtFeatures == 0){
			return null;
		} 
		int totalelement = lenghtAllFeatures*lenghtFeatures;
		ByteBuffer buffer = ByteBuffer.allocate(4 * lenghtAllFeatures*lenghtFeatures + 4);
		byte[] byteFeatures;
		
		buffer.putInt(lenghtAllFeatures*lenghtFeatures);
		try {
			for(float[] features: allFeatures){
				for(float feature: features){
					buffer.putFloat(feature);
				}
			}
			
			byteFeatures = buffer.array();
			is = new ByteArrayInputStream(byteFeatures);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}
	
	private InputStream dumpFeatures(String outputFile) throws IOException {
		
		DataOutputStream outStream = new DataOutputStream(new FileOutputStream(
				outputFile));
		outStream.writeInt(lenghtAllFeatures*lenghtFeatures);
		
		for (float[] feature : allFeatures) {
			for (float val : feature) {
				outStream.writeFloat(val);
			}
		}
		outStream.close();
		DataInputStream in = null;
		try {
			in = new DataInputStream(new FileInputStream(outputFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return in;
	}
	
	/**
	 * Convert a string that contain sequence of floats to an array float
	 * */
	public static float[] getArrayFloat(String s) throws NullPointerException, NumberFormatException{
		String split[] = s.split(" ");
		int length = split.length;
		int i = 0;
		float[] features = new float[length];
		for(i = 0; i < length; i++) {
			features[i] = Float.parseFloat(split[i]);
		}
		return features;
	}
	
	public void addFeatures(float[] features){
		allFeatures.add(features);
	}
	
	public void clearFeature(){
		allFeatures.clear();
	}

	public ArrayList<float[]> getAllFeatures() {
		return allFeatures;
	}

	public void setAllFeatures(ArrayList<float[]> allFeatures) {
		lenghtAllFeatures = allFeatures.size();
		lenghtFeatures = allFeatures.get(0).length;
		this.allFeatures = allFeatures;
	}

	public int getLenghtFeatures() {
		return lenghtFeatures;
	}

	public void setLenghtFeatures(int lenghtFeatures) {
		this.lenghtFeatures = lenghtFeatures;
	}

	public int getLenghtAllFeatures() {
		return lenghtAllFeatures;
	}

	public void setLenghtAllFeatures(int lenghtAllFeatures) {
		this.lenghtAllFeatures = lenghtAllFeatures;
	}
	
	
}
