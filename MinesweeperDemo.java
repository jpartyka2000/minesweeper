package Minesweeper;

import java.util.*;
import java.io.*;


class MinesweeperDemo {

   public static void main(String[] args) {
      int M = Integer.parseInt(args[0]);  //rows
      int N = Integer.parseInt(args[1]);  //cols
      int numOfMines = Integer.parseInt(args[2]);
      String gameMode = args[3];




      if (numOfMines > (M * N)) {

	     System.out.println("More mines than squares is illegal!");
	     System.exit(0);

      }//end if

      if (numOfMines < 0) {

	     System.out.println("At least one mine must be on the board!");
	  	 System.exit(0);

	  }//end if


	  //create gameBoard object and carry out board creation methods
	  gameBoard gb = new gameBoard(M, N, numOfMines, gameMode);
	  gb.createBoard();

	  gb.printBoard();

     //if this is player mode, then we will create a while loop here that allows the player to enter a move
     //with each square revelation having consequences (good or bad :-)). The game ends either with the player
     //hitting a mine or winning the game, so the loop below is designed to be infinite

     String playerMove = null;
     int rowVal = 0;
     int colVal = 0;
     int result = 0;
     String move = null;
     StringTokenizer strtok = null;

     if (gameMode.equals("p")) {

        while (true) {

           System.out.println();
           System.out.println();

           System.out.println("Make a move by specifying the action and coordinates of the square (ie: s,1,1)");
           System.out.println();

           BufferedReader stdin = new BufferedReader (new InputStreamReader(System.in));


           try {

              playerMove = stdin.readLine();

	       } catch (Exception e) {

              System.out.println("You did not enter a legal move. Try again.");
              continue;

	       }//end trycatch

	       //read player input and decide what to do from here
	       strtok = new StringTokenizer(playerMove, ",");

           //parse user move into row and col values
           while (strtok.hasMoreTokens()) {

              move = strtok.nextToken();
              rowVal = Integer.parseInt(strtok.nextToken());
              colVal = Integer.parseInt(strtok.nextToken());

	       }//end while

	       if (rowVal > M || colVal > N || rowVal < 0 || colVal < 0) {

              System.out.println("You did not enter a legal move. Try again.");
			  gb.printBoard();
			  continue;

	       }//end if

           //now we need to reveal the square selected by user and save result of this action
           if (move.equals("s")) {

              result = gb.revealSquare(rowVal, colVal);


              if (result == 0) {

                 //the user hit a mine, the game is over

                 //reveal entire board -- if a user incorrectly identified a mine, then I need to display that with a special
                 //symbol. Reveal the entire board and then print it

                 gb.revealFinalBoard();

                 gb.printBoard();

                 System.out.println();
                 System.out.println();
                 System.out.println("GAME OVER -- you hit a mine :-(");
                 System.exit(0);

	          }//end if

	          if (result == 1) {

                  gb.printBoard();

			      //then user has won the game!
			      System.out.println();
			      System.out.println();
			      System.out.println("YOU WIN!! :-)");
                  System.exit(0);

	         }//end if

	         //if result is 2, the game continues, and we print the updated board below

             //print out updated board
             gb.printBoard();

	      }//end if


	      if (move.equals("m")) {

             //if we simply select a mine, then there is no possibility of the game ending. So
             //all we need to do is indicate whether the mine selected is valid or not. It is
             //not possible to select a square that has been marked as a mine, whether that
             //marking is correct or not

             gb.placeMine(rowVal, colVal);

             if (gb.isGameOver()) {

		        gb.printBoard();

			    //then user has won the game!
			    System.out.println();
			    System.out.println();
			    System.out.println("YOU WIN!! :-)");
                System.exit(0);

		     }//end if

              //print out updated board
             gb.printBoard();

          }//end if


       }//end while

    }//end if


    if (gameMode.equals("c")) {

       //create reasoningEngine obj

       String cmove = null;
       int moveResult = 0;
       StringTokenizer cstrtok = null;

       while (true) {


          //we will consult the reasoningEngine class to determine whether the next move is random on a global scale
          //locally random, or is an entailed alpha from our KB (either a selection or mine detection), in which case,
          //it is guaranteed to be a correct move

          //get reasoning engine from gameBoard object
          reasoningEngine re = gb.getReasoningEngine();

          cmove = re.nextMove();

          //if computer selected a square...

          if (cmove.indexOf("s,") > -1) {

             //read input and decide what to do from here
	         cstrtok = new StringTokenizer(cmove, ",");

	         int rowCoord = 0;
	         int colCoord = 0;

             //parse user move into row and col values
             while (cstrtok.hasMoreTokens()) {

                String l = cstrtok.nextToken();
                rowCoord = Integer.parseInt(cstrtok.nextToken());
                colCoord = Integer.parseInt(cstrtok.nextToken());

	         }//end while


		     //I need to extract the row and col vals of the computer move
		    // int rowCoord = Integer.parseInt(cmove.substring(2,3));
		    // int colCoord = Integer.parseInt(cmove.substring(4,5));

		    //before revealing the square, ensure that the square has not already been revealed
		    //this could be a problem with the advanced rules
		    //if (gb.isSquareRevealed(rowCoord, colCoord)) {

		    //   System.out.println("This square has already been revealed!");

		    //} else {

		       moveResult = gb.revealSquare(rowCoord, colCoord);

		    //}//end if

	      }//end if


          if (cmove.indexOf("m,") > -1) {

			  //read input and decide what to do from here
			  cstrtok = new StringTokenizer(cmove, ",");

			  int rowCoord = 0;
	          int colCoord = 0;

			  //parse user move into row and col values
			  while (cstrtok.hasMoreTokens()) {

			     String l = cstrtok.nextToken();
			     rowCoord = Integer.parseInt(cstrtok.nextToken());
			     colCoord = Integer.parseInt(cstrtok.nextToken());

	          }//end while

			 //I need to extract the row and col vals of the computer move
		     //int rowCoord = Integer.parseInt(cmove.substring(2,3));
		     //int colCoord = Integer.parseInt(cmove.substring(4,5));

		     gb.placeMine(rowCoord, colCoord);

		     //if we placed the final mine, then celebrate!
		     if (gb.isGameOver()) {

		        gb.printBoard();

		       //then user has won the game!
		       System.out.println();
		       System.out.println();
		       System.out.println("YOU WIN!! :-)");
		       System.exit(0);

		     }//end if

	      }//end if


          if (cmove.equals("gr")) {

             //this is a global random move, so pick any square that has not been picked

             Random rowGen = new Random();
             Random colGen = new Random();
             int rVal = 0;
             int cVal = 0;

             do {

		       rVal = rowGen.nextInt(M);
		       cVal = rowGen.nextInt(N);

		    } while (gb.isSquareRevealed(rVal, cVal) || gb.isSquareMine(rVal, cVal));


            System.out.println("Computer decides to select square: (" + rVal + "," + cVal + ")");

		    //at this point, we have chosen a square that has not been revealed, so pick it
            moveResult = gb.revealSquare(rVal, cVal);

	      }//end if


	      //determine result of computer move

	      if (moveResult == 0) {

		     //the user hit a mine, the game is over

		     //reveal entire board -- if a user incorrectly identified a mine, then I need to display that with a special
		     //symbol. Reveal the entire board and then print it

		     gb.revealFinalBoard();

		     gb.printBoard();

		     System.out.println();
		     System.out.println();
		     System.out.println("GAME OVER -- you hit a mine :-(");
		     System.exit(0);

		 }//end if

		 if (moveResult == 1) {

		    gb.printBoard();

		    //then user has won the game!
		    System.out.println();
		    System.out.println();
		    System.out.println("YOU WIN!! :-)");
		    System.exit(0);

	    }//end if

		//if result is 2, the game continues, and new information has been added to the knowledge base
		//Finally, we print the updated board below

		//print out updated board
		gb.printBoard();


		//print out KB after move
		//KnowledgeBase know = re.getKnowledgeBase();
		//know.printKnowledgeBase();



       }//end while


    }//end if


   }//method main

}//class Minesweeper





