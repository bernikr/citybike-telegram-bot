library(ggmap)
library(ggplot2)

stations <- read.csv("stations.csv")
rides <- read.csv("rides.csv")[,c('start_station', 'end_station')]


location_box = c(right = 16.428252, bottom = 48.173532, left = 16.297446,top = 48.254632)

map <- get_stamenmap(bbox = location_box, zoom = 13, maptype = "toner-background")
map <- ggmap(map)

plot_rides <- function(){
  rides_uniq <- subset(data.frame(table(rides)), Freq>0)
  rides_uniq$start_lat <- stations$lat[match(rides_uniq$start_station, stations$station)]
  rides_uniq$start_lon <- stations$lon[match(rides_uniq$start_station, stations$station)]
  rides_uniq$end_lat <- stations$lat[match(rides_uniq$end_station, stations$station)]
  rides_uniq$end_lon <- stations$lon[match(rides_uniq$end_station, stations$station)]
  
  lines <- geom_segment(aes(x = start_lon,
                            y = start_lat,
                            xend=end_lon,
                            yend=end_lat,
                            alpha=Freq
                            ),
                        data = rides_uniq,
                        color="red",
                        size = 1
                        )
                        
  plot(map + lines
       + xlab("") + ylab("")
       + theme(axis.line = element_blank(),
               axis.text = element_blank(),
               axis.ticks = element_blank(),
               plot.margin = unit(c(0, 0, -1, -1), 'lines')
               )
       )
}

plot_heatmap <- function(){
  all_used_stations = data.frame(station=unlist(list(rides$start_station, rides$end_station)))
  all_used_stations$lat <- stations$lat[match(all_used_stations$station, stations$station)]
  all_used_stations$lon <- stations$lon[match(all_used_stations$station, stations$station)]
  
  heatmap <- stat_density2d(aes(x = lon,
                                y = lat,
                                fill = ..level..,
                                alpha = ..level..
                                ),
                            all_used_stations,
                            size = 0.1,
                            bins = 16,
                            geom = "polygon",
                            show.legend=F
                            )
  
  plot(map + heatmap
       + scale_fill_gradient(low = "green", high = "red")
       + scale_alpha(range = c(0, 0.5))
       + xlab("") + ylab("")
       + theme(axis.line = element_blank(),
               axis.text = element_blank(),
               axis.ticks = element_blank(),
               plot.margin = unit(c(0, 0, -1, -1), 'lines'
               )
       )
  )
}