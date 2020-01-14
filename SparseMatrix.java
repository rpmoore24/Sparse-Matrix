/*

SparseMatrix.java

Author: Ryan Moore 
Updated: 2-15-2018

The matrix implementation that I chose was a doubly linked list.
With this implementation, each nonzero element in my list(matrix)
was able to point to the next and previous nonzero element.  This
allowed me to not have to store zeros in my list and memory, and also allowed
me to order my list based on the nodes' row/column combinations.
The clear, setSize, and getSize operations all have constant 
computational complexities.  The determinant operation has a O(n^2)
complexity.  The rest of the methods have linear computational complexities.

This program calculates the determinant of a sparse matrix.
It creates a 5 x 5 matrix with all zeros by default.
The user can clear the matrix, change its size, add elements,
remove elements, return elements at a certain position in the matrix,
get the size of the matrix, return the a certain minor of a matrix,
and also prints all non-zero elements in order.

*/

public class SparseMatrix implements SparseInterface    {
    int rowSize;
    int colSize;
    Node tail;
    SparseMatrix matrix;
    
    public SparseMatrix()   {     //creates a default 5 x 5 matrix

      setSize(5);

    }
   
    public void clear()     {     //clears the matrix by setting the tail's prev pointer to null
      
      tail.prev = null;
    
    }

    public void setSize(int size)   {     //sets the size of the matrix and makes all elements 0
     
      rowSize = size;
      colSize = size;

      tail = new Node(rowSize, colSize, 0, null, null);     //creates a tail node that points to our list
   
    }  

    public void addElement(int row, int col, int data)    {     //adds elements to our matrix
      
      Node curr = tail;
      Node newElement;
      int done = 0;

      try {   //throws an error if the user trys to add an elements out of bounds
        if (col < 0 || col >= colSize || row < 0 || row >= rowSize) {
          throw new IndexOutOfBoundsException("[" + row + ", " + col + "] is out of bounds.");
        }
      } catch (IndexOutOfBoundsException e) {
          System.out.println("ERROR: " + e.getMessage());
          done = 1;
      }

      if (tail.prev == null && data != 0 && done !=1) {     //if the list is empty, adds a node to the end;
        tail.prev = new Node(row, col, data, null, tail);   //makes the tail point to the new node
      
      } else {
          
          while (curr.row > row && done != 1 && data != 0) {             //moves through the list until new element has a lower position
            if (curr.prev == null) {
              curr.prev = new Node(row, col, data, null, curr);  //adds node to beginning of list if necessary
              done = 1;
            }
            curr = curr.prev;
          }

          while(curr.row == row && curr.col > col && done != 1 && data != 0) {   //places element in right column if there is an element already in the row
            if (curr.prev == null) {
              curr.prev = new Node(row, col, data, null, curr);   //adds node to beginning of list if necessary
              done = 1;
            }
            curr = curr.prev;
          }

          if (curr == tail.prev && done != 1 && data != 0) {    
            newElement = new Node(row, col, data, curr, tail);
            tail.prev = newElement;
            curr.next = newElement;
            done = 1;
          } 
          
          if (curr.row == row && curr.col == col && done != 1) {    //changes data if row/column matches an element already in the matrix
            curr.data = data;
           
            if (curr.data == 0) {   //if user adds a 0 element, removes it from list
              curr.prev.next = curr.next;
              curr.next.prev = curr.prev;
            }
            
            done = 1;
          } 
          
          if (done != 1 && data != 0) {   //adds the new element in the correct position in the list
             newElement = new Node(row, col, data, curr, curr.next);
             curr.next = newElement;
             newElement.next.prev = newElement;
          }
      }

    }

    public void removeElement(int row, int col) {   //removes element from list, or matrix
      Node curr = tail;

      try {   //throws an error if the user trys to add an elements out of bounds
        if (col < 0 || col >= colSize || row < 0 || row >= rowSize) {
          throw new IndexOutOfBoundsException("[" + row + ", " + col + "] is out of bounds.");
        }
      } catch (IndexOutOfBoundsException e) {
          System.out.println("ERROR: " + e.getMessage());
      }

      while (curr != null)  {   //keeps moving through the list until you get to the specified element
        if (curr.row == row && curr.col == col) {
          if (curr.prev == null) {    //if element is at the end of the list (beginning of the matrix)
            curr.next.prev = null;
          } else {
              curr.next.prev = curr.prev;   //removes element from the list
              curr.prev.next = curr.next;
          }
        }

        curr = curr.prev;

      } 
    }
   
    public int getElement(int row, int col) {
      Node curr = tail.prev;
      int value = 0;
      boolean done = false;

      try {   //throws an error if the user trys to add an elements out of bounds
        if (col < 0 || col >= colSize || row < 0 || row >= rowSize) {
          throw new IndexOutOfBoundsException("[" + row + ", " + col + "] is out of bounds.");
        }
      } catch (IndexOutOfBoundsException e) {
          System.out.println("ERROR: " + e.getMessage());
      }

      while (curr != null && done != true)  {   //moves through the list until element is found

        if (curr.row == row && curr.col == col) {   //stores data of element
          value = curr.data;
          done = true;
        }
        
        curr = curr.prev;
      } 

      return value;

    }

    public int determinant()  {   //calculates determinant of matrix using a recursive algorithm
      int determinant = 0;

      if (minor(0,0).getSize() == 0)  {   //base case
        determinant = getElement(0,0);
      } else  {
        for (int i = 0; i < rowSize; i++) {
           determinant += (int)Math.pow(-1,i) * getElement(i,0) * minor(i,0).determinant();
        }    
      }

      return determinant;
    }

    public SparseMatrix minor(int row, int col)   {   //return specified minor of the matrix  
      Node curr = tail.prev;
      int minorSize = rowSize - 1;
      int minorRow = 0;
      int minorCol = 0;
      SparseMatrix minor = new SparseMatrix();
      minor.setSize(minorSize);

      while (curr != null) {    //searches through the row and doesn't add to minor if current node matches row or column
        if (curr.row == row || curr.col == col) {
          curr = curr.prev;
        } else  {

            minorRow = curr.row;
            minorCol = curr.col;
           
            if (curr.row > row && curr.col > col) {   //changes index to be added to minor
              --minorRow;
              --minorCol;
            }

            if (curr.row > row && curr.col < col) {   //changes index to be added to minor
              --minorRow;
            }

            if (curr.row < row && curr.col > col) {   //changes index to be added to minor
              --minorCol;
            }
          
          minor.addElement(minorRow, minorCol, curr.data);  //adds element to minor
          curr = curr.prev;
        }
      }

      return minor;

    }

    public String toString()  {   //return row column data of non zero elements in order
      Node curr = tail;
      String sparse = "";

      while (curr.prev != null)  {
        curr = curr.prev;
      }

      while (curr != tail)  {
        sparse = sparse + curr.row + " " + curr.col + " " + curr.data + "\n";
        curr = curr.next;
      }

      return sparse;

    }

    public int getSize()  {  //returns size of the matrix; our matrix will always be n x n
      return rowSize;
    }


}

class Node {    //node class with constructor that creates node with specified row, col, data, previous node, and next node
    int row;
    int col;
    int data;
    Node prev;
    Node next;

    Node(int row, int col, int data, Node prev, Node next)  {
      this.row = row;
      this.col = col;
      this.data = data;
      this.prev = prev;
      this.next = next;
    }
}


