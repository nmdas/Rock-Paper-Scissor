/*
A server which accepts only two players for the game of rock, paper, scissor.
The server monitors and handle messages from 2 clients simultaneously.
The server distributes the messages to all other clients who have a connection to the server.
*/
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class Server extends Thread {
	private static int port = 3456;
	private static String header = "===!!!ROCK PAPER SCISSOR!!!===\n";
	private static ServerSocket socket;

	//main method
	public static void main(String args[]) throws Exception {
		System.out.println(Server.header);
		socket = new ServerSocket(Server.port);
		System.out.println("Server is running on port " + socket.getLocalPort() + " ...\n");
		ConnectClient connect = new ConnectClient();
		connect.start();
	}

	//inner class to connect to the client
	static class ConnectClient extends Thread {
		static String player1_response = "";
		static String player2_response = "";
		static String player1_name;
		static String player2_name; 
		Socket player1;
		Socket player2;

		//Contructor
		public ConnectClient(){}

        @Override
        public void run(){
            client_t(player1, player2);
        }

        //method to handle the client 
        private void client_t(Socket player1, Socket player2){
        	String player1_input;
			String player2_input;

			try{
				//First player gets accepted
				player1 = socket.accept();
				//Sends and receives messages from the first player
				ObjectInputStream player1_in = new ObjectInputStream(player1.getInputStream());
				ObjectOutputStream player1_out = new ObjectOutputStream(player1.getOutputStream());
				
				if (player1.isConnected()) {
					System.out.println("Player 1 has joined ...... waiting for player 2.");
				}
				//Reads first player name
				player1_name = (String)player1_in.readObject();
				player1_response = "Welcome " + player1_name + "!";
				player1_out.writeObject(player1_response);

				//Second player gets accepted
				player2 = socket.accept();
				//Sends and receives messages from the second player
				ObjectInputStream player2_in = new ObjectInputStream(player2.getInputStream());
				ObjectOutputStream player2_out = new ObjectOutputStream(player2.getOutputStream());

				if (player2.isConnected()) {
					System.out.println("Player 2 has joined ...... lets start the game!!!\n");
				}
				//Reads second player name
				player2_name = (String)player2_in.readObject();
				player2_response = "Welcome " + player2_name + "!";
				player2_out.writeObject(player2_response);
				//Prints welcome message
				System.out.println("Welcome " + player1_name + "!");
				System.out.println("Welcome " + player2_name + "!");

				//Loop to play multiple times
				while(!socket.isClosed()){
					player1_input = (String)player1_in.readObject();
		            player1_input = player1_input.toUpperCase();
					player2_input = (String)player2_in.readObject();
		            player2_input = player2_input.toUpperCase();
		            
		            //Prints player choice                
		            System.out.println("\n" + player1_name + " entered " + player1_input);
		            System.out.println(player2_name + " entered " + player2_input);
					//Prints draw is both player gives same input
					if (player1_input.equals(player2_input)) {
						player1_response = "Game is Draw!";
						player2_response = "Game is Draw!";
						System.out.println("\nGame is Draw!");
					}
					//Compares player input and declares winner
					else if (player1_input.equals("R") && player2_input.equals("S")) {player1_win();}
					else if (player1_input.equals("P") && player2_input.equals("S")) {player2_win();}
					else if (player1_input.equals("S") && player2_input.equals("R")) {player2_win();}
					else if (player1_input.equals("P") && player2_input.equals("R")) {player1_win();}
					else if (player1_input.equals("R") && player2_input.equals("P")) {player2_win();}
					else if (player1_input.equals("S") && player2_input.equals("P")) {player1_win();}
		            else {
		                player1_response = "Invalid Input! Please enter 'R', 'P', or 'S'.";
		                player2_response = "Invalid Input";
		                System.out.println("\nPlayer entered invalid input.");
		            }
		            //Send response to the players
					player1_out.writeObject(player1_response.toUpperCase());
					player2_out.writeObject(player2_response.toUpperCase());
					//Get input from the players
					player1_input = (String)player1_in.readObject();
					player2_input = (String)player2_in.readObject();
					//In either player wants to end game, exit
	                if(player1_input.equals("N")||player2_input.equals("N")) {
	                    player1_out.writeObject("EXIT");
	                    player2_out.writeObject("EXIT");
	                    System.out.println("Game Over"); 
	                }
	                else{
	                	player1_out.writeObject("Continue");
	                    player2_out.writeObject("Continue");
	                }
			    }
			    //close socket if game is draw or one player wins              
				player1.close();
				player2.close();
				socket.close();
			
			}catch(IOException e){
                e.getMessage();
            }catch(ClassNotFoundException e){
                e.getMessage();
            }
		}
		//prints win message is first player wins
	    private static void player1_win() {
	    	player1_response = "You Win!!";
			player2_response = "You Lose!!";
			System.out.println("\n" + player1_name + " Wins.");
	    }
	    //prints win message is second player wins
	    private static void player2_win() {
	    	player2_response = "You Win!!";
			player1_response = "You Lose!!";
			System.out.println("\n" + player2_name + " Wins.");
	    }
	}
}

