package TurtleGraphics;
import java.io.*;
import java.util.*;

/**
 * Class that describes a student.  A student has a name and an 
 * array of grades.  You can get information about a student 
 * such as her/his name and grade average. 
 * 
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public class Student 
{
  
  //////////// fields //////////////////
  /** the name of the student: first last */
  private String name;
  /** an array of grades for this student */
  private double[] gradeArray;
  
  //////////// constructors ///////////
  
  /**
   * No argument constructor.  Leaves
   * all fields with default values 
   */
  public Student() {}
  
  /**
   * Constructor that takes the name
   * @param theName the student's name
   */
  public Student(String theName) 
  {
    this.name = theName;
  }

  /**
   * Constructor that takes the name
   * and an array of grades
   * @param theName the student's name
   * @param theGradeArray the array of grades
   */
  public Student(String theName, 
                 double[] theGradeArray)
  {
    this.name = theName;
    this.gradeArray = theGradeArray;
  }

  /////////// methods ///////////////
  
  /**
   * Method to return the name of this student
   * @return the student's name
   */
  public String getName() { return this.name; }
  
  /**
   * Method to set the name for this student
   * @param theName the new name to use
   * @return true if success else false
   */
  public boolean setName(String theName)
  {
    boolean result = false;
    if (this.name == null)
    {
      this.name = theName;
      result = true;
    }
    return result;
  }
  
  /**
   * Method to get the grade in the grade array
   * at the passed index
   * @param index the index that we want the grade for
   * @return the grade in the grade array at this passed index
   */
  public double getGrade(int index)
  {
    return this.gradeArray[index];
  }
  
  /**
   * Method to replace the array of grades
   * @param theArray the new array of grades to use
   * @return true if sucess, else false
   */
  public boolean setGradeArray(double[] theArray)
  {
    boolean result = false;

    // only set the gradeArray if it is null
    if (this.gradeArray == null)
    {
      this.gradeArray = theArray;
      result = true;
    }
    return result;
  }
  
  /**
   * Method to set a grade at an index
   * @param index the index to set it at
   * @param newGrade the grade to use
   * @return true if success else false
   */
  public boolean setGrade(int index, 
                          double newGrade)
  {
    boolean result = false;
    if (newGrade >= 0 || 
        this.gradeArray != null ||
        index < this.gradeArray.length ||
        index >= 0) {
      this.gradeArray[index] = newGrade;
      result = true;
    }
    return result;
  }

  
  /**
   * Method to return the average of the grades for this student
   * @return the average of the grades or 0.0 if no grade array or
   * no grades 
   */
  public double getAverage()
  {
    double average = 0.0;

   // if (this.gradeArray != null && this.gradeArray.length > 0)
   // {
      double sum = 0.0;
      for (int i = 0; i < this.gradeArray.length; i++)
      {
        sum = sum + this.gradeArray[i];
      }
      average = sum / this.gradeArray.length;
   // }
    return average;
  }
  
 
  
  /**
   * Method to return a string with information about this student
   * @return a string with information about the student
   */
  public String toString() {
    return "Student object named: " + this.name + 
           " Average: " + this.getAverage();
  }
  
  /* Used to test */
  public static void main (String[] args)
  {
    // test the constructor that takes a delimited string
    double[] gradeArray = {99, 89, 95, 75, 97};
    Student student = 
       new Student("Sue Ericson");
    System.out.println(student); 
  }
}