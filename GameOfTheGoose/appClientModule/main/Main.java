package main;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import main.Player;

public class Main {
	
	private static ArrayList<Player> players;
	private static boolean game_finished = false;
		
	public static void main(String[] args) {
		System.out.println("Welcome to the Game of the Goose!");
		int turn = 0; //Index of the ArrayList element containing the player that has to move
		players = new ArrayList<Player>();
		Scanner s = new Scanner(System.in);
		while (!game_finished) {
			if (s.hasNext()) {
				String[] line = s.nextLine().split(" ");
				if (line.length > 1 && line.length < 5) {
					if (line[0].equals("add") && line[1].equals("player")) {
						//Add player
						if (line.length > 3) {
							System.out.println("The player's name must not contain spaces");
						} else {
							addPlayer(line[2]);
						}
					} else if (line[0].equals("move") && (line.length == 2 || line.length == 4)) {
						//Move player
						String name = line[1];
						boolean found = false;
						for (int i=0; i < players.size(); i++) {
							if (players.get(i).getName().equals(name)) {
								found = true;
								break;
							}
						}
						if (!found) {
							System.out.println("There is not a player called " + name);
						} else {
							if (players.size() < 2) {
								System.out.println("There is only one player. Two players or more is required. Add another player to play.");
							} else {
								if (turn == players.size()) {
									turn = 0; //Each player moved. Restart from the first.
								}
								if (!players.get(turn).getName().equals(name)) {
									System.out.println("This is not the turn of " + name + ". It is the turn of " + players.get(turn).getName());
								} else {
									if (line.length == 4) {
										char first_dice = line[2].charAt(0);
										String second_dice = line[3];
										if (Character.getNumericValue(first_dice) > 0 && Character.getNumericValue(first_dice) < 7 &&
												Integer.parseInt(second_dice) > 0 && Integer.parseInt(second_dice) < 7) {
											System.out.print(name + " rolls " + line[2] + " " + line[3] + ". " + name + " moves from ");
											int n = Character.getNumericValue(first_dice) + Integer.parseInt(second_dice);
											movePlayer(name, n, turn);
											turn++;
										} else {
											System.out.println("Invalid move: both dice must be between 1 and 6");
										}	
									} else {
										Random random = new Random();
										int first_dice = random.nextInt(6) + 1;
										int second_dice = random.nextInt(6) + 1;
										System.out.print(name + " rolls " + first_dice + ", " + second_dice + ". " + name + " moves from ");
										int n = first_dice + second_dice;
										movePlayer(name, n, turn);
										turn++;
									}
								}
							}
											
						}
						
					} else {
						System.out.println("Command not allowed");
					}
				} else {
					System.out.println("Command not allowed");
				}		
			} else {
				System.out.println("Command not allowed");
			}
		}
		s.close();
	}
	
	public static void addPlayer(String name) {
		boolean clone = false;
		if (!players.isEmpty()) {
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getName().equals(name)) {
					clone = true;
					break;
				}
			}
		}
		if (clone) {
			System.out.println(name + ": already existing player");
		} else {
			Player p = new Player(name, 0);
			players.add(p);
			System.out.print("players: ");
			for (int i=0; i < players.size(); i++) {
				String player_name = players.get(i).getName();
				if (i < players.size() - 1) {
					System.out.print(player_name + ", ");
				} else {
					System.out.println(player_name);
				}
			}
		}
	}
	
	public static void movePlayer(String name, int n, int index) {
		int position = players.get(index).getPosition();
		if (position == 0) {
			System.out.print("Start to ");
		} else {
			System.out.print(position + " to ");
		}
		int new_position = position + n;
		switch (new_position) {
			case 6: 
				//The Bridge
				new_position = 12;
				System.out.print("The Bridge. " + name + " jumps to 12");
				players.get(index).setPosition(new_position);
				prank(position, new_position, name);
				break;
			case 5: case 9: case 14: case 18: case 23: case 27:
				//The Goose
				int the_goose_new_position = new_position + n;
			    players.get(index).setPosition(the_goose_new_position);
			    System.out.print(new_position + ", The Goose. " + name + " moves again and goes " +the_goose_new_position);
			    while (the_goose_new_position == 5 || the_goose_new_position == 9 || the_goose_new_position == 14 || the_goose_new_position == 18
			    		|| the_goose_new_position == 23 || the_goose_new_position == 27) {
			    	the_goose_new_position = the_goose_new_position + n;
				    players.get(index).setPosition(the_goose_new_position);
				    System.out.print(", The Goose. " + name + " moves again and goes " +the_goose_new_position);
			    }
			    prank(new_position, the_goose_new_position, name);
				break;
			case 63:
				System.out.println("63. " + name + " Wins!!");
				game_finished = true;
				break;
			default:
				if (new_position > 63) {
					int new_position_bounced = 63 - (new_position - 63);
				    players.get(index).setPosition(new_position_bounced);
					System.out.print("63. " + name + " bounces! " + name + " returns to " + new_position_bounced);
					prank(position, new_position_bounced, name);
				} else {
				    players.get(index).setPosition(new_position);
					System.out.print(new_position);
					prank(position, new_position, name);
				}
				break;
		}
		System.out.print("\n");	
	}
	
	public static void prank(int old_position, int new_position, String name) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getPosition() == new_position && !(players.get(i).getName().equals(name))) {
				players.get(i).setPosition(old_position);
				if (old_position != 0) {
					System.out.print(". On " + new_position + " there is " + players.get(i).getName() + ", who returns to " + old_position);
				} else {
					System.out.print(". On " + new_position + " there is " + players.get(i).getName() + ", who returns to Start");
				}
			}
		}
	}
	
}