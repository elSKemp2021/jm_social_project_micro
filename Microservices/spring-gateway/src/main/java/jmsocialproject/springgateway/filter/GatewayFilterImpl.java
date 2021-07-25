package jmsocialproject.springgateway.filter;

import jmsocialproject.springgateway.validator.RouteValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
public class GatewayFilterImpl implements GatewayFilter {

    @Autowired
    RouteValidation validator;


    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {

        var originalUri = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, "Unknown");

        if (!validator.isOpenApi(originalUri)) {

            var headers = exchange.getRequest().getHeaders();

            var jvtToken = headers.getFirst("authorization");

            if (jvtToken != null && validator.validateToken(jvtToken)) {
                return chain.filter(exchange);
            }

            return this.onErrorFilter(exchange);
        }

        return chain.filter(exchange);

    }

    Mono<Void> onErrorFilter(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        response.setStatusCode(BAD_REQUEST);
        return response.setComplete();

    }


}