import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HttpClient {
    public static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=lbG6AkrcaNyavA4xodCfLmQWoFvBudax1gdavSZJ");
        CloseableHttpResponse response = httpClient.execute(request);

        ObjNASA objNASA = mapper.readValue(response.getEntity().getContent(), new TypeReference<ObjNASA>() {});
        String urlContent = objNASA.getUrl();
        String nameFile =  Arrays.stream(urlContent.split("/")).filter(x -> x.contains(".png")).collect(Collectors.joining());

        HttpGet requestContent = new HttpGet(urlContent);
        CloseableHttpResponse responseContent = httpClient.execute(requestContent);

        File file = new File(nameFile);
        OutputStream writer = new FileOutputStream(file);
        writer.write(responseContent.getEntity().getContent().readAllBytes());
    }
}
