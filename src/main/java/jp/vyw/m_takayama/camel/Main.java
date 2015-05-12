package jp.vyw.m_takayama.camel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.guice.CamelModuleWithMatchingRoutes;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;

public class Main {
	public static class Module extends CamelModuleWithMatchingRoutes {
		private final CountDownLatch latch = new CountDownLatch(1);

		@Override
		protected void configure() {
			super.configure();
			bind(RouteBuilder.class);
		}

		@Provides
		CountDownLatch provideLatch() {
			return latch;
		}
	}

	public static class RouteBuilder extends org.apache.camel.builder.RouteBuilder {
		private final CountDownLatch latch;

		@Inject
		RouteBuilder(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void configure() throws Exception {
			from("file:in?noop=true")
				.unmarshal().csv()
				.log("${body}")
				.to("velocity:template.vm")
				.to("file:out?fileName=out.html")
				.process(new Processor() {
					public void process(Exchange exchange) throws Exception {
						if (exchange.getProperty("CamelBatchComplete", Boolean.class))
							latch.countDown();
					}
				});
		}
	}

	private Injector getInjector() {
		InitialContext context;
		try {
			context = new InitialContext();
			return (Injector) context.lookup(Injector.class.getName());
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	private void run(String[] args) {
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

	public static void main(String[] args) {
		new Main().run(args);
	}
}
