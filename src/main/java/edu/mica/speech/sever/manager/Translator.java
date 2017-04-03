package edu.mica.speech.sever.manager;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import edu.cmu.sphinx.result.Result;

public class Translator {
	private int mosesport = 8080;
	private String host = "172.16.75.74";
	private InetAddress ip;
	private boolean isSever = false;
	private TranslatorThread translatorThread;
	private String textTranslation = null;
	public Translator(){
		
	}
	
	public Translator(int mosesport) {
		this.mosesport = mosesport;
		
	}
	
	public Translator(String host, int mosessport) throws MalformedURLException{
		this.mosesport = mosessport;
		this.host = host;
		
		
	}
	
	public String translate(String input) throws Exception {
		if(translatorThread != null) {
			throw new Exception("thread is busy!");
		}
		
		translatorThread = new TranslatorThread(input);
		translatorThread.start();
		translatorThread.join();
		return textTranslation;
	}
	
	/*private void setIp() {
        try {
            ip = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            isSever = false;
            System.out.println("failed to resolve ip or host");
            e.printStackTrace();
        }
    }*/
	
	private class TranslatorThread extends Thread {
		private String textToTranslate;
		
		public TranslatorThread(String textToTranslate) {
			
			// TODO Auto-generated constructor stub
			super();
			this.textToTranslate = textToTranslate;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				URL url = new URL("http",Translator.this.host,Translator.this.mosesport,"/RPC2");
				System.out.println(url.toString());
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				config.setEncoding(null);
				config.setBasicEncoding(null);
				config.setServerURL(url);
				XmlRpcClient client = new XmlRpcClient();
				client.setConfig(config);
				// The XML-RPC data type used by mosesserver is <struct>. In Java, this data type can be represented using HashMap.
				HashMap<String,String> mosesParams = new HashMap<String,String>();
				mosesParams.put("text", textToTranslate);
				mosesParams.put("align", "true");
				mosesParams.put("report-all-factors", "true");
				// The XmlRpcClient.execute method doesn't x Hashmap (pParams). It's either Object[] or List. 
				Object[] params = new Object[] { null };
				//Object[] params = new Object[1];
				params[0] = mosesParams;
				
				// Invoke the remote method "translate". The result is an Object, convert it to a HashMap.
				HashMap result = (HashMap)client.execute("translate", params);
	                        // Print the returned results
				Translator.this.textTranslation = (String)result.get("text");
				System.out.println("Input : "+textToTranslate);
				System.out.println("Translation : "+textTranslation);
				if (result.get("align") != null){ 
					Object[] aligns = (Object[])result.get("align");
					System.out.println("Phrase alignments : [Source Start:Source End][Target Start]"); 
					for ( Object element : aligns) {
		                		HashMap align = (HashMap)element;	
						System.out.println("["+align.get("src-start")+":"+align.get("src-end")+"]["+align.get("tgt-start")+"]");
					}
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
		
	}
}
