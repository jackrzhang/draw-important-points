
public class Node
{
	private Object data;
	private Node next;
    
	public Node ( ) 
	{
		data = null;
		next = null;
    }
    
	public Node( Object newData, Node newNext )
    {
		this.data = newData;
		this.next = newNext; // newNext may by null
    }
   
	public Object getData( )
	{
    	return data;
	}
    
	public Node getNext( )
	{
		return next;
	}
    
	public void setData(Object newData) 
	{
		data = newData;
	}
	
	public void setNext( Node newNext )
	{
		next = newNext;
	}
	
} // end class Node