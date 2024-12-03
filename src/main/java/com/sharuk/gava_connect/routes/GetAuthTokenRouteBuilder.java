package com.sharuk.gava_connect.routes;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.sharuk.gava_connect.enums.HTTPCommonHeadersEnum.CONTENT_TYPE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
public class GetAuthTokenRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
// Initialize the reusable ApiConsumer
//        ApiConsumer apiConsumer = new ApiConsumer(true); // true disables SSL verification

//        onException(Exception.class)
//                .handled(true)
//                .removeHeaders("*")
//                .process("errorHandlingProcessor");

        rest()
                .get("gava/connnect/auth")
                .description("Adapter REST Service")
                .produces("application/json")
                .to("direct:fetchToken");

        from("direct:fetchToken")
                .noStreamCaching().noMessageHistory().noTracing()
                .removeHeaders("*", "Authorization")// Set Authorization header
                .doTry()
                .log(LoggingLevel.INFO, "\n Calling Gava Connect Auth Endpoint :: Request :: {{atomic.uri}}")
                .enrich().simple("{{atomic.uri}}").id("callServiceBack")
                .setHeader(CONTENT_TYPE.getName(), constant(APPLICATION_JSON_VALUE))
                .to("direct:fetchTokenResponse");

        from("direct:fetchTokenResponse")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("Processed response with content type: ${header.Content-Type}")
                .removeHeaders("*")
                .removeHeader("Authorization")
                .doTry()
                .process(exchange -> {
                    System.out.println("Provider Response Data -------(From Backend)");
                    String body = exchange.getIn().getBody(String.class);

                    ObjectMapper objectMapper = new ObjectMapper();

                    Map jsonData = null;
                    try {
                        // Parse JSON string into a Map
                        jsonData = objectMapper.readValue(body, Map.class);
                        // Output the parsed data
                        System.out.println("Access Token: " + jsonData.get("access_token"));
                        System.out.println("Expires In: " + jsonData.get("expires_in"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    exchange.getIn().setBody(jsonData);
                });


    }
}