package Minesweeper;

import java.util.*;

class Sentence {


   //a sentence is composed of a series of propositions, which may have an optional
   //unary NOT preceding it, along with connectives. I will use a Vector to represent the
   //sentence

   Vector sentenceParts;

   public Sentence() {

      sentenceParts = new Vector();

   }//constructor Sentence


   public Vector getSymbols() {

      return sentenceParts;

   }//method getSymbols


   //use this method to create a sentence out of all facts in the KB. We can add a single proposition or connective

   public void appendSentence(Object obj) {

      //we have already typecast the object, whether it is a symbol or a connective, to one of the Object class
      //we just need to add it to the Vector that holds the constituent parts of the sentence

      sentenceParts.add(obj);

   }//method appendSentence

   //this method helps up implement sentence lookup, which is needed to determine the next best move in
   //computerized minesweeper

   public String toString() {

      Enumeration e = sentenceParts.elements();

      String finalSentence = " ";
      String space = "";

      while (e.hasMoreElements()) {

		  finalSentence += space;

         Object obj = e.nextElement();

         if (obj instanceof Symbol) {

            Symbol symb = (Symbol)obj;

            //convert the symbol into a string and return it
            finalSentence += symb.getSymbol();

	     }//end if

	      if (obj instanceof Connective) {

		     Connective c = (Connective)obj;

		     //convert the connective into a string and return it
		     finalSentence += c.getConnective();

	     }//end if

         space = " ";

      }//end while

      return finalSentence.trim();

   }//method asString


   //this method is used to add a sentence with a string argument. It is intended to store the initial facts of the
   //KB


   public void addSentence(String sent) {


      //we need to parse the argument sent and convert each constituent part into Symbol and Connective objects

      StringTokenizer strtok = new StringTokenizer(sent, " ");
      boolean notSymbPresent = false;

      //execute while loop that parses out new token as long as they exist
      while (strtok.hasMoreTokens()) {


         String nextStr = strtok.nextToken();

         if (nextStr.equals("&&") || nextStr.equals("||")) {

            //we have a connective, so we need to construct a new object
            Connective newCon = new Connective(nextStr);

            //add this to Vector sentenceParts
            sentenceParts.add(newCon);


	     } else {

	        //we have a symbol, that may or may not be preceded by a NOT (!). Determine this first.
	        if (nextStr.indexOf("!") > -1)
	           notSymbPresent = true;


	        Symbol newSymb = new Symbol(nextStr, notSymbPresent);

	        //add this to Vector sentenceParts
	        sentenceParts.add(newSymb);


	     }//end if

      }//end while

   }//method addSentence


   public void printSentence() {

      //the purpose of this method is to print out the sentence..Useful for debugging purposes
      //we need to grab the Vector and print out its contents

      Enumeration e = sentenceParts.elements();

      while (e.hasMoreElements()) {

         //we could have either a connective or a symbol object
         Object obj = e.nextElement();

         if (obj instanceof Connective) {

		    System.out.print(((Connective)obj).getConnective() + " ");

	     }//end if


	     if (obj instanceof Symbol) {

		    System.out.print(((Symbol)obj).getSymbol() + " ");

		 }//end if


      }//end while


   }//method printSentence


}//class Sentence