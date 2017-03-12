package com.mica.speechtranslation.server;

import java.net.Socket;

public class ClientThread extends Thread{
	Socket clientSocket;
	
	public ClientThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void run(){
		
	}
	
}
