/*
 * DrawImportantPoints.java
 *
 * @jackrzhang
 * @version March 25, 2015
 * 
 * This program is the driver for a stand-alone application which will draw
 * shapes generated from the mpeg7 shape files. Computation of the most
 * "important" points determines how to redraw the original shape using a 
 * specified number of points (int numImportPoints). 
 * 
 * Input:
 *  Files consist of 100 lines with two numbers per line. The 100
 * lines represent 100 points, and the two numbers per line 
 * are the x and y coordinates of that point.
 * The files are:
 * Apple.txt
 * Bone.txt
 * Butterfly.txt
 * Octopus.txt
 * Swirl.txt
 *
 * Output:
 * 	A jframe generates a window with the shapes drawn.
 *
 */

import javax.swing.JFrame;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// NOTE: green dots/lines are important points  
// red dots are the initial points

//class which inherits JFrame
public class DrawImportantPoints extends JFrame 
{
	public static int numImportPoints; // number of important points - viable range: 1 - 100 
	private static String fileName = "";
	
	public static int WIDTH = 800;
	public static int HEIGHT = 800;
	
	public static LinkedList importPoints = new LinkedList(); // linked list
	public static ArrayList<Double> importance = new ArrayList<Double>(); // stores importance values
	
	public static ArrayList<Point> initialPointList = new ArrayList<Point>();
	public static ArrayList<Point> finalPointList = new ArrayList<Point>();
	
	/*
	 * main method - creates JFrame
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		System.out.println("\nIMPORTANT POINTS - by J. Zhang\n");
		
		// Read in the file name
		Scanner scUser = new Scanner(System.in); // User inputs file name

		while(fileName.equals("apple") == false && fileName.equals("bone") == false && fileName.equals("butterfly") == false &&
				fileName.equals("octopus") == false && fileName.equals("swirl") == false) // iterator
		{
	        System.out.print("Enter the name of shape file to be drawn - \"apple\", " +
	        	"\"bone\", \"butterfly\", \"octopus\", or \"swirl\": ");
	        fileName = scUser.nextLine();
	        System.out.println();
        }

        // Read in number of important points
        while(numImportPoints < 1 || numImportPoints > 100)
		{
	        System.out.print("Enter number of important points to be drawn - range 1 to 100: ");
	        numImportPoints = scUser.nextInt();
	        System.out.println();
        }

		Scanner input = new Scanner(new File( fileName + ".txt"));
		
		while(input.hasNext()) // iterator
		{
			Scanner sc = new Scanner( input.nextLine() ); // initialize Scanner
			int xCoordinate;
			int yCoordinate;
			Point p;
			
			while(sc.hasNext()) // iterator for individual words
			{
				xCoordinate = sc.nextInt(); // first token is the xCoordinate
				yCoordinate = sc.nextInt(); // second token is the yCoordinate
				p = new Point(xCoordinate, yCoordinate);
				
				initialPointList.add( (Point) p);
				importPoints.insertBack( (Point) p);
			}
		}
		
		importPoints.getTail().setNext(importPoints.getHead());// make the linked list circular
		
		//System.out.println(importPoints); // Print out the linked list

		while ( importPoints.size() > numImportPoints ) // Condition for the algorithm
		{
			computeImportances(); // Re-compute all importance values with each iteration
			removeLowestImportance(); // Find and remove node with point of lowest importance
		}
		
		DrawImportantPoints drawing = new DrawImportantPoints();
		drawing.setVisible(true);
	}

	
	/**
	 * Computes the importances of each point in the circular linked list
	 */
	public static void computeImportances()
	{
		importance.clear(); // clear importance with each new set of computations
		Node prior = importPoints.getTail(); // start on the Head
		Node current, next;
		Point priorPoint, currentPoint, nextPoint;
		double DistOne, DistTwo, DistThree;
		
		for ( int i = 0; i < importPoints.size(); i++ )
		{
			current = prior.getNext(); // Adjusts current and next nodes according to prior
			next = current.getNext();
			
			priorPoint = (Point) prior.getData(); // Access specific Points at each Node
			currentPoint = (Point) current.getData();
			nextPoint = (Point) next.getData();
			
			DistOne = currentPoint.distance(priorPoint); // Find distance values needed to 
			DistTwo = currentPoint.distance(nextPoint); // calculate importance value
			DistThree = priorPoint.distance(nextPoint);
			
			// Store the node's importance value in the ArrayList
			importance.add(DistOne + DistTwo - DistThree);
			prior = prior.getNext(); // Moves on to the next Node for the next loop iteration
		}
	}
	
	/**
	 * Removes the node of lowest importance from the LinkedList
	 */
	public static void removeLowestImportance()
	{
		double lowest = importance.get(0); // lowest value instantiated at first
		int indexOfLowest = 0;
		for ( int i = 1; i < importance.size(); i++ )
		{
			if ( lowest >= importance.get(i) ) // finds lower values than current lowest 
			{
				lowest = importance.get(i);
				indexOfLowest = i-1;
			}
		}
		
		// After finding index value of lowest importance, removes the node at index)
		Node current = importPoints.getHead();
		for ( int i = 0; i < indexOfLowest; i++ )
		{
			current = current.getNext();
		}
		Node temp = current.getNext().getNext(); // skip the one in between
		current.setNext(temp);
	}
	
	
	/*
	 * constructor
	 */
	public DrawImportantPoints() 
	{
		super("MPEG7 Shape \"" + fileName + "\" - " + numImportPoints + " Points");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.BLACK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Container#paint(java.awt.Graphics) draws the shapes in the
	 * JFrame
	 */
	public void paint(Graphics page) 
	{
		super.paint(page);
		
		// Draw all initial points in blue
		page.setColor (Color.red);
		int radius = 2;
		for (Point drawPoint : initialPointList )
	    {
	         page.fillOval (drawPoint.x - radius, drawPoint.y - radius,
	                        radius * 2, radius * 2);
	    }
		
		// Draw all important points in red and connect them in pink
		Node current = importPoints.getHead();
		Point currentPoint, nextPoint;
		for ( int i = 0; i < importPoints.size(); i++ )
		{
			currentPoint = (Point) current.getData();
			nextPoint = (Point) current.getNext().getData();
			
			//Draw the points
			page.setColor(Color.green);
			page.fillOval ( currentPoint.x - radius, currentPoint.y - radius,
                    radius * 2, radius * 2);
			
			// Draw the lines
			page.drawLine(currentPoint.x, currentPoint.y, nextPoint.x, nextPoint.y);
			
			current = current.getNext(); // for the next iteration
		}
		
		System.out.println("Number of Important Points: " + numImportPoints);;
	}
	
}