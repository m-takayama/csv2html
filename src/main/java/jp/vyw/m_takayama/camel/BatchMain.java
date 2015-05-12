package jp.vyw.m_takayama.camel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.camel.CamelContext;

import com.google.inject.Injector;

public abstract class BatchMain {
	private Injector getInjector() {
		InitialContext context;
		try {
			context = new InitialContext();
			return (Injector) context.lookup(Injector.class.getName());
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	protected void run(String[] args) {
		Injector injector = getInjector();

		CamelContext context = injector.getInstance(CamelContext.class);

		try {
			context.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try {
			CountDownLatch latch = injector.getInstance(CountDownLatch.class);
			latch.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
