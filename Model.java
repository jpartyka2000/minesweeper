package Minesweeper;

import java.util.*;


class Model {

   //we need the knowledge base to determine if this is an appropriate model. It is in sentence form
   Sentence kb;

   //we also need all of the symbols and their respective truth values
   HashMap symbolTable;

   //we have a parallel array that contains the truth value for each corresponding symbol in the symbolTable
   //boolean[] symbolValues;

   //the number of symbols that need to be assigned values for any given model
   //int numberOfSymbols;


   public Model(Sentence know) {

      kb = know;

      symbolTable = new HashMap();
     // numberOfSymbols = symbNum;

   }//constructor Model


   public Model extend(Symbol thisSymbol, boolean value) {

      //here, we need to assign a truth value to a symbol, thus creating a new model
      thisSymbol.setTruthValue(value);


      //add this symbol object to the data structure holding the symbol objects, which is a HashMap
      //since I am using a HashMap, I need to use its string, so that it could represent the key of the
      //symbol. However, if we have already added this object to the HashMap, but are assigning it a different
      //truth value, then we need to remove it from the HashMap first, then add it. Order of the objects
      //in the map does not matter

      String thisSymbolKey = thisSymbol.getSymbol();

      if (symbolTable.get(thisSymbolKey) != null) {

	     //this symbol is already in our HashMap. So we need to remove it first.
	     symbolTable.remove(thisSymbolKey);

      }//end if

      //add thisSymbol to symbol table, with the new truth value
      symbolTable.put(thisSymbolKey, thisSymbol);

      //return the new model
      return this;

   }//method extend


   //this method determines whether our KB entails alpha

   public boolean modelCheck(KnowledgeBase kb) {

     // System.out.println("*****ATTEMPTING TO VERIFY THE TRUTH OF THE KB IN THIS MODEL!!****************");

      //we will retrieve the sentences from the knowledge base one at a time, and evaluate them individually
      //after evaluating each individual one, we will make it part of a conjunction. The results of all sentences
      //will be tied together in this conjunction.

      for (int j = 0; j < kb.getSentenceCount(); j++) {

         Sentence s = kb.getNextSentence(j);

         //evaluate the truth of this sentence. We do not actually create a sentence with conjunctions. Instead, we can
         //mimic this by the very fact that even one false value returned means that the KB will be false.

         if (!evalSentence(s)) {

            //System.out.println("Knowledge Base is false!");
            //System.out.println("********************");
            return false;

	     }//end if

	    // System.out.println("********************");

      }//end if

      //if we get down here, then the KB is true

      return true;

   }//method modelCheck


   public boolean evalSentence(Sentence thisSentence)  {

      //thisSentence.printSentence();

      //this method evaluates the truth of any sentence
      //I will get the symbols of the sentence, then I will go through them one at a time and evaluate each symbol encountered
      //if a connective is encountered, that will signal whether I need to execute AND or OR evaluation.
      //every time I have 2 symbols evaluated and a connective encountered, then I will evaluate the truth value, since
      //this KB only has AND and OR and NOT.

      boolean[] sentenceValues = new boolean[2];
      int sentenceValuesCount = 0;
      String currConnective = null;


      //start by retrieving the symbols of thisSentence
      Object[] sentSymbols = thisSentence.getSymbols().toArray();


      for (int i = 0; i < sentSymbols.length; i++) {


         //if we extract a symbol, then evaluate its truth value according to the model contained in the symbolTable
         //then assign the truth value to sentenceValues
         Object obj = sentSymbols[i];



         if (obj instanceof Symbol) {

            String symString = ((Symbol)obj).getSymbol();

            //System.out.println("Curr Symbol is: " + symString);

            //check for the presence of a NOT (!)
            if (symString.indexOf("!") > -1) {

              // System.out.println("This symbol has a NOT");
               symString = symString.substring(1, 3);

		    }//end if


            //use symbolTable to determine if the symbol indicated by symString is true or false in this model
            if (symbolTable.containsKey(symString)) {

               boolean symTruth = ((Symbol)symbolTable.get(symString)).getTruthValue();

               //if this symbol has a not value associated with it, then we need to apply this
               if (((Symbol)obj).hasNot()) {

                  symTruth = !symTruth;

		       }//end if

		       //System.out.println("Curr Symbol truth value is: " + symTruth);

               //assign truth value to sentenceValues array
               sentenceValues[sentenceValuesCount] = symTruth;

               //increment sentenceValuesCount by one
               sentenceValuesCount++;

		    }//end if


	     }//end if


	     if (obj instanceof Connective) {

            currConnective = ((Connective)obj).getConnective();

           // System.out.println("Current connective is: " + currConnective);

         }//end if


         //evaluate intermediate boolean expression if we have obtained 2 clauses and a connective flag

		 if (sentenceValuesCount == 2) {

		    boolean result = false;

		    if (currConnective.equals("||")) {

		       result = sentenceValues[0] || sentenceValues[1];

		     //  System.out.println("Result of: " + sentenceValues[0] + "||" + sentenceValues[1] + "=" + result);

		    }//end if

		    if (currConnective.equals("&&")) {

		 	   result = sentenceValues[0] && sentenceValues[1];

		 	  // System.out.println("Result of: " + sentenceValues[0] + "&&" + sentenceValues[1] + "=" + result);

		 	}//end if

		    //assign result variable to sentenceValues[0]
		    sentenceValues[0] = result;

		    //value incremented as a result of assignment of boolean operation result just performed
		    sentenceValuesCount = 1;

		    //reset currConnective
		    currConnective = null;

	     }//end if


      }//end for

      //sentenceValues[0] should contain the final truth value of the sentence. It will be returned
      return sentenceValues[0];


   }//method evalSentence


   public void printModel() {

	   //obtain all values of HashMap as a Collections interface reference
	   Collection c = symbolTable.values();

	   Iterator i = c.iterator();
       int count = 0;

	   while (i.hasNext()) {

          Symbol sym = (Symbol)i.next();

          System.out.println("Symbol is: " + sym.getSymbol());
          System.out.println("Truth value of this symbol is: " + sym.getTruthValue());
          System.out.println("************************************");


       }//end while



   }//method printModel


}//class Model