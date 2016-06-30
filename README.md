# swanson

Spending tracker.

## Running the Postgres Docker container in development

Build:

```
docker build --rm=true -t mjamesruggiero/postgresql:9.3 .
```

To run:

```
docker run -i -t -p 5432:5432 mjamesruggiero/postgresql:9.3
```

Connect:

```
psql -h <CONTAINER-IP-ADDRESS> -p 5432 -U admin -W swanson_development
```

* When prompted for the password, enter `password`
* Note that you can grab the Docker container IP address from the `docker ps` command.


### What's with the name?

<img src="http://showrenity.com/wp-content/uploads/2013/07/tumblr_mp0mow3b8y1r8269to1_500.gif" />

## License

Copyright © 2015 Michael Ruggiero

Distributed under the Eclipse Public License either version 1.0 or any later version.
