library(ggplot2)

rides <- read.csv("rides.csv")[,c('start_time','end_time', 'elevation')]

rides$start_time <- as.POSIXct(rides$start_time, format="%d.%m.%Y %H:%M")
rides$end_time <- as.POSIXct(rides$end_time, format="%d.%m.%Y %H:%M")

#rides <- subset(rides, start_time > as.POSIXct("2017-03-28"))
#rides <- subset(rides, end_time   < as.POSIXct("2017-03-29"))

start_times = data.frame(time = rides$start_time, elevation=rep(0, length(rides$start_time)))
end_times = data.frame(time = rides$end_time, elevation=rides$elevation)

elevations = rbind(start_times, end_times)
elevations = elevations[order(elevations$time),]
elevations$total_elevation = cumsum(elevations$elevation)

plot(ggplot(aes(x = time, y = total_elevation), data = elevations) + geom_line())