package Minesweeper;

import java.util.*;

public class gameBoard {


   //number of rows in gameboard
   private int rows;

   //number of cols in gameboard
   private int cols;

   //nummber of mines specified by user for this board
   private int numOfMines;

   //array of Square objects which make up gameboard
   Square[][] squares;

   //number of squares revealed on the gameboard. we will use this to determine if the game has been won
   private int squaresRevealed;

   //number of mines correctly indicated by user
   private int minesCorrectlyIndicated;

   //the mode of the game -- either human player mode (p) or computer mode (c)
   String gameMode;

   //the reasoning engine, which is used in computer playing mode (c)
   reasoningEngine re;


   public gameBoard(int rowNum, int colNum, int mineNum, String mode) {

      rows = rowNum;
      cols = colNum;
      numOfMines = mineNum;

      gameMode = mode;

      //all squares determined to be false initially -- which means that they do not have mines
      //bombs = new boolean[rows][cols];
      squares = new Square[rows][cols];

      //set default value for squares revealed
      squaresRevealed = 0;

      //reasoning engine instantiation
      re = new reasoningEngine(rowNum, colNum, mineNum, this);

      //instantiate each one individually
      for (int i = 0; i < squares.length; i++) {

	     for (int j = 0; j < squares[i].length; j++) {

            squares[i][j] = new Square(i, j);

	     }//end for

      }//end for

   }//constructor gameBoard


   //method to create Minesweeper board

   public void createBoard() {

      //random number generators that will be used to place mines on the board
      Random mineGenX = new Random();
	  Random mineGenY = new Random();

	  int mineCount = 0;

      int xCoord = 0;
      int yCoord = 0;

      while (mineCount < numOfMines) {

          //get x coord of next mine square
          xCoord = mineGenX.nextInt(cols);

          //get y coord of next mind square
          yCoord = mineGenY.nextInt(rows);

           //assign mine as long as it is legal to assign
          if (!squares[yCoord][xCoord].isMine() && (xCoord <= cols - 1) && (yCoord <= rows - 1)) {
             squares[yCoord][xCoord].setMine();
             mineCount++;
	      }//end if


      }//end while


      for (int ySquare = 0; ySquare < rows; ySquare++) {

         for (int xSquare = 0; xSquare < cols; xSquare++) {

            //now we check to see how many neighbor squares have bombs
            //the outer loop checks the vertical dim of neighbors, the inner loop the horizontal dim

            for (int neighborRow = ySquare - 1; neighborRow <= ySquare + 1; neighborRow++) {

               for (int neighborCol = xSquare - 1; neighborCol <= xSquare + 1; neighborCol++) {

                  if (neighborRow >= 0 && neighborCol >= 0 && neighborRow < rows && neighborCol < cols && squares[neighborRow][neighborCol].isMine())
                     squares[ySquare][xSquare].incrementMineCount();

		       }//end for

	        }//end for

	     }//end for

      }//end for



   }//method createBoard


   //method that reveals the identity of a square selected by the user


   public int revealSquare(int row, int col) {


      //if user selects a mine square that has already been marked as a mine, then inform user and let the game
      //continue
      if (squares[row][col].getMineStatus()) {

         System.out.println("This square has already been marked as a mine! You cannot select it.");
         return 2;

      }//end if

      //set square indicated by params to be revealed
      squares[row][col].revealSquare();

            //if this square is a mine, return the bad news.
      if (squares[row][col].isMine())
         return 0;


     //if we are in computer game mode, then add the fact that this square is not a mine to the
     //KB

     if (gameMode.equals("c")) {

        //construct sentence indicating that this square is not a mine
        String newSentence = "!M(" + row + "," + col + ")";
		Sentence s = new Sentence();
		s.addSentence(newSentence);

		//add to KB
		re.addSentence(s);

		//add this square to the appropriate squareQueue
		re.addToQueue(squares[row][col]);

     }//end if


     //if the game is over, then return the good news. We can only do this if
     //it has been determineed that all squares except for the mine squares
     //have been revealed. We can use the goodSquaresRevealed instance
     //variable to do this



     //we have revealed one or more good squares (in the latter case, if we hit a 0 square)
     if (squares[row][col].getNeighborMineCount() == 0) {

        squaresRevealed += revealBlankBlock(row, col);

     } else {

        squaresRevealed++;

     }//end if

     //determine if the game is over
    if (isGameOver())
       return 1;

     //if neither of the above has happened, then the game continues
     return 2;

   }//method revealSquare


   public boolean isGameOver() {

      System.out.println("Squares Revealed: " + squaresRevealed);
      System.out.println("Mines Correctly Indicated: " + minesCorrectlyIndicated);
      System.out.println("Total Number of Squares: " + rows * cols);

      //determine if the game is over
      if (squaresRevealed + minesCorrectlyIndicated == (rows * cols))
         return true;
      else
         return false;


   }//method isGameOver

   private int revealBlankBlock(int r, int c) {


      //using the row and col values, we need to determine the number of other blank blocks are adjacent
      //to the selected blank block. this is the recursive floodfill algorithm, but since stack space in Java
      //is somewhat limited, we will implement the same algorithm iteratively using a queue to hold
      //all square objects that need to be processed

      Queue squareQueue = new Queue(rows * cols);

      //var to keep track of the number of empty squares that have been revealed. We already revealed the base square
      int emptySquaresRevealed = 1;

      //put the square referred to by the params into the queue first
      squareQueue.push(squares[r][c]);

      int sqCol = 0;
      int sqRow = 0;

      //now execute a loop that will process this square and all of its neighbors, and the neighbors of the neighbors, and
      //and so on.

      while (!squareQueue.isEmpty()) {

         //retrieve next square object from queue
         Square nextSquare = (Square)squareQueue.pop();

         //get row and col values for the square
         sqCol = nextSquare.getCol();
         sqRow = nextSquare.getRow();

          //reveal this square only if it has not already been revealed
          if (!nextSquare.isRevealed()) {
             nextSquare.revealSquare();
             emptySquaresRevealed++;

             //add this sentence to KB if applicable
             if (gameMode.equals("c")) {

			    //construct sentence indicating that this square is not a mine
			    String newSentence = "!M(" + sqRow + "," + sqCol + ")";
			    Sentence s = new Sentence();
			    s.addSentence(newSentence);

			    //add to KB
			    re.addSentence(s);

			    //add this square to the appropriate squareQueue
		        re.addToQueue(nextSquare);

             }//end if

	      }//end if

         //add neighbors of the square onto the queue. there should be 8 additions
         //if neighbor square meets the conditions, then add it to queue

         //neighbor check. There will be 8 of these checks


         //top left square check
		 if (sqRow - 1 >= 0 && sqRow - 1 < rows && sqCol - 1 >=0 && sqCol - 1 < cols && !squares[sqRow - 1][sqCol - 1].isMine() && squares[sqRow - 1][sqCol - 1].getNeighborMineCount() == 0 && !squares[sqRow - 1][sqCol - 1].isRevealed()) {
		    squareQueue.push(squares[sqRow - 1][sqCol - 1]);

         }//end if


         //left square check
		 if (sqRow >= 0 && sqRow < rows && sqCol - 1 >=0 && sqCol - 1 < cols && !squares[sqRow][sqCol - 1].isMine() && squares[sqRow][sqCol - 1].getNeighborMineCount() == 0 && !squares[sqRow][sqCol - 1].isRevealed()) {
		    squareQueue.push(squares[sqRow][sqCol - 1]);

	     }

         //bottom left square check
		 if (sqRow + 1 >= 0 && sqRow + 1 < rows && sqCol - 1 >=0 && sqCol - 1 < cols && !squares[sqRow + 1][sqCol - 1].isMine() && squares[sqRow + 1][sqCol - 1].getNeighborMineCount() == 0 && !squares[sqRow + 1][sqCol - 1].isRevealed()) {
		    squareQueue.push(squares[sqRow + 1][sqCol - 1]);

	     }//end if

         //above square check
		 if (sqRow - 1 >= 0 && sqRow - 1 < rows && sqCol >=0 && sqCol < cols && !squares[sqRow - 1][sqCol].isMine() && squares[sqRow - 1][sqCol].getNeighborMineCount() == 0 && !squares[sqRow - 1][sqCol].isRevealed()) {
		    squareQueue.push(squares[sqRow - 1][sqCol]);

	     }//end if



         //below square check
		 if (sqRow + 1 >= 0 && sqRow + 1 < rows && sqCol >=0 && sqCol < cols && !squares[sqRow + 1][sqCol].isMine() && squares[sqRow + 1][sqCol].getNeighborMineCount() == 0 && !squares[sqRow + 1][sqCol].isRevealed()) {
		    squareQueue.push(squares[sqRow + 1][sqCol]);

	     }//end if

		 //top right square check
		 if (sqRow - 1 >= 0 && sqRow - 1 < rows && sqCol + 1 >= 0 && sqCol + 1 < cols && !squares[sqRow - 1][sqCol + 1].isMine() && squares[sqRow - 1][sqCol + 1].getNeighborMineCount() == 0 && !squares[sqRow - 1][sqCol + 1].isRevealed()) {
		    squareQueue.push(squares[sqRow - 1][sqCol + 1]);

	     }//end if

         //right square check
		 if (sqRow >= 0 && sqRow < rows && sqCol + 1 >=0 && sqCol + 1 < cols && !squares[sqRow][sqCol + 1].isMine() && squares[sqRow][sqCol + 1].getNeighborMineCount() == 0 && !squares[sqRow][sqCol + 1].isRevealed()) {
		    squareQueue.push(squares[sqRow][sqCol + 1]);

	     }//end if

		 //bottom right square check
		 if (sqRow + 1 >= 0 && sqRow + 1 < rows && sqCol + 1 >=0 && sqCol + 1 < cols && !squares[sqRow + 1][sqCol + 1].isMine() && squares[sqRow + 1][sqCol + 1].getNeighborMineCount() == 0 && !squares[sqRow + 1][sqCol + 1].isRevealed()) {
		    squareQueue.push(squares[sqRow + 1][sqCol + 1]);


	     }//end if


      }//end while

        return emptySquaresRevealed;


  }//method revealBlankBlock


  public void revealFinalBoard() {

     //this method is executed if a player hits a mine and loses
     for (int i = 0; i < squares.length; i++) {

        for (int j = 0; j < squares[i].length; j++) {

           if (!squares[i][j].isRevealed())
              squares[i][j].revealSquare();

	    }//end for

     }//end for

  }//method revealFinalBoard


  public void placeMine(int rowVal, int colVal) {

     //need to set the mine status of this square, either on or off
     squares[rowVal][colVal].setMineStatus();

     //next, I need to determine the number of mines correctly identified by player
     //for the purposes of determining if/when the game is over

     //if we have correctly identified a mine, then we increment the value.

     if (squares[rowVal][colVal].isMine() && squares[rowVal][colVal].getMineStatus()) {
        minesCorrectlyIndicated++;

        //indicate that one of this squarez mines has been identified
        //retrieve surrounding neighbors based on values of rowVal and colVal

		for (int r = rowVal - 1, rcount = 0; r <= rowVal + 1; r++, rcount++) {

		   for (int c = colVal - 1, ccount = 0; c <= colVal + 1; c++, ccount++) {

		      //do boundary checking here
		      if (r < 0 || r >= rows || c < 0 || c >= cols)
		         continue;

		      //if we hit the current square, then skip
		      if (r == rowVal && c == colVal)
		         continue;

              //identify a neighboring mine
		      squares[r][c].mineIdentified();

	       }//end for

	   }//end for


        if (gameMode.equals("c")) {

		   //construct sentence indicating that this square is not a mine
		   String newSentence = "M(" + rowVal + "," + colVal + ")";
		   Sentence s = new Sentence();
		   s.addSentence(newSentence);

		   //add to KB
		   re.addSentence(s);

        }//end if


     }//end if

     //if we unflag a square that actually is a mine (ie: the player screws up) then
     //we need to decrease this value

     if (squares[rowVal][colVal].isMine() && !squares[rowVal][colVal].getMineStatus())
        minesCorrectlyIndicated--;


  }//end if


   //the isSquareRevealed method is useful in the logical entailment mode of Minesweeper

   public boolean isSquareRevealed(int r, int c) {

      if (squares[r][c].isRevealed())
         return true;
      else
         return false;


   }//method isSquareRevealed


   //the isSquareMine method is used in random selection of a square by the computer to determine
   //if this square is has been marked as a mine. Note that it does not matter whether the square is
   //actually a mine or not; any square marked as a mine cannot be selected.

   public boolean isSquareMine(int r, int c) {

      if (squares[r][c].isMine())
         return true;
      else
         return false;

   }//end if


   public reasoningEngine getReasoningEngine() {

      return re;


   }//method getReasoningEngine


   public void printBoard() {

       // print the gameboard
	   System.out.println();

       System.out.println();

      //first print x-axis coord system
      for (int x = 0; x < cols; x++)
         System.out.print(x + " ");

      System.out.println();
      System.out.println();


	   for (int i = 0; i < rows; i++) {

	      for (int j = 0; j < cols; j++) {

             if (squares[i][j].getMineStatus()) {

                System.out.print("M ");

             } else if (!squares[i][j].isRevealed()) {

                System.out.print("S ");

		     } else {

   	         if (squares[i][j].isMine())
	               System.out.print("* ");
	            else
	               System.out.print(squares[i][j].getNeighborMineCount() + " ");

		    }//end if

	     }//end for

	     //print out y coord value
	     System.out.print("   " + i);

	     System.out.println();

      }//end for

       System.out.println();


  }//method printBoard


}//class gameBoard