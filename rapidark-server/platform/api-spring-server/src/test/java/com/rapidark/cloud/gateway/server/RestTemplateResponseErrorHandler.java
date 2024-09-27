package com.rapidark.cloud.gateway.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/24 0:05
 */
@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse)throws IOException {
        return (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                || httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)throws IOException {
        System.out.println("=========error==================");
        traceResponse(httpResponse);
        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            throw new HttpClientErrorException(httpResponse.getStatusCode());
        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.out.println("================================404==========================");
            }
        }
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        log.info("============================response begin==========================================");
        log.debug("Status code  : {}", response.getStatusCode());
        log.debug("Status text  : {}", response.getStatusText());
        log.debug("Headers      : {}", response.getHeaders());
        log.debug("Response body: {}", inputStringBuilder.toString());
        log.info("=======================response end=================================================");
    }

}
