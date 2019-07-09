package com.kralofsky.citybikes.citybikeAPI;

import com.kralofsky.citybikes.citybikeAPI.util.Session;
import com.kralofsky.citybikes.entity.ApiUser;
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
public class DefaultRideAPI implements RideAPI {
    private Session s;
    private ApiUser user;

    DefaultRideAPI(ApiUser user) {
        this.user = user;
        s = user.getSession();

        if (s == null) {
            s = new Session();
            user.setSession(s);
        }
    }

    private void login() throws ApiException, IOException {
        Document doc = s.load("https://www.citybikewien.at/de");

        Map<String, String> loginFields = doc.select("#mloginfrm input[type='hidden']")
                .stream()
                .map(Element::attributes)
                .collect(Collectors.toMap(e -> e.get("name"), e -> e.get("value")));

        loginFields.put("username", user.getUsername());
        loginFields.put("password", user.getPassword());

        doc = s.load("https://www.citybikewien.at/de/component/users/?task=user.login&Itemid=101", Connection.Method.POST, loginFields);

        String userFullName = Optional.ofNullable(doc.selectFirst(".user-name-data"))
                .orElseThrow(() -> new ApiException("Login Error"))
                .text();
        userFullName = userFullName.substring(0, userFullName.length()-1);

        user.setFullName(userFullName);

        log.info(String.format("Logged in as %s (%s)", user.getUsername(), userFullName));
    }

    private boolean loginCheck(Document doc) {
        return doc.selectFirst(".user-name")!=null;
    }

    private Document getWithLogin(String url, Predicate<Document> loginCheck) throws ApiException {
        try {
            Document doc = s.load(url);
            if(!loginCheck.test(doc)){
                login();
                doc = s.load(url);
            }
            return doc;
        } catch (IOException e) {
            log.error("Error while connecting to the Citebike Page", e);
            throw new ApiException("Error while connecting to the Citebike Page", e);
        }
    }

    @Override
    public int getRideCount() throws ApiException {
        Document doc = getWithLogin("https://www.citybikewien.at/de/meine-fahrten", this::loginCheck);
        int i = Integer.valueOf(
                Optional.ofNullable(doc.selectFirst("#content div + p"))
                .orElseThrow(()-> new ApiException("Invalid Page format"))
                .text()
                .split(" ")[2]
        );
        log.info(String.format("Found %d rides for user %s", i, user.getUsername()));
        return i;
    }

    @SneakyThrows
    private Stream<Ride> loadRidesFromPage(int pageNr) {
        log.info(String.format("Load Page %d of user %s", pageNr, user.getUsername()));
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

    @Override
    public Stream<Ride> getRides() throws ApiException {
        int pageCount = getRideCount()/5 +1;

        return IntStream.rangeClosed(1, pageCount)
                .mapToObj(this::loadRidesFromPage)
                .flatMap(o -> o);
    }

    @Override
    public ApiUser getUser() {
        return user;
    }
}
