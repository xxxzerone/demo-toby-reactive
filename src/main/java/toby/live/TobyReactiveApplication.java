package toby.live;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class TobyReactiveApplication {

    @RestController
    public static class Controller {
        @RequestMapping("/hello")
        public Publisher<String> hello(String name) {
            return s -> s.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    s.onNext("Hello " + name);
                    s.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TobyReactiveApplication.class, args);
    }
}
