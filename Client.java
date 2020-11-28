/*
 Client class for the game of rock, paper, scissor. 
 It sends and receives responses from the server.
*/
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class Client{

    private static String host = "localhost";
    private static Integer port = 3456;
    private static String header = "===!!!ROCK PAPER SCISSOR!!!===\n";

    //main method
    public static void main (String args[]) throws Exception{
        System.out.println(Client.header);
        Socket socket = new Socket(Client.host, Client.port);
        ConnectServer connect = new ConnectServer(socket);
        connect.start();
    }
    //inner class to connect to the server
    static class ConnectServer extends Thread{
        Socket socket;
        //Contructor
        public ConnectServer(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run(){
            server_t(socket);
        }
        //intereact with the server
        private void server_t(Socket socket){
            String input = "";
            String response;
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            try{
                //Sends and receives messages from the server
                ObjectOutputStream server_output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream server_input = new ObjectInputStream(socket.getInputStream());
                //Player enters username
                System.out.println("Enter your username: ");
                String name = userInput.readLine();
                server_output.writeObject(name);
                //Server sends welcome message
                response = (String)server_input.readObject();
                System.out.println("Server: " + response);
                
                while(true){
                    //prints game rules and options
                    System.out.println("Game Starts!!!");
                    System.out.println("\nGame Rules:\n 1. Rock beats Scissor\n 2. Scissor beats Paper\n 3. Paper beats Rock\n");
                    System.out.println("Enter R for Rock, P for Paper, or S for Scissors...");
                    //takes player input and sends to the server
                    input = userInput.readLine();
                    server_output.writeObject(input);
                    System.out.println("You entered "+ input.toUpperCase());
                    //response from the server
                    response = (String)server_input.readObject();
                    System.out.println("\nServer: " + response);
                    //asks if player wants to play more
                    System.out.println("Do you want to play more? (y/n) ");
                    input = userInput.readLine();
                    if(!(input.equals("y")||input.equals("n"))){
                        System.out.println("Invalid Input! Enter 'y' or 'n'.");
                    }
                    //break loop if server sends exit message
                    server_output.writeObject(input.toUpperCase());
                    response = (String)server_input.readObject();
                    if(response.equals("EXIT")) {
                        System.out.println("GAME OVER");
                        break;
                    }                
                }
                //close socket if game is draw or one player wins
                server_output.close();
                server_input.close();
                socket.close();
                //break;
            }catch(IOException e){
                e.getMessage();
            }catch(ClassNotFoundException e){
                e.getMessage();
            }
        }

    }
}

