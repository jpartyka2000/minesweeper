package Minesweeper;

import java.util.*;

class TTEntail {


   private symbolQueue createSymbolList(Vector kbSymbols, Vector querySymbols) {


      //this method will create a Vector that is the union of the 2 input Vectors
      //the end result is a symbolQueue object that holds all symbols from the union between the
      //querySymbols and the knowledge base symbols

      symbolQueue finalSymbList = new symbolQueue(50);

      Enumeration e = kbSymbols.elements();

      while (e.hasMoreElements()) {

         //there will be repeated propositions in our facts, so account for this
         Object obj = e.nextElement();

         //check to see if this object is a symbol or a connective. If it is a connective, we ignore it

         if (obj instanceof Symbol) {

            //check to see if this object is already in our queue. Also, to account for the situation where
            //we have the same prop symbol, but with a negation added, then strip out the negation

            Symbol thisSymbol = (Symbol)obj;
            Symbol replSymbol = null;

            //if symbol has a not, then we need to represent the symbol without the not (!)
            if (thisSymbol.hasNot()) {

               String symbStr = thisSymbol.getSymbol();

               replSymbol = new Symbol(symbStr.substring(1,3), false);

               thisSymbol = replSymbol;

		    }//end if

            //if our symbol is not in the symbol list, then add it

            if (!finalSymbList.inQueue(thisSymbol)) {

               finalSymbList.push(thisSymbol);

	        }//end if


	     }//end if

      }//end while

      //now we need to add the symbols from our alpha sentence

      Enumeration f = querySymbols.elements();

      while (f.hasMoreElements()) {

         //there will be repeated propositions in our facts, so account for this
         Object obj = f.nextElement();

         //check to see if this object is a symbol or a connective. If it is a connective, we ignore it

         if (obj instanceof Symbol) {

            //check to see if this object is already in our queue. Also, to account for the situation where
            //we have the same prop symbol, but with a negation added, then strip out the negation

            Symbol thisSymbol = (Symbol)obj;
            Symbol replSymbol = null;

            //if symbol has a not, then we need to represent the symbol without the not (!)
            if (thisSymbol.hasNot()) {

               String symbStr = thisSymbol.getSymbol();

               replSymbol = new Symbol(symbStr.substring(1,3), false);

               thisSymbol = replSymbol;

		    }//end if

            //if our symbol is not in the symbol list, then add it

            if (!finalSymbList.inQueue(thisSymbol)) {

               finalSymbList.push(thisSymbol);

	        }//end if


	     }//end if

      }//end while


      return finalSymbList;

   }//method createSymbolList


   public boolean TTEntailStart(KnowledgeBase kb, String alpha) {


        //get knowledge base as a sentence in CNF
        Sentence kbSentence = kb.returnSentence();

        //make sentence out of our query, represented by alpha
		Sentence querySentence = new Sentence();
		querySentence.addSentence(alpha);

        //we want to grab all of the symbols in the knowledge base. We do not distinguish between
        //a proposition and its negated form

        Vector kbSymbols = kbSentence.getSymbols();

        //get the query sentence symbols
        Vector querySymbols = querySentence.getSymbols();

        //get the union of the symbols so that we have all possible symbols between our KB and alpha
        symbolQueue symbolList = createSymbolList(kbSymbols, querySymbols);

        //I need to convert the Queue into an array of symbols for use in the Model class
        //Symbol[] symbolArray = symbolList.toArray();


        return TTCheckAll(kb, querySentence, symbolList, new Model(kbSentence), 0);


   }//method TTEntails


   public boolean TTCheckAll(KnowledgeBase kb, Sentence alpha, symbolQueue symbolList, Model m, int symbolCounter) {

      //kb.printKnowledgeBase();

      if (symbolCounter == symbolList.size()) {

		  //print out model
		  //m.printModel();
		 // System.exit(0);

		if (m.modelCheck(kb)) {

            return (m.evalSentence(alpha));

	     } else {

			return false;

	     }//end if

      } else {

        //get the next symbol from the symbolList
        Symbol first = (Symbol)symbolList.peek(symbolCounter);

        //increase symbolCounter by one
        symbolCounter++;

        //the rest of the symbols are contained within the Queue
       // System.out.println("Assigning true value...");

        boolean firstBranch = TTCheckAll (kb, alpha, symbolList, m.extend(first, true), symbolCounter);
        boolean secondBranch = false;


        //System.out.println("Assigning false value...");

        if (!firstBranch) {
           secondBranch = TTCheckAll (kb, alpha, symbolList, m.extend(first, false), symbolCounter);

        } else {

		  return true;

	    }//end if


        return secondBranch;


      }//end if


   }//method TTCheckAll


}//class TTEntail