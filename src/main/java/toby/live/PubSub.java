package toby.live;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PubSub {

    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1)
                .limit(10)
                .collect(Collectors.toList()));
//        Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
//        Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
//        Publisher<Integer> map2Pub = mapPub(pub, s -> -s);
//        map2Pub.subscribe(logSub());
//        Publisher<Integer> sumPub = sumPub(pub);
//        sumPub.subscribe(logSub());
//        Publisher<Integer> reducePub = reducePub(pub, 0, Integer::sum);
//        Publisher<Integer> reducePub = reducePub(pub, 0, (a, b) -> a + b);
//        Publisher<String> reducePub = reducePub(pub, "", (a, b) -> a + "-" + b);
        Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(), (a, b) -> a.append(b).append(","));
        reducePub.subscribe(logSub());
//        mapPub.subscribe(logSub());
    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R acc, BiFunction<R, T, R> bf) {
        return sub -> pub.subscribe(new DelegateSub<T, R>(sub) {
            R result = acc;

            @Override
            public void onNext(T i) {
                result = bf.apply(result, i);
            }

            @Override
            public void onComplete() {
                sub.onNext(result);
                sub.onComplete();
            }
        });
//        return new Publisher<R>() {
//            @Override
//            public void subscribe(Subscriber<? super R> sub) {
//                pub.subscribe(new DelegateSub<T, R>(sub) {
//                    R result = acc;
//
//                    @Override
//                    public void onNext(T i) {
//                        result = bf.apply(result, i);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        sub.onNext(result);
//                        sub.onComplete();
//                    }
//                });
//            }
//        };
    }

//    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
//        return sub -> pub.subscribe(new DelegateSub(sub) {
//            int sum = 0;
//
//            @Override
//            public void onNext(Integer i) {
//                sum += i;
//            }
//
//            @Override
//            public void onComplete() {
//                sub.onNext(sum);
//                sub.onComplete();
//            }
//        });
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
//                pub.subscribe(new DelegateSub(sub) {
//                    int sum = 0;
//
//                    @Override
//                    public void onNext(Integer i) {
//                        sum += i;
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        sub.onNext(sum);
//                        sub.onComplete();
//                    }
//                });
//            }
//        };
//    }

    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return sub -> pub.subscribe(new DelegateSub<T, R>(sub) {
            @Override
            public void onNext(T i) {
                sub.onNext(f.apply(i));
            }
        });
//        return new Publisher<R>() {
//            @Override
//            public void subscribe(Subscriber<? super R> sub) {
//                pub.subscribe(new DelegateSub<T, R>(sub) {
//                    @Override
//                    public void onNext(T i) {
//                        sub.onNext(f.apply(i));
//                    }
//                });
//            }
//        };
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T i) {
                log.info("onNext: {}", i);
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError: {}", t);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(List<Integer> iter) {
        return sub -> sub.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                try {
                    iter.forEach(sub::onNext);
                    sub.onComplete();
                } catch (Throwable t) {
                    sub.onError(t);
                }
            }

            @Override
            public void cancel() {

            }
        });
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
//                sub.onSubscribe(new Subscription() {
//                    @Override
//                    public void request(long n) {
//                        try {
//                            iter.forEach(sub::onNext);
//                            sub.onComplete();
//                        } catch (Throwable t) {
//                            sub.onError(t);
//                        }
//                    }
//
//                    @Override
//                    public void cancel() {
//
//                    }
//                });
//            }
//        };
    }
}
