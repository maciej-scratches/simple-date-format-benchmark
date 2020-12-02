# DateUtils benchmark

`SimpleDateFormat` is expensive to create. This benchmark compares version `DateUtils#getCurrentDateTime` creating new `SimpleDateFormat` for every method invocation with a modified version of `DateUtils#getCurrentDateTime` that caches `SimpleDateFormat` in thread local variable.

The results: 
  
```
# Run complete. Total time: 00:04:05

Benchmark                           Mode  Cnt       Score       Error  Units
DateUtilsBenchmark.sdfConstructor  thrpt   20  261151.914 ± 12581.643  ops/s
DateUtilsBenchmark.sdfThreadLocal  thrpt   20  742660.062 ±  6895.499  ops/s
```