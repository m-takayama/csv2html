package jp.vyw.m_takayama.camel;

public abstract class AbstractRouteBuilder extends org.apache.camel.builder.RouteBuilder {
	private final Terminator terminator;

	protected AbstractRouteBuilder(Terminator terminator) {
		this.terminator = terminator;
	}

	protected Terminator.Processor terminator() {
		return terminator.processor();
	}
}
