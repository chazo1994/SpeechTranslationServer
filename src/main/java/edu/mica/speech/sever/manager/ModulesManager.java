package edu.mica.speech.sever.manager;

import java.awt.List;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class ModulesManager {
	private SpeechRecognizer speechRecognizer;
	private Translator translator;
	
	public ModulesManager(ConfigurationManager cm, Recognizer recognizer){
		speechRecognizer = new SpeechRecognizer(cm, recognizer);
		translator = new Translator();
	}
	public ModulesManager(ConfigurationManager cm, Recognizer recognizer, int mosesPort){
		speechRecognizer = new SpeechRecognizer(cm, recognizer);
		translator = new Translator(mosesPort);
	}
	public ModulesManager(ConfigurationManager cm, Recognizer recognizer, String host, int mosesPort) throws MalformedURLException{
		speechRecognizer = new SpeechRecognizer(cm, recognizer);
		translator = new Translator(host, mosesPort);
	}
	
	public String excuteJob(ArrayList<float[]> allFeatures) throws Exception {
		String result = null;
		speechRecognizer.setAllFeatures(allFeatures);
		result = speechRecognizer.recognize(true);
		//result = translator.translate(new String(result));
		return result;
	}

}
