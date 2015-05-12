package jp.vyw.m_takayama.camel;

import com.google.inject.Inject;

public class RouteBuilder extends AbstractRouteBuilder {
	@Inject
	RouteBuilder(Terminator terminator) {
		super(terminator);
	}

	@Override
	public void configure() throws Exception {
		from("file:in?noop=true")
			.unmarshal().csv()
			.log("${body}")
			.to("velocity:template.vm")
			.to("file:out?fileName=out.html")
			.process(terminator());
	}
}
