package Minesweeper;


class Symbol {

   String symbString;
   boolean hasNotValue;
   boolean truthValue;


   public Symbol(String symStr, boolean notVal) {

      symbString = symStr;
      hasNotValue = notVal;

   }//constructor Symbol


   public String getSymbol() {

      return symbString;


   }//method getSymbol


   public boolean hasNot() {

      return hasNotValue;

   }//method hasNot


   public void setTruthValue(boolean truthiness) {

      truthValue = truthiness;

   }//method setTruthValue


   public boolean getTruthValue() {

      return truthValue;

   }//method setTruthValue



}//class Symbol