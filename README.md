# restaurant
Voting system backend

##Vote for a restaurant by its id
```shell
curl -u User:pass -i http://localhost:8080/votes -X PUT -H "Content-Type: application/json" --data '1' | less
```
##Get user vote status for a restaurant by day
```shell
curl -u User:pass -i http://localhost:8080/votes/restaurants/1/dates/2020-12-25 -X GET | less
```
##Get user profile
```shell
curl -u User:pass -i http://localhost:8080 -X GET | less
```
##Get menus by date
```shell
curl -u Admin:pass -i http://localhost:8080/menus/dates/2020-12-25 -X GET | less
```
##Create restaurant
```shell
curl -u Admin:pass -i http://localhost:8080/restaurants -X POST -H "Content-Type: application/json" --data '{"name":"New Restaurant"}' | less
```
##Create menu
```shell
curl -u Admin:pass -i http://localhost:8080/menus -X POST -H "Content-Type: application/json" --data '{"restaurant": {"id":1}, "date":"2020-12-27", "dishes":[{"name":"Oranges", "price":7}]}' | less
```