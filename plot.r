library(ggmap)

stations <- read.csv("stations.csv")
rides <- read.csv("rides.csv")[,c('start_station', 'end_station')]

rides_freq <- subset(data.frame(table(rides)), Freq>0)



points <- geom_point(aes(x = lon, y = lat), data = stations, color="red")




location_box = c(right = 16.428252, bottom = 48.173532, left = 16.297446,top = 48.254632)

map <- get_stamenmap(bbox = location_box, zoom = 12, maptype = "toner-background")
map <- ggmap(map)

plot(map + points
     + xlab("") + ylab("")
     + theme(axis.line = element_blank(),
             axis.text = element_blank(),
             axis.ticks = element_blank(),
             plot.margin = unit(c(0, 0, -1, -1), 'lines')
             )
     )