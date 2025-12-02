package io.arkx.framework.queue2;

// package io.arkx.framework.queue;
//
/// **
// * 实现顶层接口的抽象类
// * 业务层顶层接口，自定义的小框架里可以在顶层业务接口中直接继承事件接口，不影响性能
// * 因为在初始化事件监听器时，已经过滤了没有真正实现接口方法的类，所以不会造成多余的调用
// */
// public abstract class AbstractEventListener implements IBaseEventListener {
//
// /**
// * 发布事件
// */
// protected final void publishEvent(BaseEvent<?> event) {
// // 使用自已事件工具类发布事件
// EventController.publishEvent(event);
// }
//
// //具体子类中不需要再实现
// public Class<? extends IBaseEventListener> getRealClass(){
// return this.getClass();
// }
//
// /**
// * 默认实现处理事件的方法
// */
// public void onBaseEvent(BaseEvent<?> event) {
// //这里空实现，且没有注解，这样，如果具体业务类没有重写方法，
// //初始化事件监听器时就会被过滤掉，不会造成多余调用
// }
//
// }
