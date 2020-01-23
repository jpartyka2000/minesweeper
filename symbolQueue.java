package Minesweeper;

class symbolQueue {


   private int count = 0;   //number of items on queue
   private Object[] queueItems;
   private int capacity;    //max number of items queue can hold
   private int capacityIncrement = 5;  //amount to expand queue capacity if max capacity is reached
   private int front = 0;
   private int back = 0;


  public symbolQueue(int capac) {

      //first initialize capacity of queue
      capacity = capac;

	  //now initialize array of objects which holds all of the queue's inventory
	  queueItems = new Object[capacity];

   }//constructor queue


   public boolean isEmpty() {

      return (count == 0);

   }//method isEmpty


   public void push(Object o) {

      //first check to see if the queue has maxed out its space
      if (count == queueItems.length) {

	     //if this is the case, then we need to expand queueItems by capacityIncrement
	     Object[] tempArray = new Object[count + capacityIncrement];

	    //copy the contents of queueItems to tempArray. To do this, we need to examine
	    //the values of front and back, and reorder the items in queueItems such that the meanings
	    //of front and back are conserved.

	    for (int i = 0; i < count; i++) {

		   tempArray[i] = queueItems[(i + front) % count];

	    }//end for

	    //finally, assign tempArray to be the new queueItems
	    queueItems = tempArray;

	    //front will now point to 0, back will point to the last nonnull item
	    front = 0;
	    back = count;

	    //change value of capacity
	    capacity += capacityIncrement;

      }//end if

      //push new object onto back of queue
      queueItems[back] = o;

      //increase count by one
      count++;

      //set back to be the most recent item inserted into the queue
      back = (back + 1) % capacity;

   }//method push


   public Object peek(int index) {

      //this method returns a copy of the next item to be returned from the queue
      //without eliminating it. The next item is referred to by index

      if (isEmpty()) {

	    System.out.println("Queue is empty.");
        return null;

     } else {

		//remove object being pointed to by front
		return queueItems[index];


     }//end if

   }//method peek


   public Object pop() {

      Object delObject;

      //we want to remove the item from the array being referenced by the instance variable front
      //but first, lets see if the queue has at least 1 item

      if (isEmpty()) {

         System.out.println("Queue is empty.");
         return null;

      } else {


         //first, save copy of item to be deleted
         delObject = queueItems[front];

         //delete the object
         queueItems[front] = null;

         //update front variable to point to next array entry
         front = (front + 1) % capacity;

         //update count
         count--;

      }//end if

      return delObject;


   }//method pop

   public int size() {

      return count;

   }//method size

   public boolean inQueue(Object obj) {


      //get String representations of both objects and use these to do comparisons
      String paramSymbol = ((Symbol)obj).getSymbol();
      String queueSymbol = null;

      //search for obj in queue
      for (int i = 0; i < count; i++) {

         queueSymbol = ((Symbol)queueItems[i]).getSymbol();

         if (paramSymbol.equals(queueSymbol))
            return true;

      }//end for

      //if we get down here, then we did not find the object
      return false;

   }//method search


   //I need to write a method that converts the Queue into an array of symbols
   public Symbol[] toArray() {

      //we have symbols already as an array, but they are cast as Objects. Convert each one to a symbol object

      return (Symbol[])queueItems;



   }//method toArray

   //method to perform a deep copy on the queue


   public void printQueue() {

      //this method prints out the contents of the queue. It is used mostly to print out the contentss
      //of the fringe queue

      System.out.println("\nContents of Symbol List: {");

      char comma = ' ';

      //print contents of queue from front to back
      for (int j = 0; j < count; j++) {

	     System.out.print(String.valueOf(comma));
         comma = ',';

         //all objects in the queue will be symbol objects
         System.out.print(((Symbol)queueItems[(front + j) % capacity]).getSymbol());

       //   ((RoState)queueItems[(front + j) % capacity]).printStateCity();

      }//end for

      System.out.println("}\n");


   }//method printQueue


}//class Queue







