package skip;

//key is where it is 
//value is what the value is
import java.util.*;

import main.DynamicSet;


public class SkipList<Type> implements DynamicSet<Type> {	
  public SkipListEntry<Type> head;    // First element of the top level
  public SkipListEntry<Type> tail;    // Last element of the top level
  public int n; 		// number of entries in the Skip list
  public int height;       // Height
  public Random r;    // Coin toss

  /* ----------------------------------------------
     Constructor: empty skiplist

                          null        null
                           ^           ^
                           |           |
     head --->  null <-- -inf <----> +inf --> null
                           |           |
                           v           v
                          null        null
     ---------------------------------------------- */
  public SkipList(){ //default constructor 
     SkipListEntry<Type> p1, p2;

     p1 = new SkipListEntry<Type>(SkipListEntry.negInf, null);
     p2 = new SkipListEntry<Type>(SkipListEntry.posInf, null);

     head = p1;
     tail = p2;

     p1.right = p2;
     p2.left = p1;

     n = 0;
     height = 0;

     r = new Random();
  }


  /** Returns the number of entries in the hash table. */
  public int size(){ 
    return n; 
  }

  /** Returns whether or not the table is empty. */
  public boolean isEmpty() { 
    return (n == 0); 
  }

  /* ------------------------------------------------------
     findEntry(k): find the largest key x <= k
		   on the LOWEST level of the Skip List
     ------------------------------------------------------ */
  public SkipListEntry<Type> findEntry(String k){
     SkipListEntry<Type> p;

     /* -----------------
	Start at "head"
	----------------- */
     p = head;

     while ( true ){
        /* --------------------------------------------
	   Search RIGHT until you find a LARGER entry

           E.g.: k = 34

                     10 ---> 20 ---> 30 ---> 40
                                      ^
                                      |
                                      p stops here
		p.right.key = 40
	   -------------------------------------------- */
        while ( p.right.key != SkipListEntry.posInf && 
		p.right.key.compareTo(k) <= 0 ){
           p = p.right;
//         System.out.println(">>>> " + p.key);
	}

	/* ---------------------------------
	   Go down one level if you can...
	   --------------------------------- */
	if ( p.down != null ){  
           p = p.down;
//         System.out.println("vvvv " + p.key);
        }
        else
	   break;	// We reached the LOWEST level... Exit...
     }

     return(p);         // p.key <= k
  }


  /** Returns the value associated with a key. */
  public String getValue (String k) {
     SkipListEntry<Type> p;

     p = findEntry(k);

     if ( k.equals( p.getKey() ) )
        return (String) (p.value);
     else
        return(null);
  }

  /* ------------------------------------------------------------------
     insertAfterAbove(p, q, y=(k,v) )
 
        1. create new entry (k,v)
	2. insert (k,v) AFTER p
	3. insert (k,v) ABOVE q

             p <--> (k,v) <--> p.right
                      ^
		      |
		      v
		      q

      Returns the reference of the newly created (k,v) entry
     ------------------------------------------------------------------ */
  public SkipListEntry<Type> insertAfterAbove(SkipListEntry<Type> p, SkipListEntry<Type> q, 
                                         String k)
  {
     SkipListEntry<Type> e;

     e = new SkipListEntry<Type>(k, null);

     /* ---------------------------------------
	Use the links before they are changed !
	--------------------------------------- */
     e.left = p;
     e.right = p.right;
     e.down = q;

     /* ---------------------------------------
	Now update the existing links..
	--------------------------------------- */
     p.right.left = e;
     p.right = e;
     q.up = e;

     return(e);
  }

  /** Put a key-value pair in the map, replacing previous one if it exists. */
  public String insert (String k, Type v) {
     SkipListEntry<Type> p, q;
     int i;

     p = findEntry(k);

//   System.out.println("findEntry(" + k + ") returns: " + p.key);
     /* ------------------------
	Check if key is found
	------------------------ */
     if ( k.equals( p.getKey() ) ){
    	 String old = (String) p.value;
    	 p.value = v;
    	return(old);
     }

     /* ------------------------
	Insert new entry (k,v)
	------------------------ */

     /* ------------------------------------------------------
        **** BUG: He forgot to insert in the lowest level !!!
	Link at the lowest level
	------------------------------------------------------ */
     q = new SkipListEntry<Type>(k, v);
     q.left = p;
     q.right = p.right;
     p.right.left = q;
     p.right = q;

     i = 0;                   // Current level = 0

     while ( r.nextDouble() < 0.5 ){
	// Coin flip success: make one more level....

//	System.out.println("i = " + i + ", h = " + h );

	/* ---------------------------------------------
	   Check if height exceed current height.
 	   If so, make a new EMPTY level
	   --------------------------------------------- */
        if ( i >= height ){
           SkipListEntry<Type> p1, p2;

           height = height + 1;
           p1 = new SkipListEntry<Type>(SkipListEntry.negInf,null);
           p2 = new SkipListEntry<Type>(SkipListEntry.posInf,null);
	   
		   p1.right = p2;
		   p1.down  = head;
	
		   p2.left = p1;
		   p2.down = tail;
	
		   head.up = p1;
		   tail.up = p2;
	
		   head = p1;
		   tail = p2;
	}


	/* -------------------------
	   Scan backwards...
	   ------------------------- */
	while ( p.up == null ){
//	   System.out.print(".");
	   p = p.left;
	}

//	System.out.print("1 ");

	p = p.up;


	/* ---------------------------------------------
           Add one more (k,v) to the column
	   --------------------------------------------- */
   	SkipListEntry<Type> e;
   		 
   	e = new SkipListEntry<Type>(k, null);  // Don't need the value...
   		 
   	/* ---------------------------------------
   	   Initialize links of e
   	   --------------------------------------- */
   	e.left = p;
   	e.right = p.right;
   	e.down = q;
   		 
   	/* ---------------------------------------
   	   Change the neighboring links..
   	   --------------------------------------- */
   	p.right.left = e;
   	p.right = e;
   	q.up = e;
   	q = e;		// Set q up for the next iteration
   	i = i + 1;	// Current level increased by 1

     }

     n = n + 1;

     return(null);   // No old value
  }

  /** Removes the key-value pair with a specified key. */
  public Integer delete (String key) {
     return(null);
  }

  public void printHorizontal(){
     String s = "";
     int i;

     SkipListEntry<Type> p;

     /* ----------------------------------
	Record the position of each entry
	---------------------------------- */
     p = head;

     while ( p.down != null ){
        p = p.down;
     }

     i = 0;
     while ( p != null ){
        p.pos = i++;
        p = p.right;
     }

     /* -------------------
	Print...
	------------------- */
     p = head;

     while ( p != null ){
        s = getOneRow( p );
	System.out.println(s);

        p = p.down;
     }
  }

  public String getOneRow( SkipListEntry<Type> p ){
     String s;
     int a, b, i;

     a = 0;

     s = "" + p.key;
     p = p.right;

     while ( p != null ){
    	 SkipListEntry<Type> q;
    	 q = p;
    	 while (q.down != null)
    	q = q.down;
        b = q.pos;

        s = s + " <-";


        for (i = a+1; i < b; i++)
        	s = s + "--------";
        	s = s + "> " + p.key;
        	a = b;
        	p = p.right;
     }

     return(s);
  }

  public void printVertical(){
     String s = "";
     SkipListEntry<Type> p;

     p = head;

     while ( p.down != null )
        p = p.down;

     while ( p != null ){
    	 s = getOneColumn( p );
	System.out.println(s);

        p = p.right;
     }
  }


  public String getOneColumn( SkipListEntry<Type> p ){
     String s = "";

     while ( p != null ){
        s = s + " " + p.key;
        p = p.up;
     }
     return(s);
  }


@Override
public void insert(Type key, Object e) {
	// TODO Auto-generated method stub
	
}


@Override
public void delete(Type key) {
	// TODO Auto-generated method stub
}


@Override
public Object search(Type key) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public Object minimum() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public Object maximum() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public Object successor(Type key) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public Object predecessor(Type key) {
	// TODO Auto-generated method stub
	return null;
}

} 