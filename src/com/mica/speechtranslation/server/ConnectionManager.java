package com.mica.speechtranslation.server;

import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionManager {
	ServerSocket serversocket;
	Socket clientsocket;
	int port = 8765;
	
	public ConnectionManager(){
		
	}
	
	public ConnectionManager(ServerSocket serversocket) {
		this.serversocket = serversocket;
	}
	public ConnectionManager(int port){
		this.port = port;
	}
	
	public void startServer(){
		
	}
}
