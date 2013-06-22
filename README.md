Parsely API Java Binding
========================

This library provides a pure Java interface to the Parsely API. The
documentation on which this binding is based is located at http://parsely.com/api/api\_ref.html
The project uses Apache Maven. To compile and run, you should first install
Maven usng the instructions at http://maven.apache.org/download.cgi

Getting the Code
----------------

`git clone` this repository - it is the primary source of the java-parsely code.

    git clone http://github.com/emmett9001/java-parsely.git

Package Compilation
-------------------

To compile the package and run the unit tests, use

    mvn package

Running the Tests
-----------------

To run the unit tests, use

    mvn test

Using java-parsely
------------------

To start using the Parsely API, create a `Parsely` object with your public and
private keys:

    Parsely p = new Parsely("mysite.com", "aklsdhjga09guaew09r8yvwrvy8anoeivhaknrehaerv");

To get a list of recent top posts you can use

    ArrayList<Post> posts = p.analytics(ParselyModel.kAspect.kPost, options);

This method calls the `/analytics/posts/` endpoint and returns a list of
`Post`s. The  arguments to `analytics` are an element of the enum
`ParselyModel.kAspect` indicating the return type, and an object of type
`RequestOptions`.

Request Options
---------------

The `RequestOptions` class holds the values of the most common API call
parameters. These include number of days, limit on the number of returned
items, the starting page for returned items, the date range for data on
returned items, and the published date range. The class uses the builder
pattern to allow simple initialization with the deisred parameters.

For example, to construct a set of request options that can be used across
multiple calls to the API, use

    RequestOptions options = RequestOptions.builder()
                                           .withLimit(7)
                                           .withDays(3)
                                           .build();

This creates an object with the given limit and days parameters.

Return Types
------------

The Parsely API binding returns objects that are subclasses of `ParselyModel`:
a catch-all class that delegates the construction of more specific objects.
A call to the API may return Sections, Topics, Authors, Posts, Referrers or Shares.

Some API functions take as parameters Post objects. This allows you to use the
API across multiple calls like so:

    ArrayList<Post> posts = p.analytics(ParselyModel.kAspect.kPost, options);
    Post post = p.postDetail(posts.get(0), options);

Recommendations API
-------------------

The API binding provides a `ParselyUser` class that models a single user
aroung the API endoints `/train` and `/related`. To create a user object, use

    ParselyUser user = new ParselyUser(p, "myuuid");

The `uuid` parameter is an arbitrary string used to uniquely identify the
user. To train the recommendation engine for this user on a given url, use

    user.train("http://whatever.com/my-training-link");

Once you've trained the engine, you can get custom recommendations for the
user with

    user.related(null, options);

License
-------

    Copyright (C) 2013 Emmett Butler, Parsely Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
