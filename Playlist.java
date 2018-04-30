import jm.util.*;

public class Playlist
{
	ListNode head;
	ListNode tail; 
	int size; 
	
	// add a song to the end of the list
	void add (Song song)
	{
		// if list is not empty
		if (size!=0)
		{
			// make new node with song information
			ListNode node = new ListNode(song);
			// make the node after tail the new node
			tail.next = node;
			tail.next.song = song;
			// have the new node's prev point to the tail 
			node.prev = tail;
			// set the tail to the new node at the end
			tail = tail.next;
			// increase size by 1
			size++;
		}
		else // if list is empty
		{
			// make new node at the head
			head = new ListNode(song);
			head.song = song;
			// set tail equal to head since there is only 1 node
			tail = head;
			// increase size by 1
			size++;
		}
	} 
}

