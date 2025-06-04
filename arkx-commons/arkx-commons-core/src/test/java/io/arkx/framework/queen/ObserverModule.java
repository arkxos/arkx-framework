package io.arkx.framework.queen;

import java.util.ArrayList;
import java.util.List;

//观察者
interface Observer {
	public void update(Observable observable);
}

// 被观察者
abstract class Observable {

	protected boolean isChanaged;
	protected List<Observer> observers = new ArrayList<>();

	public Observable() {
		isChanaged = false;
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	public void removeObservers() {
		observers.clear();
	}

	public void notifyObservers() {
		if (isChanaged) {
			for (int i = 0; i < observers.size(); i++) {
				observers.get(i).update(this);
			}
			isChanaged = false;
		}
	}
}

// 人，温度监测
class TemperatureObserver implements Observer {
	@Override
	public void update(Observable observable) {
		Water water = (Water) observable;
		System.out.println("温度：" + water.getTemperature() + "    状态：" + water.getStatus());
		System.out.println("TemperatureObserver observing...");
	}
}

class BoildObserver implements Observer {
	String doSomthing;

	BoildObserver(String doSomthing) {
		this.doSomthing = doSomthing;
	}

	@Override
	public void update(Observable observable) {
		Water water = (Water) observable;
		if (water.getTemperature() >= 100) {
			System.out.println("状态：" + water.getStatus());
			System.out.println("BoildObserver:" + doSomthing);
		}

	}
}

// 水，被观察者
class Water extends Observable {
	private double temperature;
	private String status;

	public Water() {
		super();
		this.temperature = 0;
		this.status = "冷水";
	}

	public Water(Observer observer) {
		this();
		observers.add(observer);
	}

	public double getTemperature() {
		return temperature;
	}

	public String getStatus() {
		return status;
	}

	public void change(double temperature) {
		this.temperature = temperature;
		if (temperature < 40) {
			status = "冷水";
		} else if (temperature >= 40 && temperature < 60) {
			status = "温水";
		} else if (temperature >= 60 && temperature < 100) {
			status = "热水";
		} else {
			status = "开水";
		}
		this.isChanaged = true;
		notifyObservers();
	}
}

// 客户端
public class ObserverModule {
	public static void main(String args[]) {
		TemperatureObserver temperatureObserver = new TemperatureObserver();
		BoildObserver boildObserver1 = new BoildObserver("关闭电源...");
		BoildObserver boildObserver2 = new BoildObserver("继续保湿...");
		Water water = new Water(temperatureObserver);
		water.addObserver(boildObserver1);
		water.addObserver(boildObserver2);
		water.change(45);
		water.change(80);
		water.change(100);
	}
}