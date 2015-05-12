package jp.vyw.m_takayama.camel;

import org.apache.camel.guice.CamelModuleWithMatchingRoutes;

public class Module extends CamelModuleWithMatchingRoutes {
	@Override
	protected void configure() {
		super.configure();

		bind(RouteBuilder.class);
	}
}
