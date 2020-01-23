package Minesweeper;

import java.util.*;


public class Square {

   //location of this square

   private int rowNum;
   private int colNum;

   //boolean determining whether this square is a mine or not
   private boolean isMine;

   //boolean indicating whether this square has been indicated as a mine. Initially false
   private boolean mineIndicated;

   //int value indicating number of mines around this square
   private int neighborMines;

   //boolean indicating whether or not this mine has been revealed or not. Initially false
   boolean hasBeenRevealed;

   //indicates the number of surrounding mines for this square that have been identified
   int minesRevealed;

   //boolean indicating whether or not this mine is still needed for the purposes of propositional entailment
   //Initially assumed to be true

   boolean isLogicallyUseful;



   public Square(int row, int col) {

      rowNum = row;
      colNum = col;

     //set various default values
     isMine = false;
     neighborMines = 0;
     hasBeenRevealed = false;
     isLogicallyUseful = true;

     minesRevealed = 0;


   }//constructor Square


   public int getRow() {

      return rowNum;

   }//method getRow


   public int getCol() {

      return colNum;

   }//method getRow


   public boolean isMine() {

      return isMine;

   }//method isMine

   public void setMine() {

      isMine = true;

   }//method setMine

   public void incrementMineCount() {

      neighborMines++;

   }//method incrementMineCount

   public int getNeighborMineCount() {

      return neighborMines;

   }//method getNeighborMineCount


   public boolean isRevealed() {

      return hasBeenRevealed;

   }//method isRevealed

   public void revealSquare() {

      hasBeenRevealed = true;

   }//method revealSquare


   public boolean isUseful() {

      return isLogicallyUseful;

   }//method isUseful


   public void mineIdentified() {

      minesRevealed++;

   }//end if

   public int getMinesIdentified() {

      return minesRevealed;


   }//method getMinesIdentified


     public boolean getMineStatus() {


      return mineIndicated;


   }//method getMineStatus


   public void setMineStatus() {

      //it is possible to toggle the mine status for a particular square
      mineIndicated = !mineIndicated;

   }//method setMineStatus


}//class Square