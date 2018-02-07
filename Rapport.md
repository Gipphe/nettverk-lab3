# Nettverkslab N-3
> Report

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
