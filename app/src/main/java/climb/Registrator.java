package climb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Registrator {
    protected final Logger log = LogManager.getLogger(this.getClass());

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .cookieHandler(new CookieManager())
            .build();

    private final String urlBase;
    private final String email;
    private final String password;

    public Registrator(String urlBase, String email, String password) {
        this.urlBase = urlBase;
        this.email = email;
        this.password = password;
        login(email, password);
    }


    private void login(String email, String password) {
        var loginResponse = post(URI.create(urlBase + "/bookings/customer/login"),
                Map.of(
                        "email", email,
                        "password", password
                )
        );
    }

    public final boolean register(LocalDateTime dateTime) {
        // creat booking
        var regResponse = post(URI.create(urlBase + "/booking/index"),
                Map.of(
                        "date", dateTime.format(DATE_FORMATTER),
                        "type", "58",
                        "hour", dateTime.format(TIME_FORMATTER),
                        "duration", "120"
                )
        );
        Document regPage = Jsoup.parse(regResponse.body());

        var date = regPage.select("input[name=date]").attr("value");
        var idResource = regPage.select("input[name=idResource]").attr("value");
        var idReservation = regPage.select("input[name=idReservation]").attr("value");

        if (String.valueOf(idReservation).isEmpty()) {
            var errorContent  = regPage.select(".c-message-indicator__content").text();
            throw new RuntimeException("Reservation ID is unknown: " + errorContent);
        }

        // confirm booking
        var payResponse = post(URI.create(urlBase + "/customerZone/newReservationPost"),
                Map.of(
                        "date", date,
                        "idResource", idResource,
                        "idReservation", idReservation,
                        "paymentPending", "",
                        "duration", "120"
                )
        );
        Document payPage = Jsoup.parse(payResponse.body());
        var sItems = payPage.select("#sItems").attr("value");
        var callback = payPage.select("#callback").attr("value");

        // pay for booking
        var resultResponse = post(payResponse.uri(),
                Map.of(
                        "sItems", sItems,
                        "callback", callback,
                        "idPaymentMethod", "969"
                )
        );
        Document resultPage = Jsoup.parse(resultResponse.body());

        var resultData = resultPage.select(".c-page-confirm__header");

        return resultData != null;
    }

    private HttpResponse<String> post(URI uri, Map<String, String> data) {

        log.debug("uri: " + uri);
        log.debug("data: " + data);

        var request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(ofFormData(data))
                .build();

        log.trace("Posting request: " + request);

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.trace("Exception on executing: " + request);
            throw new RuntimeException(e);
        }
        if (response.statusCode() != 200) {
            log.trace(request);
            log.trace(response.body());
            throw new RuntimeException("Request not succeeded. Status: " + response.statusCode());
        }

        log.trace("Response: " + response);
        log.trace("Response body: " + response.body());
        return response;

    }

    private HttpRequest.BodyPublisher ofFormData(Map<String, String> data) {
        var builder = new StringBuilder();
        for (var entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }


}
