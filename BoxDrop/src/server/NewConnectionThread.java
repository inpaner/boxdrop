package server;

class NewConnectionThread implements Runnable {
	private ClientProxy client;
	
	public NewConnectionThread(ClientProxy client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		// Send file list
		// Handle inconsistencies
		
		client.startListening();
	}

}
