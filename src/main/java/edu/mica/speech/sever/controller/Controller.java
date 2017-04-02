package edu.mica.speech.sever.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.mica.speech.sever.manager.ModulesManager;
import edu.mica.speech.sever.manager.SpeechRecognizer;

public class Controller {
	private ServerSocket serversocket;
	private boolean severOn = true;
	private int port = 9875;
	private ClientThread clientThread = null;
	private ConfigurationManager cm;
	private Recognizer recognizer;
	private String mosesHost = "172.16.75.74";
	private int mosesPort = 8080;

	public Controller() throws IOException{
		startSever();
	}
	
	public Controller(int port){
		this.port = port;
		startSever();
	}

	public void connection() throws Exception{
		try {
			while(severOn){
				Socket clientsocket = serversocket.accept();
				if(clientThread == null) {
					ClientThread clientThread = new ClientThread(clientsocket);
					clientThread.start();
				} else {
					throw new Exception("thread is busy!");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			
		} finally {
			stopServer();
		}
		
	}
	
	public void stopServer() throws IOException{
		serversocket.close();
		severOn = false;
	}
	
	public void startSever(){
		try {
			serversocket = new ServerSocket(port);
			//URL url = ClassLoader.getSystemResource("st.config.xml");
			URL url = ClassLoader.getSystemResource("stdefault.config.xml");
			cm = new ConfigurationManager(url);
			cm.lookup("recognizer");
			recognizer = (Recognizer) cm.lookup("recognizer");
			recognizer.allocate();
			severOn = true;
			
			//System.out.println("IP Address: " + InetAddress.getLocalHost().getHostAddress());
			System.out.println("Listening at port: " + port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not start server at port: " + port);
			severOn = false;
			e.printStackTrace();
		}
	}
	
	
	
	public String getMosesHost() {
		return mosesHost;
	}

	public void setMosesHost(String mosesHost) {
		this.mosesHost = mosesHost;
	}

	public int getMosesPort() {
		return mosesPort;
	}

	public void setMosesPort(int mosesPort) {
		this.mosesPort = mosesPort;
	}



	private class ClientThread extends Thread{
		private Socket clientSocket;
		private DataInputStream in;
		private DataOutputStream out;
		private ModulesManager modulesManager;
		private String result = null;
		public ClientThread(Socket clientSocket) throws MalformedURLException {
			super("Client Thread");
			this.clientSocket = clientSocket;
			modulesManager = new ModulesManager(Controller.this.cm, Controller.this.recognizer,Controller.this.mosesHost,Controller.this.mosesPort);
		}
		
		public void run(){
			try {
				ArrayList<float[]> allFeatures = new ArrayList<float[]>();
				System.out.println(clientSocket.getRemoteSocketAddress().toString());
				in = new DataInputStream(clientSocket.getInputStream());
				out = new DataOutputStream(clientSocket.getOutputStream());
				BufferedReader bufferedReader;
	            PrintWriter printWriter;
				// do something
				bufferedReader = new BufferedReader(new InputStreamReader(in));
	            printWriter = new PrintWriter(out,true);
	            String line;
	            line = bufferedReader.readLine();
	            int numAllFeatures = Integer.parseInt(line);
	            while((line = bufferedReader.readLine()) != null && !line.equals("end")){
	            	System.out.println(line);
	            	float[] features = SpeechRecognizer.getArrayFloat(line);
	            	allFeatures.add(features);
	            }
	            if(numAllFeatures != allFeatures.size()){
	            	throw new IOException("transmitting failed!");
	            }
	            result = modulesManager.excuteJob(allFeatures);
	            printWriter.println(result);
	            bufferedReader.close();
	            printWriter.close();
	            System.out.println("end of session!");
	            in.close();
				out.close();
				clientSocket.close();
			} catch (EOFException eofe){
				System.out.println("check bug");
				eofe.printStackTrace();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				try {
					in.close();
					out.close();
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
}
