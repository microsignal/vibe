package io.microvibe.booster.test.generic;

import io.microvibe.booster.core.base.entity.BaseEntity;
import org.junit.Test;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Qt
 * @since Aug 26, 2018
 */
public class GenericTest {
	@Test
	public void test() {
//		new B1();
//		new B2();
		new B3();
	}

	static class Base<Entity extends BaseEntity<ID>, ID extends Serializable> {
		public Base() {
//			System.out.println(ReflectionUtils.findParameterizedType(this.getClass(), 0));
//			System.out.println(ReflectionUtils.findParameterizedType(this.getClass(),1 ));
//			System.out.println(ReflectionUtils.findParameterizedType(this.getClass().getSuperclass(), 1));
			queue();
		}

		void queue(){
			Deque<Class> queue = new LinkedList<>();
			Class clazz = this.getClass();
			while(clazz!= null && clazz != Base.class){
				queue.offerLast(clazz);
				clazz = clazz.getSuperclass();
			}
			System.out.println(queue);
		}
	}

	static class B1 extends Base<A, Long> {

	}

	static class B2<Entity extends BaseEntity<Long>> extends Base<Entity, Long> {

	}

	static class B3 extends B2<A> {

	}

	static class A extends BaseEntity<Long> {

		@Override
		public Long getId() {
			return null;
		}

		@Override
		public void setId(Long aLong) {
		}
	}
}
