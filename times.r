rides <- read.csv("rides.csv")[,c('date','start_time','end_time')]

rides$date = as.Date(rides$date, format="%d.%m.%Y")
rides$weekday = factor(weekdays(rides$date),levels=c("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"))
#barplot(table(rides$weekday))

rides$start_time <- as.POSIXct(rides$start_time, format="%d.%m.%Y %H:%M")
rides$end_time <- as.POSIXct(rides$end_time, format="%d.%m.%Y %H:%M")

rides$start_time <- as.POSIXct(paste("01.01.2000", strftime(rides$start_time, format="%H:%M:%S")), format="%d.%m.%Y %H:%M")
#plot(rides$weekday,  rides$start_time)