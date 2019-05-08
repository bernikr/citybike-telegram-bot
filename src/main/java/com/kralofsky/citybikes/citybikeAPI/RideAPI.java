package com.kralofsky.citybikes.citybikeAPI;

import com.kralofsky.citybikes.citybikeAPI.util.Session;
import com.kralofsky.citybikes.entity.Ride;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class RideAPI {
    private Session s;
    private String username;
    private String password;

    public RideAPI(String username, String password) {
        this.username = username;
        this.password = password;

        s = new Session();
    }

    private void login() throws IOException {
        Document doc = s.load("https://www.citybikewien.at/de");

        Map<String, String> loginFields = doc.select("#mloginfrm input[type='hidden']")
                .stream()
                .map(Element::attributes)
                .collect(Collectors.toMap(e -> e.get("name"), e -> e.get("value")));

        loginFields.put("username", username);
        loginFields.put("password", password);


        doc = s.load("https://www.citybikewien.at/de/component/users/?task=user.login&Itemid=101", Connection.Method.POST, loginFields);

        String user = Optional.ofNullable(doc.selectFirst(".user-name-data"))
                .orElseThrow(() -> new IOException("Login Error"))
                .text();
        user = user.substring(0, user.length()-1);
        log.info(String.format("Logged in as %s (%s)", username, user));
    }

    private boolean loginCheck(Document doc) {
        return doc.selectFirst(".user-name")!=null;
    }

    Document getWithLogin(String url, Predicate<Document> loginCheck) throws ApiException {
        try {
            Document doc = s.load(url);
            if(!loginCheck.test(doc)){
                login();
                doc = s.load(url);
            }
            return doc;
        } catch (IOException e) {
            throw new ApiException("Error while connecting to the Citebike Page", e);
        }
    }

    private int getRideCount() throws ApiException {
        Document doc = getWithLogin("https://www.citybikewien.at/de/meine-fahrten", this::loginCheck);
        int i = Integer.valueOf(
                Optional.ofNullable(doc.selectFirst("#content div + p"))
                .orElseThrow(()-> new ApiException("Invalid Page format"))
                .text()
                .split(" ")[2]
        );
        log.info(String.format("Found %d rides for user %s", i, username));
        return i;
    }



    @SneakyThrows
    private Stream<Ride> loadRidesFromPage(int pageNr) {
        log.info(String.format("Load Page %d of user %s", pageNr, username));
        Document doc = getWithLogin(String.format("https://www.citybikewien.at/de/meine-fahrten?start=%d", (pageNr-1)*5), this::loginCheck);
        Element table = Optional.ofNullable(doc.selectFirst("#content table tbody"))
                .orElseThrow(()-> new ApiException("Invalid Page format"));

        return table.getElementsByTag("tr").stream().map(this::rowToRide);
    }

    private Ride rowToRide(Element row) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        Ride.RideBuilder rb = Ride.builder();


        rb.date(LocalDate.from(DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        .parse(row.selectFirst("td[data-title='Datum:'] span.td-date").text())));

        rb.startStation(row.selectFirst("td[data-title='Entlehnung:'] span.td-location-item").text());
        rb.startTime(LocalDateTime.from(f.parse(
                row.selectFirst("td[data-title='Entlehnung:'] span.td-date-item").text()
                + " "
                + row.selectFirst("td[data-title='Entlehnung:'] span.td-time-item").text()
        )));

        rb.endStation(row.selectFirst("td[data-title='Rückgabe:'] span.td-location-item").text());
        rb.endTime(LocalDateTime.from(f.parse(
                row.selectFirst("td[data-title='Rückgabe:'] span.td-date-item").text()
                        + " "
                        + row.selectFirst("td[data-title='Rückgabe:'] span.td-time-item").text()
        )));

        rb.price(Double.parseDouble(row.selectFirst("td[data-title='Betrag:']").text().substring(2).replace(',', '.')));
        rb.elevation(Integer.parseInt(row.selectFirst("td[data-title='Höhenmeter:']").text().replace(" m", "")));

        return rb.build();
    }

    public Stream<Ride> getRides() throws ApiException {
        int pageCount = getRideCount()/5 +1;

        return IntStream.rangeClosed(1, pageCount)
                .mapToObj(this::loadRidesFromPage)
                .flatMap(o -> o);
    }
}
