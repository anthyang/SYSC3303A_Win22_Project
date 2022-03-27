File names were chosen to be as accurate as possible:
- The class representing elevators is called Elevator
     - Elevator class has a faulty door open timer that force door close  
- The class representing the floors is called Floor
- The class representing the scheduler is called Scheduler.
     - Scheduler class has checkTimer in handRequest method for faulty elevator and re-route request to an available elevator
- A class called Request is used to communicate service requests from the floors to the elevator and scheduler.
- A class called ElevatorReport is used to communicate an elevator's status to the scheduler.
- A building class simply runs all threads concurrently.
- An abstract class called Host is used to simplify UDP functions.
- ElevatorStatus allows the scheduler to track all essential elevator data

To run the program, compile and run the program in the Eclipse IDE.

Added Class, sequence, state, and time UML diagrams.
