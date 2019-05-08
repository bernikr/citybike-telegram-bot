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
import java.util.*;
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

        String user = Optional.ofNullable(doc.select(".user-name-data").first())
                .orElseThrow(() -> new IOException("Login Error"))
                .text();
        user = user.substring(0, user.length()-1);
        log.info(String.format("Logged in as %s (%s)", username, user));
    }

    private int getRideCount() throws IOException {
        Document doc = s.load("https://www.citybikewien.at/de/meine-fahrten");
        int i = Integer.valueOf(
                Optional.of(doc.select("#content div + p").first())
                .orElseThrow(()-> new IOException("Invalid Page format"))
                .text()
                .split(" ")[2]
        );
        log.info(String.format("Found %d rides for user %s", i, username));
        return i;
    }

    @SneakyThrows
    private Stream<Ride> loadRidesFromPage(int pageNr) {
        log.info(String.format("Load Page %d of user %s", pageNr, username));
        Document doc = s.load(String.format("https://www.citybikewien.at/de/meine-fahrten?start=%d", (pageNr-1)*5));
        Element table = Optional.of(doc.select("#content table tbody").first())
                .orElseThrow(()-> new IOException("Invalid Page format"));

        return table.getElementsByTag("tr").stream().map(this::rowToRide);
    }

    @SneakyThrows
    private Ride rowToRide(Element row) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        Ride.RideBuilder rb = Ride.builder();


        rb.date(LocalDate.from(DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        .parse(row.select("td[data-title='Datum:'] span.td-date").first().text())));

        rb.startStation(row.select("td[data-title='Entlehnung:'] span.td-location-item").first().text());
        rb.startTime(LocalDateTime.from(f.parse(
                row.select("td[data-title='Entlehnung:'] span.td-date-item").first().text()
                + " "
                + row.select("td[data-title='Entlehnung:'] span.td-time-item").first().text()
        )));

        rb.endStation(row.select("td[data-title='Rückgabe:'] span.td-location-item").first().text());
        rb.endTime(LocalDateTime.from(f.parse(
                row.select("td[data-title='Rückgabe:'] span.td-date-item").first().text()
                        + " "
                        + row.select("td[data-title='Rückgabe:'] span.td-time-item").first().text()
        )));

        rb.price(Double.parseDouble(row.select("td[data-title='Betrag:']").first().text().substring(2).replace(',', '.')));
        rb.elevation(Integer.parseInt(row.select("td[data-title='Höhenmeter:']").first().text().replace(" m", "")));

        return rb.build();
    }

    public Stream<Ride> getRides() throws IOException {
        int pageCount = getRideCount()/5 +1;

        return IntStream.rangeClosed(1, pageCount)
                .mapToObj(this::loadRidesFromPage)
                .flatMap(o -> o);
    }

    public Stream<Ride> getRides(LocalDateTime since) throws IOException {
        return getRides()
                .takeWhile(r -> r.getEndTime().isAfter(since));
    }

    public static void main(String[] args) throws IOException {
        RideAPI rideAPI = new RideAPI("username", "psasword");
        rideAPI.login();
        rideAPI.getRides(LocalDateTime.of(2019,1,1,0,0))
                .forEach(System.out::println);
    }
}
