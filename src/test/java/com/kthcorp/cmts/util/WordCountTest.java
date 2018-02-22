package com.kthcorp.cmts.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.file.Files.readAllBytes;
import static java.util.stream.Collectors.counting;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WordCountTest {

    //@Test(dataProvider = "provide_description_testMethod")
    public void test(String description, TestMethod testMethod) throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100_000; i++) {
            testMethod.run();
        }
        System.out.println(description + " took " + (System.currentTimeMillis() - start) / 1000d + "s");
    }

    @DataProvider
    public Object[][] provide_description_testMethod() {
        Path path = Paths.get("e:/tmp/tmp1.txt");
        return new Object[][]{
                {"classic", (TestMethod)() -> countWordsClassic(path)},
                {"mixed", (TestMethod)() -> countWordsMixed(path)},
                {"mixed2", (TestMethod)() -> countWordsMixed2(path)},
                {"stream", (TestMethod)() -> countWordsStream(path)},
                {"stream2", (TestMethod)() -> countWordsStream2(path)},
        };
    }

    private void countWordsClassic(final Path path) throws IOException {
        final Map<String, Integer> wordCounts = new HashMap<>();
        for (String word : new String(readAllBytes(path), StandardCharsets.UTF_8).split("\\W+")) {
            Integer oldCount = wordCounts.get(word);
            if (oldCount == null) {
                wordCounts.put(word, 1);
            } else {
                wordCounts.put(word, oldCount + 1);
            }
        }
    }

    private void countWordsMixed(final Path path) throws IOException {
        final Map<String, Integer> wordCounts = new HashMap<>();
        for (String word : new String(readAllBytes(path), StandardCharsets.UTF_8).split("\\W+")) {
            wordCounts.merge(word, 1, (key, oldCount) -> oldCount + 1);
        }
    }

    private void countWordsMixed2(final Path path) throws IOException {
        final Map<String, Integer> wordCounts = new HashMap<>();
        Pattern.compile("\\W+")
                .splitAsStream(new String(readAllBytes(path), StandardCharsets.UTF_8))
                .forEach(word -> wordCounts.merge(word, 1, (key, oldCount) -> oldCount + 1));
    }

    private void countWordsStream2(final Path tmpFile) throws IOException {
        Pattern.compile("\\W+").splitAsStream(new String(readAllBytes(tmpFile), StandardCharsets.UTF_8))
                .collect(Collectors.groupingBy(Function.<String>identity(), HashMap::new, counting()));
    }

    private void countWordsStream(final Path tmpFile) throws IOException {
        Arrays.stream(new String(readAllBytes(tmpFile), StandardCharsets.UTF_8).split("\\W+"))
                .collect(Collectors.groupingBy(Function.<String>identity(), HashMap::new, counting()));
    }

    interface TestMethod {
        void run() throws Exception;
    }
}