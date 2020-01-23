package Minesweeper;

import java.util.*;

class reasoningEngine {


   //num of rows in the current game
   private int rowNum;

   //num of colums in the current game
   private int colNum;

   //number of mines total in the current game
   private int numOfMines;

   //the knowledgeBase that the engine will refer to
   KnowledgeBase kb;

   //an advanced rules knowledge base that allows the agent to make selection and mine placement
   //decisions based on multiterm neighbor-related sentences
   KnowledgeBase advancedRules;

   //object that carries out entailment process
   TTEntail ttent;

   //we need a reference to the gameboard object so that we can determine if a particular square has been revealed
   //or not

   gameBoard gb;

   //an array of 9 separate queues that hold Square objects. Each queue is distinguished by the neighborMineCount of its squares
   //we need these to determine the next move for the computer to make. It is also part of a scheme to make a move without having to
   //use the entire KB, since this would take too long.

   Queue[] squareGroups;


   public reasoningEngine (int rows, int cols, int nom, gameBoard gboard) {

      rowNum = rows;
      colNum = cols;

      numOfMines = nom;


      //define a KB with a large capacity (WOW!)
      kb = new KnowledgeBase(500);

      //define advanced rules knowledge base
      advancedRules = new KnowledgeBase(100);

      ttent = new TTEntail();

      gb = gboard;

      squareGroups = new Queue[9];

      for (int q = 0; q < squareGroups.length; q++) {

         squareGroups[q] = new Queue(50);

      }//end for


   }//constructor reasoningEngine


   public String nextMove() {


      //the next move will be determined by the sentences in our knowledge base. If the KB cannot entail any alpha based
      //on what is already on the board, then we must do a random move

      Square thisSquare = null;
      int rowVal = 0;
      int colVal = 0;
      String pattern = null;
      String selectStr = null;
      String mineStr = null;
      //int sentCount = 0;

      for (int i = 0; i < squareGroups.length; i++) {

         //I need to look at the sentence and decide if I want to use this square information to make my next move. How do
         //I determine that? First, try to find the square with the lowest mine count value. Then for each of these squares
         //look at the surrounding squares to first try to determine if based on the KB for the 8 neighbors, if it is possible
         //to make a guaranteed move (selection or mine placement). If I go through all squares of the lowest group, then
         //go to the next highest group and repeat the process. If there are no moves to make, then do a random move. Also,
         //there will be many rules in the KB, so when focusing on a specific square, only grab those rules surrounding the square
         //with the assumption being that the selection of a particular square is dependent upon its neighbors.

         //do lookup of KB knowledge based on squareQueue. First we start with all 0 squares
         for (int q = 0; q < squareGroups[i].size(); q++) {


            //local kb variable
            String[][] kbSentence = new String[3][3];

            //grab next square object
            thisSquare = (Square)squareGroups[i].peek(q);

            //check to see if all neighbors of this square have been revealed. If so, then
			//continue on to the next square

			 if (!(thisSquare.isLogicallyUseful))
			    continue;

            //grab row and col values for this square
            rowVal = thisSquare.getRow();
            colVal = thisSquare.getCol();

            System.out.println("Currently looking at square: (" + rowVal + "," + colVal + ")");

            //retrieve surrounding neighbors based on values of rowVal and colVal
            for (int r = rowVal - 1, rcount = 0; r <= rowVal + 1; r++, rcount++) {

				for (int c = colVal - 1, ccount = 0; c <= colVal + 1; c++, ccount++) {

                  //do boundary checking here
                  if (r < 0 || r >= rowNum || c < 0 || c >= colNum)
                     continue;

                   //if we hit the current square, then skip
                   if (r == rowVal && c == colVal)
                      continue;

                   //construct pattern string based on these values
                   pattern = "(" + new Integer(r).toString() + "," + new Integer(c).toString() + ")";

                   //lookup sentence based on pattern string. We also include any unsuccessful lookups to help
                   //us in determining whether to make a selection or a mine placement
                   kbSentence[rcount][ccount] = kb.lookupSentence(pattern);


			    }//end for

		   }//end for

		   //so now we have all available from the neighbors of this square. Now we need to determine if we have enough
		   //information to make a guaranteed correct selection or mine detection. If our square is a 1, then we need
		   //info from 7 other squares to make a mine detection, or we need a single mine detection rule to determine if we
		   //can make a selection on this square. Since the latter is easier (since it does not require us to look at nearly
		   //all other neighbors then we will start with this. We can only make a selection when considering 0 squares

           if ((selectStr = selectionMade(kbSentence, thisSquare.getNeighborMineCount(), thisSquare, q)) != null) {

              //return a string indicating that a selection was made and the square that was selected.
              return selectStr;

	       }//end if


	       //try a mine placement

           if ((mineStr = minePlaced(kbSentence, thisSquare.getNeighborMineCount(), thisSquare, q)) != null && thisSquare.getNeighborMineCount() > 0) {

              //return a string indicating that a selection was made and the square that was selected.
              return mineStr;

	       }//end if

           //System.out.println("Select str is: " + selectStr);

	    }//end for

     }//end for

      //if we get down here, then that means that we could not entail anything definite from the knowledge base, so we have
      //to make a global random selection

      return "gr";

   }//method nextMove


   public String selectionMade(String[][] rules, int neighborMines, Square thisSquare, int peekIndex) {

      //first convert rules into a new KB for use in the entailment process

      KnowledgeBase localKB = new KnowledgeBase(8);
      Sentence thisSentence = null;
      String[] alphaSentences = new String[8];
      int alphaCount = 0;
      int mineCount = 0;

      int rowVal = thisSquare.getRow();
      int colVal = thisSquare.getCol();

      //we must also determine our alpha sentence. Our alpha in the case of selection is one of the sentences that
      //is null

      //add each string rule to the local KB
      for (int k = 0, rv = rowVal - 1; k < rules.length; k++, rv++) {

	     for (int j = 0, cv = colVal - 1; j < rules[k].length; j++, cv++)  {

            if (rules[k][j] != null) {

               thisSentence = new Sentence();
               thisSentence.addSentence(rules[k][j]);
               localKB.addSentence(thisSentence);

		   } else {

			   //first, we have to check that we are not actually referring
			   //to the current square

			   if (!(k == 1 && j == 1) && rv >= 0 && rv < rowNum && cv >= 0 && cv < colNum) {

			      //indicate that it is one of the nulls, and thus, one of our potential alpha dogs
			      //construct the alpha sentence here

                  alphaSentences[alphaCount] = "!M(" + rv + "," + cv + ")";
                  alphaCount++;

		      }//end if

	       }//end if

	     }//end for

     }//end for

    //localKB.printKnowledgeBase();

    //for (int j = 0; j < alphaSentences.length; j++)
       //System.out.println(alphaSentences[j]);


     //if the alphaSentences array is empty, then that means that all neighbor squares around the current square
     //have been revealed. This means that there is no selection to make.

     if (alphaIsNull(alphaSentences)) {

      //if alpha is null, then that means that all squares bordering the current one have been revealed. Thus, subsequent
      //searches for a square to use during the next move cogitation will go through this one, which is pointless. So
      //remove this from its appropriate queue

      thisSquare.isLogicallyUseful = false;

      //int neighborMineCnt = thisSquare.getNeighborMineCount();

      //System.out.println("Deleting square from queue: " + thisSquare.getRow() + "," + thisSquare.getCol());

      //squareGroups[neighborMineCnt].deleteObj(peekIndex);

      return null;

     }//end if


     //now we have our alphas and our local KB created. Now we must select an alpha to be passed to the entailment algorithm
     Random alphaGen = new Random();

     int alphaChoice = alphaGen.nextInt(alphaCount);

     //now we call the entailment algorithm, which will tell us if given the local KB for the current square and our alpha
     //sentence, if our move is valid


     if (ttent.TTEntailStart(localKB, alphaSentences[alphaChoice])) {

        //do the mine check if necessary to see if this entailment is indeed valid.
        //If so, then this will be the computer's next move.

        //System.out.println("it can be entailed!");

        boolean marker = false;

        if (thisSquare.getNeighborMineCount() == 0)
           marker = true;

        for (int m = 0; m < localKB.getSentenceCount() || marker == true; m++) {

           Sentence snt = localKB.getNextSentence(m);

           if (marker == false && snt.toString().indexOf("!") == -1) {
              mineCount++;
              //System.out.println("Mine count incremented");

	       }//end if

           //if mineCount is equal to the neighborMineCount of the current square, then entailment was valid
           //and we can guarantee a selection move for the computer

           if (mineCount == neighborMines) {

              //we have discovered a mine, so the entailment was valid and we can guarantee a move
              //construct the string for the next computer move and return it

              System.out.println("s," + alphaSentences[alphaChoice].substring(3,alphaSentences[alphaChoice].length() - 1));


              return ("s," + alphaSentences[alphaChoice].substring(3,alphaSentences[alphaChoice].length() - 1));

	      }//end if

	      marker = false;

	    }//end for


     }//end if

       System.out.println("No guaranteed selection was possible");


      //if we get down here, then no guaranteed selection was possible
      return null;

   }//method selectionMade


   public String minePlaced(String[][] rules, int neighborMines, Square thisSquare, int peekIndex) {

      //first convert rules into a new KB for use in the entailment process

      KnowledgeBase localKB = new KnowledgeBase(8);
      Sentence thisSentence = null;
      String[] alphaSentences = new String[8];
      int alphaCount = 0;
      int mineCount = 0;

      int rowVal = thisSquare.getRow();
      int colVal = thisSquare.getCol();

      //we must also determine our alpha sentence. Our alpha in the case of selection is one of the sentences that
      //is null

      //add each string rule to the local KB
      for (int k = 0, rv = rowVal - 1; k < rules.length; k++, rv++) {

	     for (int j = 0, cv = colVal - 1; j < rules[k].length; j++, cv++)  {

            if (rules[k][j] != null) {

               thisSentence = new Sentence();
               thisSentence.addSentence(rules[k][j]);
               localKB.addSentence(thisSentence);

		   } else {

			   //first, we have to check that we are not actually referring
			   //to the current square

			   if (!(k == 1 && j == 1) && rv >= 0 && rv < rowNum && cv >= 0 && cv < colNum) {

			      //indicate that it is one of the nulls, and thus, one of our potential alpha dogs
			      //construct the alpha sentence here -- our alpha is a sentence that a square is a mine

                  alphaSentences[alphaCount] = "M(" + rv + "," + cv + ")";
                  alphaCount++;

		      }//end if

	       }//end if

	     }//end for

     }//end for

    //localKB.printKnowledgeBase();

    //for (int j = 0; j < alphaSentences.length; j++)
    //   System.out.println(alphaSentences[j]);


     //if the alphaSentences array is empty in the context of this method, then that means that
     //the number of revealed squares + the number of correctly indicated mines has indicated the identity
     //of all neighbor squares..Thus, we cannot place a mine.

     if (alphaIsNull(alphaSentences)) {

      //if alpha is null, then that means that all squares bordering the current one have been revealed. Thus, subsequent
      //searches for a square to use during the next move cogitation will go through this one, which is pointless. Set its
      //logical usefullness property to false so that it will not be checked again

       thisSquare.isLogicallyUseful = false;


      return null;

     }//end if



     //now we have our alphas and our local KB created. Now we must select an alpha to be passed to the entailment algorithm
     Random alphaGen = new Random();

     int alphaChoice = alphaGen.nextInt(alphaCount);

     //now we call the entailment algorithm, which will tell us if given the local KB for the current square and our alpha
     //sentence, if our move is valid

     int squareCount = 0;
     int borderingSquares = 0;

     if (ttent.TTEntailStart(localKB, alphaSentences[alphaChoice])) {

        //do the mine check if necessary to see if this entailment is indeed valid.
        //If so, then this will be the computer's next move.

        //System.out.println("it can be entailed!");

        //as long as there are mines still to place without uncertainty, then we are fine
        //this covers situations where no mine has been associated with a square with multiple
        //uncovered squares still to be opened; no chance mine placement will be made

         //corner square check
         if ((thisSquare.getCol() == 0 || thisSquare.getCol() == colNum - 1) && (thisSquare.getRow() == 0 || thisSquare.getRow() == rowNum - 1))
            borderingSquares = 3;
         else if (thisSquare.getCol() == 0 || thisSquare.getCol() == colNum - 1 || thisSquare.getRow() == 0 || thisSquare.getRow() == rowNum - 1)
		    borderingSquares = 5;
		 else
		    borderingSquares = 8;


        for (int m = 0; m < localKB.getSentenceCount(); m++) {

		   Sentence snt = localKB.getNextSentence(m);

		   if (snt.toString().indexOf("!") > -1) {
		       squareCount++;

		       //System.out.println("nonmine count incremented");

		   }//end if

		   //if squareCount is equal to borderingSquares - neighborSquareCount for this square, then entailment was valid
		   //and we can guarantee a mine placement for the computer

		   if (squareCount == (borderingSquares - neighborMines)) {

		      //we have discovered a mine, so the entailment was valid and we can guarantee a move
		      //construct the string for the next computer move and return it

		      System.out.println("m," + alphaSentences[alphaChoice].substring(2,alphaSentences[alphaChoice].length() - 1));

		      return ("m," + alphaSentences[alphaChoice].substring(2,alphaSentences[alphaChoice].length() - 1));


	       }//end if

       }//end for


     }//end if

       System.out.println("No guaranteed mine placement was possible\n");

       //try to use advanced rules to determine if we can use the alpha sentences
       //to determine if a square selection is possible based on the general location of a mine
       //or mines based on rules that take into account more than 1 square

       String advMove = null;

       int mineCnt = thisSquare.getMinesIdentified();

       if ((advMove = advancedRulesCheck(thisSquare, alphaSentences, mineCnt)) != null) {

          return advMove;

       }//end if


      //if we get down here, then no guaranteed selection was possible
      return null;

   }//method minePlaced


   public String advancedRulesCheck(Square thisSquare, String[] alphaSentences, int mineCount) {

      //we are always going to have alpha sentences that are guesses as to where mines are,
      //as opposed to guesses as to where mines are not. From the sentence, and based
      //on the mineCount param and the getNeighborMineCount of thisSquare, I can determine
      //if a selection or mine placement is indeed possible

      if (mineCount + 1 < thisSquare.getNeighborMineCount())
         return null;

      //construct an advanced rules lookup pattern consisting of all alpha sentences
	  String advPattern = " ";
	  String orStr = " ";

	  String[] advancedAlphaSentences;

	  for (int x = 0; x < alphaSentences.length; x++) {

         if (alphaSentences[x] != null) {
            advPattern += orStr + alphaSentences[x];
            orStr = " || ";

	     }//end if

      }//end for

      //get rid of leading and trailing whitespace
      advPattern = advPattern.trim();

      //in the situation where alpha is bigger than KB sentence, go through all advanced rules and
      //see if that advanced rule is contained within alpha. If so,then we can try to apply the rule

      boolean kbContained = false;
      String kbRule = " ";

      for (int s = 0; s < advancedRules.getSentenceCount(); s++) {

		  kbRule = advancedRules.getNextSentence(s).toString();

         if (advPattern.indexOf(kbRule) > -1) {

	        //then we have located our KB sentence within the alpha
            kbContained = true;
            break;

	     }//end if

      }//end for

      //if kb is bigger than alpha...

      if (!kbContained)
         kbRule = advancedRules.lookupSentence(advPattern);

      System.out.println("alpha sentence is: " + advPattern);
	  System.out.println("KB sentence is: " + kbRule);

	 // System.exit(0);


	 if (kbRule == null && !kbContained) {

         //our KB does not have any record of this set of alpha sentences. Add it as a rule

	     System.out.println("Adding alpha sentences to advancedRules KB as the rule: " + advPattern);
	     System.out.println();

	     String newSentence = advPattern;
	     Sentence cs = new Sentence();
	     cs.addSentence(newSentence);

	     //add to KB
	     advancedRules.addSentence(cs);

	  } else {

	     //here we will determine if it is possible to derive a guaranteed selection or mine placement based on
	     //existing rule in advancedRules Knowledge Base
         //if another square has a rule of the form: A || B, and this square covers both, then it can treat
         //the sentence as one mine. If there are other unrevealed squares around this square, and the mineCount
         //is equal to the neighborMineCount, then we can do a selection on the other unrevealed squares. If
         //the mineCount is not equal to neighborMineCount, then we can place a mine on these other unrevealed
         //squares. We want the alphaString to be equal to the KB string, but this probably will not be the case
         //in this situation, if the KB string is larger than the alpha string, then the KB string needs
         //to be replaced by the alpha string, since it is more precise. If the alpha string is bigger than
         //the KB string, then the alpha string will be replaced by the smaller KB string. Before doing the replace,
         //we need to get the extraneous terms and store them -- we will add these to the regular KB and
         //also use remember these as possible moves.

          //we check to see if our advPattern and our KB rule are exactly the same
		  //if so, then we cannot infer anything and must return a null

		  if (kbRule != null && kbRule.equals(advPattern)) {

		     System.out.println("Advanced rule checking cannot infer a guaranteed move.\n");
		     return null;

		  }//end if


         if (kbRule != null && kbContained)  {

            //first, determine if counting the kb sentence as a mine + the mineCount param is enough
            //to satisfy all surrounding mines for the square. If so the proceed below; if not,
            //then nothing can be inferred, and we exit

            if (mineCount + 1 == thisSquare.getNeighborMineCount()) {

	           //I need to extract the atomic clauses that are not part of the KB and first add them
	           //to the general KB. After this, ,I can return that alpha as my next move
	           //. I could use the original form of alphaSentences as a string array
               //these will be the sentences which we can select randomly

               for (int alpha = 0; alpha < alphaSentences.length; alpha++) {

                  if (kbRule.indexOf(alphaSentences[alpha]) == -1) {

                     //first add this alpha to the general knowledge base
                     Sentence newSent = new Sentence();
					 newSent.addSentence("!" + alphaSentences[alpha]);
	                 kb.addSentence(newSent);

	                 System.out.println("Advanced rule checking has determined a move! ");

	                 System.out.println("s," + alphaSentences[alpha].substring(2,alphaSentences[alpha].length() - 1));

	                 //we need to determine if this selection is valid.
					 //I need to extract the row and col val of the selected sentence and determine
					 //if the square being referenced has already been revealed

					 StringTokenizer strTok = new StringTokenizer(alphaSentences[alpha].substring(2,alphaSentences[alpha].length() - 1), ",");
					 int c1 = 0;
					 int c2 = 0;

					 while (strTok.hasMoreTokens()) {

					    c1 = Integer.parseInt(strTok.nextToken());
					    c2 = Integer.parseInt(strTok.nextToken());

					 }//end while

					 if (gb.isSquareRevealed(c1, c2)) {

					    System.out.println("This square has already been revealed!\n");
					    return null;

					 } else {

					    //now return this unit clause as our next move
	                    return ("s," + alphaSentences[alpha].substring(2,alphaSentences[alpha].length() - 1));

		              }//end if


			      }//end if

		       }//end for


		    } else {

               System.out.println("Advanced rule checking cannot infer a guaranteed move.");
			   return null;

		    }//end if


	     }//end if




         //here, our alpha sentence is actually more precise than our KB sentence. we need to find all extraneous
         //clauses in our KB, add each one to the general rules KB, replace the KB rule with the alpha sentence,
         //and finally return our move

	     if (kbRule != null && !kbContained) {

            String extraClause = " ";
            //int clauseCount = 0;
            String thisString = null;

            String newKBRule = " ";
            int clauseMarker = 0;
            String orString = " ";

            StringTokenizer st = new StringTokenizer(kbRule, " |", false);

            while (st.hasMoreTokens()) {

               thisString = st.nextToken();

              if (advPattern.indexOf(thisString) == -1) {

			      //we have identified one of the superfluous kb clauses. Add it to the general KB
			      //and then add it to the extraClauses array, where it may be selected randomly
                 if (clauseMarker == 0) {

  			         //first add this alpha to the general knowledge base
				     Sentence newSent = new Sentence();
				     newSent.addSentence("!" + thisString);
	                 kb.addSentence(newSent);

	                 extraClause = thisString;
	                // clauseCount++;
	                clauseMarker = 1;

			    }//end if


		      }//end if

		      //add to new KB rule to be submitted
			  if (clauseMarker != 1) {
			     newKBRule += orString + thisString;
			     orString = " || ";
		      }

              if (clauseMarker == 1)
                 clauseMarker = 2;


		    }//end while

		    //trim leading whitespace from newKBRule
		    newKBRule = newKBRule.trim();

		    //replace KB sentence with alpha sentence, since it is more precise
            //advancedRules.replaceSentence(kbRule, advPattern);
            advancedRules.replaceSentence(kbRule, newKBRule);

		    //finally, choose randomly among the extra clauses for the next move
		    Random clauseGen = new Random();

		    //int sClause = clauseGen.nextInt(clauseCount);

            System.out.println("Advanced rule checking has determined a move! ");

            //System.out.println ("Extra Clause value is: " + extraClause);

            //System.out.println("s," + extraClauses[sClause].substring(2,extraClauses[sClause].length() - 1));
            System.out.println("s," + extraClause.substring(2,extraClause.length() - 1));

            //we need to determine if this selection is valid.
            //I need to extract the row and col val of the selected sentence and determine
            //if the square being referenced has already been revealed

            //StringTokenizer strTok = new StringTokenizer(extraClauses[sClause].substring(2,extraClauses[sClause].length() - 1), ",");
            StringTokenizer strTok = new StringTokenizer(extraClause.substring(2,extraClause.length() - 1), ",");
            int c1 = 0;
            int c2 = 0;

            while (strTok.hasMoreTokens()) {

			   c1 = Integer.parseInt(strTok.nextToken());
			   c2 = Integer.parseInt(strTok.nextToken());

	        }//end while

	        if (gb.isSquareRevealed(c1, c2)) {

               System.out.println("This square has already been revealed!\n");
               return null;

		    } else {

	           //now return this unit clause as our next move
	           //return ("s," + extraClauses[sClause].substring(2,extraClauses[sClause].length() - 1));
	           return ("s," + extraClause.substring(2,extraClause.length() - 1));

		    }//end if

	     }//end if


      }//end if

      return null;

   }//method advancedRules


   private boolean alphaIsNull(String[] alphas) {

      for (int i = 0; i < alphas.length; i++) {

         if (alphas[i] != null)
            return false;

      }//end for

      //if we get here, then alphas really was null throughout
      return true;


   }//method isAlphaNull



   public KnowledgeBase getKnowledgeBase() {

      return kb;

   }//method getKnowledgeBase


   //add a new sentence to the knowledge base

   public void addSentence(Sentence s) {

      kb.addSentence(s);

   }//method addSentence

   //when a square is selected, add that square to the appropriate square Queue to make it easier for the computer to
   //make its next move

   public void addToQueue(Square s) {

      int queueNum = s.getNeighborMineCount();

      squareGroups[queueNum].push(s);

   }//method addToQueue



}//class reasoningEngine



