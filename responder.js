const Mono = Java.type('reactor.core.publisher.Mono');
const DefaultPayload = Java.type('io.rsocket.util.DefaultPayload');

function requestResponse(payload) {
    const data = payload.getDataUtf8();
    console.log("[Request from Server] > " + data);
    return Mono.just(DefaultPayload.create("Hello " + data + "!"));
}
