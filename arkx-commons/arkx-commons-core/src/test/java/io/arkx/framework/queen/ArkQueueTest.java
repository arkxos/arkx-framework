package io.arkx.framework.queen;

public class ArkQueueTest {

	public static void main(String[] args) {
		// This is recommended implementation.
		// In a case if you want to handle all events in current thread only
		// you can use EventBusSimple
		EventBus<Event> eventBus = new EventBusAsync<>();

		
		// Now just subscribe your listeners here,
		// but keep in mind that handlers subscribed using weak links,
		// so you should store hard links to handlers somewhere or make them
		// singletons.
		eventBus.subscribe(new SimpleHandler());
		eventBus.subscribe(new AdvHandler());

		Event ev1 = new Event("SIMPLE_EVENT");
		ev1.set("value_key", 42);
		ev1.set("value_other_key", "You can put any object here");
		eventBus.publish(ev1); // Will be send to SimpleHandler

		Event ev2 = new Event("USER_CREATED");
		ev2.set("id", 42);
		eventBus.publish(ev2); // To AdvHandler

		// To AdvHandler using event builder
		eventBus.publish(EventBuilder.create("USER_EMAIL_CONFIRMED").set("id", 4242).build());
		
		eventBus.closeOnFinish(()->{
			System.out.println("EventBus execute finished...");
		}).start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class SimpleHandler implements MesssageHandler<Event> {
	public String getType() {
		return "SIMPLE_EVENT"; // Event type we want to handle here
	}

	public boolean canHandle(String eventType) {
		return true; // As long as getType() return not null this method is not
						// called at all
	}

	public void handle(Event event) {
		System.out.println("I've got an event " + event.getType());
		// And other stuff
	}
}

class AdvHandler implements MesssageHandler<Event> {
	public String getType() {
		return null; // Means that now canHandle method is in charge
	}

	public boolean canHandle(String eventType) {
		return eventType.startsWith("USER_");
	}

	public void handle(Event event) {
		// Handle all events with types like: USER_CREATED, USER_DEL, USER_LOGIN
		// etc.
		System.out.println("I've got an event " + event.getType());
		// And other stuff
	}
}