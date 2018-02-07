# Nettverkslab N-3

> Report by: Jørgen Eide, Christoffer Arntzen, Victor Bakke and Jonas Iversen

## What is a socket

A socket would allow different network software applications to communicate
through a network. The technology would enable two (or more) computers to
communicate, but also communication between local software applications is
possible through a inter-process.
There are different types of sockets, but the main types are datagram socket,
which uses UDP technology, and the stream socket, which uses the TCP
technology:

- In a datagram socket, each package is individually routed and sent/received.
  By using this type of socket the order of the packages may not be reliable,
  and the package may also get lost in the transmission. The advantage is that
  the datagram socket uses less time to process/send/receive the different
  packages.
- In a stream socket, there is a built-in guarantee that the packages are sent
  in order (package two would come after package one and before package
  three). It also is very reliable when it comes to package losses.

So why would we use the datagram socket when the stream socket is so reliable?
The reliability comes with a cost: time. Stream socket use more time to
send/receive and process the transmission. It is therefore important to use
the to different sockets in the correct context. For time sensitive
applications like VoIP, games, videostreams etc. the datagram socket (UDP)
would have an advantage compared to stream socket as the demands for high
speed transmission is present. Tasks like file transferring should use stream
sockets, as you do not want files with content that are randomly ordered
or/and missing.

In this report, we would use most of our focus on the stream socket
technology.

## Transmission through a Transfer Control Protocol (TCP)-socket

The following is a short description of what is occurring when establishing a
TCP connection between to different network applications:

1. Server must be up and running (through a ServerSocket-object). It is
  specified what port number the transmission is running through.
1. The server must be listening – waiting for an incoming “client”.
1. The client then creates/instantiates a socket object, specifying the
  IP-address and the port number to communicate on.
1. Initial communication established, and the TCP-server creates a new socket
  where the client and the server can communicate. This allows for several
  client to make contact with the server within the same time-period.

## Application Layer protocol

We took great inspiration from the assignment description, and directly
implemented the suggested protocol from the text:

```text
###FROM2TO
```

- `###`: the amount to convert. Must be a number. Allows decimal points as both
  `.` and `,`.
- `FROM`: the currency to convert from, case insensitive. Should be a valid
  `String` representation of a currency code. If the currency code is not
  recognized, "Invalid currency" followed by the inputted currency, is
  returned from the server.
- `TO`: The currency to convert to, case insensitive. Should be a valid `String`
  representation of a currency code. If the currency code is not recognized,
  "Invalid currency" followed by the inputted currency, is returned from the
  server.

If the request does not adhere to this contract, "Invalid request" is returned
from the server.
Otherwise, the result from the conversion is returned, as a `String`
representation of a `double`

An example exchange would be like the following:

Request:

```text
200.50NOK2USD
```

Response:

```text
25.57880001
```

If the request simply reads `curr`, `currency` or `currencies`, a list of the
available currencies for conversion is returned.

If the request simply reads `help`, a textual description of the conversion
protocol above is returned.

## Server

The server allows multiple clients to connect to it through TCP at a time. It
defaults to port `5555`, but allows the user to initialize it with a port of
their choosing by passing the port as a CLI argument. It also prints the IPv4
addresses of the computer the server is running on in the console, and loads
the CSV file containing currency rates and instanciates a new
`CurrencyConverter` with the loaded currency rates.

The server, a `CurrencyTCPServer` instance, initializes by binding to the
specified port, and start listening for incoming connection requests. Upon
receiving a connection request, a new `ClientTransciever` is assigned to the
connection and passed the `CurrencyConverter`.

The `ClientTransciever` communicates with the client, and interprets input
received from the assigned client. If the input adheres to the conversion
protocol's contract, it attempts to convert the currency amount, and replies
with the resulting amount.

## Client

The client starts with a welcome message where the user can input a valid IPv4-address. 
With the help of the `ipCheck` method, the user will not be able to continue before the 
IP-address has a correct format (IPv4). Next, the client will ask the user for a port
number. The `hostPortNumber`method makes sure that the user input is valid port (below 65536 etc.).

Once the user has entered the ipv4-address and a valid port, the client creates a Socket and tries to
connect to host. If unsuccesful, the application will return "Could not connect to host" and exit.
If successful, the user will receive a console message (example below): 

  *You are now connected to the server /xxx.xx.xxx.xx through port number: 49285*
  _The client IP is xxx.xx.xxx.xxx and is using the local port: 49285_

While the client is connected, the `keyboardInput` will catch what the user are typing, and the 
`outboundToServer` sends the String `userinput` (which is equal to `keyboardInput.readline()`) to
the server. The server then sends (through `serverInput`) a respons to the clients request.

The client is using reg.ex (and also if-constraints) to display the respons from the server in a 
user friendly matter. If the server respons is i.e "25.2323414", the user will be presented with:

*You asked how much 500 JPY is in EUR.*
_500 JPY is 25.2323414 EUR_
