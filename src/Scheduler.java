import java.util.*;

public class Scheduler{

	private ArrayList<Request> masterQueue;//not sure there is much use in a master queue
	private Hashtable<Elevator, ArrayList> elevQueueMap;
	private int FLOORS;
	private int ELEVATORS;
	private boolean endCheck = false;

	public Scheduler(){
		FLOORS = 7;
		ELEVATORS = 1;
		masterQueue = new ArrayList((2 * FLOORS) - 2);
		elevQueueMap = new Hashtable(ELEVATORS);
	}

	public Scheduler(int floors, int elevators){
		FLOORS = floors;
		ELEVATORS = elevators;
		masterQueue = new ArrayList((2 * floors) - 2);
		elevQueueMap = new Hashtable(elevators);
	}

	public void addExternalRequest(Request request){
		if(endCheck) {
			this.stop();
		}
		masterQueue.add(request);
		for(Elevator e : elevQueueMap.keySet()){
			ArrayList a = elevQueueMap.get(e);
			a.add(request);
			sort(e, a);
		}
	}

	public void addInternalRequest(Elevator e, Request request){
		if(endCheck) {
			this.stop();
		}
		elevQueueMap.get(e).add(request);
		sort(e, elevQueueMap.get(e));
	}

	public void serviceComplete(int destFloor){
		//masterQueue.remove(request);
		for(Elevator e : elevQueueMap.keySet()){//for each elevator in the map,
			ArrayList a = elevQueueMap.get(e);
			elevQueueMap.get(e).remove(request);//remove the request from the respective queue
			sort(e, a);//sort the queue
		}
	}

	public ArrayList<Request> updateQueue(Elevator e){
		return elevQueueMap.get(e);
	}

	private void sort(Elevator e, ArrayList<Request> q){
		Boolean sorted = true;
		while(sorted){
		sorted = true;
			for(int i = 0; i < q.size(); i++){
				if(q.get(i).getDirection.equals(q.get(i + 1).getDirection)){//sorting direction of request
					swap(q, i);
					sorted = false;
				}
			}
		}
		while(sorted) {
			sorted = true;
			for(int i = 0; i < q.size(); i++){
				if(q.get(i) != q.get(i + 1)){//if direction of request != direction of the next, skip (it's the middle of the queue)
					i++;
				}
				switch(q.get(i).getDirection()){//sorting floor number of request
					case UP:
						if(q.get(i).getFloor() > q.get(i + 1).getFloor()){//lower floor numbers should be at the front of the "up" section
							swap(q, i);
						}
					case DOWN:
						if(q.get(i).getFloor() < q.get(i + 1).getFloor()){//higher floors should be at the end of the "down" section
							swap(q, i);
						}
				}
			}
		}
	}

	/*
	* sorting is the scheduling of this implementation
	*/
	private void swap(ArrayList<Request> q, int index){
		Request temp = q.get(index);
		q.add(index, q.get(index + 1));
		q.add(index + 1, temp);
	}
	
	//called by floor to end scheduler
	public void endActions() {
		if(masterQueue.isEmpty()) {
			endCheck = true;
		}
	}
	
	private void stop() {
		exit = true;
	}
}
