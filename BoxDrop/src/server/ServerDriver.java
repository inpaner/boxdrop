package server;

import java.io.IOException;

public class ServerDriver {
	public static void main(String args[]) throws IOException {

		Server server = new Server(8080);
		new Thread(new AcceptConnectionThread(server)).start();

		/*long lastUpdateTime = 0;

		while (true) {

			long currentTime = System.currentTimeMillis();

			if (currentTime - lastUpdateTime >= Constants.DANCE_TIME_MS) {

				lastUpdateTime = currentTime;

				Helper.clearConsole();

				Socket currDancer = DanceFloorManager.getInstance().getCurrDancer();
				if (currDancer != null)
					System.out.println(currDancer.getRemoteSocketAddress() + " is dancing.");
				else
					System.out.println("No one is dancing.");
			}

		}
		 */
	}
}
