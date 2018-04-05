rides <- read.csv("rides.csv")[,c('date','start_time','end_time', 'elevation')]

rides$date = as.Date(rides$date, format="%d.%m.%Y")
rides$weekday = factor(weekdays(rides$date),levels=c("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"))
barplot(table(rides$weekday))