package com.redhat.gpe.training.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.direct.DirectConsumerNotAvailableException;

public class ManagedRoute extends RouteBuilder {

    MyCustomRoutePolicy policy = new MyCustomRoutePolicy();

    @Override
    public void configure() throws Exception {

        onException(DirectConsumerNotAvailableException.class).handled(true).log("Route 'direct:foo' is closed !!!").process(
                new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                       exchange.getContext().getRoute("timer-managed-route").getConsumer().stop();
                    }
                }
        );

        from("timer:managed").routeId("timer-managed-route")
                .setBody().constant("Hello World")
                .log("Route 'direct:foo' is called").to("direct:foo")
           .setBody().constant("STOP")
                .log("Route 'direct:foo' will be stopped").to("direct:foo")
                .setBody().constant("Hello World")
                .log("Exception will be thrown as the route/consumer is stopped").to("direct:foo") ;

        from("direct:foo")
           .routePolicy(policy)
           .log("Route direct:foo has been called with the Body : ${body}");
    }
}
