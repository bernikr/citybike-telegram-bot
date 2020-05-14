def station_to_text(station, distance=None):
    def count_to_emoji(i):
        if i == 0:
            return "❌"
        if i <= 5:
            return "⚠️"
        else:
            return "✅"

    def distance_text(m):
        if m is None:
            return ""
        elif m < 1000:
            return " (%dm)" % m
        else:
            return " (%0.1fkm)" % (m / 1000)

    station_text = "[{s.name}](http://maps.google.com/maps?q={s.loc.lat},{s.loc.lon}){distance_text}\n" \
                   "{free_bikes_emoji} Bikes (*{s.free_bikes}*)\n" \
                   "{free_boxes_emoji} Slots (*{s.free_boxes}*)"
    return station_text.format(
        s=station,
        free_bikes_emoji=count_to_emoji(station.free_bikes),
        free_boxes_emoji=count_to_emoji(station.free_boxes),
        distance_text=distance_text(distance)
    )
