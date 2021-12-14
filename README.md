

### Storage
The app uses in memory storage.

## The app itself
The application is listening to port 8085, and:

### Register shipment
The application is able to receive a
POST `http://localhost:8085/api/register` with payload (store it as a `shipment`):

```javascript
{
  "reference":"ABCD123456",
  "parcels" : [
  {
    "weight":1,
    "width": 10,
    "height": 10,
    "length": 10
  },
  {
    "weight":2,
    "width": 20,
    "height": 20,
    "length": 20
  }
  ]
}
```


### Push tracking

The application is able to receive a
PUT `http://localhost:8085/api/push` with the following payloads (`tracking`):

#### A

```javascript
{
  "status":"WAITING_IN_HUB",
  "parcels":2,
  "weight":null,
  "reference":"ABCD123456"
}
```

#### B

```javascript
{
  "status":"WAITING_IN_HUB",
  "parcels":2,
  "weight":2,
  "reference":"ABCD123456"
}
```

#### C

```javascript
{
  "status":"WAITING_IN_HUB",
  "parcels":1,
  "weight":15,
  "reference":"ABCD123456"
}
```

#### D

```javascript
{
  "status":"WAITING_IN_HUB",
  "parcels":2,
  "weight":30,
  "reference":"ABCD123456"
}
```

#### E

```javascript
{
  "status":"DELIVERED",
  "parcels":2,
  "weight":2,
  "reference":"ABCD123456"
}
```

#### F

```javascript
{
  "status":"DELIVERED",
  "parcels":2,
  "weight":30,
  "reference":"ABCD123456"
}
```

#### G

```javascript
{
  "status":"DELIVERED",
  "parcels":2,
  "weight":30,
  "reference":"EFGH123456"
}
```

#### H

```javascript
{
  "status":"DELIVERED",
  "parcels":null,
  "weight":30,
  "reference":"ABCD123456"
}
```

#### Business logic
Using the above examples:

-----

Given the provided `shipment` 
When
- `shipment` reference is equal to `tracking` reference 
- `shipment` parcel number is equal to `tracking` parcel number.
- `shipment` total weight is less than `tracking` weight.
- `tracking` status is `DELIVERED`

Then dispatches an application event

```javascript
{
  "reference":"ABCD123456",
  "status": "CONCILLIATION_REQUEST"
}
```
AND prints it into the console

- - - - - 

Given the provided `shipment` 
When
- `shipment` reference is equal to `tracking` reference. 
- `shipment` parcel number is equal to `tracking` parcel number.
- `shipment` total weight is **greater or equal** than `tracking` weight.
- `tracking` status is `DELIVERED`.

Then dispatches an application event

```javascript
{
  "reference":"ABCD123456",
  "status": "NOT_NEEDED"
}
```
AND print it into the console

- - - - - 

Given the provided `shipment` 
When

- `shipment` reference is equal to `tracking` reference 
- `tracking` status is not `DELIVERED`

Then dispatches an application event

```javascript
{
  "reference":"ABCD123456",
  "status": "INCOMPLETE"
}
```
AND print it into the console

- - - - - 
Given the provided `shipment` 
When

- `shipment` reference is equal to `tracking` reference 
- any other `tracking` field is null

Then dispatches an application event

```javascript
{
  "reference":"ABCD123456",
  "status": "INCOMPLETE"
}
```
AND print it into the console
- - - - - 
Given the provided `shipment` 
When
- `tracking` reference is not equal to`shipment` reference 

Then dispatches an application event

```javascript
{
  "reference":"EFGH123456",
  "status": "NOT_FOUND"
}
```
AND print it into the console

- - - - - 

#Instructions & reasoning

#####Dependencies
The language chosen for the implementation is [Scala](https://www.scala-lang.org/), so this repo 
assumes you have **Scala Build Tool (sbt)**  installed on your machine. 
If you don't have it please [follow the instructions](https://www.scala-sbt.org/download.html) in their website to do so.

#####Chosen language and frameworks
The application server is built on top of [Akka](https://akka.io/) for the main HTTP server. 
It has a bit of further dependencies and bindings in order to ensure proper route modelling and testing.

#### Run it
Once you have cloned the repo, execute 
 - `sbt test` to execute the unit tests
 - or `sbt run` to bring up the API and test it yourself
 
#Status of the challenge
The desired functionality for the test is mostly done, just lacking some testing configuration so the tests don't fail when run concurrently. More on this on the "testing" section here.
___
###My understanding of the challenge instructions
I interpreted `Shipment` and `Tracking` to be two different entities that can be included anytime by HTTP requests idempotently.
Provided that all bussiness cases descriptions began with 
> Given the provided `shipment`

I understood that `Shipment` was the *originator* of the application event provided the possible already existing `Traking`s. 
At the same time, I understood that the business logic can happen only *once* per `Shipment` and `Tracking` coincidence, and only when the `Shipment` is being registered. 
This way, the `Tracking` pusher only stores the value to the corresponding InMemory repository. 

I also understood the term `application event` as a Domain Event, which triggers the console printout separatedly 
from the use case.


___
###Technical reasons behind some choices
#####Domain driven design principles 
Most of the application is held inside the "core" bounded context, although some of the functionality not inherent to the domain (the challenge) is put inside the "shared kernel" module.
There's the usage of a layered architecture so as to decouple infrastructure (api and other tools) from the business logic (domain), and to have business use cases defined (application).
As the instructions of the code challenge didn't indicate any state change on both `Shipments` and `Trackings`, I treat both units as Value Objects being stored in a persistence mechanism.

##### Functional programming paradigm
As most the application is _plenty_ of side-effects, I tried to use the [Reader Monad](https://medium.com/rahasak/dependency-injection-with-reader-monad-in-scala-fe05b29e04dd) which is implemented
in [Cats](https://typelevel.org/cats/) to solve Dependency Injection in a functional way. As I'm not an expert on FP, this is for the mere fact of proving interest on functional programming, and the the api is not fully functional.

#### Testing
There are Unit,Integration,and End to End tests in the test module of the challenge. In order to differentiate between them, you can search in the challenge files
by typing "Unit|Integration|E2E" in your IDE of choice so as to find the files in it.

There are different flavors of testing, but the E2E tries to mimic the business requirements, following Gherkin notation.

**Important**: There's a problem with the tests as they are being executed concurrently and some of them may fail when run from the same suite. Execute them separatedly by their package (e2e,infrastructure,module) and they'll pass.


#What I could have done with more time

Provided the time I got to do the test (weekend) I would have liked to add completeness to the challenge with the following thigs:

- Being able to get `InMemoryRepository` runtime values in order to do background checks for the matches on E2E tests.
- Being able to capture part of the logs and stdout of the application to use them in E2E tests.
- Proper Domain Event -> Event Listener binding, in a Trait or some abstraction so the knowledge is not in the event itself
    - On this topic, also firing the events with the Actor Model so as to decouple event listening from code execution.
 
