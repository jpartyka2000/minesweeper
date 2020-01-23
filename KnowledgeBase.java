package Minesweeper;

import java.util.*;

class KnowledgeBase {


   //this will contain all of our facts, or sentences
   Sentence[] sent;
   int sentenceCount;


   public KnowledgeBase(int capacity) {

      sent = new Sentence[capacity];
      sentenceCount = 0;


   }//constructor KnowledgeBase


   public int getSentenceIndex(String indexSentence) {

       Sentence thisSentence = new Sentence();
	   String sentString = " ";


	   for (int s = 0; s < getSentenceCount(); s++) {

	      //retrieve sentence from knowledge base
	      thisSentence = sent[s];

	      //convert this sentence to string form
	      sentString = thisSentence.toString().trim();


	      //System.exit(0);

	      //if this sentence matches the pattern in our param, then this is the one to return
	      if (sentString.equals(indexSentence)) {

	         return s;

	      }//end if

	   }//end for

	  //if we get here, that means that our lookup failed
      return -1;

   }//method getSentenceIndex


   public void replaceSentence(String oldSentence, String newSentence) {

      int index = getSentenceIndex(oldSentence);

      //convert new sentence to a sentence object
      Sentence newSent = new Sentence();
      newSent.addSentence(newSentence);

      sent[index] = newSent;

   }//method replaceSentence


   public int getSentenceCount() {

      return sentenceCount;

   }//method getSentenceCount

   //this method returns the sentence specified by the user as a numeric index
   public Sentence getNextSentence(int index) {

      return sent[index];

   }//method getNextSentence


   //add a new sentence to the knowledge base

   public void addSentence(Sentence s) {


      sent[sentenceCount] = s;
      sentenceCount++;


   }//method addSentence


   public Sentence returnSentence() {

      //we need to go through all stored sentences in our knowledge base and return all symbols and connectives
      //as one very long sentence connected via conjunctions

      Sentence kbSentence = new Sentence();

      for (int s = 0; s < sentenceCount; s++) {

         //get all symbols and connectives from the current sentence
         Vector vl = sent[s].getSymbols();

	     //convert Vector an Enumeration
	     Enumeration e = vl.elements();

	     while (e.hasMoreElements()) {

            Object obj = e.nextElement();

            //append to sentence
            kbSentence.appendSentence(obj);

	     }//end while

	     //now, since the KB in sentence form is one long conjunction, I need to add a conjunction
         if (s < sentenceCount - 1)
            kbSentence.appendSentence(new Connective("&&"));

      }//end for

      return kbSentence;


  }//method returnSentence


  //I need this method as a means of looking up minesweeper rules from the knowledge base relating to our current
  //square

  public String lookupSentence(String pattern) {

     Sentence thisSentence = null;
     String sentString = null;

     for (int s = 0; s < getSentenceCount(); s++) {

        //retrieve sentence from knowledge base
        thisSentence = sent[s];

        //convert this sentence to string form
        sentString = thisSentence.toString();

        //if this sentence matches the pattern in our param, then this is the one to return
        if (sentString.indexOf(pattern) > -1)
           return sentString;

     }//end for

     //if we get here, that means that our lookup failed
     return null;

  }//method returnSentence


  public void printKnowledgeBase() {

       //this method prints out the contents of the KB.

     /* System.out.println("\nContents of KB: {");

      char comma = ' ';

      //print contents of queue from front to back
      for (int j = 0; j < sentenceCount; j++) {

         //get current sentence
         Sentence thisSentence = sent[j];

         //get Vector of symbols and connectives associated with this sentence
         Vector objList = thisSentence.getSymbols();

	     //convert to enumeration and then print out each individual symbol
	     Enumeration e = objList.elements();

	     while (e.hasMoreElements()) {

	        Object obj = e.nextElement();

	        if (obj instanceof Connective) {

		       System.out.print(((Connective)obj).getConnective());

		    }//end if

            if (obj instanceof Symbol) {

		       System.out.print(((Symbol)obj).getSymbol());

		    }//end if

	     }//end while

        //print demarcation line between sentences
        System.out.println("\n******************************");


      }//end for

      System.out.println("}\n");*/


   }//method printKnowledgeBase



}//class KnowledgeBase