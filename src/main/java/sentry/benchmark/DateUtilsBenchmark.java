package sentry.benchmark;

import io.sentry.DateUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;

public class DateUtilsBenchmark {

    @Benchmark
    @Fork(value = 1, warmups = 2)
    @BenchmarkMode(Mode.Throughput)
    public void sdfConstructor(Blackhole blackhole) {
        blackhole.consume(DateUtils.getCurrentDateTime());
    }

    @Benchmark
    @Fork(value = 1, warmups = 2)
    @BenchmarkMode(Mode.Throughput)
    public void sdfThreadLocal(Blackhole blackhole) {
        blackhole.consume(sentry.benchmark.DateUtils.getCurrentDateTime());
    }
}
