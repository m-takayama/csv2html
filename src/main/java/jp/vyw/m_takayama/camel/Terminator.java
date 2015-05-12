package jp.vyw.m_takayama.camel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;

import com.google.inject.Inject;

public class Terminator {
	public static class Processor implements org.apache.camel.Processor {
		private final CountDownLatch latch = new CountDownLatch(1);

		private final CamelContext context;

		@Inject
		Processor(CamelContext context) {
			this.context = context;
		}

		@Override
		public void process(Exchange exchange) throws Exception {
			if (exchange.getProperty("CamelBatchComplete", Boolean.class))
				context.stop();
		}

		public void await(long timeout, TimeUnit unit) {
			try {
				latch.await(timeout, unit);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private final Processor processor;

	@Inject
	Terminator(Processor processor) {
		this.processor = processor;
	}

	public Processor processor() {
		return processor;
	}

	public void await(long timeout, TimeUnit unit) {
		processor.await(timeout, unit);
	}
}
