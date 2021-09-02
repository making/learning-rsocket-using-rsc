# Learning RSocket Using RSC

https://springone.io/2021/sessions/learning-rsocket-using-rsc

Slide: https://docs.google.com/presentation/d/1ehwAm1mvf7l6q2KOS8zESvF8nTSam0kCr1aYr77gHVM

## Demo1: 4 Interaction Models

### Request Response

```
rsc --route=request-response --data=Hello tcp://localhost:7000
```

```
rsc --route=request-response --data=Hello tcp://localhost:7000 --debug
```

```
rsc --route=request-response --data=Hello tcp://localhost:7000 --wiretap
```

### Request Stream

```
rsc --route=request-stream --data=Hello --stream tcp://localhost:7000
```

```
rsc --route=request-stream --data=Hello --stream --take 10 tcp://localhost:7000
```

### Request Channel

```
rsc --route=request-channel --data=- --channel tcp://localhost:7000
```

### Fire and Forget

```
rsc --route=fire-and-forget --data=Hello --fnf tcp://localhost:7000 --debug
```

## Demo2: Backpressure

```
rsc --route=request-stream --data=Hello --stream --take 10 tcp://localhost:7000
```

```
rsc --route=request-stream --data=Hello --take 10 --delayElement=100 --stream tcp://localhost:7000
```

```
rsc --route=request-stream --data=Hello --limitRate=4 --take 10 --delayElement=100 --stream tcp://localhost:7000
```

```
rsc --route=request-stream --data=Hello --limitRate=4 --take 10 --delayElement=100 --stream tcp://localhost:7000 --debug
```

```
rsc --route=request-stream --data=Hello --limitRate=1 --take 10 --delayElement=100 --stream tcp://localhost:7000
```

## Demo3: Session Resumption

```
socat -d TCP-LISTEN:7002,fork,reuseaddr TCP:localhost:7000
```

```
rsc --route=request-stream --data=Hello --limitRate=1 --delayElement=1000 --stream --resume 60 tcp://localhost:7002
```

## Demo4: Client Responder

> This feature in RSC has not been released

```
rsc-0.10.0-SNAPSHOT --route requester --responder ~/git/learning-rsocket-using-rsc/responder.js  tcp://localhost:7000 
```

## Demo5: Authentication

```
rsc --route=hello --data=SpringOne tcp://localhost:7000
```

```
rsc --authSimple=jdoe:rsocket --route=hello --data=SpringOne tcp://localhost:7000
```

```
rsc --authSimple=jdoe:password --route=hello --data=SpringOne tcp://localhost:7000
```

## Demo6: Tracing

```
rsc --route=tracing --stream tcp://localhost:7000
```

```
rsc --route=tracing --stream --trace=DEBUG --zipkinUrl=https://zipkin-insightful-aardvark-gf.apps.pcfone.io tcp://localhost:7000
```