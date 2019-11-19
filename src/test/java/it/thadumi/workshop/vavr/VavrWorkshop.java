package it.thadumi.workshop.vavr;

import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction3;
import io.vavr.Function1;
import io.vavr.PartialFunction;
import io.vavr.collection.CharSeq;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Validation;
import io.vavr.test.Arbitrary;
import io.vavr.test.Property;

import org.junit.Test;
import java.util.Random;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Predicates.isIn;

/**
 * Vavr workshop
 * @author Theodor A. Dumitrescu
 * @email dumitrescu_theodor_a_@hotmail.it
 * @twitter tha_dumi
 * @github thadumi
 *
 * @version 1.0.0 Trento Nov 20, 2019
 */
public class VavrWorkshop {

    /**
     *    ````             ```       ````       ````             ```        ``````
     *    -hhh+           +hhh-     `ydddo      :hhh/           ohhh.    ./shddddh
     *     +mNm/         /mNm/      sNNmNN+      omNm:         oNNm:    +mNmdysooo
     *      omNm:       /mNm+      oNNd+mNm/      sNNm-       +mNm/    +NNm/`
     *       oNNm-     :mNmo      /mNm: +mNm:     `sNNd-     /mNm+     mNNs
     *        sNNd-   -mNNo      :mNN+   sNNd.     `yNNd.   :mNm+     `mNNo
     *        `sNNd. -dNNs      -mNNs    `hNNd.     `yNNh` -mNmo      `mNNo
     *         `yNNh-dNNs`     .dNNh`     .dNNy`     `hNNh:dNNo       `mNNo  `.
     *          `hNNmNNy`     `hNNd.       -mNNs      .hNNmNNs        `mNNo  ././-/`
     *           `ydddy`      oddd-         /ddd/      .hddds`         ddd+`-././-/`
     *            `````       ````           ````       ````           ```` `   ``
     * ROADMAP
     *
     *  ## Functions
     *      ### arity
     *      ### curring
     *      ### memoization
     *      ### composition
     *      ### partial functions to total functions
     *  ## Suppliers vs Laziness
     *  ## Tuples
     *  ## Persistent data structures
     *  ## Options: only evil peoples return null!
     *  ## Try
     *  ## Either
     *  ## Pattern matching
     *  ## Checking
     *      ### Property
     *      ### Pattern Matching
     *      ### Validators
     */

























    @SuppressWarnings("GrazieInspection")
    @Test
    public void patter_matching() {
        int i = 1;
        String s = Match(i).of(
                Case($(1), "one"),
                Case($(2), "two"),
                Case($(), "?")
        );
        System.out.println(s);

        Number plusOne = Match((Number)i).of(
                Case($(instanceOf(Integer.class)), _i -> ++_i),
                Case($(instanceOf(Double.class)), _d -> ++_d),
                Case($(), o -> {throw new NumberFormatException(); })
        );
        System.out.println(plusOne);

        String value = Match(Option.none()).of(
                Case($Some($()), "defined"),
                Case($None(), "empty")
        );
        System.out.println(value);

        // patter matching for checking arguments
        String arg = "-h";
        Match(arg).of(
                Case($(isIn("-h", "--help")), o -> run(this::displayHelp)),
                Case($(isIn("-v", "--version")), o -> run(this::displayVersion)),
                Case($(), o -> run(() -> {
                    throw new IllegalArgumentException(arg);
                }))
        );
    }

    @Test
    public void validators() {
        Validation<String,String> validName = Validation.valid("Daniel");
        Validation<String,Integer> validAge = Validation.valid(5);
        Validation.Builder<String, String, Integer> combine = Validation.combine(validName, validAge);
        Validation<Seq<String>, Person> newPersonWithValidator = combine.ap(Person::new);

        // Valid(Person(John Doe, 30))
        Validation<Seq<String>, Person> valid = PersonValidator.validatePerson("John Doe", 30);

        // Invalid(List(Name contains invalid characters: '!4?', Age must be greater than 0))
        Validation<Seq<String>, Person> invalid = PersonValidator.validatePerson("John? Doe!4", -1);
    }

    @Test
    public void properties() {
        Arbitrary<Integer> ints = Arbitrary.integer();

        // square(int) >= 0: OK, passed 1000 tests.
        Property.def("square(int) >= 0")
                .forAll(ints)
                .suchThat(i -> i * i >= 0)
                .check(100, 1_000)
                .assertIsSatisfied();
    }

    // for examples on the pattern matching
    private void displayHelp() {
        System.out.println("display help");
    }

    private void displayVersion() {
        System.out.println("display version");
    }
}

class Person {
    final String name;
    final int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

class PersonValidator {

    private static final String VALID_NAME_CHARS = "[a-zA-Z ]";
    private static final int MIN_AGE = 0;

    static Validation<Seq<String>, Person> validatePerson(String name, int age) {
        return Validation.combine(validateName(name), validateAge(age)).ap(Person::new);
    }

    private static Validation<String, String> validateName(String name) {
        return CharSeq.of(name).replaceAll(VALID_NAME_CHARS, "").transform(seq -> seq.isEmpty()
                ? Validation.valid(name)
                : Validation.invalid("Name contains invalid characters: '"
                + seq.distinct().sorted() + "'"));
    }

    private static Validation<String, Integer> validateAge(int age) {
        return age < MIN_AGE
                ? Validation.invalid("Age must be at least " + MIN_AGE)
                : Validation.valid(age);
    }

}













